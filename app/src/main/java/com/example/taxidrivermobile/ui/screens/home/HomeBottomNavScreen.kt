package com.example.taxidrivermobile.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taxidrivermobile.ui.screens.mytrips.MyTripsScreen
import com.example.taxidrivermobile.ui.screens.profile.ProfileScreen
import com.example.taxidrivermobile.ui.screens.profile.ProfileViewModel

data class BottomNavItem(val title: String, val icon: ImageVector)

@Composable
fun HomeBottomNavScreen(
    onLogout: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val items = listOf(
        BottomNavItem("Aktif İşler", Icons.Default.AlarmOn),
        BottomNavItem("Yolculuklarım", Icons.AutoMirrored.Filled.List),
        BottomNavItem("Profil", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            when (selectedIndex) {
                0 -> {
                    val activeTripViewModel: ActiveTripViewModel = hiltViewModel()
                    ActiveTripScreen(viewModel = activeTripViewModel)
                }

                1 -> {
                    MyTripsScreen()
                }

                2 -> {
                    val profileViewModel: ProfileViewModel = hiltViewModel()
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}
