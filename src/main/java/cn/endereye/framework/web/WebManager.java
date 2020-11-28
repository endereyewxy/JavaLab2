package cn.endereye.framework.web;

import cn.endereye.framework.ioc.IOCFrameworkException;
import cn.endereye.framework.ioc.IOCManager;
import cn.endereye.framework.scanner.Scanner;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.annotations.RequestParam;
import cn.endereye.framework.web.model.UploadFiles;
import util.Pair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

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
        final RequestController annotation = controller.getAnnotation(RequestController.class);
        if (annotation != null                                     // must have @RequestController
            && controller.getAnnotation(Deprecated.class) == null // must not have @Depreciated
            && controllers.add(controller)) {                     // must not be already registered
            for (Method method : controller.getDeclaredMethods()) {
                final RequestEndpoint endpoint = method.getAnnotation(RequestEndpoint.class);
                if (endpoint != null) {
                    if (Modifier.isStatic(method.getModifiers()))
                        throw new WebFrameworkException("Endpoint must not be static: " + method.toGenericString());
                    if (!WebResponse.class.isAssignableFrom(method.getReturnType()))
                        throw new WebFrameworkException("Endpoint must return a response: " + method.toGenericString());
                    final Pair<String, String> key = new Pair<>(
                            annotation.value() + endpoint.value(), // request path
                            endpoint.method().toUpperCase());      // request method
                    if (endpoints.containsKey(key)) {
                        final String errMsg = String.format("Endpoint for %s is conflict: %s and %s",
                                                            key.getKey() + ":" + key.getValue(),
                                                            endpoints.get(key).toGenericString(),
                                                            method.toGenericString());
                        throw new WebFrameworkException(errMsg);
                    }
                    endpoints.put(key, method);
                }
            }
        }
    }

    public WebResponse dispatch(String url, HttpServletRequest req, HttpServletResponse resp, UploadFiles files) throws WebFrameworkException, UnsupportedEncodingException {
        for (Map.Entry<Pair<String, String>, Method> entry : endpoints.entrySet()) {
            if (!Pattern.matches(entry.getKey().getKey(), url) || !entry.getKey().getValue().equals(req.getMethod()))
                continue;
            final Method            endpoint = entry.getValue();
            final ArrayList<Object> args     = new ArrayList<>(endpoint.getParameterCount());
            for (Parameter param : endpoint.getParameters()) {
                Object arg = null;
                if (param.getType().isAssignableFrom(HttpServletRequest.class))
                    arg = req;
                if (param.getType().isAssignableFrom(HttpServletResponse.class))
                    arg = resp;
                if (param.getType().isAssignableFrom(UploadFiles.class))
                    arg = files;
                if (arg == null) {
                    final RequestParam annotation = param.getAnnotation(RequestParam.class);
                    if (annotation != null) {
                        req.setCharacterEncoding("UTF-8");//解决中文乱码问题
                        arg = WebParser.parse(param.getType(), req.getParameter(annotation.value()));
                    }
                }
                args.add(arg);
            }
            try {
                final Object controller = IOCManager.getInstance().getSingleton(endpoint.getDeclaringClass());
                return (WebResponse) endpoint.invoke(controller, args.toArray());
            } catch (IOCFrameworkException | IllegalAccessException | InvocationTargetException e) {
                throw new WebFrameworkException(e);
            }
        }
        // Cannot find a matched URL.
        return WebResponse.error(HttpServletResponse.SC_NOT_FOUND);
    }

    public void scan(String pkg) throws WebFrameworkException {
        try {
            Scanner.scan(pkg, this::register);
        } catch (Exception e) {
            throw new WebFrameworkException(e);
        }
    }

    private WebManager() {
    }
}
