package cn.endereye.framework.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet
public class Servlet extends HttpServlet {
    private final WebManager manager = WebManager.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        manager.dispatch(req.getRequestURI(), req, resp).doResponse(req, resp);
    }
}
