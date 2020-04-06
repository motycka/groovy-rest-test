package com.motycka.test.rest

import kong.unirest.Config

class ClientOptions {

    final int connectionTimeout

    ClientOptions() {
        this.connectionTimeout = Config.DEFAULT_CONNECT_TIMEOUT
    }

    ClientOptions(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout
    }

    Config getConfig() {
        new Config().connectTimeout(this.connectionTimeout)
    }
}
