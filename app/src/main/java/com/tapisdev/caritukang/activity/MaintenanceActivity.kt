package com.tapisdev.caritukang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.caritukang.MainActivity
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.base.BaseActivity

class MaintenanceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
    }
}