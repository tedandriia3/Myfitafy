package com.byted.myfitafy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.byted.myfitafy.model.PartType
import com.byted.myfitafy.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    category: String,
    viewModel: EditorViewModel = hiltViewModel()
) {
    LaunchedEffect(category) {
        viewModel.loadInitialData(category)
    }

    val selectedParts by viewModel.selectedParts.collectAsState()
    val options by viewModel.availableOptions.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState() // Le prix calculé

    var currentEditingType by remember { mutableStateOf(PartType.COLLAR) }

    Scaffold(
        bottomBar = {
            // --- BARRE DE PRIX ET ACTION ---
            Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(
                            text = "${totalPrice.toInt()} Ar", // Affiche le prix
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = { /* TODO: Sauvegarder dans Firestore */ },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Commander")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            // --- ZONE 1 : MANNEQUIN ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                // Superposition Z-Index
                val layers = selectedParts.values.filterNotNull().sortedBy { it.zIndex }

                if (layers.isEmpty()) {
                    CircularProgressIndicator()
                } else {
                    layers.forEach { part ->
                        AsyncImage(
                            model = part.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.9f),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            // --- ZONE 2 : SÉLECTEURS ---
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("Personnaliser :", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                // Onglets de types (Manche, Col...)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    PartType.values().forEach { type ->
                        val isSelected = currentEditingType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                currentEditingType = type
                                viewModel.loadOptions(category, type)
                            },
                            label = { Text(type.name) },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Liste des options visuelles
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(options) { part ->
                        val isSelected = selectedParts[part.type]?.id == part.id

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = part.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.selectPart(part) }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${part.price.toInt()} Ar", // Prix de l'option en Ariary
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}