package com.tapisdev.caritukang.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.sdsmdg.tastytoast.TastyToast
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.activity.admin.EditKategoriActivity
import com.tapisdev.caritukang.activity.admin.EditTukangActivity
import com.tapisdev.caritukang.activity.admin.ListKategoriActivity
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.caritukang.util.PermissionHelper
import com.tapisdev.mysteam.model.Kategori
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_show_tukang.*
import kotlinx.android.synthetic.main.row_kategori.view.*
import java.io.Serializable
import java.util.ArrayList

class ShowTukangActivity : BaseActivity(),PermissionHelper.PermissionListener {

    lateinit var i : Intent
    lateinit var tukang : Tukang
    lateinit var  permissionHelper : PermissionHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_tukang)
        mUserPref = UserPreference(this)
        i = intent
        tukang = i.getSerializableExtra("tukang") as Tukang

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        btnLokasi.setOnClickListener {
            val i = Intent(this,LihatLokasiActivity::class.java)
            i.putExtra("tukang",tukang as Serializable)
            startActivity(i)
        }
        btnHubungi.setOnClickListener {
            requestPermissionCall()
        }
        btnEditTukang.setOnClickListener {
            val i = Intent(this,EditTukangActivity::class.java)
            i.putExtra("tukang",tukang as Serializable)
            startActivity(i)
        }
        btnHapusTukang.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Hapus Tukang ini ?")
                .setContentText("Data yang sudah dihapus tidak bisa dikembalikan")
                .setConfirmText("Ya")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    showLoading(this)

                    tukangRef.document(tukang.id_tukang).delete().addOnCompleteListener { task ->
                        dismissLoading()
                        if (task.isSuccessful){
                            TastyToast.makeText(
                                this,
                                "Data tukang dihapus",
                                TastyToast.LENGTH_SHORT,
                                TastyToast.SUCCESS
                            )
                            onBackPressed()

                        }else{
                            TastyToast.makeText(
                                this,
                                "Terjadi kesalahan, coba lagi nanti",
                                TastyToast.LENGTH_SHORT,
                                TastyToast.ERROR
                            )
                            Log.d("hapusTukang","err : "+task.exception)
                        }
                    }
                }
                .setCancelButton(
                    "Tidak"
                ) {
                        sDialog -> sDialog.dismissWithAnimation()
                }
                .show()
        }

        updateUI()
    }

    private fun requestPermissionCall() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.CALL_PHONE)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    fun updateUI(){
        Glide.with(this)
            .load(tukang.foto_tukang)
            .into(ivTukang)

        tvNamaTukang.setText(tukang.nama_tukang)
        tvAlamat.setText(tukang.alamat_tukang)
        tvJamKerja.setText(tukang.jam_kerja)

        if (tukang.rating == 0){
            tvRating.setText("Belum ada rating")
        }

        if (mUserPref.getJenisUser().equals("admin")){
            lineSetting.visibility = View.VISIBLE
        }else{
            lineSetting.visibility = View.GONE
        }
    }

    override fun onPermissionCheckDone() {
        var number = tukang.phone_tukang
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }
}