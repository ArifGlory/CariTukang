package com.tapisdev.caritukang.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.Query
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.adapter.AdapterLayananToTukang
import com.tapisdev.caritukang.adapter.AdapterTukang
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.SharedVariable
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.LayananKategori
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_list_layanan.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : BaseActivity() {

    var TAG_GET_TUKANG = "getTukang"
    var type = ""
    var keyword = ""
    var id_kategori = ""

    lateinit var i : Intent

    lateinit var adapter: AdapterTukang
    var listTukang = ArrayList<Tukang>()

    var TAG_GET_LAYANAN = "getLayanan"
    var TAG_LAYANAN_TUKANG = "layananDipilih"
    lateinit var adapterLayananToTukang: AdapterLayananToTukang
    var listLayanan = ArrayList<LayananKategori>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        mUserPref = UserPreference(this)
        i = intent
        adapterLayananToTukang = AdapterLayananToTukang(listLayanan)

        type = i.getStringExtra("type").toString()

        if (type.equals("search")){
            keyword = i.getStringExtra("keyword").toString()
            getDataTukangBySearch()
        }else if (type.equals("kategori")){
            id_kategori = i.getStringExtra("id_kategori").toString()
            getDataTukangByKategori()
        }

        adapter = AdapterTukang(listTukang)
        rvResult.setHasFixedSize(true)
        rvResult.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvResult.adapter = adapter

        ivBackTukang.setOnClickListener {
            onBackPressed()
        }
        btnFilter.setOnClickListener {
            SharedVariable.resetLayananDipilih()

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_filter_layanan)

            val btnTerapkanFilter = dialog.findViewById(R.id.btnTerapkanFilter) as Button
            val rvLayanan = dialog.findViewById(R.id.rvLayanan) as RecyclerView
            val animation_view_layanan = dialog.findViewById(R.id.animation_view_layanan) as LottieAnimationView

            rvLayanan.setHasFixedSize(true)
            rvLayanan.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
            rvLayanan.adapter = adapterLayananToTukang
            getDataLayanan(animation_view_layanan)

            btnTerapkanFilter.setOnClickListener {
                showIsiArrayLayanan()
                if (SharedVariable.arrLayananDipilih.size == 0){
                    showErrorMessage("anda belum memilih layanan")
                }else{
                    getDataTukangByLayanan()
                    dialog.dismiss()
                }
            }

            dialog.show()
        }
    }

    fun showIsiArrayLayanan(){
        val tsLong = System.currentTimeMillis() / 1000

        Log.d(TAG_LAYANAN_TUKANG,""+tsLong+" isi arraylist "+SharedVariable.arrLayananDipilih.toString())
    }

    fun getDataLayanan(animation_view_layanan: LottieAnimationView) {

        layananRef.whereEqualTo("active",1)
            .get().addOnSuccessListener { result ->
                listLayanan.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var layanan : LayananKategori = document.toObject(LayananKategori::class.java)
                    layanan.id_layanan = document.id
                    layanan.active = 0

                    listLayanan.add(layanan)

                }
                if (listLayanan.size == 0){
                    animation_view_layanan.setAnimation(R.raw.empty_box)
                    animation_view_layanan.playAnimation()
                    animation_view_layanan.loop(false)
                }else{
                    animation_view_layanan.visibility = View.INVISIBLE
                }
                adapterLayananToTukang.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_LAYANAN,"err : "+exception.message)
            }

    }


    fun getDataTukangByLayanan(){
        listTukang.clear()
        adapter.notifyDataSetChanged()

        tukangRef.whereEqualTo("active",1)
            .orderBy("jml_order",Query.Direction.DESCENDING)
            .get().addOnSuccessListener { result ->
                listTukang.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var tukang : Tukang = document.toObject(Tukang::class.java)
                    tukang.id_tukang = document.id

                    var listLayananTukang : List<String>
                    listLayananTukang = tukang.layanan_tukang.split(",")

                    for (i in 0 until listLayananTukang.size){
                        if (SharedVariable.arrLayananDipilih.contains(listLayananTukang.get(i))){
                           if (!listTukang.contains(tukang)){
                               listTukang.add(tukang)
                           }
                        }
                    }
                }
                Log.d(TAG_GET_TUKANG,"jml tukang : "+listTukang.size)
                if (listTukang.size == 0){
                    animation_view_result.setAnimation(R.raw.empty_box)
                    animation_view_result.playAnimation()
                    animation_view_result.loop(false)

                    animation_view_result.visibility = View.VISIBLE
                }else{
                    animation_view_result.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_TUKANG,"err : "+exception.message)
            }
    }

    fun getDataTukangBySearch(){
        Log.d("resAct","keyword "+keyword)

        tukangRef.whereEqualTo("active",1)
            .orderBy("jml_order",Query.Direction.DESCENDING)
            .get().addOnSuccessListener { result ->
                listTukang.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var tukang : Tukang = document.toObject(Tukang::class.java)
                    tukang.id_tukang = document.id

                    if (!keyword.equals("") || keyword != ""){
                        var nama_tukang = tukang.nama_tukang.lowercase()

                        keyword = keyword.lowercase()
                        if(nama_tukang.contains(keyword)){
                            listTukang.add(tukang)
                        }
                    }else{
                        listTukang.add(tukang)
                    }

                }
                if (listTukang.size == 0){
                    animation_view_result.setAnimation(R.raw.empty_box)
                    animation_view_result.playAnimation()
                    animation_view_result.loop(false)
                }else{
                    animation_view_result.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_TUKANG,"err : "+exception.message)
            }
    }

    fun getDataTukangByKategori(){
        Log.d("resAct","id kategori "+id_kategori)

        tukangRef.whereEqualTo("active",1)
            .orderBy("jml_order",Query.Direction.DESCENDING)
            .get().addOnSuccessListener { result ->
                listTukang.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var tukang : Tukang = document.toObject(Tukang::class.java)
                    tukang.id_tukang = document.id

                    if (!id_kategori.equals("") || id_kategori != ""){
                        var idKategTukang = tukang.id_kategori

                        if(id_kategori.equals(idKategTukang)){
                            listTukang.add(tukang)
                        }
                    }else{
                        listTukang.add(tukang)
                    }

                }
                if (listTukang.size == 0){
                    animation_view_result.setAnimation(R.raw.empty_box)
                    animation_view_result.playAnimation()
                    animation_view_result.loop(false)
                }else{
                    animation_view_result.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_TUKANG,"err : "+exception.message)
            }
    }
}