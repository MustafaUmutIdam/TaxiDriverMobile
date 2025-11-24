package com.example.taxidrivermobile.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxidrivermobile.data.model.Driver
import com.example.taxidrivermobile.data.model.Station

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.driver != null) {
            ProfileContent(
                driver = uiState.driver!!,
                onEditProfile = viewModel::showEditDialog,
                onChangePassword = viewModel::showPasswordDialog,
                onStatusChange = viewModel::updateStatus,
                onLogout = viewModel::showLogoutDialog,
                isUpdatingStatus = uiState.isUpdatingStatus
            )
        }

        // Error Snackbar
        if (uiState.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = viewModel::clearError) {
                        Text("Tamam")
                    }
                }
            ) {
                Text(uiState.error!!)
            }
        }
    }

    // Edit Profile Dialog
    if (uiState.showEditDialog) {
        EditProfileDialog(
            driver = uiState.driver!!,
            onDismiss = viewModel::hideEditDialog,
            onConfirm = { fullName, email, phone, license, plate ->
                viewModel.updateProfile(fullName, email, phone, license, plate)
            },
            isLoading = uiState.isUpdating
        )
    }

    // Change Password Dialog
    if (uiState.showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = viewModel::hidePasswordDialog,
            onConfirm = { current, new ->
                viewModel.changePassword(current, new)
            },
            isLoading = uiState.isChangingPassword
        )
    }

    // Logout Confirmation Dialog
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = viewModel::hideLogoutDialog,
            title = { Text("Çıkış Yap") },
            text = { Text("Hesabınızdan çıkmak istediğinizden emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout(onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Çıkış Yap")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideLogoutDialog) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun ProfileContent(
    driver: Driver,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onStatusChange: (String) -> Unit,
    onLogout: () -> Unit,
    isUpdatingStatus: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "👤",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = driver.fullName ?: "İsim yok",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = driver.phone ?: "Telefon yok",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Status Selection
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Durum",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isUpdatingStatus) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                StatusChipRow(
                    currentStatus = driver.status ?: "offline",
                    onStatusChange = onStatusChange,
                    enabled = !isUpdatingStatus
                )
            }
        }

        // Personal Info
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Kişisel Bilgiler",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                InfoRow(Icons.Default.Person, "Ad Soyad", driver.fullName ?: "Ad Soyad yok")
                InfoRow(Icons.Default.Phone, "Telefon", driver.phone ?: "Telefon yok")
                if (!driver.email.isNullOrEmpty()) {
                    InfoRow(Icons.Default.Email, "Email", driver.email)
                }
                InfoRow(Icons.Default.Badge, "Ehliyet No", driver.licenseNumber ?: "Ehliyet yok")
                InfoRow(Icons.Default.DirectionsCar, "Plaka", driver.vehiclePlate ?: "Plaka yok")
            }
        }

        // Station Info
        val station = driver.station
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Durak Bilgileri",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(Icons.Default.LocationOn, "Durak", station?.name ?: "Durak yok")
                InfoRow(Icons.Default.Place, "Adres", station?.address ?: "Adres yok")
            }
        }

        // Stats
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "İstatistikler",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Toplam Yolculuk", driver.totalTrips.toString())
                    StatItem("Puan", String.format("%.1f", driver.rating))
                    StatItem("Bakiye", "${driver.balance} ₺")
                }
            }
        }

        // Actions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Profili Düzenle")
            }

            OutlinedButton(
                onClick = onChangePassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Şifre Değiştir")
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Çıkış Yap")
            }
        }
    }
}

@Composable
fun StatusChipRow(
    currentStatus: String,
    onStatusChange: (String) -> Unit,
    enabled: Boolean
) {
    val statuses = listOf(
        "active" to "Müsait",
        "break" to "Molada",
        "offline" to "Çevrimdışı"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statuses.forEach { (status, label) ->
            FilterChip(
                selected = currentStatus == status,
                onClick = { if (enabled) onStatusChange(status) },
                label = { Text(label) },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    driver: Driver,
    onDismiss: () -> Unit,
    onConfirm: (String?, String?, String?, String?, String?) -> Unit,
    isLoading: Boolean
) {
    var fullName by remember { mutableStateOf(driver.fullName) }
    var email by remember { mutableStateOf(driver.email ?: "") }
    var phone by remember { mutableStateOf(driver.phone) }
    var license by remember { mutableStateOf(driver.licenseNumber) }
    var plate by remember { mutableStateOf(driver.vehiclePlate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Profili Düzenle") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName ?: "",
                    onValueChange = { fullName = it },
                    label = { Text("Ad Soyad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone ?: "",
                    onValueChange = { phone = it },
                    label = { Text("Telefon") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = license ?: "",
                    onValueChange = { license = it },
                    label = { Text("Ehliyet No") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = plate ?: "",
                    onValueChange = { plate = it },
                    label = { Text("Plaka") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        fullName.takeIf { it != driver.fullName },
                        email.takeIf { it.isNotEmpty() && it != driver.email },
                        phone.takeIf { it != driver.phone },
                        license.takeIf { it != driver.licenseNumber },
                        plate.takeIf { it != driver.vehiclePlate }
                    )
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Kaydet")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    isLoading: Boolean
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Şifre Değiştir") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Mevcut Şifre") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Yeni Şifre") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Yeni Şifre (Tekrar)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        currentPassword.isEmpty() -> error = "Mevcut şifre gerekli"
                        newPassword.length < 6 -> error = "Yeni şifre en az 6 karakter olmalı"
                        newPassword != confirmPassword -> error = "Şifreler eşleşmiyor"
                        else -> {
                            error = null
                            onConfirm(currentPassword, newPassword)
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Değiştir")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
