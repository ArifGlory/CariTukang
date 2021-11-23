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
import com.stepstone.apprating.listener.RatingDialogListener
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
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import com.stepstone.apprating.AppRatingDialog
import com.tapisdev.caritukang.adapter.AdapterKategori
import com.tapisdev.mysteam.model.RatingTukang


class ShowTukangActivity : BaseActivity(),PermissionHelper.PermissionListener,RatingDialogListener {

    lateinit var i : Intent
    lateinit var tukang : Tukang
    lateinit var  permissionHelper : PermissionHelper
    var listKategori = ArrayList<Kategori>()
    var TAG_GET = "getKategori"
    var TAG_Rating = "rating"

    var TAG_GET_RATING = "getRating"
    var listRating = ArrayList<RatingTukang>()

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
        btnRating.setOnClickListener {
            showDialog()
        }
        btnLayanan.setOnClickListener {
            val i = Intent(this,LayananTukangActivity::class.java)
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
        getDataKategori()
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
        tvKeahlian.setText("Keahlian : "+tukang.keahlian)
        tvJasa.setText("Jasa : "+tukang.jasa)
        tvMulaiOperasi.setText("Mulai Operasi Sejak "+tukang.mulai_operasi)

        if (mUserPref.getJenisUser().equals("admin")){
            lineSetting.visibility = View.VISIBLE
        }else{
            lineSetting.visibility = View.GONE
        }

        getDataRating()
    }

    fun getDataRating(){
        pgRating.visibility = View.VISIBLE

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        ratingRef
            .get().addOnSuccessListener { result ->
                listRating.clear()
                pgRating.visibility = View.INVISIBLE
                var deviceID = getDeviceID()

                for (document in result){
                    var ratingTukang : RatingTukang = document.toObject(RatingTukang::class.java)
                    ratingTukang.id_rating = document.id

                    if (ratingTukang.id_tukang.equals(tukang.id_tukang)){
                        listRating.add(ratingTukang)
                    }
                    if (ratingTukang.id_tukang.equals(tukang.id_tukang) && ratingTukang.device_id.equals(deviceID)){
                        btnRating.visibility = View.GONE
                    }
                }
                if (listRating.size == 0){
                    tvRating.visibility = View.VISIBLE
                    tvRating.setText("Belum ada rating")
                }else{
                    var totalRating = 0
                    for (i in 0 until listRating.size){
                        var nilaiRating = listRating.get(i).nilai_rating
                        totalRating += nilaiRating
                    }
                    var AVG_RATING = totalRating / listRating.size
                    tvRating.visibility = View.VISIBLE
                    tvRating.setText(""+AVG_RATING)
                }

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET,"err : "+exception.message)
            }
    }

    override fun onPermissionCheckDone() {
        var number = tukang.phone_tukang
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }

    fun getDataKategori(){
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        kategoriRef
            .get().addOnSuccessListener { result ->
                listKategori.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var kategori : Kategori = document.toObject(Kategori::class.java)
                    kategori.id_kategori = document.id

                    if (kategori.id_kategori.equals(tukang.id_kategori)){
                        tvKategori.setText(""+kategori.nama_kategori)
                    }
                    listKategori.add(kategori)
                }


            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET,"err : "+exception.message)
            }
    }

    private fun showDialog() {
        AppRatingDialog.Builder()
            .setPositiveButtonText("Submit")
            .setNegativeButtonText("Cancel")
            .setNeutralButtonText("Later")
            .setNoteDescriptions(
                Arrays.asList(
                    "Very Bad",
                    "Not good",
                    "Quite ok",
                    "Very Good",
                    "Excellent !!!"
                )
            )
            .setDefaultRating(1)
            .setTitle("Beri Rating Tukang ini")
            .setDescription("Silahkan pilih nilai rating pada tukang")
            .setCommentInputEnabled(false)
            .setDefaultComment("This is pretty cool !")
            .setStarColor(R.color.orange_500)
            .setNoteDescriptionTextColor(R.color.about_text)
            .setTitleTextColor(R.color.about_title)
            .setDescriptionTextColor(R.color.contentTextColor)
            .setHint("Please write your comment here ...")
            .setHintTextColor(R.color.about_text)
            .setCommentTextColor(R.color.black)
            .setCommentBackgroundColor(R.color.colorPrimaryDark)
            .setWindowAnimation(R.style.MyDialogFadeAnimation)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .create(this)
            //.setTargetFragment(this, TAG_Rating) // only if listener is implemented by fragment
            .show()
    }

    fun saveRating(rate : Int){
        showLoading(this)

        var deviceID = getDeviceID()
        var rating = RatingTukang(
            tukang.id_tukang,
            tukang.nama_tukang,
            deviceID!!,
            rate,
            ""
        )

        ratingRef.document().set(rating).addOnCompleteListener {
                task ->
            if (task.isSuccessful){

                dismissLoading()
                showLongSuccessMessage("Tambah Rating Berhasil")

                updateUI()
            }else{
                dismissLoading()
                showLongErrorMessage("Error pendaftaran, coba lagi nanti ")
                Log.d(TAG_Rating,"err : "+task.exception)
            }
        }

    }


    override fun onNegativeButtonClicked() {

    }

    override fun onNeutralButtonClicked() {

    }

    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        saveRating(rate)
    }
}