package com.example.a10xandroid.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a10xandroid.data.model.User
import com.example.a10xandroid.ui.auth.AuthViewModel
import com.example.a10xandroid.ui.theme.Spacing

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = Spacing.large)
        )

        currentUser?.let { user ->
            UserInfoCard(user = user)
        }

        Button(
            onClick = {
                viewModel.signOut()
                onSignOut()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.large)
        ) {
            Text("Sign Out")
        }
    }
}

@Composable
private fun UserInfoCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.medium)
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.medium)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            Text(
                text = "Email",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            Text(
                text = "Display Name",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = user.displayName ?: "Not set",
                style = MaterialTheme.typography.bodyLarge
            )

            if (user.photoUrl != null) {
                Spacer(modifier = Modifier.height(Spacing.medium))
                Text(
                    text = "Profile Photo",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = user.photoUrl,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
