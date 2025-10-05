# LectorRSS: Un Agregador de Feeds Inteligente para Android

LectorRSS es una aplicación de Android moderna, construida con Jetpack Compose y las mejores prácticas de la industria. Permite a los usuarios suscribirse a sus blogs y sitios de noticias favoritos a través de feeds RSS y leerlos en una interfaz limpia, organizada y visualmente atractiva.

Este proyecto ha sido desarrollado siguiendo una Arquitectura Limpia (Clean Architecture) y un patrón MVVM, asegurando un código robusto, mantenible y escalable.

## ✨ Características

*   **Interfaz de Usuario Moderna:** Construida 100% con [Jetpack Compose](https://developer.android.com/jetpack/compose) para una UI declarativa, reactiva y atractiva.
*   **Gestión de Suscripciones:**
    *   Añadir nuevas suscripciones a través de su URL de feed RSS.
    *   Visualizar todas las suscripciones en una lista, cada una con su icono.
*   **Lector de Artículos:**
    *   Lista de artículos para cada suscripción, mostrando la imagen en miniatura (`thumbnail`), título y fecha.
    *   Vista de detalle del artículo que muestra la imagen de cabecera y el contenido completo en un `WebView` para una correcta renderización de HTML.
*   **Arquitectura Robusta "Offline-First":**
    *   Todos los datos (suscripciones y artículos) se persisten en una base de datos local ([Room](https://developer.android.com/training/data-storage/room)), permitiendo el acceso sin conexión.
    *   Los datos se obtienen de la red y se guardan en la base de datos, que actúa como única fuente de verdad para la UI.
*   **Sincronización de Datos:** La lógica de obtención de datos de la red está encapsulada, permitiendo futuras mejoras como la sincronización en segundo plano.

## 🛠️ Arquitectura y Tecnologías

El proyecto sigue los principios de **Clean Architecture** dividiendo el código en capas de UI, Dominio (Repositorios) y Datos (Red y Local), con un patrón **MVVM (Model-View-ViewModel)** en la capa de UI.

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/)
*   **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Asincronía:** [Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Arquitectura:** Clean Architecture + MVVM
*   **Navegación:** [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
*   **Inyección de Dependencias:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
*   **Base de Datos:** [Room](https://developer.android.com/training/data-storage/room) con migraciones.
*   **Networking:** [Ktor](https://ktor.io/) (cliente HTTP)
*   **Parseo de XML:** [Rome](https://github.com/rometools/rome) (para parsear los feeds RSS)
*   **Carga de Imágenes:** [Coil](https://coil-kt.github.io/coil/) (para cargar los iconos y miniaturas)
*   **Renderizado Web:** [Accompanist WebView](https://google.github.io/accompanist/web/) (para mostrar el contenido de los artículos).
*   **Testing:**
    *   [JUnit 5](https://junit.org/junit5/)
    *   [MockK](https://mockk.io/)
    *   AndroidX Test & Turbine

## 🚀 Cómo Empezar

1.  Clona este repositorio.
2.  Abre el proyecto en Android Studio.
3.  Sincroniza el proyecto con Gradle.
4.  Ejecuta la aplicación en un emulador o dispositivo físico.

La aplicación se compilará y podrás empezar a añadir tus feeds RSS favoritos.
