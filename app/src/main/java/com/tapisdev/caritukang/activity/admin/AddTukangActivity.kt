package com.tapisdev.caritukang.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_tukang.*

class AddTukangActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tukang)
        mUserPref = UserPreference(this)


        ivBackCreateTukang.setOnClickListener {
            onBackPressed()
        }
    }
}