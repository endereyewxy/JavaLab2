package cn.endereye.framework.ioc.test.a;

import cn.endereye.framework.ioc.annotations.InjectSource;

@InjectSource(name = "aImplA")
public class AImplA implements A {
    @Override
    public String getMessageA() {
        return "a-impl-a";
    }
}
