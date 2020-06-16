package com.cds_project_client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.cds_project_client.data.ItemStreaming
import com.cds_project_client.login.LoginActivity
import com.cds_project_client.streaming.StreamerActivity
import com.cds_project_client.util.CMClient
import com.cds_project_client.util.CMClientEventHandler
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent

class MainActivity : AppCompatActivity() {

    lateinit var cmClient: CMClient

    lateinit var u_id:String
    lateinit var u_pw:String
    lateinit var adapter: StreamerListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCM()
        init()
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()

    }

    private fun init(){

        var sListener = object: CMClientEventHandler.cmSessListener{
            override fun sessionRefresh(sessionNums: ArrayList<String>) {
//                TODO("Not yet implemented")
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        }

        val layoutManager = LinearLayoutManager(this, VERTICAL, false)
        adapter = StreamerListAdapter(this, cmClient.cmSessions, cmClient)

        Log.d("SESSION_INFO", cmClient.cmSessions.toString())
//        adapter.notifyDataSetChanged()
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter

        streaming_btn.setOnClickListener {

            val due: CMDummyEvent = CMDummyEvent()
            due.dummyInfo = "STREAMINGSTART"+"#"+cmClient.cmClientStub.myself.name
            cmClient.cmClientStub.send(due, "SERVER");

            val intent = Intent(this, StreamerActivity::class.java)
            cmClient.cmClientStub.joinSession("session2")
//            cmClient.cmClientStub.cha
            startActivity(intent)
        }

        logout_btn.setOnClickListener {
            var bRequestResult = false
            println("====== logout from default server")
            bRequestResult = cmClient.cmClientStub.logoutCM()
            if (bRequestResult)
                println("successfully sent the logout request.")
            else
                System.err.println("failed the logout request!")
            println("======")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        cmClient.cmEventHandler.sListener = sListener
    }

    fun initCM(){
        cmClient = (application as mApplication).cmClient
        cmClient.cmClientStub.requestSessionInfo()
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