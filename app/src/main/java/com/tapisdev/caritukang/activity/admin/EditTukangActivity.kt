package com.tapisdev.caritukang.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import com.tapisdev.caritukang.model.SharedVariable
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
import java.io.ByteArrayOutputStream
import java.io.IOException
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
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)
        adapter = AdapterKategori(listKategori)

        btnLokasi.setOnClickListener {
            val i = Intent(this,SelectMapsActivity::class.java)
            startActivity(i)
        }
        rlImageTukang.setOnClickListener {
            launchGallery()
        }
        btnUpdateTukang.setOnClickListener {
            checkValidation()
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
        switchTukang.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked){
                switchTukang.setText("Status Aktif")
            }else{
                switchTukang.setText("Status Non-Aktif")
            }
        }


        updateUI()
        getDataKategori()
    }

    fun checkValidation(){
        var getName = edNamaTukang.text.toString()
        var getPhone = edPhoneTukang.text.toString()
        var getAlamat = edAlamatTukang.text.toString()
        var getjamkerja = edJamKerja.text.toString()
        var getJmlOrder = edJmlOrder.text.toString()
        var status = tukang.active
        if (switchTukang.isChecked){
            status = 1
        }else{
            status = 0
        }


        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        }else if (getPhone.equals("") || getPhone.length == 0){
            showErrorMessage("telepon Belum diisi")
        }else if (getAlamat.equals("") || getAlamat.length == 0){
            showErrorMessage("alamat Belum diisi")
        }else if (getjamkerja.equals("") || getjamkerja.length == 0){
            showErrorMessage("Jam Kerja Belum diisi")
        }else if (getJmlOrder.equals("") || getJmlOrder.length == 0){
            showErrorMessage("Jumlah order belum diisi")
        }else if (lat == 0.0){
            showErrorMessage("Lokasi belum dpilih")
        }
        else if (fileUri == null){
            tukang.nama_tukang = getName
            tukang.phone_tukang = getPhone
            tukang.alamat_tukang = getAlamat
            tukang.jam_kerja = getjamkerja
            tukang.lat = lat.toString()
            tukang.lon = lon.toString()
            tukang.id_kategori = selectedIdKategori
            tukang.jml_order = getJmlOrder.toInt()
            tukang.active = status

            updateDataOnly()
        }
        else {

            tukang.nama_tukang = getName
            tukang.phone_tukang = getPhone
            tukang.alamat_tukang = getAlamat
            tukang.jam_kerja = getjamkerja
            tukang.lat = lat.toString()
            tukang.lon = lon.toString()
            tukang.id_kategori = selectedIdKategori
            tukang.jml_order = getJmlOrder.toInt()
            tukang.active = status
            uploadFotoAndUpdate()
        }
    }

    fun updateDataOnly(){
        showLoading(this)

        tukangRef.document(tukang.id_tukang).update("nama_tukang",tukang.nama_tukang)
        tukangRef.document(tukang.id_tukang).update("phone_tukang",tukang.phone_tukang)
        tukangRef.document(tukang.id_tukang).update("alamat_tukang",tukang.alamat_tukang)
        tukangRef.document(tukang.id_tukang).update("jam_kerja",tukang.jam_kerja)
        tukangRef.document(tukang.id_tukang).update("lat",tukang.lat)
        tukangRef.document(tukang.id_tukang).update("lon",tukang.lon)
        tukangRef.document(tukang.id_tukang).update("id_kategori",tukang.id_kategori)
        tukangRef.document(tukang.id_tukang).update("jml_order",tukang.jml_order)
        tukangRef.document(tukang.id_tukang).update("active",tukang.active).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah Data Berhasil")
                val i = Intent(this,ListTukangActivity::class.java)
                startActivity(i)
                finish()
            }else{
                showErrorMessage("Terjadi kesalahan, coba lagi nanti")
            }
        }
    }

    fun uploadFotoAndUpdate(){
        showLoading(this)

        if (fileUri != null){
            Log.d(TAG_SIMPAN,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_SIMPAN, exception.toString())
            }.addOnSuccessListener {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                showSuccessMessage("Image Berhasil di upload")
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                    }

                    fileReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAu.getInstance().getCurrentUser().getUid());
                        val url = downloadUri!!.toString()
                        Log.d(TAG_SIMPAN,"download URL : "+ downloadUri.toString())// This is the one you should store
                        tukang.foto_tukang = url

                        tukangRef.document(tukang.id_tukang).update("foto_tukang",tukang.foto_tukang)
                        tukangRef.document(tukang.id_tukang).update("nama_tukang",tukang.nama_tukang)
                        tukangRef.document(tukang.id_tukang).update("phone_tukang",tukang.phone_tukang)
                        tukangRef.document(tukang.id_tukang).update("alamat_tukang",tukang.alamat_tukang)
                        tukangRef.document(tukang.id_tukang).update("jam_kerja",tukang.jam_kerja)
                        tukangRef.document(tukang.id_tukang).update("lat",tukang.lat)
                        tukangRef.document(tukang.id_tukang).update("lon",tukang.lon)
                        tukangRef.document(tukang.id_tukang).update("id_kategori",tukang.id_kategori)
                        tukangRef.document(tukang.id_tukang).update("jml_order",tukang.jml_order)
                        tukangRef.document(tukang.id_tukang).update("active",tukang.active).addOnCompleteListener { task ->
                            dismissLoading()
                            if (task.isSuccessful){
                                showSuccessMessage("Ubah Data Berhasil")
                                val i = Intent(this,ListTukangActivity::class.java)
                                startActivity(i)
                                finish()
                            }else{
                                showErrorMessage("Terjadi kesalahan, coba lagi nanti")
                            }
                        }
                    } else {
                        dismissLoading()
                        showErrorMessage("Terjadi kesalahan, coba lagi nanti")
                    }
                }
            }.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                pDialogLoading.setTitleText("Uploaded " + progress.toInt() + "%...")
            }


        }else{
            dismissLoading()
            showErrorMessage("Anda belum memilih file")
        }
    }

    fun updateUI(){
        edNamaTukang.setText(tukang.nama_tukang)
        edPhoneTukang.setText(tukang.phone_tukang)
        edAlamatTukang.setText(tukang.alamat_tukang)
        edJamKerja.setText(tukang.jam_kerja)
        edJmlOrder.setText(""+tukang.jml_order)

        Glide.with(this)
            .load(tukang.foto_tukang)
            .into(imageTukang)
        selectedIdKategori = tukang.id_kategori

        if (tukang.active == 1){
            switchTukang.isChecked = true
            switchTukang.setText("Status Aktif")
        }else{
            switchTukang.isChecked = false
            switchTukang.setText("Status Non-Aktif")
        }

        lat = tukang.lat.toDouble()
        lon = tukang.lon.toDouble()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            fileUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                fotoBitmap = bitmap
                imageTukang.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SharedVariable.centerLatLon.latitude != 0.0){
            lat = SharedVariable.centerLatLon.latitude
            lon = SharedVariable.centerLatLon.longitude

            val img: Drawable = btnLokasi.context.resources.getDrawable(R.drawable.ic_check_black_24dp)
            btnLokasi.setText("Lokasi Telah dipilih")
            btnLokasi.setCompoundDrawables(img,null,null,null)

            /* var alamatLokasi = getCompleteAddress(lat,lon)
             edAlamat.setText(alamatLokasi)*/
        }
    }
}