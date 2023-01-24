package simplepingapplication.network.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.mockito.Mockito.*;
import static simplepingapplication.network.http.HttpPinger.HTTP_SCHEME;

class HttpPingerTest {

    private final HttpClient httpClient = mock();
    private HttpPinger httpPinger;

    @BeforeEach
    public void setup() {
        reset(httpClient);
        httpPinger = new HttpPinger(httpClient);
    }

    @Test
    public void shouldConvertValidResponse() throws InterruptedException, IOException {
        HttpResponse mockResponse = mock();
        int expectedStatus = 200;
        when(mockResponse.statusCode()).thenReturn(expectedStatus);
        when(httpClient.send(any(), any())).thenReturn(mockResponse);

        String host = "google.com";
        var result = httpPinger.ping(host);

        Assertions.assertEquals(expectedStatus, result.status());
        Assertions.assertEquals(HTTP_SCHEME + host, result.url());

    }

    @Test
    public void shouldReturnNullStatusWhenClientThrows() throws InterruptedException, IOException {

        when(httpClient.send(any(), any())).thenThrow(new IOException());

        String host = "google.com";
        var result = httpPinger.ping(host);

        Assertions.assertNull(result.status());
        Assertions.assertEquals(HTTP_SCHEME + host, result.url());

    }
}