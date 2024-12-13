package com.annoapp.raspberry

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import java.text.FieldPosition

@RequiresApi(Build.VERSION_CODES.O)
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val planif = getItem(position) as Planif
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_layout)
    }
}