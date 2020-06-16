package com.cds_project_client.streaming

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaPlayer
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cds_project_client.MainActivity
import com.cds_project_client.R
import com.cds_project_client.StreamerListAdapter
import com.cds_project_client.mApplication
import com.cds_project_client.util.CMClient
import com.cds_project_client.util.CMClientEventHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_streamer.*
import kotlinx.android.synthetic.main.activity_viewer.*

class ViewerActivity : AppCompatActivity() {

    var is_full = false
    lateinit var cmClient: CMClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)
        cmClient = (application as mApplication).cmClient
        var listener = object: CMClientEventHandler.cmChatListener {
            override fun printChat(u_id: String, msg: String) {
//                TODO("Not yet implemented")
                runOnUiThread {
                    var str = "${u_id}: $msg\n"
                    chatting.append(str)
                }
            }
        }
        cmClient.cmEventHandler.cListener = listener
        init()
    }

    fun setFullScreen(is_full:Boolean){
//        var params = LayoutPa

    }

    private fun init(){
        chatting.movementMethod = ScrollingMovementMethod()

        // Send Message
        send_message.setOnClickListener {
            var strTarget = "/s"
            var strMsg = input_chatting.text.toString()
            cmClient.cmClientStub.chat(strTarget, strMsg)
            input_chatting.text.clear()
        }

        leave_btn.setOnClickListener {
            cmClient.cmClientStub.leaveSession()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
//        var matrix = Matrix()
////                    matrix.setScale(-1f, 1f)
//        matrix.postRotate(90f)
//        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
//        var canvas = captured_view.lockCanvas()
//        canvas.drawBitmap(bitmap, 0f, 0f,null)
//        captured_view.unlockCanvasAndPost(canvas)
//        streaming_view.
//        getVideo()
    }

}
