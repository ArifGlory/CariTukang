package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Tukang(
    var nama_tukang : String = "",
    var phone_tukang : String = "",
    var foto_tukang : String = "",
    var alamat_tukang : String = "",
    var jam_kerja : String = "",
    var lat : String = "",
    var lon : String = "",
    var id_kategori : String = "",
    var id_tukang : String = "",
    var rating : Int = 0,
    var jml_order : Int = 0,
    var active : Int = 0,
    var keahlian : String = "",
    var jasa : String = "",
    var mulai_operasi : String = ""
) : Parcelable,java.io.Serializable