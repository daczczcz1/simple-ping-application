package simplepingapplication.reporting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpRequest.BodyPublishers.ofString;

@RequiredArgsConstructor
@Slf4j
public class ReportingHttpClient {
    private final HttpClient httpClient;
    private final URI reportingUrl;
    private final FailureLogger failureLogger;

    public void report(String host, JobLastResponses lastResponses) throws InterruptedException {
        String body = JsonFormatter.formatJson(host, lastResponses);
        try {
            httpClient.send(HttpRequest.newBuilder()
                    .uri(reportingUrl)
                    .POST(ofString(body)).build(), HttpResponse.BodyHandlers.discarding());


        } catch (IOException e) {
            log.error("Problem reporting, IOException during request, message: {}", e.getLocalizedMessage());
        } finally {
            failureLogger.log(body);
        }
    }
}
