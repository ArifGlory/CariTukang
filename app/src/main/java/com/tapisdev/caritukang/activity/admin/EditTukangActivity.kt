package com.tapisdev.caritukang.activity.admin

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.adapter.AdapterKategori
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.caritukang.util.PermissionHelper
import com.tapisdev.mysteam.model.Kategori
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_add_tukang.*
import kotlinx.android.synthetic.main.activity_edit_tukang.*
import kotlinx.android.synthetic.main.activity_edit_tukang.btnLokasi
import kotlinx.android.synthetic.main.activity_edit_tukang.edAlamatTukang
import kotlinx.android.synthetic.main.activity_edit_tukang.edJamKerja
import kotlinx.android.synthetic.main.activity_edit_tukang.edNamaTukang
import kotlinx.android.synthetic.main.activity_edit_tukang.edPhoneTukang
import kotlinx.android.synthetic.main.activity_edit_tukang.imageTukang
import kotlinx.android.synthetic.main.activity_edit_tukang.rlImageTukang
import kotlinx.android.synthetic.main.activity_edit_tukang.spKategori
import java.util.ArrayList

class EditTukangActivity : BaseActivity(),PermissionHelper.PermissionListener {

    lateinit var i : Intent

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    lateinit var tukang : Tukang
    lateinit var kategoriOld : Kategori
    var TAG_SIMPAN = "simpanTukang"
    var lat = 0.0
    var lon = 0.0

    var TAG_GET_KATEGORI = "getKategori"
    var listKategori = ArrayList<Kategori>()
    var listNamaKategori = ArrayList<String>()
    lateinit var adapterSpinnerKategori : ArrayAdapter<String>
    var selectedIdKategori = ""

    lateinit var adapter: AdapterKategori

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tukang)
        mUserPref = UserPreference(this)
        i = intent
        tukang = i.getSerializableExtra("tukang") as Tukang

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)
        adapter = AdapterKategori(listKategori)

        btnLokasi.setOnClickListener {

        }
        rlImageTukang.setOnClickListener {
            launchGallery()
        }
        btnUpdateTukang.setOnClickListener {

        }

        adapterSpinnerKategori = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, listNamaKategori)
        spKategori.adapter = adapterSpinnerKategori
        spKategori.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                var pos = position+1
                Log.d(TAG_GET_KATEGORI,"nama kategori : "+listNamaKategori.get(position))
                Log.d(TAG_GET_KATEGORI,"id kategori : "+listKategori.get(position).id_kategori)

                //yang posisi paling awal diisi kategori yg lama
                if (position != 0){
                    selectedIdKategori = listKategori.get(position).id_kategori
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


        updateUI()
        getDataKategori()
    }

    fun updateUI(){
        edNamaTukang.setText(tukang.nama_tukang)
        edPhoneTukang.setText(tukang.phone_tukang)
        edAlamatTukang.setText(tukang.alamat_tukang)
        edJamKerja.setText(tukang.jam_kerja)

        Glide.with(this)
            .load(tukang.foto_tukang)
            .into(imageTukang)
        selectedIdKategori = tukang.id_kategori
    }


    fun getDataKategori(){
        showLoading(this)
        Log.d("kategori"," : id kategori tukang : "+tukang.id_kategori)

        kategoriRef
            .get().addOnSuccessListener { result ->
                dismissLoading()
                listKategori.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var kategori : Kategori = document.toObject(Kategori::class.java)
                    kategori.id_kategori = document.id
                    listKategori.add(kategori)
                    Log.d("kategori"," : get from fb : "+kategori.toString())

                    if (kategori.id_kategori.equals(tukang.id_kategori)){
                        kategoriOld = kategori
                    }
                }
                if (listKategori.size == 0){
                    showInfoMessage("Kategori kosong..")
                }
                adapter.notifyDataSetChanged()

                listNamaKategori.add(kategoriOld.nama_kategori)
                for (c in 0 until listKategori.size){
                    var kategori  = listKategori.get(c)
                    listNamaKategori.add(kategori.nama_kategori.toString())
                }
                adapterSpinnerKategori.notifyDataSetChanged()


            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_KATEGORI,"err : "+exception.message)
            }
    }

    private fun launchGallery() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    override fun onPermissionCheckDone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
}