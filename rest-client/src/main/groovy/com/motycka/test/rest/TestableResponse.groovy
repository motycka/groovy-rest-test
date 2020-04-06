package com.motycka.test.rest

class TestableResponse<T> implements Response, ResponseHeaders {

    Integer status
    String rawBody
    T body

    boolean equals(TestableResponse<T> expected) {
        this.status == expected.status && this.body == expected.body
    }

    boolean isBadRequest(String message) {
        this.status == 400
        this.body.error == "Bad Request"
        this.body.message == message
        true
    }

}

