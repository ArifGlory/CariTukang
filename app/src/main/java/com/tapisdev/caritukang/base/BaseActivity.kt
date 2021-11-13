package com.tapisdev.caritukang.base

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.sdsmdg.tastytoast.TastyToast
import com.tapisdev.caritukang.model.UserPreference
import java.text.SimpleDateFormat

open class BaseActivity : AppCompatActivity() {

    lateinit var pDialogLoading : SweetAlertDialog
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var currentUser : FirebaseUser
    lateinit var mUserPref : UserPreference

    val myDB = FirebaseFirestore.getInstance()
    val userRef = myDB.collection("users")
    val settingsRef = myDB.collection("settings")
    val kategoriRef = myDB.collection("kategori")
    val tukangRef = myDB.collection("tukang")

    override fun setContentView(view: View?) {
        super.setContentView(view)

        pDialogLoading = SweetAlertDialog(applicationContext, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        var settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
        myDB.firestoreSettings = settings

    }

    open fun showLoading(mcontext : Context){
        pDialogLoading = SweetAlertDialog(mcontext, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        pDialogLoading.show()
    }

    fun dismissLoading(){
        pDialogLoading.dismiss()
    }

    fun showErrorMessage(message : String){

        TastyToast.makeText(
            applicationContext,
            message,
            TastyToast.LENGTH_SHORT,
            TastyToast.ERROR
        )

    }

    fun showSuccessMessage(message : String){
        //applicationContext?.let { Toasty.success(it, message, Toast.LENGTH_SHORT, true).show() }

        TastyToast.makeText(
            applicationContext,
            message,
            TastyToast.LENGTH_SHORT,
            TastyToast.SUCCESS
        )
    }

    fun showLongSuccessMessage(message : String){
        TastyToast.makeText(
            applicationContext,
            message,
            TastyToast.LENGTH_LONG,
            TastyToast.SUCCESS
        )
    }

    fun showLongErrorMessage(message : String){
        TastyToast.makeText(
            applicationContext,
            message,
            TastyToast.LENGTH_LONG,
            TastyToast.ERROR
        )
    }

    fun showInfoMessage(message : String){
        TastyToast.makeText(
            applicationContext,
            message,
            TastyToast.LENGTH_SHORT,
            TastyToast.INFO
        )
    }

    fun showWarningMessage(message : String){
        TastyToast.makeText(
            applicationContext,
            message,
            TastyToast.LENGTH_SHORT,
            TastyToast.WARNING
        )
    }

    fun showSweetInfo(message : String){
        SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
            .setTitleText("Informasi")
            .setContentText(message)
            .show()
    }

    fun logout(){
        mUserPref.saveName("")
        mUserPref.saveAlamat("")
        mUserPref.saveDeskripsi("")
        mUserPref.saveEmail("")
        mUserPref.saveJenisUser("")
        mUserPref.savePhone("")
    }

    fun convertDate(tanggal : String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val output = formatter.format(parser.parse(tanggal))

        return output
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            currentUser = auth.currentUser!!
        }
    }


}