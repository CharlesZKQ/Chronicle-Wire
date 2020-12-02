package net.openhft.chronicle.wire.method;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.assertTrue;

public class MethodWriterProxyTest extends MethodWriterTest {
    @Before
    public void before() {
        System.setProperty("disableProxyCodegen", "true");
    }

    @After
    public void after() {
        System.clearProperty("disableProxyCodegen");
    }

    @Ignore("https://github.com/OpenHFT/Chronicle-Wire/issues/159")
    @Test
    public void multiOut() {
        super.multiOut();
    }

    @Test
    public void testPrimitives() {
        super.doTestPrimitives(true);
    }

    @Override
    protected void checkWriterType(Object writer) {
        assertTrue(writer instanceof Proxy);
    }
}

