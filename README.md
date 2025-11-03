# Restock Mobile - Android App

## Summary

Aplicación móvil Android de Restock Platform desarrollada con Kotlin, Jetpack Compose y arquitectura limpia (Clean Architecture). Permite gestionar inventario, recetas, ventas y perfiles de usuario.

## Tecnologías

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** MVVM + Clean Architecture
- **Inyección de dependencias:** Hilt
- **Networking:** Retrofit + OkHttp
- **Imágenes:** Coil + Cloudinary
- **Base de datos local:** Room
- **Navegación:** Navigation Compose

## Configuración del Proyecto

### 1. Requisitos previos

- Android Studio Hedgehog o superior
- JDK 17
- Android SDK 24+
- Cuenta de Cloudinary (para subida de imágenes)

### 2. Configurar variables de entorno

Este proyecto usa `local.properties` para almacenar configuraciones sensibles. **NUNCA** commitees este archivo al repositorio.

#### Paso a paso:

1. **Copia el archivo de ejemplo:**
   ```bash
   cp local.properties.example local.properties
   ```

2. **Obtén tus credenciales de Cloudinary:**
   - Ve a [Cloudinary Console](https://cloudinary.com/console)
   - Inicia sesión en tu cuenta
   - Copia tu **Cloud Name**, **API Key** y **API Secret**

3. **Crea un Upload Preset (importante):**
   - En Cloudinary Console, ve a **Settings** (⚙️)
   - Click en la pestaña **Upload**
   - Scroll hasta **Upload presets**
   - Click en **Add upload preset**
   - Configura:
     - **Preset name:** `restock_recipes` (o el nombre que prefieras)
     - **Signing Mode:** **Unsigned** ⚠️ (muy importante)
     - **Folder:** `restock/recipes` (opcional, pero recomendado)
   - Click en **Save**

4. **Edita `local.properties` con tus credenciales:**
   ```properties
   sdk.dir=/path/to/your/Android/Sdk

   # Cloudinary Configuration
   cloudinary.cloud.name=tu_cloud_name
   cloudinary.api.key=tu_api_key
   cloudinary.api.secret=tu_api_secret
   cloudinary.upload.preset=restock_recipes
   ```

5. **Sincroniza el proyecto:**
   - En Android Studio: `File > Sync Project with Gradle Files`

### 3. Compilar y ejecutar

```bash
./gradlew assembleDebug
```

O desde Android Studio: `Run > Run 'app'`

## Estructura del Proyecto

```
app/src/main/java/com/uitopic/restockmobile/
├── core/                           # Módulos compartidos
│   ├── auth/                       # Autenticación y tokens
│   ├── cloudinary/                 # Integración con Cloudinary
│   └── network/                    # Configuración de Retrofit
│
├── features/                       # Módulos por característica
│   ├── auth/                       # Login y registro
│   ├── home/                       # Pantalla principal
│   ├── monitoring/                 # Ventas y métricas
│   ├── planning/                   # Recetas
│   ├── profiles/                   # Perfiles de usuario
│   └── resources/                  # Inventario y supplies
│
└── ui/                             # Tema y estilos
```

## Arquitectura

Cada feature sigue Clean Architecture con las siguientes capas:

```
📱 Presentation Layer
   ├── screens/          # Composables de UI
   ├── viewmodels/       # ViewModels (lógica de presentación)
   ├── states/           # Estados de UI
   └── navigation/       # Configuración de navegación

💼 Domain Layer
   ├── models/           # Entidades de negocio
   ├── repositories/     # Interfaces de repositorios
   └── usecases/         # Casos de uso (si aplica)

💾 Data Layer
   ├── remote/           # API services y DTOs
   ├── local/            # Room entities y DAOs
   ├── repositories/     # Implementaciones de repositorios
   └── mappers/          # Conversión entre DTOs y entidades
```

## Flujo de Subida de Imágenes

### Perfiles (Avatares)
1. Usuario selecciona imagen
2. `ProfileViewModel.uploadAvatar(uri)`
3. `ImageUploadRepository.uploadImage(uri)` → Cloudinary
4. URL se guarda inmediatamente en el backend

### Recetas
1. Usuario selecciona imagen
2. `RecipesViewModel.uploadRecipeImage(uri)`
3. `ImageUploadRepository.uploadImage(uri)` → Cloudinary
4. URL se guarda en el estado del formulario
5. Al hacer "Save Recipe", se envía todo al backend

## Troubleshooting

### Error: "Cloudinary credentials not configured"
- Verifica que `local.properties` tenga las variables correctas
- Asegúrate de que las variables no tengan espacios
- Sincroniza el proyecto con Gradle

### Error: "Upload failed: 401"
- El **Upload Preset** debe ser **Unsigned**
- Verifica que el preset exista en tu cuenta de Cloudinary

### Error: "Upload failed: 400"
- El nombre del preset es incorrecto
- Verifica el nombre exacto en Cloudinary Settings

### BuildConfig no se genera
- Asegúrate de tener `buildConfig = true` en `build.gradle.kts`
- Limpia y reconstruye: `Build > Clean Project` → `Build > Rebuild Project`

## Equipo

- Desarrolladores del curso de Aplicaciones para Dispositivos Móviles
- UTOPIC Team

## Licencia

Este proyecto es de uso académico.