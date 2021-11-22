package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LayananKategori(
    var nama_layanan : String = "",
    var nama_kategori : String = "",
    var active : Int = 1,
    var id_kategori : String = "",
    var id_layanan : String = ""
) : Parcelable,java.io.Serializable