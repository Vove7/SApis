package cn.vove7

import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature


val httpClient
    get() =
        HttpClient {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }

        }
