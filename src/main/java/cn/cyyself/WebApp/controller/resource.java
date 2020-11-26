package cn.cyyself.WebApp.controller;

import cn.endereye.framework.web.WebResponse;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.annotations.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestController("/resource/")
public class resource {
    @RequestEndpoint(".*")
    public WebResponse doStatic(@RequestParam("") HttpServletRequest req) {
        return WebResponse.file(req.getRequestURI());
    }
}
