package cn.endereye.framework.test.services;

import cn.endereye.framework.ioc.annotations.InjectSource;

@InjectSource("sbDouble")
public class SBImplDouble implements SB<Double> {
    @Override
    public Double div(Double a, Double b) {
        return a / b;
    }
}
