package simplepingapplication.reporting;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FailureLogger {
    public void log(String body) {
        log.warn(body);
    }
}
