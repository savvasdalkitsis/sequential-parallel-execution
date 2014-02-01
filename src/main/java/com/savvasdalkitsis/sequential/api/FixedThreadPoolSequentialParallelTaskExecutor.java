package com.savvasdalkitsis.sequential.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.savvasdalkitsis.sequential.api.TaskResult.Builder;
import static com.savvasdalkitsis.sequential.api.WrappedTask.wrap;

class FixedThreadPoolSequentialParallelTaskExecutor<T> implements SequentialParallelTaskExecutor<T> {

    @Override
    public TaskResult<T> executeAndGet(int timeout, TimeUnit timeUnit, SequentialTask<T>... tasks) {
        ExecutorService executorService = Executors.newFixedThreadPool(tasks.length);
        ResultHolder<T> resultHolder = new ResultHolder<T>();
        Semaphore globalLock = notAllowed();
        List<Semaphore> taskSemaphores = new ArrayList<Semaphore>(tasks.length);

        executeAllTasks(executorService, resultHolder, globalLock, taskSemaphores, tasks);

        boolean gotResult = waitForResult(timeout, timeUnit, globalLock);

        releaseAll(taskSemaphores);
        if (gotResult) {
            return Builder.<T>executionResult()
                    .success()
                    .withValue(resultHolder.getResult())
                    .build();
        }
        return Builder.<T>executionResult()
                .failure()
                .build();
    }

    private boolean waitForResult(int timeout, TimeUnit timeUnit, Semaphore globalLock) {
        boolean gotResult = false;
        try {
            gotResult = globalLock.tryAcquire(timeout, timeUnit);
        } catch (InterruptedException e) {
            // no results
            e.printStackTrace();
        }
        return gotResult;
    }

    private void executeAllTasks(ExecutorService executorService, ResultHolder<T> resultHolder, Semaphore globalLock, List<Semaphore> taskSemaphores, SequentialTask<T>[] tasks) {
        Semaphore allowToDeliverResults = allowed();
        taskSemaphores.add(allowToDeliverResults);
        for (SequentialTask<T> task : tasks) {
            Semaphore allowNextTaskToDeliver = notAllowed();
            executorService.execute(wrap(task, resultHolder, allowToDeliverResults, allowNextTaskToDeliver, globalLock));
            allowToDeliverResults = allowNextTaskToDeliver;
            taskSemaphores.add(allowToDeliverResults);
        }
    }

    private void releaseAll(List<Semaphore> taskSemaphores) {
        for (Semaphore taskSemaphore : taskSemaphores) {
            taskSemaphore.release();
        }
    }

    private Semaphore allowed() {
        return new Semaphore(1);
    }

    private Semaphore notAllowed() {
        return new Semaphore(0);
    }
}
