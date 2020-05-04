package com.cds_project_client.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import com.cds_project_client.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        init()
    }
    private fun init(){
        submit.setOnClickListener {

            var user_id = sign_up_id.text.toString()
            var user_pw = sign_up_pw.text.toString()
            var user_pw_confirm = sign_up_pw_confirm.text.toString()

            if(user_id.length < 6){
                return@setOnClickListener
            }
            ///////////////////////////////////////////////
            //               Validate ID                 //
            ///////////////////////////////////////////////

            if(user_pw != user_pw_confirm){
                is_same_pw.visibility = VISIBLE
                return@setOnClickListener
            } else {

                ///////////////////////////////////////////////
                //              Register User                //
                ///////////////////////////////////////////////

                finish()
            }

        }
    }

}
