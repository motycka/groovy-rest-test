package com.motyka.test.rest

import kong.unirest.Headers

trait ResponseHeaders {

    Headers headers

    List<String> getHeader(String name) {
        this.headers.get(name)
    }

}