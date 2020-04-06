package com.motycka.test.rest

import groovy.json.JsonOutput
import groovy.json.StringEscapeUtils


trait JsonBody implements MapBody {

    String asJsonString(Closure fields) {
        JsonOutput.toJson(fields)
    }

    String asJsonString() {
        def denulled = asMap()
        StringEscapeUtils.unescapeJava(
                JsonOutput.toJson(denulled)
        )
    }

    String toString() {
        this.asJsonString()
    }

    Map asMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            boolean exclude = ExcludeSerialization in it.declaredAnnotations*.annotationType()
            if (this."$it.name" != null && !exclude) {
                [ (it.name):this."$it.name" ]
            } else {
                [:]
            }
        }
    }
}
