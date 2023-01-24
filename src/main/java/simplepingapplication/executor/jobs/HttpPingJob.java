package simplepingapplication.executor.jobs;

import lombok.RequiredArgsConstructor;
import simplepingapplication.common.JobKey;
import simplepingapplication.common.JobResult;
import simplepingapplication.common.JobStatus;
import simplepingapplication.network.http.HttpPinger;
import simplepingapplication.network.http.HttpPingerResult;
import simplepingapplication.reporting.Reporter;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class HttpPingJob implements Consumer<JobKey> {

    private static final String HTTP_RESULT_FORMAT = "URL: %s, status: %d, responseTimeMillis: %d";

    private final HttpPinger httpPinger;
    private final Reporter reporter;

    @Override
    public void accept(JobKey key) {
        try {
            var result = buildResult(key, httpPinger.ping(key.host()));
            reporter.report(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static JobResult buildResult(JobKey key, HttpPingerResult httpPingerResult) {
        return JobResult.builder()
                .status(httpPingerResult.status() == null
                        ? JobStatus.FAILED
                        : JobStatus.OK)
                .key(key)
                .output(HTTP_RESULT_FORMAT.formatted(httpPingerResult.url(), httpPingerResult.status(), httpPingerResult.responseTimeMillis()))
                .build();
    }
}
