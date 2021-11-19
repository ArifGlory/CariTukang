package com.tapisdev.caritukang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.adapter.AdapterTukang
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : BaseActivity() {

    var TAG_GET_TUKANG = "getTukang"
    var type = ""
    var keyword = ""
    var id_kategori = ""

    lateinit var i : Intent

    lateinit var adapter: AdapterTukang
    var listTukang = ArrayList<Tukang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        mUserPref = UserPreference(this)
        i = intent

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
    }

    fun getDataTukangBySearch(){
        Log.d("resAct","keyword "+keyword)

        tukangRef.whereEqualTo("active",1)
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