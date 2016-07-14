package com.sebastian_daschner.junit5.extension;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockedSystem {

    private static final int STARTUP_TIME = 10;

    private final WebTarget target;

    /**
     * Builds up a mocked target as if the JAX-RS client library was used against a resource that takes 10 seconds to be up and running.
     */
    MockedSystem() {
        final WebTarget tut = mock(WebTarget.class);
        final Invocation.Builder builder = mock(Invocation.Builder.class);
        final Response notFoundResponse = mock(Response.class);
        final Response okResponse = mock(Response.class);
        final long start = System.currentTimeMillis();

        when(tut.request()).thenReturn(builder);
        when(builder.get()).then(a -> {
            if (System.currentTimeMillis() - start < STARTUP_TIME * 1000)
                return notFoundResponse;
            return okResponse;
        });

        when(notFoundResponse.getStatus()).thenReturn(404);
        when(notFoundResponse.getStatusInfo()).thenReturn(Response.Status.NOT_FOUND);

        when(okResponse.getStatus()).thenReturn(200);
        when(okResponse.getStatusInfo()).thenReturn(Response.Status.OK);
        when(okResponse.getHeaders()).thenReturn(new MultivaluedHashMap<>(Collections.singletonMap("X-Hello", "World")));
        when(okResponse.getHeaderString(anyString())).then(a -> ((Response) a.getMock()).getHeaders().getFirst(a.getArgument(0)));

        this.target = tut;
    }

    public WebTarget target() {
        return target;
    }

}
