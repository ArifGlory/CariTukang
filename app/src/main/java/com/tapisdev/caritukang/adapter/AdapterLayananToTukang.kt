package com.tapisdev.caritukang.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.GLDebugHelper
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sdsmdg.tastytoast.TastyToast
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.activity.ResultActivity
import com.tapisdev.caritukang.activity.admin.AddLayananToTukangActivity
import com.tapisdev.caritukang.activity.admin.EditKategoriActivity
import com.tapisdev.caritukang.activity.admin.ListKategoriActivity
import com.tapisdev.caritukang.activity.admin.ListLayananActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.Kategori
import com.tapisdev.mysteam.model.LayananKategori
import com.tapisdev.mysteam.model.UserModel
import kotlinx.android.synthetic.main.row_kategori.view.*
import kotlinx.android.synthetic.main.row_layanan.view.*
import kotlinx.android.synthetic.main.row_layanan.view.lineLayanan
import kotlinx.android.synthetic.main.row_layanan.view.tvNamaKategori
import kotlinx.android.synthetic.main.row_layanan.view.tvNamaLayanan
import kotlinx.android.synthetic.main.row_layanan_to_tukang.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AdapterLayananToTukang(private val list:ArrayList<LayananKategori>) : RecyclerView.Adapter<AdapterLayananToTukang.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_layanan_to_tukang,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val myDB = FirebaseFirestore.getInstance()
    lateinit var pDialogLoading : SweetAlertDialog
    val layananRef = myDB.collection("layanan")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        mUserPref = UserPreference(holder.view.lineLayanan.context)

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat
        pDialogLoading = SweetAlertDialog(holder.view.lineLayanan.context, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)


        holder.view.tvNamaKategori.text = list?.get(position)?.nama_kategori
        holder.view.tvNamaLayanan.text = list?.get(position)?.nama_layanan


        holder.view.lineLayanan.setOnClickListener {

        }

        if (list?.get(position).active == 1){
            holder.view.cbLayanan.isChecked = true
        }else{
            holder.view.cbLayanan.isChecked = false
        }

        holder.view.cbLayanan.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked){
                if (holder.view.lineLayanan.context is AddLayananToTukangActivity){
                    (holder.view.lineLayanan.context as AddLayananToTukangActivity).fillArrayLayanan(list?.get(position)?.id_layanan)
                }
            }else{
                if (holder.view.lineLayanan.context is AddLayananToTukangActivity){
                    (holder.view.lineLayanan.context as AddLayananToTukangActivity).removeFromArrayLayanan(list?.get(position)?.id_layanan)
                }
            }
        }


    }

    fun convertDate(tanggal : String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd")
        //val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val output = formatter.format(parser.parse(tanggal))

        return output
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}