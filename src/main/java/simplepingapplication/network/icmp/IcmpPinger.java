package simplepingapplication.network.icmp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class IcmpPinger {

    private static final String PING_COMMAND_FORMAT = "ping -n 5 %s";

    private final Runtime runtime;

    public IcmpPingerResult ping(String host) throws InterruptedException {
        try {
            Process process = runtime.exec(String.format(PING_COMMAND_FORMAT, host));
            int exitCode = process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());
            log.info("Ping command for host {} exited with code {}", host, exitCode);
            log.debug("Ping command for host {} output: {}", exitCode, output);
            return new IcmpPingerResult(exitCode, output);
        } catch (IOException ex) {
            throw new RuntimeException("Error executing ping command for host: %s, message: %s".formatted(host, ex.getLocalizedMessage()));
        }
    }
}
