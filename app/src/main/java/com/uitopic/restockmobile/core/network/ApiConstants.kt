package com.uitopic.restockmobile.core.network

object ApiConstants {
    // ==========================================
    // CONFIGURACIÓN DEL BACKEND
    // ==========================================
    // IMPORTANTE: Cambiar según tu entorno:
    //
    // 1. EMULADOR ANDROID: usa "10.0.2.2"
    //    Ejemplo: private const val BASE_IP = "10.0.2.2"
    //
    // 2. DISPOSITIVO FÍSICO: usa tu IP local
    //    Ejemplo: private const val BASE_IP = "192.168.1.100"
    //    (Para obtener tu IP: ejecuta "ipconfig" en Windows CMD)
    //
    // 3. LOCALHOST EN TU PC: usa "localhost" o "127.0.0.1"
    //    SOLO si estás usando un navegador web, NO para Android
    //
    // 4. SERVIDOR EN LA NUBE: usa la URL completa
    //    Ejemplo: private const val BASE_IP = "tu-servidor.com"
    // ==========================================

    //private const val BASE_IP = "https://restock-platform-production.up.railway.app/" // Cambiar aquí con la IP de tu backend
    //private const val PORT = "8080"        // Cambiar aquí con el puerto de tu backend

    private const val BASE_URL_PROD = "http://10.0.2.2:8080/api/v1/"
    //private const val BASE_URL_PROD = "https://restock-platform-production.up.railway.app/api/v1/"
    //const val BASE_URL = "http://$BASE_IP:$PORT/api/v1/"
    const val BASE_URL = BASE_URL_PROD
    // Timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Headers
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
}