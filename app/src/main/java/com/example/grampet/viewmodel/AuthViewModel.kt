package com.example.grampet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

// Modelo para representar un usuario en nuestra "base de datos"
data class User(val email: String, val pass: String)

// Estado de la UI para las pantallas de autenticación
data class AuthUiState(
    val email: String = "",
    val pass: String = "",
    val confirmPass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val confirmPassError: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,      // Evento para la navegación inicial
    val signUpSuccess: Boolean = false,     // Evento para navegar hacia atrás desde el registro
    val generalError: String? = null,
    val isUserLoggedIn: Boolean = false // Estado persistente para saber si el usuario está autenticado
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    // "Base de datos" simulada en memoria. Se reiniciará cada vez que la app se cierre.
    private val userDatabase = mutableListOf(
        User("test@test.com", "123456")
    )

    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    // --- Handlers para cambios en los campos de texto ---
    fun onEmailChange(email: String) { _uiState.value = _uiState.value.copy(email = email, emailError = null, generalError = null) }
    fun onPassChange(pass: String) { _uiState.value = _uiState.value.copy(pass = pass, passError = null, generalError = null) }
    fun onConfirmPassChange(confirmPass: String) { _uiState.value = _uiState.value.copy(confirmPass = confirmPass, confirmPassError = null, generalError = null) }

    // --- Lógica de Login ---
    fun validateAndLogin() {
        val email = _uiState.value.email
        val pass = _uiState.value.pass
        var hasError = false

        if (!emailPattern.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(emailError = "Formato de email inválido")
            hasError = true
        }
        if (pass.length < 6) {
            _uiState.value = _uiState.value.copy(passError = "La contraseña debe tener al menos 6 caracteres")
            hasError = true
        }

        if (!hasError) {
            login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalError = null)
            delay(2000)

            val email = _uiState.value.email
            val pass = _uiState.value.pass
            val user = userDatabase.find { it.email == email && it.pass == pass }

            if (user != null) {
                // Éxito: el usuario fue encontrado
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loginSuccess = true,
                    isUserLoggedIn = true // Marcamos al usuario como logueado
                )
            } else {
                // Error: el usuario no existe o la contraseña es incorrecta
                _uiState.value = _uiState.value.copy(isLoading = false, generalError = "Credenciales incorrectas")
            }
        }
    }

    // --- Lógica de Registro ---
    fun validateAndSignUp() {
        val email = _uiState.value.email
        val pass = _uiState.value.pass
        val confirmPass = _uiState.value.confirmPass
        var hasError = false

        if (userDatabase.any { it.email == email }) {
            _uiState.value = _uiState.value.copy(emailError = "Este email ya está registrado")
            hasError = true
        }
        if (!emailPattern.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(emailError = "Formato de email inválido")
            hasError = true
        }
        if (pass.length < 6) {
            _uiState.value = _uiState.value.copy(passError = "La contraseña debe tener al menos 6 caracteres")
            hasError = true
        }
        if (pass != confirmPass) {
            _uiState.value = _uiState.value.copy(confirmPassError = "Las contraseñas no coinciden")
            hasError = true
        }

        if (!hasError) {
            signUp()
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(2000)

            // ¡Guardamos al nuevo usuario en nuestra "base de datos"!
            val newUser = User(email = _uiState.value.email, pass = _uiState.value.pass)
            userDatabase.add(newUser)

            _uiState.value = _uiState.value.copy(isLoading = false, signUpSuccess = true)
        }
    }

    // --- Lógica de Logout ---
    fun logout() {
        // En una app real, aquí limpiarías tokens guardados, etc.
        // Por ahora, solo reseteamos el estado a sus valores iniciales.
        _uiState.value = AuthUiState()
    }

    // --- Handlers para eventos de navegación ---
    fun onLoginHandled() {
        // Resetea solo el evento de navegación, no el estado de 'isUserLoggedIn'
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }

    fun onSignUpHandled() {
        // Resetea el evento y los campos de texto
        _uiState.value = _uiState.value.copy(signUpSuccess = false, email = "", pass = "", confirmPass = "")
    }
}

