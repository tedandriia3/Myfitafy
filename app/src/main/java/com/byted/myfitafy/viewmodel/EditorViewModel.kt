package com.byted.myfitafy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byted.myfitafy.model.ClothingPart
import com.byted.myfitafy.model.PartType
import com.byted.myfitafy.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: ClothingRepository
) : ViewModel() {

    // Les parties sélectionnées
    private val _selectedParts = MutableStateFlow<Map<PartType, ClothingPart?>>(emptyMap())
    val selectedParts = _selectedParts.asStateFlow()

    // Les options disponibles pour le sélecteur
    private val _availableOptions = MutableStateFlow<List<ClothingPart>>(emptyList())
    val availableOptions = _availableOptions.asStateFlow()

    // --- NOUVEAU : Calcul dynamique du Prix Total ---
    // On observe selectedParts. Dès que ça change, on recalcule la somme.
    val totalPrice = _selectedParts.map { partsMap ->
        partsMap.values.filterNotNull().sumOf { it.price }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun loadInitialData(category: String) {
        viewModelScope.launch {
            // 1. Charger la base (Corps)
            val bases = repository.getClothingParts(category, PartType.BASE)
            if (bases.isNotEmpty()) {
                selectPart(bases.first())
            }
            // 2. Charger les options par défaut (ex: Cols)
            loadOptions(category, PartType.COLLAR)
        }
    }

    fun loadOptions(category: String, type: PartType) {
        viewModelScope.launch {
            val result = repository.getClothingParts(category, type)

            // AJOUTE CES LIGNES POUR VOIR DANS LE "LOGCAT"
            println("DEBUG: Chargement pour $category - ${type.name}")
            println("DEBUG: Nombre d'éléments trouvés : ${result.size}")
            result.forEach {
                println("DEBUG: Trouvé -> ${it.name} (URL: ${it.imageUrl})")
            }

            _availableOptions.value = result
        }
    }

    // Fonction modifiée pour gérer le "Toggle" (Ajout / Suppression)
    fun selectPart(part: ClothingPart) {
        val currentMap = _selectedParts.value.toMutableMap()

        // On regarde quelle pièce est actuellement sélectionnée pour ce type (ex: quel Col ?)
        val currentSelectedPart = currentMap[part.type]

        // SI la pièce cliquée est DÉJÀ celle sélectionnée (Même ID)
        if (currentSelectedPart?.id == part.id) {
            // ALORS on l'enlève (Désélectionner)
            currentMap.remove(part.type)
        } else {
            // SINON on la remplace/ajoute (Sélectionner)
            currentMap[part.type] = part
        }

        // On met à jour l'état (ce qui va rafraîchir l'écran et le prix)
        _selectedParts.value = currentMap
    }
}