package com.byted.myfitafy.model

// Représente une partie du vêtement (Manche, Col, etc.)
data class ClothingPart(
    val id: String = "",
    val name: String = "Pièce standard", // Ajout du nom
    val type: PartType = PartType.BASE,
    val category: String = "men",
    val imageUrl: String = "",
    val zIndex: Int = 0,
    val price: Double = 0.0
)

// Les types de parties possibles
enum class PartType {
    BASE,   // Le corps du vêtement
    SLEEVE, // Les manches
    COLLAR, // Le col
    POCKET  // Accessoires/Poches
}

// Conseil du jour
data class DailyTip(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Long = System.currentTimeMillis()
)

// Collection de la semaine
data class WeeklyItem(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = ""
)