package com.cds_project_client.login

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cds_project_client.MainActivity
import com.cds_project_client.R
import com.cds_project_client.util.CMClient
import com.cds_project_client.util.CMClientEventHandler
import kotlinx.android.synthetic.main.activity_login.*
import kr.ac.konkuk.ccslab.cm.manager.CMConfigurator
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


class LoginActivity : AppCompatActivity() {

    lateinit var cmClient: CMClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    private fun init(){
        cmClient = CMClient(this)

            login_btn.setOnClickListener {

            ///////////////////////////////////////////////
            //               Login Check                 //
            ///////////////////////////////////////////////

            val u_id = input_id.text.toString()
            val u_pw = input_pw.text.toString()

            val loginAckEvent = cmClient.cmClientStub.syncLoginCM(u_id, u_pw)
//            val bRequestResult = cmClientStub.loginCM(u_id, u_pw)
            Log.d("LOGIN_RESPONSE", loginAckEvent.toString())
            if(loginAckEvent != null){
                if(loginAckEvent.isValidUser == 0){
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                } else if(loginAckEvent.isValidUser == -1){
                    Toast.makeText(this, "Already Login", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("user_id", u_id)
                    intent.putExtra("user_pw", u_pw)
                    startActivity(intent)
                    finish()
                }
            }
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
