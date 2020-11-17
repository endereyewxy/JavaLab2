package cn.endereye.framework.ioc;

import cn.endereye.framework.ioc.annotations.InjectTarget;
import cn.endereye.framework.ioc.test.a.A;
import cn.endereye.framework.ioc.test.b.B;
import cn.endereye.framework.ioc.test.c.CBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCase {
    static class Instance {
        public static Instance instance = null;

        @InjectTarget(policy = InjectTarget.Policy.BY_NAME)
        private A aImplB;

        @InjectTarget
        private B bImplB;

        @InjectTarget(policy = InjectTarget.Policy.BY_NAME)
        private CBase cSub;

        public Instance() {
            instance = this;
        }

        public String getMessage() {
            return aImplB.getMessageA() + " " + bImplB.getMessageB() + " " + cSub.getMessageC();
        }
    }

    @Test
    public void testBasic() {
        final IOCManager manager = IOCManager.getInstance();
        Assertions.assertDoesNotThrow(() -> {
            manager.register(Instance.class);
            manager.scan("cn.endereye.framework.ioc.test");
            manager.inject();
        });
        Assertions.assertEquals("a-impl-b b-impl-b c-sub", Instance.instance.getMessage());
    }
}
