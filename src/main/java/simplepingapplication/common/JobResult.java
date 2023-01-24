package simplepingapplication.common;

import lombok.Builder;

@Builder
public record JobResult(JobKey key, String output, JobStatus status) {
}
