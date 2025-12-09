package com.example.grampet.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.grampet.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    // --- ¡SOLUCIÓN AQUÍ! AÑADE ESTE BLOQUE ---
    // Este efecto se ejecuta cada vez que 'uiState.loginSuccess' cambia.
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            // Si el login fue exitoso, llama al callback para navegar.
            onLoginSuccess()
            // Importante: Resetea el estado en el ViewModel para evitar
            // que se dispare la navegación de nuevo si la pantalla se recompone.
            authViewModel.onLoginHandled()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("GramPet", style = MaterialTheme.typography.displayMedium)

            // --- CAMPO DE EMAIL ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { authViewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.emailError != null,
                singleLine = true
            )
            uiState.emailError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            }

            // --- CAMPO DE CONTRASEÑA ---
            OutlinedTextField(
                value = uiState.pass,
                onValueChange = { authViewModel.onPassChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.passError != null,
                singleLine = true
            )
            uiState.passError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            }

            // --- ERROR GENERAL ---
            uiState.generalError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            // --- BOTÓN DE LOGIN ---
            Button(
                onClick = { authViewModel.validateAndLogin() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text("Iniciar Sesión")
            }

            // --- INDICADOR DE CARGA ---
            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- OPCIÓN DE REGISTRO ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta?")
                TextButton(onClick = onNavigateToSignUp) {
                    Text("Regístrate aquí")
                }
            }
        }
    }
}
