package com.example.a10xandroid.ui.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Formularz zawierający pola do wprowadzenia danych rejestracji.
 */
@Composable
fun RegisterForm(
    email: String,
    password: String,
    confirmPassword: String,
    acceptedTerms: Boolean,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    termsError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTermsChange: (Boolean) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email field
        EmailField(
            value = email,
            onValueChange = onEmailChange,
            error = emailError,
            isEnabled = !isLoading,
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        PasswordField(
            value = password,
            onValueChange = onPasswordChange,
            error = passwordError,
            isEnabled = !isLoading,
            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm password field
        ConfirmPasswordField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            error = confirmPasswordError,
            isEnabled = !isLoading,
            isConfirmPasswordVisible = isConfirmPasswordVisible,
            onTogglePasswordVisibility = onToggleConfirmPasswordVisibility,
            onDone = { focusManager.clearFocus() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Terms acceptance
        TermsCheckbox(
            checked = acceptedTerms,
            onCheckedChange = onTermsChange,
            error = termsError,
            isEnabled = !isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Register button
        RegisterButton(
            onClick = onRegisterClick,
            isEnabled = !isLoading && email.isNotBlank() && password.isNotBlank() &&
                confirmPassword.isNotBlank() && acceptedTerms &&
                emailError == null && passwordError == null && confirmPasswordError == null,
            isLoading = isLoading
        )
    }
}

/**
 * Pole tekstowe do wprowadzenia adresu e-mail.
 */
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isEnabled: Boolean,
    onNext: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Email") },
        placeholder = { Text("Enter your email") },
        singleLine = true,
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Email icon"
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() }
        ),
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Pole tekstowe do wprowadzenia hasła.
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isEnabled: Boolean,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onNext: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Password") },
        placeholder = { Text("Enter your password") },
        singleLine = true,
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Password icon"
            )
        },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() }
        ),
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Pole tekstowe do wprowadzenia potwierdzenia hasła.
 */
@Composable
fun ConfirmPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isEnabled: Boolean,
    isConfirmPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onDone: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Confirm Password") },
        placeholder = { Text("Confirm your password") },
        singleLine = true,
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        },
        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Confirm password icon"
            )
        },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (isConfirmPasswordVisible) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password"
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Pole wyboru dla akceptacji warunków korzystania z aplikacji.
 */
@Composable
fun TermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    error: String?,
    isEnabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = isEnabled,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = "I accept the terms of service and privacy policy",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

/**
 * Przycisk do zatwierdzenia formularza rejestracji.
 */
@Composable
fun RegisterButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text("Sign up")
        }
    }
}
