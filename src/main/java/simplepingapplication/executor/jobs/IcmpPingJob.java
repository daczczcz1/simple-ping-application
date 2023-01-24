package simplepingapplication.executor.jobs;

import lombok.RequiredArgsConstructor;
import simplepingapplication.common.JobKey;
import simplepingapplication.common.JobResult;
import simplepingapplication.common.JobStatus;
import simplepingapplication.network.icmp.IcmpPinger;
import simplepingapplication.network.icmp.IcmpPingerResult;
import simplepingapplication.reporting.Reporter;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class IcmpPingJob implements Consumer<JobKey> {

    private final IcmpPinger icmpPinger;
    private final Reporter reporter;

    @Override
    public void accept(JobKey key) {
        try {
            var result = buildResult(key, icmpPinger.ping(key.host()));
            reporter.report(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static JobResult buildResult(JobKey key, IcmpPingerResult icmpPingerResult) {
        return JobResult.builder()
                .status(icmpPingerResult.exitCode() != 0
                        ? JobStatus.FAILED
                        : JobStatus.OK)
                .key(key)
                .output(icmpPingerResult.output())
                .build();
    }
}
