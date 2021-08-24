package com.projects.authenticate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projects.authenticate.utils.showAlert
import com.projects.authenticate.utils.showToast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    private val type:Int by lazy {
        intent!!.getIntExtra(TYPE,1)
    }

    companion object{
        const val STUDENT_USERNAME = "bank@gmail.com"
        const val STUDENT_PASSWORD = "asd123!@#A"
        const val DOCUMENT_USERNAME = "personal@gmail.com"
        const val DOCUMENT_PASSWORD = "asd123!@#A"
        const val TYPE = "type"
        const val BANK = 1
        const val PERSONAL = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(type== BANK){
            collapsingToolbar.title = "Bank Login"
        }else{
            collapsingToolbar.title = "Personal Login"
        }
        btn_sign_in.setOnClickListener {
            if(type== BANK){
                if(!(et_email.text!!.trim().toString()== STUDENT_USERNAME && et_password.text!!.trim().toString()== STUDENT_PASSWORD)){
                    showToast(message = "Incorrect email or password", Toast.LENGTH_SHORT)
                }else{
                    val intent = Intent(this,HomeActivity::class.java)
                    intent.putExtra(TYPE, BANK)
                    startActivity(intent)
                    finishAffinity()
                }
            }else{
                if(!(et_email.text!!.trim().toString()== DOCUMENT_USERNAME && et_password.text!!.trim().toString()== DOCUMENT_PASSWORD)){
                    showToast(message = "Incorrect email or password",Toast.LENGTH_SHORT)
                }else{
                    val intent = Intent(this,HomeActivity::class.java)
                    intent.putExtra(TYPE, PERSONAL)
                    startActivity(intent)
                    finishAffinity()
                }
            }

        }


    }

}