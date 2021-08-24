package com.projects.authenticate.document

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.authenticate.R
import kotlinx.android.synthetic.main.item_document.view.*

class DocumentAdapter constructor(private val listDocuments:MutableList<DocumentModel> = mutableListOf()): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).onBind(position)
    }

    override fun getItemCount(): Int = listDocuments.size

    inner class ViewHolder(private val view: View):RecyclerView.ViewHolder(view){
        fun onBind(position: Int){
            val document = listDocuments[position]
            with(view){
                tv_filename.text = document.fileName
                tv_serial_number.text = document.serialNumber
                tv_size.text = document.size
            }
        }
    }

}