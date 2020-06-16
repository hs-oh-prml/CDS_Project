package com.cds_project_client.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import com.cds_project_client.R
import com.cds_project_client.mApplication
import com.cds_project_client.util.CMClient
import com.cds_project_client.util.CMClientEventHandler
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    lateinit var cmClient:CMClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        init()
    }
    private fun init(){
        cmClient = (application as mApplication).cmClient
        var listener = object: CMClientEventHandler.cmRegisterListener{
            override fun registerUser(ret: Int) {
//                TODO("Not yet implemented")
                if(ret == 1){
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Register Success", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Register Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        cmClient.cmEventHandler.rListener = listener
        submit.setOnClickListener {

            var user_id = sign_up_id.text.toString()
            var user_pw = sign_up_pw.text.toString()
            var user_pw_confirm = sign_up_pw_confirm.text.toString()

            if(user_id.length < 6){
                Toast.makeText(this, "ID must be over 6 lengths letters", Toast.LENGTH_SHORT).show()
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
                cmClient.cmClientStub.registerUser(user_id, user_pw)
//                finish()
            }

        }
    }

}
