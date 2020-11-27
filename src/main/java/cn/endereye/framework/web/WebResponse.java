package cn.endereye.framework.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface WebResponse {
    void doResponse(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    static WebResponse error(int errCode) {
        return (req, resp) -> resp.sendError(errCode);
    }

    static WebResponse raw(byte[] raw) {
        return (req, resp) -> {
            resp.setHeader("Content-Type","application/octet-stream");
            resp.getOutputStream().write(raw);
            resp.getOutputStream().close();
        };
    }

    static WebResponse string(String s) {
        return (req, resp) -> {
            resp.getWriter().write(s);
            resp.getWriter().close();
        };
    }

    static WebResponse jsp(Map<String, Object> attributes, String jsp) {
        return (req, resp) -> {
            attributes.forEach(req::setAttribute);
            req.getRequestDispatcher(jsp).forward(req, resp);
        };
    }

    static WebResponse redirect(String url) {
        return (req, resp) -> resp.sendRedirect(url);
    }

    static WebResponse file(String path) {
        return (req, resp) ->
                req.getServletContext().getNamedDispatcher("default").forward(new HttpServletRequestWrapper(req) {
                    @Override
                    public String getServletPath() {
                        return path;
                    }
                }, resp);
    }
}
