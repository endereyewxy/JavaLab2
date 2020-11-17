package cn.endereye.framework.web;

import cn.endereye.framework.ioc.IOCFrameworkException;
import cn.endereye.framework.ioc.IOCManager;
import cn.endereye.framework.utils.Annotations;
import cn.endereye.framework.utils.scanner.Scanner;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.response.Response;
import javafx.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class WebManager {
    private static WebManager instance = null;

    private final HashSet<Class<?>> controllers = new HashSet<>();

    private final HashMap<Pair<String, String>, Method> endpoints = new HashMap<>();

    public static WebManager getInstance() {
        if (instance == null) {
            synchronized (IOCManager.class) {
                if (instance == null)
                    instance = new WebManager();
            }
        }
        return instance;
    }

    public void register(Class<?> controller) throws WebFrameworkException {
        final RequestController annotation = Annotations.findAnnotation(RequestController.class, controller);
        if (annotation != null                                        // must hav @RequestController
                && controller.getAnnotation(Deprecated.class) == null // must not have @Depreciated
                && controllers.add(controller)) {                     // must not be already registered
            for (Method method : controller.getDeclaredMethods()) {
                final RequestEndpoint endpoint = method.getAnnotation(RequestEndpoint.class);
                if (endpoint != null) {
                    if (Modifier.isStatic(method.getModifiers()))
                        throw new WebFrameworkException("RequestEndpoint must not be static: " + method.toGenericString());
                    if (method.getReturnType() != Response.class &&
                            Arrays.asList(method.getReturnType().getInterfaces()).contains(Response.class))
                        throw new WebFrameworkException("RequestEndpoint must return a ModelAndView: " + method.toGenericString());
                    // Construct URL.
                    // At any time, if the current url starts with a slash, terminate the process immediately and ignore
                    // all higher level configurations.
                    String url = endpoint.path();
                    if (url.charAt(0) != '/')
                        url = annotation.path() + url;
                    if (url.charAt(0) != '/')
                        url = "/" + url;
                    final Pair<String, String> key = new Pair<>(url, endpoint.method().toUpperCase());
                    if (endpoints.containsKey(key))
                        throw new WebFrameworkException(String.format("URI %s is already matched to endpoint %s: %s",
                                url,
                                endpoints.get(key).toGenericString(),
                                method.toGenericString()));
                    endpoints.put(key, method);
                }
            }
        }
    }

    public Response dispatch(String url, HttpServletRequest req, HttpServletResponse resp) {
        final Pair<String, String> key = new Pair<>(url, req.getMethod());
        if (endpoints.containsKey(key)) {
            final Method endpoint = endpoints.get(key);
            final ArrayList<Object> args = new ArrayList<>(endpoint.getParameterCount());
            for (Parameter param : endpoint.getParameters()) {
                Object arg = null;
                if (param.getType().isAssignableFrom(HttpServletRequest.class))
                    arg = req;
                if (param.getType().isAssignableFrom(HttpServletResponse.class))
                    arg = resp;
                if (arg == null) {
                    final Class<?> type = param.getType();
                    final String val = req.getParameter(param.getName());
                    if (type.isAssignableFrom(String.class)) {
                        // 1st approach.
                        // Check if the target type can be directly assigned from strings.
                        arg = val;
                    } else {
                        // 2nd approach.
                        // Check if the target type has a static `valueOf` method. Usually this happens on primitive
                        // types.
                        try {
                            final Method valueOf = type.getMethod("valueOf", String.class);
                            if (Modifier.isStatic(valueOf.getModifiers()))
                                arg = valueOf.invoke(null, val);
                        } catch (NoSuchMethodException ignored) {
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            // Here the exception is thrown during converting HTTP request parameters from strings into
                            // custom data types.
                            e.printStackTrace();
                            return Response.error(HttpServletResponse.SC_BAD_REQUEST);
                        }
                        // 3nd approach.
                        // Regard the source value as a JSON string and parse it.
                        if (arg == null) {
                            throw new NotImplementedException();
                        }
                    }
                }
                args.add(arg);
            }
            try {
                final Object controller = IOCManager.getInstance().getSingleton(endpoint.getDeclaringClass());
                return (Response) endpoint.invoke(controller, args.toArray());
            } catch (IOCFrameworkException e) {
                // Cannot get instance of controller.
                e.printStackTrace();
                return Response.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // Similar to before.
                e.printStackTrace();
                return Response.error(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            // Cannot find a matched URL.
            return Response.error(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void scan(String pkg) throws WebFrameworkException {
        try {
            Scanner.scan(pkg, this::register);
        } catch (Exception e) {
            throw new WebFrameworkException("Failed when scanning classes");
        }
    }

    private WebManager() {
    }
}
