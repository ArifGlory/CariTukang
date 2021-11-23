package com.tapisdev.caritukang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.activity.admin.AddLayananToTukangActivity
import com.tapisdev.caritukang.adapter.AdapterLayanan
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.LayananKategori
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_layanan_tukang.*
import kotlinx.android.synthetic.main.activity_layanan_tukang.animation_view_layanan
import java.io.Serializable

class LayananTukangActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var tukang : Tukang
    var TAG_GET_LAYANAN = "getLayanan"

    lateinit var adapter: AdapterLayanan
    var listLayanan = ArrayList<LayananKategori>()
    var listLayananTukang = arrayOf<String>()
    var layananTukang = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_tukang)
        mUserPref = UserPreference(this)
        i = intent
        tukang = i.getSerializableExtra("tukang") as Tukang
        if (tukang.layanan_tukang != null && !tukang.layanan_tukang.equals("")){
            layananTukang = tukang.layanan_tukang
            listLayananTukang = layananTukang.split(",").toTypedArray()
        }

        adapter = AdapterLayanan(listLayanan)
        rvLayanan.setHasFixedSize(true)
        rvLayanan.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvLayanan.adapter = adapter

        ivBackLayananTukang.setOnClickListener {
            onBackPressed()
        }
        btnAddLayanan.setOnClickListener {
            val i = Intent(this,AddLayananToTukangActivity::class.java)
            i.putExtra("tukang",tukang as Serializable)
            startActivity(i)
        }

        updateUI()
        getDataLayanan()
    }

    fun updateUI(){
        if (mUserPref.getJenisUser().equals("admin")){
            btnAddLayanan.visibility = View.VISIBLE
        }else{
            btnAddLayanan.visibility = View.GONE
        }
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
                    if (listLayananTukang.contains(layanan.id_layanan)){
                        listLayanan.add(layanan)
                    }
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

    override fun onResume() {
        super.onResume()
        getDataLayanan()
    }
}