package cn.endereye.framework.test.services;

import cn.endereye.framework.ioc.annotations.InjectSource;

@InjectSource("sbInteger")
public class SBImplInteger implements SB<Integer> {
    @Override
    public Integer div(Integer a, Integer b) {
        return a / b;
    }
}
