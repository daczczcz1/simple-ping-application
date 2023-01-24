package simplepingapplication.network.traceroute;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
public class TraceRouteClient {
    private static final String TRACE_COMMAND_WINDOWS_FORMAT = "tracert.exe %s";
    private static final String TRACE_COMMAND_LINUX_FORMAT = "traceroute %s";

    private final Runtime runtime;

    private static final String TRACE_COMMAND_EFFECTIVE_FORMAT =
            System.getProperty("os.name").toLowerCase().startsWith("windows")
                    ? TRACE_COMMAND_WINDOWS_FORMAT
                    : TRACE_COMMAND_LINUX_FORMAT;

    public TraceRouteResult traceRouteTo(String host) throws InterruptedException {
        try {

            Instant timestamp = Instant.now();
            Process process = runtime.exec(String.format(TRACE_COMMAND_EFFECTIVE_FORMAT, host));
            int exitCode = process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());
            log.info("Trace route command for host {} exited with code {}", host, exitCode);
            log.debug("Trace route command for host {} output: {}", exitCode, output);
            return new TraceRouteResult(timestamp, output);
        } catch (IOException ex) {
            throw new RuntimeException("Error executing tracing command for host: %s, message: %s".formatted(host, ex.getLocalizedMessage()));
        }
    }
}
