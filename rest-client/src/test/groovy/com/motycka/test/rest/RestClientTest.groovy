package com.motycka.test.rest

import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class RestClientTest extends Specification {

    class PostmanEcho extends RestClient {

        PostmanEcho(String host) {
            super(host)
        }

        def GET = new ServiceRoute() {
            def test(Map params = [:]) {
                get("get", params)
            }
        }

        def POST = new ServiceRoute() {
            def test(body, Map params = [:]) {
                post("post", body, params)
            }
        }

        def PUT = new ServiceRoute() {
            def test(body, Map params = [:]) {
                put("put", body, params)
            }
        }

        def DOWNLOAD = new ServiceRoute() {
            def test(String fileName, Map params = [:]) {
                download("get", fileName, params)
            }
        }

    }

    @Shared
    def postmanEchoUrl = "https://postman-echo.com"

    @Shared
    def api = new PostmanEcho(postmanEchoUrl)

    @Unroll
    def "#method should accept query parameters as map with list"(String method, Closure<TestableResponse<Map>> call) {

        when:
        def response = call([domains: ["amazon.com", "ebay.com"], category: "Electronics"])

        then:
        response.body.args == [domains: "amazon.com,ebay.com", category: "Electronics"]

        where:
        method | call
        "GET"  | { Map query -> api.GET.test(query) }
        "POST" | { Map query -> api.POST.test("", query) }
        "PUT"  | { Map query -> api.PUT.test("", query) }
    }

    @Unroll
    def "#method should accept body"(String method, Closure<TestableResponse<Map>> call) {

        when:
        def response = call("TEST")

        then:
        response.body.data == "TEST"

        where:
        method | call
        "POST" | { String body -> api.POST.test(body, [:]) }
        "PUT"  | { String body -> api.PUT.test(body, [:]) }
    }

    def "should allow to set request headers"() {

        given:
        api.setHeaders(
                "Content-Type": "application/json",
                "Accept": "*/*",
                "Authorization": "XXX"
        )

        when:
        def response = api.GET.test()

        then:
        response.body.headers.'content-type' == "application/json"
        response.body.headers.'accept' == "*/*"
    }

    def "should provide response body, status and headers"() {

        when:
        def response = api.GET.test()

        then:
        response.body != null && response.body != ""

        and:
        response.status == 200

        and:
        response.headers.size() > 0
        response.headers.get('content-type') == ["application/json; charset=utf-8"]
    }

    def "should download a file"() {

        given:
        def fileName = "/tmp/testfile-${System.currentTimeMillis()}.json"

        when:
        TestableResponse<File> response = api.DOWNLOAD.test(fileName)

        then:
        response.status == 200

        and:
        def file = new File(fileName)
        file.exists()

        def content = new JsonSlurper().parse(file)
        content.url == "${postmanEchoUrl}/get"

        cleanup:
        file.delete()
    }

    def "should create new instance"() {

        when:
        def client1 = new RestClient(postmanEchoUrl, new ClientOptions(11111))
        def client2 = new RestClient(postmanEchoUrl, new ClientOptions(22222))

        then:
        client1.clientInstance.config().connectionTimeout == 11111
        client2.clientInstance.config().connectionTimeout == 22222
    }

    def "should support quiet request"() {

        when:
        api.silent()
        def response = api.GET.test()
        api.verbose()

        then:
        response.status == 200

        // just sanity, unable to further assert this
    }

    def "should accept JsonBody"() {

        when:
        def response = api.POST.test(new JsonBodyTest(message: "Hello!"))

        then:
        response.status == 200
    }

}

