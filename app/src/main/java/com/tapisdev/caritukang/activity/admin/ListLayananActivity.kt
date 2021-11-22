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
import com.tapisdev.caritukang.adapter.AdapterLayanan
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.Kategori
import com.tapisdev.mysteam.model.LayananKategori
import kotlinx.android.synthetic.main.activity_list_kategori.*
import kotlinx.android.synthetic.main.activity_list_layanan.*

class ListLayananActivity : BaseActivity() {


    var TAG_GET_LAYANAN = "getLayanan"

    lateinit var adapter: AdapterLayanan
    var listLayanan = ArrayList<LayananKategori>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_layanan)
        mUserPref = UserPreference(this)

        adapter = AdapterLayanan(listLayanan)
        rvLayanan.setHasFixedSize(true)
        rvLayanan.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvLayanan.adapter = adapter


        ivBackLayanan.setOnClickListener {
            onBackPressed()
        }
        btnCreateLayanan.setOnClickListener {
            val i = Intent(this,AddLayananActivity::class.java)
            startActivity(i)
        }

        getDataLayanan()
    }

    fun getDataLayanan(){

        layananRef.whereEqualTo("active",1)
            .get().addOnSuccessListener { result ->
                listLayanan.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var layanan : LayananKategori = document.toObject(LayananKategori::class.java)
                    layanan.id_layanan = document.id

                    listLayanan.add(layanan)

                }
                if (listLayanan.size == 0){
                    animation_view_layanan.setAnimation(R.raw.empty_box)
                    animation_view_layanan.playAnimation()
                    animation_view_layanan.loop(false)
                }else{
                    animation_view_layanan.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_LAYANAN,"err : "+exception.message)
            }

    }

    fun refreshData(){
        getDataLayanan()
    }

    override fun onResume() {
        super.onResume()
        getDataLayanan()
    }
}