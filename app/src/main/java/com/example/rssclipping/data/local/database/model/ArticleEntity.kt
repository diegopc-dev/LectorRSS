package com.example.rssclipping.data.local.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Define la estructura de la tabla 'articles' en la base de datos Room.
 * Cada instancia de esta clase representa un artículo individual de un feed RSS.
 */
@Entity(
    tableName = "articles",
    // Define una clave foránea para mantener la integridad referencial.
    // Cada artículo debe estar asociado a una suscripción existente.
    foreignKeys = [
        ForeignKey(
            entity = SubscriptionEntity::class,
            parentColumns = ["id"], // La clave primaria en la tabla 'subscriptions'.
            childColumns = ["subscriptionId"], // La clave foránea en la tabla 'articles'.
            onDelete = ForeignKey.CASCADE // Si se borra una suscripción, se borrarán todos sus artículos.
        )
    ],
    indices = [
        // Crea un índice único en la columna 'guid' para evitar duplicados.
        Index(value = ["guid"], unique = true),
        // Crea un índice en 'subscriptionId' para acelerar las consultas que filtran por suscripción.
        Index(value = ["subscriptionId"])
    ]
)
data class ArticleEntity(
    /**
     * Clave primaria autoincremental para identificar de forma única cada artículo.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * La clave foránea que vincula este artículo a su [SubscriptionEntity] padre.
     */
    val subscriptionId: Long,

    /**
     * El Identificador Único Global del artículo, proporcionado por el feed RSS.
     */
    val guid: String,

    /**
     * El título del artículo.
     */
    val title: String,

    /**
     * La URL que enlaza al contenido completo del artículo.
     */
    val link: String,

    /**
     * La fecha de publicación del artículo, como un String.
     */
    val pubDate: String,

    /**
     * El cuerpo o contenido del artículo, usualmente en formato HTML.
     */
    val content: String,

    /**
     * La URL de la imagen en miniatura del artículo.
     */
    val thumbnailUrl: String
)
