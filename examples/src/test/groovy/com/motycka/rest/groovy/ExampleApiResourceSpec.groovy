package com.motycka.rest.groovy

class ExampleApiResourceSpec extends ExampleApiSpec {

    def "GET /status - evaluate response status"() {

        when:
        def response = exampleApi.GET.status(200)

        then:
        response.status == 200
    }

    def "GET /get - query parameters example"() {

        given:
        def query = [foo: "bar"]

        when:
        def response = exampleApi.GET.get(query)

        and:
        response.body.args == query
        response.body.headers.host == "postman-echo.com"
    }

    def "POST /post - post body as map example"() {

        given:
        def requestBody = [foo: "bar"]

        when:
        def response = exampleApi.POST.post(requestBody)

        and:
        response.body.data == "{\"foo\":\"bar\"}"
    }

    def "POST /post - post body as JsonBody"() {

        given:
        def requestBody = new ExampleBody(name: "Foo", number: 2)

        when:
        def response = exampleApi.POST.post(requestBody)

        and:
        response.body.data == "{\"name\":\"Foo\",\"number\":2}"
    }

    def "GET /headers - set request headers"() {

        given:
        exampleApi.setHeaders(["Authentication": "my-token"])

        when:
        def response = exampleApi.GET.headers()

        and:
        response.body.headers["authentication"] == "my-token"
    }

    def "GET /response-headers - get request headers"() {

        when:
        def response = exampleApi.GET.headers([foo: "bar"])

        and:
        response.headers.get("foo") == ["bar"]
        // or
        response.getHeader("foo") == ["bar"]
    }

}
