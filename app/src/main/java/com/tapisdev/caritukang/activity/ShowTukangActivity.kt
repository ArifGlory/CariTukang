package com.tapisdev.caritukang.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.caritukang.util.PermissionHelper
import com.tapisdev.mysteam.model.Kategori
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_show_tukang.*
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