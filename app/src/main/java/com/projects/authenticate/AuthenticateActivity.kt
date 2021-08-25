package com.projects.authenticate

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.projects.authenticate.facerecognition.FaceDetectionActivity
import com.projects.authenticate.prefs.IPrefsManager
import com.projects.authenticate.prefs.PrefsManager
import com.projects.authenticate.utils.CommonUtils
import com.projects.authenticate.utils.Constants
import com.projects.authenticate.utils.Utilities
import com.projects.authenticate.utils.showToast
import kotlinx.android.synthetic.main.activity_authenticate.*
import kotlinx.android.synthetic.main.bottomsheet_password.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor

class AuthenticateActivity : AppCompatActivity() {


    private lateinit var executor: Executor
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var biometricPrompt: BiometricPrompt

    private val REQUEST_CAMERA_PERMISSION = 101
    private val REQUEST_DIRECTORY_ACCESS  = 102
    private var isSerializedDataStored = false

    // Serialized data will be stored ( in app's private storage ) with this filename.
    private val SERIALIZED_DATA_FILENAME = "image_data"

    // Shared Pref key to check if the data was stored.
    private val SHARED_PREF_IS_DATA_STORED_KEY = "is_data_stored"

    lateinit var prefManager: IPrefsManager

    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    var imageNumber = 0

    lateinit var takePhoto: ActivityResultLauncher<Void>
    lateinit var secondPhoto: ActivityResultLauncher<Void>
    lateinit var thirdPhoto: ActivityResultLauncher<Void>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)
        prefManager = providePrefsManager(provideSharePreference(this))
        executor = ContextCompat.getMainExecutor(this)


        takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            savePhotoToInternalStorage(UUID.randomUUID().toString(), it)
            imageNumber++
            prefManager.setInt("NUMBER",imageNumber)
            application.showToast("2 images remaining", Toast.LENGTH_LONG)

            secondPhoto.launch(null,null)
        }

        secondPhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            savePhotoToInternalStorage(UUID.randomUUID().toString(), it)
            imageNumber++
            prefManager.setInt("NUMBER",imageNumber)
            application.showToast("1 image remaining", Toast.LENGTH_LONG)
            thirdPhoto.launch(null,null)

        }

        thirdPhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            savePhotoToInternalStorage(UUID.randomUUID().toString(), it)
            Toast.makeText(this, "Images saved successfully", Toast.LENGTH_SHORT).show()
            prefManager.setBool("PHOTOS",true)
            setUpForPassword()

        }

        btn_authenticate.setOnClickListener {
            setUpForPassword()
        }

        if(!prefManager.getBool("PHOTOS")){
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val files = filesDir.listFiles()
                    files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                        deletePhotoFromInternalStorage(it.name)
                    } ?: listOf()
                }
            }
        }
        if ( ActivityCompat.checkSelfPermission( this , Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this , arrayOf( Manifest.permission.CAMERA ) , REQUEST_CAMERA_PERMISSION )
            Log.d("PermissionGranted","Premis")
        }else{
            Log.d("SETUP","FROM PERMISSION")
            setUpForPassword()
        }

    }

    private fun setPrompt() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    ic_fingerprint.setImageDrawable(ContextCompat.getDrawable(this@AuthenticateActivity, R.drawable.ic_check_green))
                    view_fingerprint.background = (ContextCompat.getDrawable(this@AuthenticateActivity, R.color.green))
                    /*CommonUtils.openSelection(this@AuthenticateActivity,student = {
                        val intent = Intent(this@AuthenticateActivity,LoginActivity::class.java)
                        intent.putExtra(LoginActivity.TYPE,LoginActivity.BANK)
                        startActivity(intent)
                        finishAffinity()
                    },document = {
                        val intent = Intent(this@AuthenticateActivity,LoginActivity::class.java)
                        intent.putExtra(LoginActivity.TYPE,LoginActivity.PERSONAL)
                        startActivity(intent)
                        finishAffinity()
                    })*/

                    val intent = Intent(this@AuthenticateActivity,FaceDetectionActivity::class.java)
                    startActivityForResult(intent,200)


                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })
    }

    private fun initBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        setDeviceCred: Boolean
    ) {
        if (setDeviceCred) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val authFlag = BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_STRONG
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setAllowedAuthenticators(authFlag)
                    .build()
            } else {
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setDeviceCredentialAllowed(true)
                    .build()
            }
        } else {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButtonText(Constants.CANCEL)
                .build()
        }

    }
    fun provideSharePreference(context: Context): SharedPreferences {
        // create the master key
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()        // Create the EncryptedSharedPreferences

        return EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )


    }

    fun providePrefsManager(pref: SharedPreferences): IPrefsManager = PrefsManager(pref)


    fun setUpForPassword(){
        ic_fingerprint.setImageDrawable(ContextCompat.getDrawable(this@AuthenticateActivity, R.drawable.ic_check_black))
        view_fingerprint.background = (ContextCompat.getDrawable(this@AuthenticateActivity, R.color.grey))
        ic_password.setImageDrawable(ContextCompat.getDrawable(this@AuthenticateActivity, R.drawable.ic_check_black))
        ic_face.setImageDrawable(ContextCompat.getDrawable(this@AuthenticateActivity, R.drawable.ic_check_black))
        view_face.background = (ContextCompat.getDrawable(this@AuthenticateActivity, R.color.grey))
        if(prefManager.getString("PASSWORD").isEmpty()){
            CommonUtils.openNewPassword(this,confirm = {
                Log.d("SETUP","FROM NEW CONFIRM")
               setUpForPassword()
            },cancel = {
                Log.d("SETUP","FROM NEW CANCEL")
                setUpForPassword()
                ic_password.setImageDrawable(ContextCompat.getDrawable(this@AuthenticateActivity, R.drawable.ic_check_black))
            },prefManager)
        }else{
            if(prefManager.getBool("PHOTOS")){
                CommonUtils.openEnterPassword(this,confirm = {
                    ic_password.setImageDrawable(ContextCompat.getDrawable(this@AuthenticateActivity, R.drawable.ic_check_green))
                    setPrompt()
                    initBiometricPrompt(
                        Constants.BIOMETRIC_AUTHENTICATION,
                        Constants.BIOMETRIC_AUTHENTICATION_SUBTITLE,
                        Constants.BIOMETRIC_AUTHENTICATION_DESCRIPTION,
                        false
                    )
                    biometricPrompt.authenticate(promptInfo)
                },cancel = {
                    showToast("TimeOut", Toast.LENGTH_SHORT)
                    finishAffinity()

                },prefManager)
            }else{
                application.showToast("Plz take three selfie to add the face data.", Toast.LENGTH_LONG)
                takePhoto.launch(null, null)

            }

        }
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==200){
            ic_face.setImageDrawable(ContextCompat.getDrawable(this@AuthenticateActivity, R.drawable.ic_check_green))
            view_face.background = (ContextCompat.getDrawable(this@AuthenticateActivity, R.color.green))
            CommonUtils.openSelection(this@AuthenticateActivity,student = {
                val intent = Intent(this@AuthenticateActivity,LoginActivity::class.java)
                intent.putExtra(LoginActivity.TYPE,LoginActivity.BANK)
                startActivity(intent)
                finishAffinity()
            },document = {
                val intent = Intent(this@AuthenticateActivity,LoginActivity::class.java)
                intent.putExtra(LoginActivity.TYPE,LoginActivity.PERSONAL)
                startActivity(intent)
                finishAffinity()
            })
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ( requestCode == REQUEST_CAMERA_PERMISSION ) {
            if ( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                setUpForPassword()
            }
            else if ( grantResults[ 0 ] == PackageManager.PERMISSION_DENIED ) {
                val alertDialog = AlertDialog.Builder( this ).apply {
                    setTitle( "Camera Permission")
                    setMessage( "The app couldn't function without the camera permission." )
                    setCancelable( false )
                    setPositiveButton( "ALLOW" ) { dialog, which ->
                        dialog.dismiss()
                        ActivityCompat.requestPermissions( context as FaceDetectionActivity ,
                            arrayOf( Manifest.permission.CAMERA ) ,
                            REQUEST_CAMERA_PERMISSION )
                    }
                    setNegativeButton( "CLOSE" ) { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }
                    create()
                }
                alertDialog.show()
            }
        }
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean {
        return try {
            deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

