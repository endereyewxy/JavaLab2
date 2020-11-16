package cn.endereye.framework.ioc.test.a;

import cn.endereye.framework.ioc.annotations.InjectSource;

@InjectSource(name = "aImplB")
public class AImplB implements A {
    @Override
    public String getMessageA() {
        return "a-impl-b";
    }
}
