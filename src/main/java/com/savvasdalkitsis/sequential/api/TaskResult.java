package com.savvasdalkitsis.sequential.api;

public class TaskResult<T> {
    private T value;
    private boolean success;

    private TaskResult(Builder<T> builder) {
        this.value = builder.value;
        this.success = builder.success;
    }

    public T getValue() {
        return value;
    }

    public boolean isSuccess() {
        return success;
    }

    public static class Builder<T> {
        private T value;
        private boolean success;

        public static <T> Builder<T> executionResult() {
            return new Builder<T>();
        }

        public Builder<T> withValue(T value) {
            this.value = value;
            return this;
        }

        public Builder<T> success() {
            this.success = true;
            return this;
        }

        public Builder<T> failure() {
            this.success = false;
            return this;
        }

        public TaskResult<T> build() {
            return new TaskResult<T>(this);
        }
    }
}
