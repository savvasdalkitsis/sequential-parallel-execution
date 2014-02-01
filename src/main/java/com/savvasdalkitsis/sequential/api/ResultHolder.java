package com.savvasdalkitsis.sequential.api;

class ResultHolder<T> {
    T result;

    public T getResult() {
        return result;
    }

    /**
     * Only allows the value to be set once. This way after the first task sets the value, when all the rest of the
     * tasks are allowed to finish, they cannot affect the final result.
     */
    public void setResultOnlyOnce(T result) {
        if (this.result == null) {
            this.result = result;
        }
    }
}
