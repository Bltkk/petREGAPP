
plugins {
    // Declara los plugins que estarán disponibles para los módulos.
    // SIEMPRE deben llevar "apply false".
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
