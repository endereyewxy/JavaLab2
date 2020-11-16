package cn.endereye.framework.ioc.test.c;

import cn.endereye.framework.ioc.annotations.InjectSource;

@InjectSource(name = "cBase")
public class CBase {
    public String getMessageC() {
        return "c-base";
    }
}
