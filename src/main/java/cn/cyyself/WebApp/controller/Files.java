package cn.cyyself.WebApp.controller;

import cn.cyyself.WebApp.service.ListFiles;
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
        String new_path = ListFiles.getPath(path);
        System.out.println(path);
        if (!new_path.equals(path)) return WebResponse.redirect("/files"+ new_path);
        if (!path.endsWith("/")) {
            //is file
            return WebResponse.raw(ListFiles.getFile(path));
        }
        else {
            //is directory
            Map<String,Object> jsp_params = new HashMap<String,Object>();
            jsp_params.put("path",path);
            return WebResponse.jsp(jsp_params,"/listFile.jsp");
        }
    }
}