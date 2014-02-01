import com.savvasdalkitsis.sequential.api.SequentialParallelTaskExecutors;
import com.savvasdalkitsis.sequential.api.SequentialTask;
import com.savvasdalkitsis.sequential.api.SequentialParallelTaskExecutor;
import com.savvasdalkitsis.sequential.api.TaskResult;

import java.util.concurrent.TimeUnit;

import static com.savvasdalkitsis.sequential.api.TaskResult.Builder;

public class Main {

    private static SequentialParallelTaskExecutor<String> executor = SequentialParallelTaskExecutors.fixedThreadPoolSequentialParallelTaskExecutor();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        run(executor, after(0, "task1"), after(2, "task2"));
        run(executor, failAfter(1), after(2, "task2"));
        run(executor, failAfter(4), after(0, "task2"));
        run(executor, failAfter(1), failAfter(5), failAfter(20));
        run(executor, after(20, "task1"));

        println("DONE");
    }

    private static void run(SequentialParallelTaskExecutor<String> executor, SequentialTask<String>... tasks) {
        long start = now();
        String string = "Start";
        println(string);
        TaskResult<String> result = executor.executeAndGet(10, TimeUnit.SECONDS, tasks);
        if (result.isSuccess()) {
            println(result.getValue());
        } else {
            println("FAILED");
        }
        println("End: " + (now() - start));
        println("");
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    private static SequentialTask<String> failAfter(final int sleep) {
        return waitAndReturn(sleep, Builder.<String>executionResult()
                .failure()
                .build());
    }

    private static SequentialTask<String> after(final int sleep, final String name) {
        return waitAndReturn(sleep, Builder.<String>executionResult()
                .withValue(name)
                .success()
                .build());
    }

    private static SequentialTask<String> waitAndReturn(final int sleep, final TaskResult<String> result) {
        return new SequentialTask<String>() {
            @Override
            public TaskResult<String> execute() {
                try {
                    Thread.sleep(sleep * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return result;
            }
        };
    }

    private static void println(String string) {
        System.out.println(string);
    }

}
