package com.example.rssclipping.data.network.model

import android.util.Log
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Objeto de utilidad para parsear y normalizar fechas provenientes de feeds RSS.
 */
object DateParser {

    private const val TAG = "DateParser"

    // Define una lista de los formatos de fecha más comunes encontrados en feeds RSS.
    private val formatters = listOf(
        // Formato RFC_1123_DATE_TIME (ej. "Tue, 03 Jun 2008 11:05:30 GMT"). Es el más común.
        DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.ENGLISH),
        // Formato específico encontrado en los logs (ej. "Sat Oct 11 08:31:57 GMT 2025")
        DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH),
        // Formato ISO_ZONED_DATE_TIME (ej. "2011-12-03T10:15:30+01:00[Europe/Paris]")
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        // Formatos personalizados para variaciones comunes.
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
    )

    // Formato de salida estándar y ordenable (ISO 8601 en UTC)
    private val outputFormatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"))

    /**
     * Normaliza un string de fecha a un formato estándar (ISO 8601) que pueda ser ordenado
     * alfabéticamente. Si no se puede parsear la fecha, se loggea una advertencia.
     *
     * @param dateString La fecha en formato de texto, tal como viene del feed RSS.
     * @return La fecha normalizada como un String, o un string vacío si no se pudo parsear.
     */
    fun normalizeDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        var lastException: DateTimeParseException? = null

        // 1. Intentar con la lista de formateadores comunes.
        for (formatter in formatters) {
            try {
                val zonedDateTime = ZonedDateTime.parse(dateString, formatter)
                return outputFormatter.format(zonedDateTime)
            } catch (e: DateTimeParseException) {
                lastException = e
                continue
            }
        }

        // 2. Último intento: probar si es un Instant simple (formato ISO 8601 ya normalizado).
        try {
            val instant = Instant.parse(dateString)
            return outputFormatter.format(instant)
        } catch (e: DateTimeParseException) {
            lastException = e
        }

        // 3. Si todo ha fallado, loggear la advertencia con la última excepción capturada.
        Log.w(TAG, "Failed to parse date string after all attempts: '$dateString'", lastException)
        return ""
    }
}
