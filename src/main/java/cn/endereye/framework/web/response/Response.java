package cn.endereye.framework.web.response;

import cn.endereye.framework.web.WebManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface Response {
    void doResponse(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    static Response error(int errCode) {
        return (req, resp) -> resp.sendError(errCode);
    }

    static Response jsp(Map<String, Object> attributes, String jsp) {
        return (req, resp) -> {
            for (Map.Entry<String, Object> entry : attributes.entrySet())
                req.setAttribute(entry.getKey(), entry.getValue());
            req.getRequestDispatcher(jsp).forward(req, resp);
        };
    }

    static Response redirect(String url) {
        return (req, resp) -> resp.sendRedirect(url);
    }

    static Response forward(String url) {
        return (req, resp) -> WebManager.getInstance().dispatch(url, req, resp).doResponse(req, resp);
    }
}
