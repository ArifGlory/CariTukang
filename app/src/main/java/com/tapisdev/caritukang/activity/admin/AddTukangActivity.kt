package com.tapisdev.caritukang.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.gms.maps.model.LatLng
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
import kotlinx.android.synthetic.main.activity_add_kategori.*
import kotlinx.android.synthetic.main.activity_add_tukang.*
import kotlinx.android.synthetic.main.activity_register_tukang.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class AddTukangActivity : BaseActivity(),PermissionHelper.PermissionListener {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    lateinit var tukang : Tukang
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
        setContentView(R.layout.activity_add_tukang)
        mUserPref = UserPreference(this)
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)
        adapter = AdapterKategori(listKategori)


        btnLokasi.setOnClickListener {
            val i = Intent(this,SelectMapsActivity::class.java)
            startActivity(i)
        }
        btnSimpanTukang.setOnClickListener {
            checkValidation()
        }
        rlImageTukang.setOnClickListener {
            launchGallery()
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
                selectedIdKategori = listKategori.get(position).id_kategori

                /*if (!namaKategori.equals("Pilih Kategori")){
                    val i = Intent(requireContext(), ResultActivity::class.java)
                    i.putExtra("jenis","provinsi")
                    i.putExtra("key",namaProvinsi)
                    i.putExtra("value",namaProvinsi)
                    startActivity(i)
                }*/

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        getDataKategori()
    }

    fun getDataKategori(){
        showLoading(this)
        kategoriRef.whereEqualTo("active",1)
            .get().addOnSuccessListener { result ->
                dismissLoading()
                listKategori.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var kategori : Kategori = document.toObject(Kategori::class.java)
                    kategori.id_kategori = document.id
                    listKategori.add(kategori)

                }
                if (listKategori.size == 0){
                    showInfoMessage("Kategori kosong..")
                }
                adapter.notifyDataSetChanged()

                for (c in 0 until listKategori.size){
                    var kategori  = listKategori.get(c)
                    listNamaKategori.add(kategori.nama_kategori.toString())
                }
                adapterSpinnerKategori.notifyDataSetChanged()

                if (listNamaKategori.size != 0){
                    selectedIdKategori = listKategori.get(0).id_kategori
                }

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_KATEGORI,"err : "+exception.message)
            }
    }


    fun checkValidation(){
        var getName = edNamaTukang.text.toString()
        var getPhone = edPhoneTukang.text.toString()
        var getAlamat = edAlamatTukang.text.toString()
        var getjamkerja = edJamKerja.text.toString()


        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        }else if (getPhone.equals("") || getPhone.length == 0){
            showErrorMessage("telepon Belum diisi")
        }else if (getAlamat.equals("") || getAlamat.length == 0){
            showErrorMessage("alamat Belum diisi")
        }else if (getjamkerja.equals("") || getjamkerja.length == 0){
            showErrorMessage("Jam Kerja Belum diisi")
        }else if (lat == 0.0){
            showErrorMessage("Lokasi belum dpilih")
        }
        else if (fileUri == null){
            showErrorMessage("anda belum memilih foto")
        }
        else {
            tukang = Tukang(
                getName,
                getPhone,
                "",
                getAlamat,
                getjamkerja,
                lat.toString(),
                lon.toString(),
                selectedIdKategori,
                "",
                0,
                0,
                1
            )
            uploadFoto()
        }
    }

    fun uploadFoto(){
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
                        saveData()
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

    fun saveData(){
        var resetLokasi = LatLng(0.0,0.0)
        SharedVariable.centerLatLon = resetLokasi

        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")

        tukangRef.document().set(tukang).addOnCompleteListener {
                task ->
            if (task.isSuccessful){

                dismissLoading()
                showLongSuccessMessage("Tambah Tukang Berhasil")
                onBackPressed()
                finish()
            }else{
                dismissLoading()
                showLongErrorMessage("Error pendaftaran, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }

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
                textSelectImageTukang.visibility = View.GONE
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

    fun getCompleteAddress(latitude : Double,longitude : Double) : String{
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address: String = addresses[0]
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city: String = addresses[0].getLocality()
        val state: String = addresses[0].getAdminArea()
        val country: String = addresses[0].getCountryName()
        val postalCode: String = addresses[0].getPostalCode()
        val knownName: String =
            addresses[0].getFeatureName() // Only if available else return NULL

        return address
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