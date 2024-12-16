package com.annoapp.raspberry;

import androidx.annotation.RequiresApi;
import android.os.Build;

import java.io.Serializable;
import java.sql.Time
import java.time.LocalTime
import java.util.UUID;

@RequiresApi(Build.VERSION_CODES.O)
class Planif(private var Actif: Boolean) :Serializable {
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
}
