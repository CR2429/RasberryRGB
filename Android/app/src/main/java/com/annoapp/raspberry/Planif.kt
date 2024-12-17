package com.annoapp.raspberry;

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.util.UUID;
import kotlinx.parcelize.Parcelize

@Parcelize
class Planif(private var Actif: Boolean) :Parcelable {
    //Propriete
    private lateinit var Titre: String
    private lateinit var Heure: LocalTime
    private lateinit var Commande: String
    private val ID = UUID.randomUUID().toString()
    private var HeureString: String = "0"
    private var MinuteString: String = "0"

    // get-set
    fun getID(): String {
        return ID
    }
    fun getTitre() : String {
        return Titre
    }
    fun setTitre(titre:String) {
        this.Titre = titre
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getHeure(): LocalTime {
        if (HeureString.isNotEmpty()) {
            Heure = LocalTime.of(HeureString.toInt(),MinuteString.toInt())
        }
        return Heure
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setHeure(heure:LocalTime) {
        this.Heure = heure
        this.HeureString = heure.hour.toString()
        this.MinuteString = heure.minute.toString()
    }
    fun getCommande() : String {
        return Commande
    }
    fun setCommande(commande : String) {
        this.Commande = commande
    }
    fun getActif() : Boolean {
        return Actif
    }
    fun setActif(actif : Boolean) {
        this.Actif = actif
    }

    fun isHeureInitialized(): Boolean {
        return this::Heure.isInitialized
    }
    fun isCommandeInitialized(): Boolean {
        return this::Commande.isInitialized
    }
}
