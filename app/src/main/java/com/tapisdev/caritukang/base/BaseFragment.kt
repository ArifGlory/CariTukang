package com.tapisdev.cateringtenda.base

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.sdsmdg.tastytoast.TastyToast
import com.tapisdev.caritukang.model.UserPreference
import java.text.SimpleDateFormat

open class BaseFragment : Fragment() {

    lateinit var pDialogLoading : SweetAlertDialog
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var currentUser : FirebaseUser
    lateinit var mUserPref : UserPreference

    val myDB = FirebaseFirestore.getInstance()

    val userRef = myDB.collection("users")
    val kategoriRef = myDB.collection("kategori")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pDialogLoading = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        /*var settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
        myDB.firestoreSettings = settings*/

    }

    open fun showLoading(mcontext: Activity?){
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
            activity,
            message,
            TastyToast.LENGTH_SHORT,
            TastyToast.ERROR
        )
    }

    fun showLongErrorMessage(message : String){
        TastyToast.makeText(
            activity,
            message,
            TastyToast.LENGTH_LONG,
            TastyToast.ERROR
        )
    }

    fun showSuccessMessage(message : String){
        TastyToast.makeText(
            activity,
            message,
            TastyToast.LENGTH_SHORT,
            TastyToast.SUCCESS
        )
    }

    fun showInfoMessage(message : String){
        TastyToast.makeText(
            activity,
            message,
            TastyToast.LENGTH_SHORT,
            TastyToast.INFO
        )
    }

    fun showWarningMessage(message : String){
        TastyToast.makeText(
            activity,
            message,
            TastyToast.LENGTH_SHORT,
            TastyToast.WARNING
        )
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

