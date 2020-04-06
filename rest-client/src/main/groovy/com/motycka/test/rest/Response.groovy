package com.motycka.test.rest

interface Response<T> {
    Integer getStatus()
    String getRawBody()
    T getBody()
    T getHeaders()
}
