package com.tapisdev.caritukang.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.activity.HomePenggunaActivity
import com.tapisdev.caritukang.adapter.AdapterTukang
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_home_admin.*
import kotlinx.android.synthetic.main.activity_list_tukang.*

class HomeAdminActivity : BaseActivity() {

    var TAG_GET_TUKANG = "getTukang"

    lateinit var adapter: AdapterTukang
    var listTukang = ArrayList<Tukang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)
        mUserPref = UserPreference(this)
        adapter = AdapterTukang(listTukang)
        rvTukang.setHasFixedSize(true)
        rvTukang.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvTukang.adapter = adapter

        lineProfil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineKategoriAdmin.setOnClickListener {
            startActivity(Intent(this, ListKategoriActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineTukangAdmin.setOnClickListener {
            startActivity(Intent(this, ListTukangActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }


        getDataTukang()
    }

    fun getDataTukang(){

        tukangRef.limit(5)
            .get().addOnSuccessListener { result ->
                listTukang.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var tukang : Tukang = document.toObject(Tukang::class.java)
                    tukang.id_tukang = document.id

                    listTukang.add(tukang)

                }
                if (listTukang.size == 0){
                    av_tukang.setAnimation(R.raw.empty_box)
                    av_tukang.playAnimation()
                    av_tukang.loop(false)
                }else{
                    av_tukang.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_TUKANG,"err : "+exception.message)
            }

    }
}