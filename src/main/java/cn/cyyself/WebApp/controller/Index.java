package cn.cyyself.WebApp.controller;

import cn.endereye.framework.web.WebResponse;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.annotations.RequestParam;

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
}