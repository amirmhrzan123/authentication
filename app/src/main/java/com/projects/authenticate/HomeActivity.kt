package com.projects.authenticate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.projects.authenticate.document.DocumentFragments
import com.projects.authenticate.student.StudentFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {


    private val type:Int by lazy {
        intent!!.getIntExtra(LoginActivity.TYPE,1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if(type==LoginActivity.BANK){
            dashboardToolbar.title = "Bank"
            supportFragmentManager.beginTransaction().replace(R.id.container,StudentFragment()).commit()

        }else{
            dashboardToolbar.title = "Personal Info"
            supportFragmentManager.beginTransaction().replace(R.id.container,DocumentFragments()).commit()

        }

    }

}