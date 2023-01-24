package simplepingapplication;

import lombok.extern.slf4j.Slf4j;
import simplepingapplication.common.JobKey;
import simplepingapplication.common.JobType;
import simplepingapplication.executor.JobExecutor;
import simplepingapplication.executor.jobs.HttpPingJob;
import simplepingapplication.executor.jobs.IcmpPingJob;
import simplepingapplication.executor.jobs.TraceRouteJob;
import simplepingapplication.network.http.HttpPinger;
import simplepingapplication.network.icmp.IcmpPinger;
import simplepingapplication.network.traceroute.TraceRouteClient;
import simplepingapplication.reporting.FailureLogger;
import simplepingapplication.reporting.Reporter;
import simplepingapplication.reporting.ReportingHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SimplePingApplication {

    private static final String PROPERTIES_FILE = "/app.properties";
    private static final String HOSTS_FILE = "/hosts.txt";
    private static final String HTTP_PING_TIMEOUT_MILLIS_PROPERTY = "httpPingTimeoutMillis";
    private static final String HTTP_PING_DELAY_MILLIS_PROPERTY = "httpPingDelayMillis";
    private static final String ICMP_PING_DELAY_MILLIS_PROPERTY = "icmpPingDelayMillis";
    private static final String REPORT_URL_PROPERTY = "reportUrl";
    private static final String TRACE_ROUTE_DELAY_MILLIS_PROPERTY = "traceRouteDelayMillis";

    public static void main(String[] args) throws IOException {
        // load configuration
        Properties properties = loadProperties();
        List<String> hosts = loadHosts();

        // initialize classes
        JobExecutor jobExecutor = new JobExecutor(Executors.newCachedThreadPool());

        HttpPinger httpPinger = createHttpPinger(properties);
        IcmpPinger icmpPinger = new IcmpPinger(Runtime.getRuntime());
        TraceRouteClient traceRouteClient = new TraceRouteClient(Runtime.getRuntime());
        Reporter reporter = createReporter(properties);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // schedule jobs
        scheduledExecutorService.scheduleAtFixedRate(
                () -> hosts.forEach(host -> jobExecutor.execute(new JobKey(host, JobType.HTTP), new HttpPingJob(httpPinger, reporter))),
                0,
                Long.parseLong(properties.getProperty(HTTP_PING_DELAY_MILLIS_PROPERTY)),
                TimeUnit.MILLISECONDS
        );

        scheduledExecutorService.scheduleAtFixedRate(
                () -> hosts.forEach(host -> jobExecutor.execute(new JobKey(host, JobType.ICMP), new IcmpPingJob(icmpPinger, reporter))),
                0,
                Long.parseLong(properties.getProperty(ICMP_PING_DELAY_MILLIS_PROPERTY)),
                TimeUnit.MILLISECONDS
        );

        scheduledExecutorService.scheduleAtFixedRate(
                () -> hosts.forEach(host -> jobExecutor.execute(new JobKey(host, JobType.TRACE_ROUTE), new TraceRouteJob(traceRouteClient, reporter))),
                0,
                Long.parseLong(properties.getProperty(TRACE_ROUTE_DELAY_MILLIS_PROPERTY)),
                TimeUnit.MILLISECONDS
        );
    }

    private static Reporter createReporter(Properties properties) {
        FailureLogger failureLogger = new FailureLogger();

        ReportingHttpClient reportingHttpClient = new ReportingHttpClient(
                HttpClient.newBuilder().build(),
                URI.create(properties.getProperty(REPORT_URL_PROPERTY)),
                failureLogger);

        return new Reporter(reportingHttpClient);
    }

    private static HttpPinger createHttpPinger(Properties properties) {
        Duration timeout = Duration.of(Long.parseLong(properties.getProperty(HTTP_PING_TIMEOUT_MILLIS_PROPERTY)),
                ChronoUnit.MILLIS);

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();

        return new HttpPinger(httpClient);
    }

    private static Properties loadProperties() throws IOException {
        try (InputStream in = SimplePingApplication.class.getResourceAsStream(PROPERTIES_FILE)) {
            Properties appProps = new Properties();
            if (in != null) {
                appProps.load(new InputStreamReader(in));
            } else {
                throw new IllegalStateException("Could not find app.properties file.");
            }
            return appProps;
        }
    }

    private static List<String> loadHosts() throws IOException {
        var result = new ArrayList<String>();
        InputStream inputStream = SimplePingApplication.class.getResourceAsStream(HOSTS_FILE);
        if (inputStream == null) {
            throw new IllegalStateException("Could not find hosts.txt file.");
        }
        try (BufferedReader r = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = r.readLine();
            while (line != null) {
                result.add(line);
                line = r.readLine();
            }
        }
        return Collections.unmodifiableList(result);
    }
}