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
    // Crea un índice único en la columna 'guid'. Esto es crucial para evitar duplicados,
    // ya que el 'guid' (Globally Unique Identifier) es único para cada artículo en un feed.
    indices = [Index(value = ["guid"], unique = true)]
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
    val pubDate: String
)
