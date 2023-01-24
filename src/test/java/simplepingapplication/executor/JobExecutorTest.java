package simplepingapplication.executor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simplepingapplication.common.JobKey;
import simplepingapplication.common.JobType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"InfiniteLoopStatement", "StatementWithEmptyBody"})
class JobExecutorTest {

    JobExecutor jobExecutor;

    Executor internalExecutor = Executors.newCachedThreadPool();

    @BeforeEach
    public void setup() {
        jobExecutor = new JobExecutor(internalExecutor);
    }

    @Test
    public void shouldAcceptAJob() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean hasJobRan = new AtomicBoolean(false);
        jobExecutor.execute(new JobKey("somehost", JobType.HTTP), it -> {
            hasJobRan.set(true);
            latch.countDown();
        });
        latch.await();
        Assertions.assertTrue(hasJobRan.get());
    }

    @Test
    public void shouldAcceptAJobWithNewKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean hasJobRan = new AtomicBoolean(false);
        jobExecutor.execute(new JobKey("somehost", JobType.HTTP), it -> {
            while (true) {
            }
        });
        jobExecutor.execute(new JobKey("otherhost", JobType.HTTP), it -> {
            hasJobRan.set(true);
            latch.countDown();
        });

        latch.await();
        Assertions.assertTrue(hasJobRan.get());
    }

    @Test
    public void shouldAcceptAJobWithSameKeyButNotRunIt() throws InterruptedException {

        jobExecutor.execute(new JobKey("somehost", JobType.HTTP), it -> {
            while (true) {
            }
        });
        Thread.sleep(150);
        jobExecutor.execute(new JobKey("somehost", JobType.HTTP), it ->
                Assertions.fail("Job should not have ran while another is in progress"));
    }
}