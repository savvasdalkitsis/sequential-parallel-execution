package com.savvasdalkitsis.sequential.api;

import java.util.concurrent.TimeUnit;

public interface SequentialParallelTaskExecutor<T> {
    TaskResult<T> executeAndGet(int timeout, TimeUnit timeUnit, SequentialTask<T>... tasks);
}
