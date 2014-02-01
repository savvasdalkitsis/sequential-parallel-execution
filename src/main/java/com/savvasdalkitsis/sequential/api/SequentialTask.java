package com.savvasdalkitsis.sequential.api;

public interface SequentialTask<T> {
    TaskResult<T> execute();
}
