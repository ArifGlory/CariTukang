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
import com.tapisdev.caritukang.activity.ShowTukangActivity
import com.tapisdev.caritukang.adapter.AdapterLayanan
import com.tapisdev.caritukang.adapter.AdapterLayananToTukang
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.LayananKategori
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_add_layanan_to_tukang.*
import kotlinx.android.synthetic.main.activity_add_layanan_to_tukang.animation_view_layanan
import kotlinx.android.synthetic.main.activity_add_layanan_to_tukang.rvLayanan
import java.io.Serializable
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
    var arrLayanan = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_layanan_to_tukang)
        mUserPref = UserPreference(this)
        i = intent
        tukang = i.getSerializableExtra("tukang") as Tukang
        if (tukang.layanan_tukang != null && !tukang.layanan_tukang.equals("")){

            var listLayananTukang : List<String>
            listLayananTukang = tukang.layanan_tukang.split(",")
            Log.d(TAG_LAYANAN_TUKANG," isi : "+listLayananTukang)

            for (i in 0 until listLayananTukang.size){
                arrLayanan.add(listLayananTukang.get(i))
            }

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

            //convert the arraylist to string, and move the brackets
            var layanan_tukang = arrLayanan.toString()
            layanan_tukang = layanan_tukang.drop(1)
            layanan_tukang = layanan_tukang.dropLast(1)
            //layanan_tukang = layanan_tukang.trim()
            layanan_tukang = layanan_tukang.filter { !it.isWhitespace() }
            Log.d(TAG_LAYANAN_TUKANG," before save : "+layanan_tukang)

            updateLayananTukang(layanan_tukang)
        }

        getDataLayanan()
    }

    fun updateLayananTukang(layanan_tukang : String){
        showLoading(this)
        tukangRef.document(tukang.id_tukang).update("layanan_tukang",layanan_tukang).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah Data Berhasil")
                val i = Intent(this,ListTukangActivity::class.java)
                i.putExtra("tukang",tukang as Serializable)
                startActivity(i)
                finish()
            }else{
                showErrorMessage("Terjadi kesalahan, coba lagi nanti")
            }
        }
    }

    fun fillArrayLayanan(id_layanan : String){
        arrLayanan.add(id_layanan)

        showIsiArrayLayanan()
    }

    fun removeFromArrayLayanan(id_layanan: String){
        arrLayanan.remove(id_layanan)
        arrLayanan.sort()

        showIsiArrayLayanan()
    }

    fun showIsiArrayLayanan(){
        val tsLong = System.currentTimeMillis() / 1000

        Log.d(TAG_LAYANAN_TUKANG,""+tsLong+" isi arraylist "+arrLayanan.toString())
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
                    if (arrLayanan.contains(layanan.id_layanan)){
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