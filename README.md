# LectorRSS: Un Agregador de Feeds Inteligente para Android
​
 LectorRSS es una aplicación de Android moderna, construida siguiendo las mejores prácticas de la industria, que permite a los usuarios suscribirse a sus blogs y sitios de noticias favoritos a través de feeds RSS y leerlos en una interfaz limpia y organizada.
​
 Este proyecto ha sido desarrollado siguiendo una estricta metodología **TDD (Test-Driven Development)** y una **Arquitectura Limpia (Clean Architecture)**, asegurando un código robusto, mantenible y escalable.
​
 ## ✨ Características del MVP
​
 La versión actual (MVP) se centra en la sincronización y el almacenamiento de datos, sentando una base sólida para futuras funcionalidades.
​
 *   **Sincronización en Segundo Plano:** La aplicación actualiza los feeds RSS de forma automática y periódica (cada 12 horas) usando `WorkManager`, incluso si la aplicación no está abierta.
 *   **Almacenamiento Offline:** Todos los artículos se guardan en una base de datos local (`Room`), permitiendo la lectura sin conexión. La aplicación sigue un patrón "offline-first".
 *   **Arquitectura Robusta:**
     *   **Capa de Datos:** Orquestada por un `Repository` que gestiona la obtención de datos desde la red o la base de datos local.
     *   **Capa de Red:** Construida con `Ktor` para peticiones HTTP y `Rome` para un parseo eficiente de feeds XML.
     *   **Inyección de Dependencias:** `Hilt` gestiona las dependencias en toda la aplicación, incluyendo la integración con `WorkManager`.
 *   **Testing Riguroso:**
     *   **Pruebas Unitarias:** Para la lógica de negocio en la capa de datos y de red, usando `JUnit5` y `MockK`.
     *   **Pruebas de Instrumentación:** Para validar la base de datos (`Room`) y el `WorkManager`, asegurando que los componentes que dependen de Android funcionen correctamente.
​
 ## 🛠️ Tecnologías y Librerías
​
 *   **Lenguaje:** [Kotlin](https://kotlinlang.org/)
 *   **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (en futuros tickets)
 *   **Asincronía:** [Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) & [Flow](https://kotlinlang.org/docs/flow.html)
 *   **Arquitectura:** Clean Architecture + MVVM (en futuros tickets)
 *   **Inyección de Dependencias:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
 *   **Base de Datos:** [Room](https://developer.android.com/training/data-storage/room)
 *   **Trabajo en Segundo Plano:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
 *   **Networking:** [Ktor](https://ktor.io/)
 *   **Parseo de XML:** [Rome](https://github.com/rometools/rome)
 *   **Testing:**
     *   [JUnit 5](https://junit.org/junit5/)
     *   [MockK](https://mockk.io/)
     *   AndroidX Test & Espresso
