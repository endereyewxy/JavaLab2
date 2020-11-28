package cn.endereye.framework.web;

import cn.endereye.framework.web.model.UploadFiles;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@javax.servlet.annotation.WebServlet(urlPatterns = "/")
public class WebServlet extends HttpServlet {
    private final WebManager manager = WebManager.getInstance();
    private final int maxFileSize = 1024 * 1024 * 16;
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UploadFiles files = null;
        if (ServletFileUpload.isMultipartContent(req)) {
            DiskFileItemFactory dff = new DiskFileItemFactory();
            dff.setSizeThreshold(maxFileSize);
            ServletFileUpload upload = new ServletFileUpload(dff);
            upload.setSizeMax(maxFileSize);
            try {
                files = new UploadFiles(upload.parseRequest(req));
            } catch (FileUploadException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        try {
            manager.dispatch(req.getRequestURI(), req, resp, files).doResponse(req, resp);
        } catch (WebFrameworkException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
