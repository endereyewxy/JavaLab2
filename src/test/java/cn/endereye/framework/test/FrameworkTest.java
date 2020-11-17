package cn.endereye.framework.test;

import cn.endereye.framework.Loader;
import cn.endereye.framework.web.WebServlet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FrameworkTest {
    private static final WebServlet servlet = new WebServlet();

    static {
        Loader.load("cn.endereye.framework.test");
    }

    private static class FakeRequestBuilder {
        private final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        private final HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        public FakeRequestBuilder(String url, String method) {
            Mockito.when(req.getRequestURI()).thenReturn(url);
            Mockito.when(req.getMethod()).thenReturn(method);
        }

        public FakeRequestBuilder setParam(String key, String val) {
            Mockito.when(req.getParameter(Mockito.eq(key))).thenReturn(val);
            return this;
        }

        public FakeRequestBuilder checkErrorCode(int statusCode) throws IOException {
            Mockito.doAnswer(invocation -> {
                assertEquals(statusCode, invocation.getArgumentAt(0, int.class));
                return null;
            }).when(resp).sendError(Mockito.anyInt());
            return this;
        }

        public FakeRequestBuilder checkRaw(String raw) throws IOException {
            final PrintWriter writer = Mockito.mock(PrintWriter.class);
            Mockito.when(resp.getWriter()).thenReturn(writer);
            Mockito.doAnswer(invocation -> {
                assertEquals(raw, invocation.getArgumentAt(0, String.class));
                return null;
            }).when(writer).write(Mockito.anyString());
            return this;
        }

        public FakeRequestBuilder checkRedirect(String url) throws IOException {
            Mockito.doAnswer(invocation -> {
                assertEquals(url, invocation.getArgumentAt(0, String.class));
                return null;
            }).when(resp).sendRedirect(Mockito.anyString());
            return this;
        }

        public void testWith(HttpServlet servlet) throws ServletException, IOException {
            servlet.service(req, resp);
        }
    }

    @Test
    public void test404() {
        assertDoesNotThrow(() -> new FakeRequestBuilder("/no/such/endpoint", "GET")
                .checkErrorCode(404)
                .testWith(servlet));
    }

    @Test
    public void test500() {
        assertDoesNotThrow(() -> new FakeRequestBuilder("/b/integer", "GET")
                .setParam("a", "1")
                .setParam("b", "0")
                .checkErrorCode(500)
                .testWith(servlet));
    }

    @Test
    public void test_A_A_GET() {
        assertDoesNotThrow(() -> new FakeRequestBuilder("/a/a", "GET")
                .setParam("something", "some parameter")
                .checkRaw("a-impl-a: some parameter")
                .testWith(servlet));
    }

    @Test
    public void test_A_A_POST() {
        assertDoesNotThrow(() -> new FakeRequestBuilder("/a/a", "POST")
                .setParam("from", "http://www.baidu.com/")
                .checkRedirect("http://www.baidu.com/?token=a-impl-a")
                .testWith(servlet));
    }

    @Test
    public void test_B_INTEGER() {
        assertDoesNotThrow(() -> new FakeRequestBuilder("/b/integer", "GET")
                .setParam("a", "9")
                .setParam("b", "3")
                .checkRaw("3")
                .testWith(servlet));
    }

    @Test
    public void test_B_DOUBLE() {
        assertDoesNotThrow(() -> new FakeRequestBuilder("/b/double", "GET")
                .setParam("a", "9")
                .setParam("b", "4")
                .checkRaw("2.25")
                .testWith(servlet));
    }
}
