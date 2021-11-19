package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RatingTukang(
    var id_tukang : String = "",
    var nama_tukang : String = "",
    var device_id : String = "",
    var nilai_rating : Int = 1,
    var id_rating : String = ""
) : Parcelable,java.io.Serializable