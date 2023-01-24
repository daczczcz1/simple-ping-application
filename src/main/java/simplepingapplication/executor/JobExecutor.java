package simplepingapplication.executor;

import lombok.extern.slf4j.Slf4j;
import simplepingapplication.common.JobKey;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
@Slf4j
public class JobExecutor {
    private final Set<JobKey> jobsInProgress = ConcurrentHashMap.newKeySet();

    private final Executor executor;

    public JobExecutor(Executor executor) {
        this.executor = executor;
    }

    public void execute(JobKey jobKey, Consumer<JobKey> job) {
        if (!jobsInProgress.contains(jobKey)) {
            executor.execute(() -> {
                jobsInProgress.add(jobKey);
                try {
                    job.accept(jobKey);
                } finally {
                    jobsInProgress.remove(jobKey);
                }
            });
        } else {
            log.debug("Job of type {} for host {} still in progress, doing nothing", jobKey.jobType(), jobKey.host());
        }
    }
}
