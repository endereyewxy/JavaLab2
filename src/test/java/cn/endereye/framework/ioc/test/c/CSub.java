package cn.endereye.framework.ioc.test.c;

import cn.endereye.framework.ioc.annotations.InjectSource;

@InjectSource(name = "cSub")
public class CSub extends CBase {
    @Override
    public String getMessageC() {
        return "c-sub";
    }
}
