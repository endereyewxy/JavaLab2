package cn.cyyself.WebApp.controller;

import cn.endereye.framework.web.WebResponse;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.annotations.RequestParam;
import cn.endereye.framework.Loader;
@RequestController("/a")
public class Index {
    static {
        Loader.load("cn.cyyself.WebApp");
    }
    @RequestEndpoint("/a")
    public WebResponse doAGet(@RequestParam("something") String something) {
        return WebResponse.raw(something);
    }

    @RequestEndpoint(value = "/a", method = "POST")
    public WebResponse doAPost(@RequestParam("from") String from) {
        return WebResponse.redirect(from + "?token=" );
    }
}