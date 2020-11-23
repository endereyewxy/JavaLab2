package cn.endereye.framework.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@javax.servlet.annotation.WebServlet(urlPatterns = "/")
public class WebServlet extends HttpServlet {
    private final WebManager manager = WebManager.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            manager.dispatch(req.getRequestURI(), req, resp).doResponse(req, resp);
        } catch (WebFrameworkException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
