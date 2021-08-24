package com.projects.authenticate.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.authenticate.R
import kotlinx.android.synthetic.main.item_student.view.*

class StudentAdapter constructor(private val listStudents:MutableList<StudentModel> = mutableListOf()): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).onBind(position)
    }

    override fun getItemCount(): Int = listStudents.size
    
    inner class ViewHolder(private val view: View):RecyclerView.ViewHolder(view){
        fun onBind(position: Int){
            val student = listStudents[position]
            with(view){
                tv_full_name.text = student.firstName+" "+student.lastName
                tv_serial_number.text = student.serialNumber
                tv_date_of_birth.text = student.dateOfBirth
                tv_grade.text = "Grade: ${student.grade}"
            }
        }
    }

}