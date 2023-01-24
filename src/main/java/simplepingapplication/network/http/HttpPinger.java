package simplepingapplication.network.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Slf4j
@RequiredArgsConstructor
public final class HttpPinger {
    static final String HTTP_SCHEME = "http://";
    private final HttpClient httpClient;

    public HttpPingerResult ping(final String host) throws InterruptedException {
        HttpPingerResult result;
        String url = HTTP_SCHEME + host;

        var start = System.currentTimeMillis();

        try {
            var rawResponse = httpClient.send(
                    HttpRequest.newBuilder().uri(new URI(url)).GET().build(),
                    HttpResponse.BodyHandlers.discarding());
            var stop = System.currentTimeMillis();
            result = new HttpPingerResult(rawResponse.statusCode(), stop - start, url);

        } catch (IOException | URISyntaxException ex) {
            var stop = System.currentTimeMillis();
            result = new HttpPingerResult(null, stop - start, url);
        }
        log.info("HTTP request to host {} completed {}", host, result.status() == null ? "with failure" : "successfully");
        log.debug("HTTP result: {}", result);

        return result;
    }
}
