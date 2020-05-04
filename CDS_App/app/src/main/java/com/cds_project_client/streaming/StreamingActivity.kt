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
import kotlinx.android.synthetic.main.activity_streaming.*

class StreamingActivity : AppCompatActivity() {

    lateinit var chat_list: ArrayList<ItemChatting>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming)
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


    private fun init(){
        chat_list = ArrayList()

        getData()

        // Chatting Init
        var adapter = ChattingAdapter(this, chat_list)
        var layoutManager = LinearLayoutManager(this, VERTICAL, false)
        chatting.layoutManager = layoutManager
        chatting.adapter = adapter

        // Send Message
        send_message.setOnClickListener {

        }

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
