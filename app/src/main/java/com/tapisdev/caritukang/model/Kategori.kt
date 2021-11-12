package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Kategori(
    var nama_kategori : String = "",
    var foto_kategori : String = "",
    var active : Int = 1,
    var id_kategori : String = ""
) : Parcelable,java.io.Serializable