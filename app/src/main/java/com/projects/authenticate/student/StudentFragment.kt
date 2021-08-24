package com.projects.authenticate.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.authenticate.R
import com.projects.authenticate.document.DocumentAdapter
import kotlinx.android.synthetic.main.fragment_document.*
import kotlinx.android.synthetic.main.fragment_student.*

class StudentFragment:Fragment() {


    private val adapter: StudentAdapter by lazy {
        StudentAdapter(getStudentList())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        rv_student.layoutManager = layoutManager
        rv_student.adapter = adapter
    }

    fun getStudentList():MutableList<StudentModel>{
        return mutableListOf(
            StudentModel("Alex","Smith","0001","12","","12 July, 1999"),
            StudentModel("James","Pattinson","002","11","","14 August, 2001"),
            StudentModel("John","Nakarmi","003","14","","9 December,1996"),
            StudentModel("Ram","Shrestha","004","11","","2 April,1998"),
            StudentModel("Ram","Shrestha","004","11","","2 April,1998"),
            StudentModel("Hari","Shakya","005","12","","6 April,1999"),
            StudentModel("Ujwal","Khadgi","006","16","","7 January,1993"),
            StudentModel("Dhruba","Sunuwar","007","15","","3 March,1998"),
            StudentModel("Sanjeev","Shrestha","008","11","","2 April,1998" ),
            StudentModel("Ram","Shrestha","004","11","","2 April,1998"))
    }
}