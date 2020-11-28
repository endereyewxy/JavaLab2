package cn.endereye.framework.test.controllers;

import cn.endereye.framework.ioc.annotations.InjectTarget;
import cn.endereye.framework.test.services.SB;
import cn.endereye.framework.web.WebResponse;
import cn.endereye.framework.web.annotations.RequestController;
import cn.endereye.framework.web.annotations.RequestEndpoint;
import cn.endereye.framework.web.annotations.RequestParam;

@RequestController("/b")
public class B {
    @InjectTarget
    private SB<Integer> sbInteger;

    @InjectTarget
    private SB<Double> sbDouble;

    @RequestEndpoint("/integer")
    public WebResponse doInteger(@RequestParam("a") int a,
                                 @RequestParam("b") int b) {
        return WebResponse.string(String.valueOf(sbInteger.div(a, b)));
    }

    @RequestEndpoint("/double")
    public WebResponse doDouble(@RequestParam("a") double a,
                                @RequestParam("b") double b) {
        return WebResponse.string(String.valueOf(sbDouble.div(a, b)));
    }
}
