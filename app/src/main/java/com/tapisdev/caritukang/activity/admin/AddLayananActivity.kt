package com.tapisdev.caritukang.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.adapter.AdapterKategori
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.Kategori
import com.tapisdev.mysteam.model.LayananKategori
import kotlinx.android.synthetic.main.activity_add_layanan.*
import kotlinx.android.synthetic.main.activity_add_layanan.spKategori
import java.util.ArrayList

class AddLayananActivity : BaseActivity() {

    lateinit var layanan : LayananKategori
    var TAG_SIMPAN = "simpanLayanan"

    var TAG_GET_KATEGORI = "getKategori"
    var listKategori = ArrayList<Kategori>()
    var listNamaKategori = ArrayList<String>()
    lateinit var adapterSpinnerKategori : ArrayAdapter<String>
    var selectedIdKategori = ""
    var selectedNamaKategori = ""

    lateinit var adapter: AdapterKategori

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_layanan)
        mUserPref = UserPreference(this)


        btnSimpanLayanan.setOnClickListener {
            checkValidation()
        }

        adapter = AdapterKategori(listKategori)
        adapterSpinnerKategori = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, listNamaKategori)
        spKategori.adapter = adapterSpinnerKategori
        spKategori.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                var pos = position+1
                Log.d(TAG_GET_KATEGORI,"nama kategori : "+listNamaKategori.get(position))
                Log.d(TAG_GET_KATEGORI,"id kategori : "+listKategori.get(position).id_kategori)
                selectedIdKategori = listKategori.get(position).id_kategori
                selectedNamaKategori = listKategori.get(position).nama_kategori

                /*if (!namaKategori.equals("Pilih Kategori")){
                    val i = Intent(requireContext(), ResultActivity::class.java)
                    i.putExtra("jenis","provinsi")
                    i.putExtra("key",namaProvinsi)
                    i.putExtra("value",namaProvinsi)
                    startActivity(i)
                }*/

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        getDataKategori()
    }

    fun checkValidation(){
        var getName = edNamaLayanan.text.toString()


        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        }
        else {
            layanan = LayananKategori(
                getName,
                selectedNamaKategori,
                1,
                selectedIdKategori,
                ""
            )
            simpanData()
        }
    }

    fun simpanData(){
        showLoading(this)
        layananRef.document().set(layanan).addOnCompleteListener {
                task ->
            if (task.isSuccessful){

                dismissLoading()
                showLongSuccessMessage("Tambah layanan Berhasil")
                onBackPressed()
                finish()
            }else{
                dismissLoading()
                showLongErrorMessage("Error pendaftaran, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }

    fun getDataKategori(){
        showLoading(this)
        kategoriRef.whereEqualTo("active",1)
            .get().addOnSuccessListener { result ->
                dismissLoading()
                listKategori.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var kategori : Kategori = document.toObject(Kategori::class.java)
                    kategori.id_kategori = document.id
                    listKategori.add(kategori)

                }
                if (listKategori.size == 0){
                    showInfoMessage("Kategori kosong..")
                }
                adapter.notifyDataSetChanged()

                for (c in 0 until listKategori.size){
                    var kategori  = listKategori.get(c)
                    listNamaKategori.add(kategori.nama_kategori.toString())
                }
                adapterSpinnerKategori.notifyDataSetChanged()

                if (listNamaKategori.size != 0){
                    selectedIdKategori = listKategori.get(0).id_kategori
                    selectedNamaKategori = listKategori.get(0).nama_kategori
                }

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_KATEGORI,"err : "+exception.message)
            }
    }
}