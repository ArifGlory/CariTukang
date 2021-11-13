package com.tapisdev.caritukang.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.adapter.AdapterKategori
import com.tapisdev.caritukang.adapter.AdapterTukang
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.Kategori
import com.tapisdev.mysteam.model.Tukang
import kotlinx.android.synthetic.main.activity_list_kategori.*
import kotlinx.android.synthetic.main.activity_list_tukang.*

class ListTukangActivity : BaseActivity() {

    var TAG_GET_TUKANG = "getTukang"

    lateinit var adapter: AdapterTukang
    var listTukang = ArrayList<Tukang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_tukang)
        mUserPref = UserPreference(this)
        adapter = AdapterTukang(listTukang)
        rvListTukang.setHasFixedSize(true)
        rvListTukang.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvListTukang.adapter = adapter


        btnCreateTukang.setOnClickListener {

        }
        ivBackTukang.setOnClickListener {
            onBackPressed()
        }

        updateUI()
        getDataTukang()
    }

    fun updateUI(){
        if (!mUserPref.getJenisUser().equals("admin")){
            btnCreateTukang.visibility = View.INVISIBLE
        }
    }

    fun getDataTukang(){

        tukangRef
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
                    animation_view_tukang.setAnimation(R.raw.empty_box)
                    animation_view_tukang.playAnimation()
                    animation_view_tukang.loop(false)
                }else{
                    animation_view_tukang.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_TUKANG,"err : "+exception.message)
            }

    }

    fun refreshData(){
        getDataTukang()
    }

    override fun onResume() {
        super.onResume()
        getDataTukang()
    }
}