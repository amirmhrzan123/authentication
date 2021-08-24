package com.projects.authenticate.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.authenticate.R
import kotlinx.android.synthetic.main.fragment_document.*

class DocumentFragments: Fragment() {

    private val adapter: DocumentAdapter by lazy {
        DocumentAdapter(getDocumentList())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_document,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        rv_document.layoutManager = layoutManager
        rv_document.adapter = adapter

    }

    fun getDocumentList():MutableList<DocumentModel>{
        return mutableListOf(
            DocumentModel("NodeMcu","Pdf","001","12Mb"),
            DocumentModel
                ("Flutter","Txt","002","5Mb"),
            DocumentModel(
                "Tourism","Video","003","0.9Mb"
            ),
            DocumentModel
                ("Vehicles","Exe","004","2.3Mb"))
    }
}