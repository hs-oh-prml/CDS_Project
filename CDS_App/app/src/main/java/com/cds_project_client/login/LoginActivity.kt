package com.cds_project_client.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cds_project_client.MainActivity
import com.cds_project_client.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init(){
        login_btn.setOnClickListener {

            ///////////////////////////////////////////////
            //               Login Check                 //
            ///////////////////////////////////////////////

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        sign_up_btn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

}
