package cn.endereye.framework.test.controllers;

import cn.endereye.framework.ioc.annotations.InjectTarget;
import cn.endereye.framework.test.services.SA;
import cn.endereye.framework.web.WebResponse;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.annotations.RequestParam;

@RequestController("/a")
public class A {
    @InjectTarget
    private SA sa;

    @RequestEndpoint("/a")
    public WebResponse doAGet(@RequestParam("something") String something) {
        return WebResponse.string(sa.getMessageA() + ": " + something);
    }

    @RequestEndpoint(value = "/a", method = "POST")
    public WebResponse doAPost(@RequestParam("from") String from) {
        return WebResponse.redirect(from + "?token=" + sa.getMessageA());
    }
}
