package com.example.grampet.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.grampet.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateBackToLogin: () -> Unit
) {
    // Recolecta el estado de la UI desde el ViewModel.
    // La pantalla reaccionará a cualquier cambio en este estado.
    val authState by authViewModel.uiState.collectAsState()

    // Este LaunchedEffect se dispara solo cuando 'authState.signUpSuccess' cambia a 'true'.
    // Es ideal para manejar eventos de navegación de un solo uso.
    LaunchedEffect(authState.signUpSuccess) {
        if (authState.signUpSuccess) {
            onSignUpSuccess() // Ejecuta la navegación (ej: navController.popBackStack())
            authViewModel.onSignUpHandled() // Notifica al ViewModel que el evento ha sido manejado.
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear una cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // --- CAMPO DE EMAIL ---
        OutlinedTextField(
            value = authState.email, // El valor viene del estado
            onValueChange = { authViewModel.onEmailChange(it) }, // Los cambios se notifican al ViewModel
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            isError = authState.emailError != null, // Reacciona al error específico del email
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        // Muestra el mensaje de error del email si existe
        authState.emailError?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO DE CONTRASEÑA ---
        OutlinedTextField(
            value = authState.pass,
            onValueChange = { authViewModel.onPassChange(it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            isError = authState.passError != null, // Reacciona al error específico de la contraseña
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        // Muestra el mensaje de error de la contraseña si existe
        authState.passError?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO DE CONFIRMAR CONTRASEÑA ---
        OutlinedTextField(
            value = authState.confirmPass,
            onValueChange = { authViewModel.onConfirmPassChange(it) },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            isError = authState.confirmPassError != null, // Reacciona al error de confirmación
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        // Muestra el mensaje de error de la confirmación si existe
        authState.confirmPassError?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(32.dp))

        // --- BOTÓN DE REGISTRARSE ---
        Button(
            onClick = { authViewModel.validateAndSignUp() }, // Llama a la función pública que inicia la validación
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading // El botón se deshabilita mientras carga
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para volver a la pantalla de Login
        OutlinedButton(
            onClick = onNavigateBackToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ya tengo una cuenta")
        }

        // Muestra un indicador de progreso si isLoading es true
        if (authState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
