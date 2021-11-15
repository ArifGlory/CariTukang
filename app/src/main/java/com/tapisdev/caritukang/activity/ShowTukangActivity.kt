package com.tapisdev.caritukang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.caritukang.R
import com.tapisdev.caritukang.base.BaseActivity
import com.tapisdev.caritukang.model.UserPreference

class ShowTukangActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_tukang)
        mUserPref = UserPreference(this)
    }
}