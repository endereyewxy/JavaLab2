package cn.endereye.framework.ioc;

import cn.endereye.framework.ioc.annotations.InjectSource;
import cn.endereye.framework.ioc.annotations.InjectTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestA {
    interface A {
        String getMessageA();
    }

    @InjectSource(name = "aImplA")
    static class AImplA implements A {
        @Override
        public String getMessageA() {
            return "AImplA";
        }
    }

    @InjectSource(name = "aImplB")
    static class AImplB implements A {
        @Override
        public String getMessageA() {
            return "AImplB";
        }
    }

    interface B {
        String getMessageB();
    }

    interface X {
        String getMessageX();
    }

    @InjectSource()
    static class BImplX implements B, X {
        @Override
        public String getMessageB() {
            return "BImplX";
        }

        @Override
        public String getMessageX() {
            return "XImplB";
        }
    }

    static class C {
        public static C instance;

        public C() {
            instance = this;
        }

        @InjectTarget(policy = InjectTarget.Policy.BY_NAME)
        private A aImplA;

        @InjectTarget(policy = InjectTarget.Policy.BY_NAME)
        private A aImplB;

        @InjectTarget
        public B b;

        @InjectTarget
        public X x;

        public String getMessageC() {
            return aImplA.getMessageA() + aImplB.getMessageA() + b.getMessageB();
        }
    }

    @Test
    public void testBasic() {
        final Manager manager = new Manager();
        Assertions.assertDoesNotThrow(() -> {
            manager.register(AImplA.class);
            manager.register(AImplB.class);
            manager.register(BImplX.class);
            manager.register(C.class);
            manager.inject();
        });
        Assertions.assertEquals("AImplAAImplBBImplX", C.instance.getMessageC());
        Assertions.assertSame(C.instance.b, C.instance.x);
    }
}
