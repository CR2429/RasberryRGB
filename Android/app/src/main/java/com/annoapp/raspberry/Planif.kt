package com.annoapp.raspberry;

import android.os.Parcelable
import java.time.LocalTime
import java.util.UUID;
import kotlinx.parcelize.Parcelize

@Parcelize
class Planif(private var Actif: Boolean) :Parcelable {
    //Propriete
    private lateinit var Titre: String
    private lateinit var Heure: LocalTime
    private lateinit var Commande: String
    private val ID = UUID.randomUUID().toString();

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
    fun getHeure() : LocalTime {
        return Heure
    }
    fun setHeure(heure:LocalTime) {
        this.Heure = heure
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
