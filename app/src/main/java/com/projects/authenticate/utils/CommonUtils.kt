package com.projects.authenticate.utils

import android.app.Activity
import android.app.Dialog
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.projects.authenticate.*
import com.projects.authenticate.prefs.IPrefsManager
import kotlinx.android.synthetic.main.bottomsheet_new_password.*
import kotlinx.android.synthetic.main.bottomsheet_new_password.et_password
import kotlinx.android.synthetic.main.bottomsheet_new_password.ti_password
import kotlinx.android.synthetic.main.bottomsheet_new_password.tv_cancel
import kotlinx.android.synthetic.main.bottomsheet_new_password.tv_confirm
import kotlinx.android.synthetic.main.bottomsheet_password.*
import kotlinx.android.synthetic.main.bottomsheet_password.view.*
import kotlinx.android.synthetic.main.bottomsheet_select.*

object CommonUtils {
    fun openNewPassword(activity: Activity, confirm: (password: String) -> Unit,cancel:()->Unit,prefsManager: IPrefsManager){
        val view = LayoutInflater.from(activity).inflate(R.layout.bottomsheet_new_password,null)
        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        with(mBottomSheetDialog){
            setContentView(view)
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window!!.setGravity(Gravity.BOTTOM)

            show()
            var error = true

            et_password.afterTextChanged {
                if(it.isEmpty()){
                    tv_confirm.isEnabled = false
                    ti_password.error = "Please enter password."
                }else if(!it.containsDigit()){
                    tv_confirm.isEnabled = false
                    ti_password.error = "Must contain at least one digit"
                }else if(!it.containsUpperCase()){
                    tv_confirm.isEnabled = false
                    ti_password.error = "Must contain at least one upper case letter."
                }else if(!it.containsLowerCase()){
                    tv_confirm.isEnabled = false
                    ti_password.error = "Must contain at least one lower case letter"
                }else if(!it.containsSpecialCharacter()){
                    tv_confirm.isEnabled = false
                    ti_password.error = "Must contain at least one special character"
                }else if(it.length<8){
                    tv_confirm.isEnabled = false
                    ti_password.error = "Must contain at least eight character"
                }else{
                    ti_password.error = ""
                    error = false
                    tv_confirm.isEnabled = et_confirm_password.text!!.trim() == it
                }
            }

            et_confirm_password.afterTextChanged {
                if(it != et_password.text?.trim().toString()){
                    tv_confirm.isEnabled = false
                    ti_confirm_password.error = "Password doesn't match"
                }else{
                    tv_confirm.isEnabled = !error
                    ti_confirm_password.error = ""
                }
            }

            tv_confirm.setOnClickListener {
                prefsManager.setString("PASSWORD",et_password.text!!.trim().toString())
                confirm(et_password.text!!.trim().toString())
                dismiss()
            }

            tv_cancel.setOnClickListener {
                cancel()
                dismiss()
            }
        }
    }

    fun openEnterPassword(activity: Activity, confirm: (password: String) -> Unit,cancel:()->Unit,prefsManager: IPrefsManager){
        val view = LayoutInflater.from(activity).inflate(R.layout.bottomsheet_password,null)
        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        var time =40
        val timerModel = object: CountDownTimer(40000,1000){
            override fun onTick(p0: Long) {
                time--
                view.timer.text = time.toString()

            }

            override fun onFinish() {
                activity.showToast("Timeout", Toast.LENGTH_SHORT)
                mBottomSheetDialog.dismiss()
            }

        }
        timerModel.start()
        with(mBottomSheetDialog){
            setContentView(view)
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window!!.setGravity(Gravity.BOTTOM)

            show()


            tv_confirm_password.setOnClickListener {
                if(prefsManager.getString("PASSWORD")==et_password_enter.text?.trim().toString()){
                    confirm(et_password_enter.text!!.trim().toString())
                    timerModel.cancel()
                    dismiss()
                }else{
                    ti_password_enter.error = "Password Not Match."
                }

            }

            tv_cancel_password.setOnClickListener {
                cancel()
                timerModel.cancel()
                dismiss()
            }
        }
    }


    fun openSelection(activity: Activity, student: () -> Unit,document:()->Unit){
        val view = LayoutInflater.from(activity).inflate(R.layout.bottomsheet_select,null)
        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        with(mBottomSheetDialog){
            setContentView(view)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window!!.setGravity(Gravity.BOTTOM)

            show()

            tvStudent.setOnClickListener {
                student()
            }
            tvdocument.setOnClickListener {
                document()
            }
        }
    }
    enum class NEWPASSWORD{
        CANCEL,CONFIRM
    }
}