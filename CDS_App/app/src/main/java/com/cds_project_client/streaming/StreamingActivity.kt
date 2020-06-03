package com.cds_project_client.streaming

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.cds_project_client.R
import com.cds_project_client.data.ItemChatting
import com.cds_project_client.util.CMClient
import kotlinx.android.synthetic.main.activity_streaming.*

class StreamingActivity : AppCompatActivity() {

    var is_full = false
    var streaming_mode = 0
    lateinit var cmClient:CMClient
    lateinit var chat_list: ArrayList<ItemChatting>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming)
        streaming_mode = intent.getIntExtra("streaming_mode", 0)
        init()
    }

    fun getData(){

        // Test Data
        for(i in 0..10){
            chat_list.add(ItemChatting("user${i}", "message${i}"))
        }
        ///////////////////////////////////////////////
        //      Get Chatting List from Server        //
        ///////////////////////////////////////////////

    }

    fun setFullScreen(is_full:Boolean){
//        var params = LayoutPa

    }

    private fun init(){
        cmClient = CMClient(this)

        chat_list = ArrayList()
        getData()

        // Chatting Init
        var adapter = ChattingAdapter(this, chat_list)
        var layoutManager = LinearLayoutManager(this, VERTICAL, false)
        chatting.layoutManager = layoutManager
        chatting.adapter = adapter

        // Send Message
        send_message.setOnClickListener {
            var strTarget = "/g"
            var strMsg = input_chatting.text.toString()
            cmClient.cmClientStub.chat(strTarget, strMsg)
            input_chatting.text.clear()
        }

        if(streaming_mode == 0){
            getVideo()
        } else {
            startStreaming()
        }
    }
    fun startStreaming(){

    }

    fun getVideo(){
        // Test Video
        var url = "http://distribution.bbb3d.renderfarming.net/video/mp4/bbb_sunflower_1080p_60fps_normal.mp4"
        streaming_view.setVideoPath(url)
        var mediaController = MediaController(this)
        streaming_view.setMediaController(mediaController)
        streaming_view.requestFocus()
        streaming_view.setOnPreparedListener(object: MediaPlayer.OnPreparedListener{
            override fun onPrepared(p0: MediaPlayer?) {
//                TODO("Not yet implemented")
                streaming_view.start()
            }
        })

        ///////////////////////////////////////////////
        //     Get Streaming Video from Server       //
        ///////////////////////////////////////////////

    }

}
