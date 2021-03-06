package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

public class BaseTestEndpoint {

    protected String userString = "John$5$Mobile$1";

    protected HttpServletRequest createMockRequest() {
        HttpServletRequest result = Mockito.mock(HttpServletRequest.class);

        Mockito.when(result.getHeader(HttpsUserContextFactory.SSL_CLIENT_KEY))
                .thenReturn(userString);

        return result;
    }
}
