package com.tapisdev.caritukang.model

import com.google.android.gms.maps.model.LatLng
import com.tapisdev.mysteam.model.UserModel

class SharedVariable {

    companion object {
        lateinit var user : UserModel
        var centerLatLon : LatLng = LatLng(0.0,0.0)
        var arrLayananDipilih = ArrayList<String>()
        var isFilterJarakTerdekat = false

        open fun resetLayananDipilih(){
            arrLayananDipilih.clear()
        }
    }


}