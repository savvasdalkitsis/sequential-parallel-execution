package com.savvasdalkitsis.sequential.api;

public class SequentialParallelTaskExecutors {
    public static <T> SequentialParallelTaskExecutor<T> fixedThreadPoolSequentialParallelTaskExecutor() {
        return new FixedThreadPoolSequentialParallelTaskExecutor<T>();
    }
}
