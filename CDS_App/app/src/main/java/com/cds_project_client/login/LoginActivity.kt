package com.cds_project_client.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cds_project_client.MainActivity
import com.cds_project_client.R
import com.cds_project_client.mApplication
import com.cds_project_client.util.CMClient
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var cmClient: CMClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    private fun init(){
        var app = application as mApplication
        app.initCM(this)
        cmClient = app.cmClient

        login_btn.setOnClickListener {

            ///////////////////////////////////////////////
            //               Login Check                 //
            ///////////////////////////////////////////////

            val u_id = input_id.text.toString()
            val u_pw = input_pw.text.toString()

//            val bRequestResult = cmClient.cmClientStub.loginCM(u_id, u_pw)
//            if(bRequestResult) {
//                Toast.makeText(this, "successfully sent the login request.", Toast.LENGTH_SHORT)
//                    .show()
//                val intent = Intent(this, MainActivity::class.java)

            val loginAckEvent = cmClient.cmClientStub.syncLoginCM(u_id, u_pw)
//            Log.d("LOGIN_RESPONSE", loginAckEvent.toString())
            if(loginAckEvent != null) {
                if (loginAckEvent.isValidUser == 0) {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                } else if (loginAckEvent.isValidUser == -1) {
                    Toast.makeText(this, "Already Login", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("user_id", u_id)
                    intent.putExtra("user_pw", u_pw)
                    startActivity(intent)
                    finish()
                }
            }
//            else
//                Toast.makeText(this, "failed the login request!", Toast.LENGTH_SHORT).show()

//            val loginAckEvent = cmClient.cmClientStub.syncLoginCM(u_id, u_pw)
//            Log.d("LOGIN_RESPONSE", loginAckEvent.toString())
//            if(loginAckEvent != null){
//                if(loginAckEvent.isValidUser == 0){
//                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
//                } else if(loginAckEvent.isValidUser == -1){
//                    Toast.makeText(this, "Already Login", Toast.LENGTH_SHORT).show()
//                } else {
//                    val intent = Intent(this, MainActivity::class.java)
//                    intent.putExtra("user_id", u_id)
//                    intent.putExtra("user_pw", u_pw)
//                    startActivity(intent)
//                    finish()
//                }
//            }
//            if(bRequestResult){
//            } else{
//                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
//            }
        }
        sign_up_btn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
