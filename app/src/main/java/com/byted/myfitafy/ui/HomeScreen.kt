package com.byted.myfitafy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.byted.myfitafy.R // Assure-toi que c'est TON package
import com.byted.myfitafy.model.WeeklyItem
import com.byted.myfitafy.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- SECTION 1 : CRÉER ---
        Text("Créer votre design", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Grille des catégories avec Lottie
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Remplace R.raw.anim_man par le VRAI nom de tes fichiers
            CategoryCard("Homme", R.raw.anim_man) { navController.navigate("editor/men") }
            CategoryCard("Femme", R.raw.anim_woman) { navController.navigate("editor/women") }
            CategoryCard("Enfant", R.raw.anim_child) { navController.navigate("editor/child") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECTION 2 : CONSEIL ---
        viewModel.dailyTip.value?.let { tip ->
            Text("Conseil du jour", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(tip.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(tip.content, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECTION 3 : COLLECTION ---
        Text("Collection de la semaine", style = MaterialTheme.typography.titleLarge)
        LazyRow(
            contentPadding = PaddingValues(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(viewModel.weeklyCollection.value) { item ->
                WeeklyItemCard(item)
            }
        }
    }
}

// --- NOUVELLE VERSION DE LA CARTE AVEC LOTTIE ---
@Composable
fun CategoryCard(
    title: String,
    lottieRes: Int,
    onClick: () -> Unit
) {
    // Chargement de l'animation
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))

    // Cette Column sert à aligner la Carte ET le Texte verticalement
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp) // On fixe la largeur de la colonne pour bien centrer
    ) {
        // 1. La Carte (La Grille) : Ne contient QUE l'animation
        Card(
            onClick = onClick,
            modifier = Modifier
                .size(100.dp), // Une forme carrée propre (100x100)
            shape = RoundedCornerShape(20.dp), // Arrondi un peu plus prononcé
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(4.dp), // Padding interne pour que l'anim ne touche pas les bords
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Espace entre la carte et le texte
        Spacer(modifier = Modifier.height(8.dp))

        // 2. Le Texte : À l'extérieur de la carte
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium, // Style un peu plus fin/élégant
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface // Couleur du texte standard (noir/gris foncé)
        )
    }
}

@Composable
fun WeeklyItemCard(item: WeeklyItem) {
    Card(
        modifier = Modifier.size(160.dp, 220.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = item.name,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}