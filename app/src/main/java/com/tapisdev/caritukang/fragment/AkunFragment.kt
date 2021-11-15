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
import com.tapisdev.caritukang.MainActivity
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.activity.RegisterTukangActivity
import com.tapisdev.caritukang.adapter.AdapterKategori
import com.tapisdev.cateringtenda.base.BaseFragment
import com.tapisdev.mysteam.model.Kategori
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AkunFragment : BaseFragment() {


    lateinit var animation_view_puskes : LottieAnimationView

    lateinit var lineRegisterTukang : LinearLayout
    lateinit var btnLoginAdmin : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_akun, container, false)

        lineRegisterTukang = root.findViewById(R.id.lineRegisterTukang)
        btnLoginAdmin = root.findViewById(R.id.btnLoginAdmin)


        btnLoginAdmin.setOnClickListener {
            val i = Intent(activity,MainActivity::class.java)
            startActivity(i)
        }
        lineRegisterTukang.setOnClickListener {
            val i = Intent(activity,RegisterTukangActivity::class.java)
            startActivity(i)
        }


        return root
    }



    companion object {
        fun newInstance(): AkunFragment{
            val fragment = AkunFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onResume() {
        super.onResume()

    }

}
