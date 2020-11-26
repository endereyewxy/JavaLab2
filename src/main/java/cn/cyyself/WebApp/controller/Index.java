package cn.cyyself.WebApp.controller;

import cn.endereye.framework.web.WebResponse;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.annotations.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestController("/a")
public class Index {
    @RequestEndpoint("/a")
    public WebResponse doAGet(@RequestParam("something") String something) {
        return WebResponse.raw(something);
    }

    @RequestEndpoint(value = "/a", method = "POST")
    public WebResponse doAPost(@RequestParam("from") String from) {
        return WebResponse.redirect(from + "?token=");
    }

    // FIXME Just for testing
    @RequestEndpoint("/static/(.*)")
    public WebResponse doStatic(@RequestParam("") HttpServletRequest req) {
        return WebResponse.file(req.getRequestURI().substring(2)); // remove the beginning "/a"
    }
}