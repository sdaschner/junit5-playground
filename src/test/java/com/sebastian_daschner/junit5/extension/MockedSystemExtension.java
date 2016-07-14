package com.sebastian_daschner.junit5.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.opentest4j.AssertionFailedError;

import javax.ws.rs.core.Response;
import java.util.concurrent.locks.LockSupport;

public class MockedSystemExtension implements ParameterResolver {

    private static final int STARTUP_TIMEOUT = 30;
    private static final int STARTUP_PING_DELAY = 2;

    @Override
    public boolean supports(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MockedSystem.class);
    }

    @Override
    public Object resolve(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        final MockedSystem mockedSystem = new MockedSystem();

        waitForStartUp(mockedSystem);

        return mockedSystem;
    }

    /**
     * Blocks until the system is started.
     */
    private void waitForStartUp(MockedSystem mockedSystem) {
        final long timeout = System.currentTimeMillis() + STARTUP_TIMEOUT * 1000;
        while (mockedSystem.target().request().get().getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            System.out.println("waiting for application startup");
            LockSupport.parkNanos(1_000_000_000 * STARTUP_PING_DELAY);
            if (System.currentTimeMillis() > timeout)
                throw new AssertionFailedError("Application wasn't started before timeout!");
        }
    }

}
