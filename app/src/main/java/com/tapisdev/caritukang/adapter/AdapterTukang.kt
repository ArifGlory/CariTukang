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
import com.tapisdev.caritukang.activity.admin.EditKategoriActivity
import com.tapisdev.caritukang.activity.admin.ListKategoriActivity
import com.tapisdev.caritukang.activity.admin.ListTukangActivity
import com.tapisdev.caritukang.model.UserPreference
import com.tapisdev.mysteam.model.Kategori
import com.tapisdev.mysteam.model.Tukang
import com.tapisdev.mysteam.model.UserModel
import kotlinx.android.synthetic.main.row_kategori.view.*
import kotlinx.android.synthetic.main.row_tukang.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AdapterTukang(private val list:ArrayList<Tukang>) : RecyclerView.Adapter<AdapterTukang.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_tukang,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val myDB = FirebaseFirestore.getInstance()
    lateinit var pDialogLoading : SweetAlertDialog
    val tukangRef = myDB.collection("tukang")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        mUserPref = UserPreference(holder.view.lineTukang.context)

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat
        pDialogLoading = SweetAlertDialog(holder.view.lineTukang.context, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)


        holder.view.tvNamaTukang.text = list?.get(position)?.nama_tukang
        holder.view.tvAlamatTukang.text = list?.get(position)?.alamat_tukang

        if (list?.get(position)?.foto_tukang.equals("")){
            holder.view.ivFotoTukang.setImageResource(R.drawable.ic_placeholder)
        }else {
            Glide.with(holder.view.ivFotoTukang.context)
                .load(list?.get(position)?.foto_tukang)
                .into(holder.view.ivFotoTukang)
        }

        holder.view.lineTukang.setOnClickListener {
            /*val i = Intent(holder.view.lineTukang.context, DetailServiceActivity::class.java)
            i.putExtra("kategori",list.get(position) as Serializable)
            holder.view.lineTukang.context.startActivity(i)*/
        }
        holder.view.lineTukang.setOnLongClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())

            if (mUserPref.getJenisUser().equals("admin")){
                SweetAlertDialog(holder.view.lineTukang.context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Edit / Hapus")
                    .setContentText("Anda dapat mengubah / menghapus tukang ini")
                    .setConfirmText("Ubah")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()

                        /*val i = Intent(holder.view.lineTukang.context, EditKategoriActivity::class.java)
                        i.putExtra("kategori",list.get(position) as Serializable)
                        holder.view.lineTukang.context.startActivity(i)*/

                    }
                    .setCancelButton(
                        "Hapus"
                    ) {
                            sDialog -> sDialog.dismissWithAnimation()
                        pDialogLoading.show()

                        tukangRef.document(list?.get(position)?.id_tukang).delete().addOnCompleteListener { task ->
                           pDialogLoading.dismiss()
                           if (task.isSuccessful){
                               TastyToast.makeText(
                                   holder.view.lineTukang.context,
                                   "Data tukang dihapus",
                                   TastyToast.LENGTH_SHORT,
                                   TastyToast.SUCCESS
                               )
                               if (holder.view.lineTukang.context is ListTukangActivity){
                                   (holder.view.lineTukang.context as ListTukangActivity).refreshData()
                               }
                           }else{
                               TastyToast.makeText(
                                   holder.view.lineTukang.context,
                                   "Terjadi kesalahan, coba lagi nanti",
                                   TastyToast.LENGTH_SHORT,
                                   TastyToast.ERROR
                               )
                               Log.d("ubahKategori","err : "+task.exception)
                           }
                       }
                    }
                    .show()
            }



            true
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