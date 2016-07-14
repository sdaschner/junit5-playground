package com.sebastian_daschner.junit5;

import com.sebastian_daschner.junit5.extension.MockedSystem;
import com.sebastian_daschner.junit5.extension.MockedSystemExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.Response;

import static com.sebastian_daschner.junit5.TestUtils.assertThat;
import static org.hamcrest.CoreMatchers.is;

@ExtendWith(MockedSystemExtension.class)
public class SystemTestExtendedWithTest {

    @Test
    public void test(MockedSystem mockedSystem) {
        final Response response = mockedSystem.target().request().get();

        assertThat(response.getStatusInfo().getFamily(), is(Response.Status.Family.SUCCESSFUL), () -> "status code is not 2xx");
        assertThat(response.getHeaderString("X-Hello"), is("World"), () -> "header 'X-Hello' not correct");
    }

}
