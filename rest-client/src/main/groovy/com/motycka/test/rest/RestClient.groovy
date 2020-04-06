package com.motycka.test.rest

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import kong.unirest.HttpRequest
import kong.unirest.HttpRequestWithBody
import kong.unirest.HttpResponse
import kong.unirest.UnirestInstance

@Slf4j
class RestClient {

    final String url
    final UnirestInstance clientInstance

    private Map<String, String> headers = [:]
    private boolean verbose = true

    RestClient(String url, ClientOptions clientOptions = new ClientOptions()) {
        this.url = url
        this.clientInstance = new UnirestInstance(clientOptions.getConfig())
        this.clientInstance.config().socketTimeout(3 * 60 * 1000)
    }

    def silent() {
        this.verbose = false
    }

    def verbose() {
        this.verbose = true
    }

    def setHeaders(Map<String, String> headers) {
        this.headers = headers
    }

    TestableResponse get(String route, Map<String, ?> queryParams = [:]) {
        send this.clientInstance.get("${this.url}/$route"), queryParams
    }

    TestableResponse post(String route, body, Map<String, ?> queryParams = [:]) {
        send this.clientInstance.post("${this.url}/$route"), queryParams, body
    }

    TestableResponse post(String route, Map<String, ?> queryParams = [:]) {
        send this.clientInstance.post("${this.url}/$route"), queryParams
    }

    TestableResponse post(String route, List<FormData> formData, Map<String, ?> queryParams = [:]) {
        sendMultipart this.clientInstance.post("${this.url}/$route"), formData, queryParams
    }

    TestableResponse put(String route, body, Map<String, ?> queryParams = [:]) {
        send this.clientInstance.put("${this.url}/$route"), queryParams, body
    }

    TestableResponse delete(String route, Map<String, ?> queryParams = [:]) {
        send this.clientInstance.delete("${this.url}/$route"), queryParams
    }

    TestableResponse options(String host, String route, Map<String, ?> queryParams = [:]) {
        send this.clientInstance.options("${this.url}/$route"), queryParams
    }

    TestableResponse patch(String route, body, Map<String, ?> queryParams = [:]) {
        send this.clientInstance.patch("${this.url}/$route"), queryParams, body
    }

    TestableResponse<File> download(String route, String fileName, Map<String, ?> queryParams = [:]) {
        def request = this.clientInstance.get("${this.url}/$route")
                .headers(this.headers)
                .queryString(toFlatQuery(queryParams))
        logBefore(request, null)

        HttpResponse<File> response = request.asFile(fileName)
        return new TestableResponse<File>(status: response.status, body: response.body, headers: response.headers)
    }

    private TestableResponse send(HttpRequest request, Map<String, ?> queryParams = [:], body = null) {
        request
                .headers(this.headers)
                .queryString(toFlatQuery(queryParams))
        logBefore(request, body)
        resolveResponse(body ? (request as HttpRequestWithBody).body(typeConversion(body)) : request)
    }

    TestableResponse sendMultipart(HttpRequestWithBody request, List<FormData> formData, Map<String, ?> queryParams = [:]) {
        request
                .headers(this.headers)
                .queryString(toFlatQuery(queryParams))
                .fields() //required to make this into form-source request
        formData.each { fd ->
            switch (fd) {
                case { fd instanceof FormDataText }:
                    request.field(fd.name, fd.value)
                    break
                case { fd instanceof FormDataFile }:
                    request.field(fd.name, fd.bytes, fd.fileName)
                    break
                default:
                    throw new IllegalArgumentException("Unknown multipart source type")
            }
        }
        logBefore(request, null)
        resolveResponse(request)
    }

    private logBefore(HttpRequest request, body) {
        if (verbose) {
            log.info("Request: {}", "${request.getHttpMethod()} ${request.getUrl()}")
            log.info("Request headers: {}", request.getHeaders().all().collect { "${it.name}: ${it.value}" })
            if (request.body != null) log.info("Request body: {}", body)
        }
    }

    private logAfter(HttpResponse response) {
        if (verbose) {
            log.info("Response status: {}", response.status)
            log.info("Response headers: {}", response.headers.all().collect { "${it.name}: ${it.value}" })
            log.info("Response body: {}", response.body.toString())
        }
    }

    TestableResponse resolveResponse(HttpRequest request) {
        HttpResponse<String> response = request.asString()
        logAfter(response)

        String contentType = response.getHeaders().get("Content-Type")
        switch (contentType) {
            case { contentType.contains(ContentType.APPLICATION_JSON) }:
                return new TestableResponse<Map>(status: response.status, rawBody: response.body.toString(), body: new JsonSlurper().parseText(response.body.toString()), headers: response.headers)
            case { contentType.contains(ContentType.TEXT_PLAIN) }:
                return new TestableResponse<String>(status: response.status, rawBody: response.body.toString(), body: response.body.toString(), headers: response.headers)
            case { contentType.contains(ContentType.TEXT_HTML) }:
                return new TestableResponse<String>(status: response.status, rawBody: response.body.toString(), body: response.body.toString(), headers: response.headers)
            case { contentType.contains(ContentType.APPLICATION_OPENXML) }:
                def bytes = request.asBytes()
                return new TestableResponse<ByteArrayInputStream>(status: response.status, rawBody: response.body.toString(), body: new ByteArrayInputStream(bytes.body as byte[]), headers: response.headers)
            default:
                return new TestableResponse<String>(status: response.status, rawBody: response.body.toString(),body: response.body?.toString(), headers: response.headers)
        }
    }

    private static typeConversion(object) {
        if (object instanceof JsonBody) {
            object.asJsonString()
        } else if (object instanceof Map) {
            JsonOutput.toJson(object)
        } else {
            object
        }
    }

    private static Map<String, Object> toFlatQuery(Map<String, Object> map) {
        map.collectEntries {
            [(it.key): (it.value instanceof List ? it.value.join(",") : it.value)]
        } as Map<String, Object>
    }

}