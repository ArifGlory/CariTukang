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


class InfoFragment : BaseFragment() {




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_info, container, false)




        return root
    }



    companion object {
        fun newInstance(): InfoFragment{
            val fragment = InfoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onResume() {
        super.onResume()

    }

}
