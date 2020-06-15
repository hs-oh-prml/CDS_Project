package com.cds_project_client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.cds_project_client.data.ItemStreaming
import com.cds_project_client.streaming.StreamerActivity
import com.cds_project_client.util.CMClient
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub

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
        for(i in 0..cmClient.cmSessions.size-1){
            if(cmClient.cmSessions[i].streamer_id != "") itemList.add(cmClient.cmSessions[i])
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

            val intent = Intent(this, StreamerActivity::class.java)
            startActivity(intent)
        }
    }

    fun initCM(){
        cmClient = (application as mApplication).cmClient
        Log.d("CM_INFO", cmClient.cmClientStub.myself.currentSession)
        Log.d("CM_INFO", cmClient.cmClientStub.myself.currentGroup)
        Log.d("CM_INFO", cmClient.cmClientStub.myself.name)

//        = CMClient(this)
//        u_id = intent.getStringExtra("user_id")!!
//        u_pw = intent.getStringExtra("user_pw")!!
//        cmClient.initCM()
//        var loginAckEvent = cmClient.cmClientStub.syncLoginCM(u_id, u_pw)
//        if(loginAckEvent != null){
//            if(loginAckEvent.isValidUser == 0){
//                Log.d("Sync Login", "Login Failed")
//            } else if(loginAckEvent.isValidUser == -1){
//                Log.d("Sync Login", "Already Login")
//            } else {
//                Log.d("Sync Login", "Login Succeed")
//            }
//        }
//        val ret = cmClient.cmClientStub.syncJoinSession("session1")
//        if(ret != null){
//            Log.d("CM Response", "(${ret.groupNum}) Groups")
//        } else {
//            Log.d("CM Response", "Failed The Session-Join Request!")
//        }
    }

}
