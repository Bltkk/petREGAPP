package com.example.grampet.screens



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// --- Estructuras de datos sencillas para las notificaciones ---

private data class Notification(
    val id: Int,
    val username: String,
    val profilePicUrl: String,
    val message: String,
    val type: NotificationType
)

private enum class NotificationType(val icon: ImageVector, val tint: Color) {
    LIKE(Icons.Default.Favorite, Color.Red)
    // Se podrían añadir otros tipos como COMMENT, FOLLOW, etc.
}

// --- Datos de ejemplo para mostrar en la pantalla ---

private val mockNotifications = listOf(
    Notification(1, "duoc_uc", "https://picsum.photos/seed/duoc/100", "le ha dado Me Gusta a tu foto.", NotificationType.LIKE),
    Notification(2, "user_dev", "https://picsum.photos/seed/dev/100", "le ha dado Me Gusta a tu foto.", NotificationType.LIKE),
    Notification(3, "programacion_movil", "https://picsum.photos/seed/movil/100", "le ha dado Me Gusta a tu foto.", NotificationType.LIKE),
    Notification(4, "gato_influencer", "https://picsum.photos/seed/gato/100", "le ha dado Me Gusta a tu foto.", NotificationType.LIKE),
    Notification(5, "android_fan", "https://picsum.photos/seed/android/100", "le ha dado Me Gusta a tu foto.", NotificationType.LIKE),
    Notification(6, "jetpack_compose", "https://picsum.photos/seed/compose/100", "le ha dado Me Gusta a tu foto.", NotificationType.LIKE)
)

/**
 * La pantalla principal de notificaciones que muestra la lista de items.
 * Esta es la función que será llamada desde el NavHost en MainScreen.
 */
@Composable
fun NotificationsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre cada item
    ) {
        items(mockNotifications, key = { it.id }) { notification ->
            NotificationItem(notification = notification)
        }
    }
}

@Composable
private fun NotificationItem(notification: Notification) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de perfil
        AsyncImage(
            model = notification.profilePicUrl,
            contentDescription = "Foto de perfil de ${notification.username}",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Texto de la notificación (con el nombre de usuario en negrita)
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(notification.username)
                }
                append(" ")
                append(notification.message)
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f) // Ocupa el espacio sobrante
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Icono de la acción (el corazón en este caso)
        Icon(
            imageVector = notification.type.icon,
            contentDescription = null, // Es decorativo
            tint = notification.type.tint,
            modifier = Modifier.size(24.dp)
        )
    }
}
