package com.homocodian.chargeguard.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = onOkClick
            ) {
                Text(
                    text = "Request"
                )
            }
        },
        title = {
            Text(text = "Permission required")
        },
        text = {
            Text(text = permissionTextProvider.getDescription(false))
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class NotificationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined notification permission." +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs notification permission so it can alert you when your battery reaches the specified level.\n" +
                    "You can go to the app settings to grant it."
        }
    }
}