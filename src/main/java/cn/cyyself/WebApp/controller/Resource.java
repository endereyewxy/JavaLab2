package cn.cyyself.WebApp.controller;

import cn.endereye.framework.ioc.annotations.InjectSource;
import cn.endereye.framework.web.WebResponse;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;

import javax.servlet.http.HttpServletRequest;

@InjectSource
@RequestController("/resource/")
public class Resource {
    @RequestEndpoint(".*")
    public WebResponse doStatic(HttpServletRequest req) {
        return WebResponse.file(req.getRequestURI());
    }
}
