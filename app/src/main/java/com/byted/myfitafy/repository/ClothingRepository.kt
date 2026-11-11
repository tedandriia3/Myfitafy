package com.byted.myfitafy.repository

import com.byted.myfitafy.model.ClothingPart
import com.byted.myfitafy.model.DailyTip
import com.byted.myfitafy.model.PartType
import com.byted.myfitafy.model.WeeklyItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ClothingRepository {
    suspend fun getDailyTip(): DailyTip?
    suspend fun getWeeklyCollection(): List<WeeklyItem>
    suspend fun getClothingParts(category: String, type: PartType): List<ClothingPart>
}

class ClothingRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ClothingRepository {

    override suspend fun getDailyTip(): DailyTip? {
        return try {
            // Récupère le premier document de la collection daily_tips
            val snapshot = db.collection("daily_tips").limit(1).get().await()
            snapshot.documents.firstOrNull()?.toObject(DailyTip::class.java)
        } catch (e: Exception) {
            // Donnée factice en cas d'erreur ou offline
            DailyTip("1", "Assortir les couleurs", "Le bleu marine va parfaitement avec le beige.")
        }
    }

    override suspend fun getWeeklyCollection(): List<WeeklyItem> {
        return try {
            val snapshot = db.collection("weekly_collection").get().await()
            snapshot.toObjects(WeeklyItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getClothingParts(category: String, type: PartType): List<ClothingPart> {
        // --- MODE ESPION ACTIVÉ ---
        try {
            // 1. On récupère TOUT sans filtre pour voir ce qui existe
            val debugSnapshot = db.collection("clothing_parts").get().await()

            println("--- DÉBUT INSPECTION FIRESTORE ---")
            debugSnapshot.documents.forEach { doc ->
                val typeDansDb = doc.getString("type")
                val catDansDb = doc.getString("category")

                // J'ajoute des crochets [] pour voir les espaces cachés !
                // Si tu vois [SLEEVE ] avec un vide avant le crochet, c'est ça l'erreur !
                println("Document ID: ${doc.id}")
                println("   -> Type lu : [$typeDansDb]")
                println("   -> Categ lu : [$catDansDb]")

                // Vérification logicielle
                if (typeDansDb == type.name && catDansDb == category) {
                    println("   => CORRESPONDANCE TROUVÉE ! Ce document devrait s'afficher.")
                } else {
                    println("   => REJETÉ. (On cherchait Type=[${type.name}] et Categ=[$category])")
                }
            }
            println("--- FIN INSPECTION ---")

            // 2. La vraie requête (Celle qui échoue actuellement)
            val snapshot = db.collection("clothing_parts")
                .whereEqualTo("category", category)
                .whereEqualTo("type", type.name)
                .get().await()

            return snapshot.toObjects(ClothingPart::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}