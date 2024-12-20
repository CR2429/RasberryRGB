package com.annoapp.raspberry

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi


class PlanifAdapter(private val context: Context, private val dataList: ArrayList<Planif>): BaseAdapter() {
    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val planif = getItem(position) as Planif
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.lists_layout, parent, false)

        //identifier mes elements
        val TvTitre = view.findViewById<TextView>(R.id.TvTitreForm)
        val TvCommande = view.findViewById<TextView>(R.id.TvCommande)
        val TvHeure = view.findViewById<TextView>(R.id.TvHeure)
        val switch = view.findViewById<Switch>(R.id.SActif)

        //customisation
        TvTitre.text = planif.getTitre()
        switch.isChecked = planif.getActif()
        if (planif.getCommande() == "Eteint") {
            TvCommande.text = planif.getCommande()
        }
        else {
            TvCommande.text = "Commande : ${planif.getCommande()}"
        }
        val time = planif.getHeure()
        val heure = time.hour
        val minute = time.minute
        TvHeure.text = "${heure}:${minute}"

        //Interaction
        view.setOnClickListener{

        }

        return view
    }
}