package cn.endereye.framework.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface WebResponse {
    void doResponse(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    static WebResponse error(int errCode) {
        return (req, resp) -> resp.sendError(errCode);
    }

    static WebResponse raw(String raw) {
        return (req, resp) -> {
            resp.getWriter().write(raw);
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
}
