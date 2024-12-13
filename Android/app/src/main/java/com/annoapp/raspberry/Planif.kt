package com.annoapp.raspberry;

import androidx.annotation.RequiresApi;
import android.os.Build;

import java.io.Serializable;
import java.sql.Time
import java.util.UUID;

@RequiresApi(Build.VERSION_CODES.O)
class Planif(private var Titre: String, private var Heure: Time, private var Commande: String, private var Color: String, private var Actif: Boolean) :Serializable {
    //Propriete
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
    fun getHeure() : Time {
        return Heure
    }
    fun setHeure(heure:Time) {
        this.Heure = heure
    }
    fun getCommande() : String {
        return Commande
    }
    fun setCommande(commande : String) {
        this.Commande = commande
    }
    fun getColor() : String {
        return Color
    }
    fun setColor(color : String) {
        this.Color = color
    }
    fun getActif() : Boolean {
        return Actif
    }
    fun setActif(actif : Boolean) {
        this.Actif = actif
    }
}
