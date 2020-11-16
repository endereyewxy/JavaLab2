package cn.endereye.framework.ioc.test.b;

import cn.endereye.framework.ioc.annotations.InjectSource;

@InjectSource
@Deprecated
public class BImplA implements B {
    @Override
    public String getMessageB() {
        return "b-impl-a";
    }
}
