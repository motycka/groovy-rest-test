package com.motycka.test.rest

trait MapBody {

    Map asMap() {
        def fields = this.class.declaredFields
        fields.findAll { !it.synthetic }.collectEntries {
            boolean exclude = ExcludeSerialization in it.declaredAnnotations*.annotationType()
            if (this."$it.name" != null && !exclude) {
                String name = it.name.contains("__") ? it.name.substring(it.name.lastIndexOf("_") + 1) : it.name
                if (this."$it.name" instanceof MapBody) {
                    [(name): this."$it.name".asMap()]
                } else if (this."$it.name" instanceof MapBody[]) {
                    [(name): this."$it.name".collect { it.asMap() }]
                } else {
                    [(name): this."$it.name"]
                }
            } else {
                [:]
            }
        }
    }

}