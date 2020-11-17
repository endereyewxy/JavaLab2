package cn.endereye.framework.test.services;

import cn.endereye.framework.ioc.annotations.InjectSource;

@InjectSource
public class SAImpl implements SA {
    @Override
    public String getMessageA() {
        return "a-impl-a";
    }
}
