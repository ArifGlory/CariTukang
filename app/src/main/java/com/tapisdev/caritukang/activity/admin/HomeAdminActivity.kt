package com.tapisdev.caritukang.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.activity.HomePenggunaActivity
import com.tapisdev.caritukang.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home_admin.*

class HomeAdminActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)

        lineProfil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineKategoriAdmin.setOnClickListener {
            startActivity(Intent(this, ListKategoriActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineTukangAdmin.setOnClickListener {
            startActivity(Intent(this, ListTukangActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

    }
}