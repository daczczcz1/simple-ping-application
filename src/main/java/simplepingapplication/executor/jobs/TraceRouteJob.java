package simplepingapplication.executor.jobs;

import lombok.RequiredArgsConstructor;
import simplepingapplication.common.JobKey;
import simplepingapplication.common.JobResult;
import simplepingapplication.network.traceroute.TraceRouteClient;
import simplepingapplication.network.traceroute.TraceRouteResult;
import simplepingapplication.reporting.Reporter;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class TraceRouteJob implements Consumer<JobKey> {

    private static final String TRACE_RESULT_FORMAT = "Timestamp: %d\n%s";

    private final TraceRouteClient traceRouteClient;
    private final Reporter reporter;

    @Override
    public void accept(JobKey key) {
        try {
            var result = buildResult(key, traceRouteClient.traceRouteTo(key.host()));
            reporter.report(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static JobResult buildResult(JobKey key, TraceRouteResult traceRouteResult) {
        return JobResult.builder()
                .key(key)
                .output(TRACE_RESULT_FORMAT.formatted(traceRouteResult.timestamp().toEpochMilli(), traceRouteResult.output()))
                .build();
    }
}
