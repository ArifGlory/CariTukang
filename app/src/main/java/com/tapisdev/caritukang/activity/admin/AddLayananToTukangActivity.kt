package com.tapisdev.caritukang.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdsmdg.tastytoast.TastyToast
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.adapter.AdapterLayanan
import com.tapisdev.caritukang.adapter.AdapterLayananToTukang
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.LayananKategori
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_add_layanan_to_tukang.*
import kotlinx.android.synthetic.main.activity_add_layanan_to_tukang.animation_view_layanan
import kotlinx.android.synthetic.main.activity_add_layanan_to_tukang.rvLayanan
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class AddLayananToTukangActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var tukang : Tukang
    var TAG_GET_LAYANAN = "getLayanan"
    var TAG_LAYANAN_TUKANG = "layTukang"

    lateinit var adapter: AdapterLayananToTukang
    var listLayanan = ArrayList<LayananKategori>()
    lateinit var listLayananTukang : List<String>
    var layananTukang = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_layanan_to_tukang)
        mUserPref = UserPreference(this)
        i = intent
        tukang = i.getSerializableExtra("tukang") as Tukang
        if (tukang.layanan_tukang != null && !tukang.layanan_tukang.equals("")){
            layananTukang = tukang.layanan_tukang
            listLayananTukang = layananTukang.split(",")
            Log.d(TAG_LAYANAN_TUKANG," isi : "+listLayananTukang)

            showIsiArrayLayanan()
        }


        adapter = AdapterLayananToTukang(listLayanan)
        rvLayanan.setHasFixedSize(true)
        rvLayanan.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvLayanan.adapter = adapter


        ivBackAddLayananTukang.setOnClickListener {
            onBackPressed()
        }
        btnSimpanLayananTukang.setOnClickListener {
            showIsiArrayLayanan()


        }

        getDataLayanan()
    }

    fun fillArrayLayanan(id_layanan : String){
        showInfoMessage("isi list layanan "+listLayananTukang.size)

        showIsiArrayLayanan()
    }

    fun removeFromArrayLayanan(id_layanan: String){
        for (i in 0 until listLayananTukang.size){
            if (listLayananTukang.get(i).equals(id_layanan)){
                listLayananTukang.drop(i)
            }
        }

        showInfoMessage("isi list layanan "+listLayananTukang.size)

        showIsiArrayLayanan()
    }

    fun showIsiArrayLayanan(){
        val tsLong = System.currentTimeMillis() / 1000

        for (i in 0 until listLayananTukang.size){
            Log.d(TAG_LAYANAN_TUKANG,""+tsLong+" isi array ke $i "+listLayananTukang.get(i))
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
                        //buat checked checkbox nya
                        layanan.active = 1
                    }else{
                        layanan.active = 0
                    }

                    if (layanan.id_kategori.equals(tukang.id_kategori)){
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
}