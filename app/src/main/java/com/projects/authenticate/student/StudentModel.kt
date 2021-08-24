package com.projects.authenticate.student

import com.projects.authenticate.document.DocumentModel

data class StudentModel(
    val firstName:String,
    val lastName:String,
    val serialNumber:String,
    val grade:String,
    val image:String,
    val dateOfBirth:String
)