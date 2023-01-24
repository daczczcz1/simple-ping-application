package simplepingapplication.reporting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simplepingapplication.common.JobKey;
import simplepingapplication.common.JobResult;
import simplepingapplication.common.JobStatus;
import simplepingapplication.common.JobType;

import java.io.IOException;

import static org.mockito.Mockito.*;

class ReporterTest {

    public static final String HOST = "host";
    private final ReportingHttpClient httpClient = mock();
    private Reporter reporter;

    @BeforeEach
    public void setup() {
        reset(httpClient);
        reporter = new Reporter(httpClient);
    }

    @Test
    public void shouldNotReportIfJobSuccessful() throws IOException, InterruptedException {
        reporter.report(new JobResult(new JobKey(HOST, JobType.HTTP), "output", JobStatus.OK));

        verifyNoInteractions(httpClient);
    }

    @Test
    public void shouldReportIfJobFailed() throws IOException, InterruptedException {
        reporter.report(new JobResult(new JobKey(HOST, JobType.HTTP), "output", JobStatus.FAILED));

        verify(httpClient).report(any(), any());
    }

    @Test
    public void shouldMergeResponses() throws IOException, InterruptedException {
        String responseHttp = "responseHttp";
        String responseIcmp = "responseIcmp";
        String responseTrace = "responseTrace";

        reporter.report(new JobResult(new JobKey(HOST, JobType.HTTP), responseHttp, JobStatus.OK));
        reporter.report(new JobResult(new JobKey(HOST, JobType.ICMP), responseIcmp, JobStatus.OK));
        reporter.report(new JobResult(new JobKey(HOST, JobType.TRACE_ROUTE), responseTrace, JobStatus.FAILED));

        JobLastResponses expected = JobLastResponses.builder()
                .traceResponse(responseTrace)
                .tcpResponse(responseHttp)
                .icmpResponse(responseIcmp)
                .build();

        verify(httpClient).report(eq(HOST), eq(expected));
    }

    @Test
    public void shouldOverridePreviousResponse() throws IOException, InterruptedException {
        String responseHttp = "responseHttp";
        String newResponseHttp = "newResponseHttp";

        reporter.report(new JobResult(new JobKey(HOST, JobType.HTTP), responseHttp, JobStatus.OK));
        reporter.report(new JobResult(new JobKey(HOST, JobType.HTTP), newResponseHttp, JobStatus.FAILED));

        JobLastResponses expected = JobLastResponses.builder().tcpResponse(newResponseHttp).build();

        verify(httpClient).report(eq(HOST), eq(expected));
    }
}