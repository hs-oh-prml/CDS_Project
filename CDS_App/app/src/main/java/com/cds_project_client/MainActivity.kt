package com.cds_project_client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.cds_project_client.data.ItemStreaming
import com.cds_project_client.streaming.StreamingActivity
import com.cds_project_client.util.CMClient
import com.cds_project_client.util.CMClientEventHandler
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.konkuk.ccslab.cm.manager.CMConfigurator
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class MainActivity : AppCompatActivity() {

    lateinit var itemList: ArrayList<ItemStreaming>
    lateinit var cmClient: CMClient
    lateinit var cmClientStub: CMClientStub

    lateinit var u_id:String
    lateinit var u_pw:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCM()
        init()
    }

    fun dataInit(){
        // Dummy Data
        for(i in 0..10){
            itemList.add(ItemStreaming("${i}-Streamer", "${0/8}"))
        }
        
        ///////////////////////////////////////////////
        //           Get List from Server            //
        ///////////////////////////////////////////////

    }

    private fun init(){
        cmClient = CMClient(this)
        cmClientStub = cmClient.cmClientStub
        itemList = ArrayList()
        dataInit()
        val layoutManager = LinearLayoutManager(this, VERTICAL, false)
        val adapter = StreamerListAdapter(this, itemList, cmClient)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter

        streaming_btn.setOnClickListener {

            val intent = Intent(this, StreamingActivity::class.java)
            intent.putExtra("streaming_mode", 1)
//            cmClientStub.
            startActivity(intent)
        }
    }

    fun initCM(){
        cmClient = CMClient(this)
        u_id = intent.getStringExtra("user_id")!!
        u_pw = intent.getStringExtra("user_pw")!!
        Log.d("Sync Join", u_id)
        cmClientStub = cmClient.cmClientStub
        val loginAckEvent = cmClientStub.syncLoginCM(u_id, u_pw)
        if(loginAckEvent != null){
            Log.d("LOGIN_RESPONSE", loginAckEvent.toString())
            Log.d("LOGIN_RESPONSE", loginAckEvent.isValidUser.toString())
            if(loginAckEvent.isValidUser == 0){
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            } else if(loginAckEvent.isValidUser == -1){
                Toast.makeText(this, "Already Login", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Success Login", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
