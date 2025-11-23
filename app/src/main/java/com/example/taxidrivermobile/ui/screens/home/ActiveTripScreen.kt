package com.example.taxidrivermobile.ui.screens.home

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxidrivermobile.data.model.Trip
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActiveTripScreen(
    viewModel: ActiveTripViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.trip != null -> {
                TripContent(
                    trip = uiState.trip!!,
                    onAccept = viewModel::acceptTrip,
                    onReject = viewModel::rejectTrip,
                    onStart = viewModel::startTrip,
                    onComplete = { fare -> viewModel.completeTrip(fare) },
                    isAccepting = uiState.isAccepting,
                    isRejecting = uiState.isRejecting,
                    isStarting = uiState.isStarting,
                    isCompleting = uiState.isCompleting
                )
            }
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                EmptyState()
            }
        }

        // Hata mesajı
        if (uiState.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(uiState.error!!)
            }
        }
    }
}

@Composable
fun TripContent(
    trip: Trip,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onStart: () -> Unit,
    onComplete: (Double) -> Unit,
    isAccepting: Boolean,
    isRejecting: Boolean,
    isStarting: Boolean,
    isCompleting: Boolean
) {
    var showCompleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Badge
        trip.status?.let { StatusBadge(it ) }

        // Müşteri Bilgileri
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Müşteri",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = trip.customer?.name ?: "Müşteri",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = { /* TODO: Telefon aç */ }) {
                        Icon(Icons.Default.Phone, contentDescription = "Ara")
                    }
                }

                Text(
                    text = trip.customer?.phone ?: " Telefon Numarası",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Güzergah Bilgileri
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Başlangıç
                LocationRow(
                    icon = Icons.Default.LocationOn,
                    title = "Alınacak Adres",
                    address = trip.pickup?.address ?:  " Adres",
                    iconColor = MaterialTheme.colorScheme.primary
                )

                Divider()

                // Bitiş
                LocationRow(
                    icon = Icons.Default.Place,
                    title = "Bırakılacak Adres",
                    address = trip.dropoff?.address ?: " Adres",
                    iconColor = MaterialTheme.colorScheme.error
                )
            }
        }

        // Yolculuk Detayları
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Yolculuk Detayları",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (trip.distance != null) {
                    InfoRow("Mesafe", "${trip.distance} km")
                }

                if (trip.estimatedDuration != null) {
                    InfoRow("Tahmini Süre", "${trip.estimatedDuration} dakika")
                }

                if (trip.estimatedFare != null) {
                    InfoRow(
                        "Tahmini Ücret",
                        "${trip.estimatedFare} ₺",
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                }

                if (trip.notes != null) {
                    Divider()
                    Text(
                        text = "Not: ${trip.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        when (trip.status) {
            "assigned" -> {
                // Kabul Et / Reddet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        enabled = !isRejecting && !isAccepting,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isRejecting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("Reddet")
                        }
                    }

                    Button(
                        onClick = onAccept,
                        enabled = !isAccepting && !isRejecting,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isAccepting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Kabul Et")
                        }
                    }
                }
            }
            "accepted" -> {
                // Yolculuğu Başlat
                Button(
                    onClick = onStart,
                    enabled = !isStarting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isStarting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yolculuğu Başlat")
                    }
                }
            }
            "in_progress" -> {
                // Yolculuğu Bitir
                Button(
                    onClick = { showCompleteDialog = true },
                    enabled = !isCompleting,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    if (isCompleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yolculuğu Bitir")
                    }
                }
            }
        }
    }

    // Complete Dialog
    if (showCompleteDialog) {
        CompleteTripDialog(
            estimatedFare = trip.estimatedFare ?: 0.0,
            onDismiss = { showCompleteDialog = false },
            onConfirm = { fare ->
                onComplete(fare)
                showCompleteDialog = false
            }
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (text, color) = when (status) {
        "assigned" -> "Yeni İş" to MaterialTheme.colorScheme.primary
        "accepted" -> "Kabul Edildi" to MaterialTheme.colorScheme.tertiary
        "in_progress" -> "Devam Ediyor" to MaterialTheme.colorScheme.secondary
        else -> status to MaterialTheme.colorScheme.onSurface
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LocationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    address: String,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🚕", fontSize = 64.sp)
            Text(
                text = "Yeni İş Bekleniyor",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Yeni bir yolculuk talebi geldiğinde burada göreceksiniz",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteTripDialog(
    estimatedFare: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var fareText by remember { mutableStateOf(estimatedFare.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yolculuğu Tamamla") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Alınan ücreti giriniz:")
                OutlinedTextField(
                    value = fareText,
                    onValueChange = { fareText = it },
                    label = { Text("Ücret (₺)") },
                    singleLine = true
                )
                Text(
                    text = "Tahmini: $estimatedFare ₺",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    fareText.toDoubleOrNull()?.let { onConfirm(it) }
                }
            ) {
                Text("Tamamla")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}