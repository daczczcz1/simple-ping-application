package simplepingapplication.reporting;

import lombok.RequiredArgsConstructor;
import simplepingapplication.common.JobKey;
import simplepingapplication.common.JobResult;
import simplepingapplication.common.JobStatus;
import simplepingapplication.common.JobType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class Reporter {
    private final ReportingHttpClient httpClient;
    private final Map<String, JobLastResponses> lastResponses = new ConcurrentHashMap<>();

    public void report(final JobResult result) throws InterruptedException {
        JobKey key = result.key();
        String host = key.host();
        JobType jobType = key.jobType();
        String response = result.output();

        mergeResponse(host, jobType, response);

        if (JobStatus.FAILED == result.status()) {
            httpClient.report(host, lastResponses.get(host));
        }
    }

    private void mergeResponse(String host, JobType jobType, String response) {
        switch (jobType) {
            case ICMP -> lastResponses.merge(
                    host,
                    JobLastResponses.builder().icmpResponse(response).build(),
                    (oldValue, newValue) -> oldValue.toBuilder().icmpResponse(response).build());
            case HTTP -> lastResponses.merge(
                    host,
                    JobLastResponses.builder().tcpResponse(response).build(),
                    (oldValue, newValue) -> oldValue.toBuilder().tcpResponse(response).build());
            case TRACE_ROUTE -> lastResponses.merge(
                    host,
                    JobLastResponses.builder().traceResponse(response).build(),
                    (oldValue, newValue) -> oldValue.toBuilder().traceResponse(response).build());
        }
    }
}

