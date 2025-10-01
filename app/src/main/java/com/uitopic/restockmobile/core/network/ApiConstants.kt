package com.uitopic.restockmobile.core.network

object ApiConstants {
    // IMPORTANTE: Cambiar según el entorno
    // Emulador Android: usa 10.0.2.2
    // Dispositivo físico: usa tu IP local (192.168.18.111)
    private const val BASE_IP = "10.0.2.2" // Tu IP local
    private const val PORT = "8080"

    const val BASE_URL = "http://$BASE_IP:$PORT/api/v1/"

    // Timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Headers
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
}