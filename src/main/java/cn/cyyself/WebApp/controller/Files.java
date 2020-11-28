package cn.cyyself.WebApp.controller;

import cn.cyyself.WebApp.service.FileIO;
import cn.endereye.framework.web.WebResponse;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.annotations.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@RequestController("/files/")
public class Files {
    @RequestEndpoint(".*")
    public WebResponse doAGet(@RequestParam("") HttpServletRequest req) throws IOException {
        String path = URLDecoder.decode(req.getRequestURI().substring("/files".length()),"UTF-8");
        String new_path = FileIO.getPath(path);
        if (!new_path.equals(path)) return WebResponse.redirect("/files"+ new_path);
        if (!path.endsWith("/")) {
            //is file
            return WebResponse.raw(FileIO.getFile(path));
        }
        else {
            //is directory
            Map<String,Object> jsp_params = new HashMap<String,Object>();
            jsp_params.put("path",path);
            return WebResponse.jsp(jsp_params,"/listFile.jsp");
        }
    }
    @RequestEndpoint(value = ".*", method = "POST")
    public WebResponse doAPost(@RequestParam("action") String action, @RequestParam("dir_name") String dir_name, HttpServletRequest req) throws IOException {
        String path = URLDecoder.decode(req.getRequestURI().substring("/files".length()),"UTF-8");
        String new_path = FileIO.getPath(path);
        if (path.equals(new_path)) {
            if (action == null) return WebResponse.string("{\"status\":404,\"msg\":\"action is null\"}");
            switch (action) {
                case "mkdir":
                    if (FileIO.mkdir(path,dir_name)) return WebResponse.string("{\"status\":200,\"msg\":\"ok\"}");
                    else return WebResponse.string("{\"status\":500,\"msg\":\"Server Error\"}");
                case "upload_file":
                    return WebResponse.string("{\"status\":404,\"msg\":\"unknow action\"}");
                default:
                    return WebResponse.string("{\"status\":404,\"msg\":\"unknow action\"}");
            }
        }
        else return WebResponse.string("{\"status\":403,\"msg\":\"path unavailable\"}");
    }
}