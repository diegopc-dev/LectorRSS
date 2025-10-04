package com.example.rssclipping.data.local.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Prueba de instrumentación para validar las migraciones y el esquema de la base de datos Room.
 * Siguiendo la metodología TDD, esta prueba asegura que la definición de la base de datos
 * es correcta y que las futuras migraciones se gestionan de forma segura.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    // Nombre para la base de datos de prueba.
    private val TEST_DB = "migration-test"

    /**
     * Regla de JUnit que proporciona una instancia de [MigrationTestHelper].
     * Esta clase de ayuda de Room es fundamental para probar las migraciones
     * y validar el esquema de la base de datos. Se le pasa la clase de la BBDD
     * para que pueda encontrar el fichero de esquema en los assets.
     */
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = AppDatabase::class.java
    )

    /**
     * Valida el esquema de la base de datos para la versión 1.
     * Esta es la primera prueba TDD para la capa de datos.
     */
    @Test
    @Throws(IOException::class)
    fun validateSchemaVersion1() {
        // El helper crea una base de datos con el nombre y la versión especificados.
        // Durante este proceso, busca el fichero de esquema `1.json` en los assets,
        // lo compara con el esquema real que se crearía a partir de las entidades,
        // y falla el test si no coinciden.
        val db = helper.createDatabase(TEST_DB, 1)

        // Es una buena práctica cerrar la conexión a la base de datos después de la prueba
        // para liberar recursos y evitar memory leaks en el entorno de prueba.
        db.close()
    }
}
