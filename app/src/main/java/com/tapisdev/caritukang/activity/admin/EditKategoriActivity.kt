package com.tapisdev.caritukang.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.caritukang.util.PermissionHelper
import com.tapisdev.mysteam.model.Kategori
import kotlinx.android.synthetic.main.activity_add_kategori.*
import kotlinx.android.synthetic.main.activity_add_kategori.imageKategori
import kotlinx.android.synthetic.main.activity_add_kategori.textSelectImage
import kotlinx.android.synthetic.main.activity_edit_kategori.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class EditKategoriActivity : BaseActivity(),PermissionHelper.PermissionListener {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    lateinit var kategori : Kategori
    var TAG_UPDATE = "ubahKategori"
    lateinit var i : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_kategori)
        mUserPref = UserPreference(this)
        storageReference = FirebaseStorage.getInstance().reference.child("images")
        i = intent
        kategori = i.getSerializableExtra("kategori") as Kategori

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)


        ivBackEditKategori.setOnClickListener {
            onBackPressed()
        }
        rlImageUpdate.setOnClickListener {
            launchGallery()
        }
        btnUpdateKategori.setOnClickListener {
            checkValidation()
        }

        updateUI()
    }

    fun updateUI(){
        edNamaKategoriUpdate.setText(kategori.nama_kategori)
        Glide.with(this)
            .load(kategori.foto_kategori)
            .into(imageKategoriUpdate)
    }

    fun checkValidation(){
        var getName = edNamaKategoriUpdate.text.toString()


        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        }else if (fileUri == null){
            kategori.nama_kategori = getName
            updateDataOnly()
        }
        else {
            kategori.nama_kategori = getName
            uploadFoto()
        }
    }

    fun updateDataOnly(){
        showLoading(this)
        kategoriRef.document(kategori.id_kategori).update("nama_kategori",kategori.nama_kategori).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah Data Berhasil")
                val i = Intent(this,ListKategoriActivity::class.java)
                startActivity(i)
                finish()
            }else{
                showErrorMessage("Terjadi kesalahan, coba lagi nanti")
            }
        }
    }


    fun uploadFoto(){
        showLoading(this)

        if (fileUri != null){
            Log.d(TAG_UPDATE,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_UPDATE, exception.toString())
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
                        Log.d(TAG_UPDATE,"download URL : "+ downloadUri.toString())// This is the one you should store
                        kategori.foto_kategori = url

                        kategoriRef.document(kategori.id_kategori).update("foto_kategori",url)
                        kategoriRef.document(kategori.id_kategori).update("nama_kategori",kategori.nama_kategori).addOnCompleteListener { task ->
                            dismissLoading()
                            if (task.isSuccessful){
                                showSuccessMessage("Ubah Data Berhasil")
                                val i = Intent(this,ListKategoriActivity::class.java)
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
                imageKategoriUpdate.setImageBitmap(bitmap)
                textSelectImage.visibility = View.GONE
            } catch (e: IOException) {
                e.printStackTrace()
            }
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