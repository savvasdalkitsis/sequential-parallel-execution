package com.savvasdalkitsis.sequential.api;

import java.util.concurrent.Semaphore;

class WrappedTask<T> implements Runnable {

    private final SequentialTask<T> task;
    private final ResultHolder<T> resultHolder;
    private final Semaphore allowedToDeliverResults;
    private final Semaphore allowNextTaskToDeliver;
    private final Semaphore globalLock;

    static <T> Runnable wrap(SequentialTask<T> task, ResultHolder<T> resultHolder, Semaphore allowedToDeliverResults,
                          Semaphore currentSemaphore, Semaphore globalLock) {
        return new WrappedTask<T>(task, resultHolder, allowedToDeliverResults, currentSemaphore, globalLock);
    }

    private WrappedTask(SequentialTask<T> task, ResultHolder<T> resultHolder, Semaphore allowedToDeliverResults, Semaphore allowNextTaskToDeliver,
                       Semaphore globalLock) {
        this.task = task;
        this.resultHolder = resultHolder;
        this.allowedToDeliverResults = allowedToDeliverResults;
        this.allowNextTaskToDeliver = allowNextTaskToDeliver;
        this.globalLock = globalLock;
    }

    @Override
    public void run() {
        TaskResult<T> result = task.execute();
        if (result.isSuccess()) {
            try {
                allowedToDeliverResults.acquire();
                resultHolder.setResultOnlyOnce(result.getValue());
                globalLock.release();
            } catch (InterruptedException e) {
                // dropping results. other task delivered results first or canceled.
                e.printStackTrace();
            }
        }
        allowNextTaskToDeliver.release();
    }
}
