package com.tapisdev.caritukang.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.Query
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.adapter.AdapterKategori
import com.tapisdev.cateringtenda.base.BaseFragment
import com.tapisdev.mysteam.model.Kategori
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment() {


    lateinit var animation_view_puskes : LottieAnimationView
    lateinit var rv_kategori : RecyclerView
    lateinit var btn_view_all : Button
    lateinit var btn_submit_search : Button
    lateinit var edt_search : EditText

    var TAG_GET = "getKategori"
    lateinit var adapter: AdapterKategori
    var listKategori = ArrayList<Kategori>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        animation_view_puskes = root.findViewById(R.id.animation_view_puskes)
        rv_kategori = root.findViewById(R.id.rv_kategori)
        btn_view_all = root.findViewById(R.id.btn_view_all)
        btn_submit_search = root.findViewById(R.id.btn_submit_search)
        edt_search = root.findViewById(R.id.edt_search)

        adapter = AdapterKategori(listKategori)
        rv_kategori.setHasFixedSize(true)
        rv_kategori.layoutManager = LinearLayoutManager(requireContext()) as RecyclerView.LayoutManager?
        rv_kategori.adapter = adapter

        btn_view_all.setOnClickListener {
            /*val i = Intent(requireActivity(), ListPuskesmasActivity::class.java)
            startActivity(i)*/
        }
        btn_submit_search.setOnClickListener {
            var keyword = edt_search.text.toString()
            if (keyword.equals("") || keyword.length == 0){
                showErrorMessage("Anda belum menuliskan kata kunci pencarian")
            }else{
               /* val i = Intent(requireActivity(), ListPuskesmasActivity::class.java)
                i.putExtra("keyword",keyword)
                startActivity(i)*/
            }
        }

        getDataKategori()
        return root
    }

    fun getDataKategori(){
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
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
                    animation_view_puskes.setAnimation(R.raw.empty_box)
                    animation_view_puskes.playAnimation()
                    animation_view_puskes.loop(false)
                }else{
                    animation_view_puskes.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET,"err : "+exception.message)
            }
    }


    companion object {
        fun newInstance(): HomeFragment{
            val fragment = HomeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onResume() {
        super.onResume()
        getDataKategori()
    }

}
