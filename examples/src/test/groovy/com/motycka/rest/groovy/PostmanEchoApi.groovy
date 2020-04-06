package com.motycka.rest.groovy

import com.motycka.test.rest.RestClient
import com.motycka.test.rest.ServiceRoute
import com.motycka.test.rest.TestableResponse

class PostmanEchoApi extends RestClient {

    PostmanEchoApi(String host) {
        super(host)
    }

    def GET = new ServiceRoute() {
        TestableResponse get(Map params = [:]) {
            get("get", params)
        }

        TestableResponse<Map> headers(Map params = [:]) {
            get("headers", params)
        }

        TestableResponse responseHeaders(Map params = [:]) {
            get("response-headers", params)
        }

        TestableResponse status(long code, Map params = [:]) {
            get("status/$code", params)
        }
    }

    def POST = new ServiceRoute() {
        TestableResponse post(body, Map params = [:]) {
            post("post", body, params)
        }
    }

}
