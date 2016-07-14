package com.sebastian_daschner.junit5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.concurrent.locks.LockSupport;

import static com.sebastian_daschner.junit5.TestUtils.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Example of a system test scenario where a JAX-RS client is used to hammer an API.
 * The client needs to wait until the server has been started.
 * <p>
 * Before JUnit 5 this could be done with a {@code @Rule}.
 * This example is with inline functionality
 * @see SystemTestExtendedWithTest
 */
public class SystemTest {

    private static final int STARTUP_TIMEOUT = 30;
    private static final int STARTUP_PING_DELAY = 2;

    // this would be the target for a real test, we're using a Mock here, just for demo purposes
    private WebTarget tut;

    @BeforeEach
    public void setUp() {
        tut = buildMockedTarget();
        waitForApplicationStartUp();
    }

    private void waitForApplicationStartUp() {
        final long timeout = System.currentTimeMillis() + STARTUP_TIMEOUT * 1000;
        while (tut.request().get().getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            System.out.println("waiting for application startup");
            LockSupport.parkNanos(1_000_000_000 * STARTUP_PING_DELAY);
            if (System.currentTimeMillis() > timeout)
                throw new AssertionFailedError("Application wasn't started before timeout!");
        }
    }

    @Test
    public void test() {
        final Response response = tut.request().get();

        assertThat(response.getStatusInfo().getFamily(), is(Response.Status.Family.SUCCESSFUL), () -> "status code is not 2xx");
        assertThat(response.getHeaderString("X-Hello"), is("World"), () -> "header 'X-Hello' not correct");
    }

    /**
     * Builds up a mocked target as if the JAX-RS client library was used against a resource that takes 10 seconds to be up and running.
     */
    private static WebTarget buildMockedTarget() {
        final WebTarget tut = mock(WebTarget.class);
        final Invocation.Builder builder = mock(Invocation.Builder.class);
        final Response notFoundResponse = mock(Response.class);
        final Response okResponse = mock(Response.class);
        final long start = System.currentTimeMillis();

        when(tut.request()).thenReturn(builder);
        when(builder.get()).then(a -> {
            if (System.currentTimeMillis() - start < 10_000)
                return notFoundResponse;
            return okResponse;
        });

        when(notFoundResponse.getStatus()).thenReturn(404);
        when(notFoundResponse.getStatusInfo()).thenReturn(Response.Status.NOT_FOUND);

        when(okResponse.getStatus()).thenReturn(200);
        when(okResponse.getStatusInfo()).thenReturn(Response.Status.OK);
        when(okResponse.getHeaders()).thenReturn(new MultivaluedHashMap<>(Collections.singletonMap("X-Hello", "World")));
        when(okResponse.getHeaderString(anyString())).then(a -> ((Response) a.getMock()).getHeaders().getFirst(a.getArgument(0)));

        return tut;
    }

}
