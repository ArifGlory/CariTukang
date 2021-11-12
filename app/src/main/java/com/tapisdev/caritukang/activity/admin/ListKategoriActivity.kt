package com.tapisdev.caritukang.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.adapter.AdapterKategori
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.Kategori
import kotlinx.android.synthetic.main.activity_list_kategori.*

class ListKategoriActivity : BaseActivity() {

    var TAG_GET_KATEGORI = "getKategori"
    var TAG_SIMPAN = "simpanKategori"

    lateinit var adapter: AdapterKategori
    var listKategori = ArrayList<Kategori>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_kategori)
        mUserPref = UserPreference(this)

        adapter = AdapterKategori(listKategori)
        rvKategori.setHasFixedSize(true)
        rvKategori.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvKategori.adapter = adapter

        ivBackKategori.setOnClickListener {
            onBackPressed()
        }
        btnCreateKategori.setOnClickListener {
            val i = Intent(this,AddKategoriActivity::class.java)
            startActivity(i)
        }

        updateUI()
        getDataKategori()
    }

    fun updateUI(){
        if (!mUserPref.getJenisUser().equals("admin")){
            btnCreateKategori.visibility = View.INVISIBLE
        }
    }

    fun getDataKategori(){

        kategoriRef.whereEqualTo("active",1)
            .get().addOnSuccessListener { result ->
                listKategori.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var kategori : Kategori = document.toObject(Kategori::class.java)
                    kategori.id_kategori = document.id

                    listKategori.add(kategori)

                }
                if (listKategori.size == 0){
                    animation_view_kategori.setAnimation(R.raw.empty_box)
                    animation_view_kategori.playAnimation()
                    animation_view_kategori.loop(false)
                }else{
                    animation_view_kategori.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_KATEGORI,"err : "+exception.message)
            }

    }

    override fun onResume() {
        super.onResume()
        getDataKategori()
    }
}