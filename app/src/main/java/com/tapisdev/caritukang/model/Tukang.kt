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
    var id_tukang : String = ""
) : Parcelable,java.io.Serializable