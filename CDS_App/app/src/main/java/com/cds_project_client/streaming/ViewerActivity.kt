package com.cds_project_client.streaming

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
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
import kotlinx.coroutines.delay
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription

class ViewerActivity : AppCompatActivity() {

    lateinit var cmClient: CMClient
    var streamerId = ""

    private lateinit var rtcClient: RTCClient
    private lateinit var signallingClient: SignallingClient

    private val sdpObserver = object : AppSdpObserver() {
        override fun onCreateSuccess(p0: SessionDescription?) {
            super.onCreateSuccess(p0)
            signallingClient.send(p0)
        }

        override fun onSetFailure(p0: String?) {
            super.onSetFailure(p0)
            Log.d("VIEWER_APPSDPOBSERVER_STATUS", "SET FAILED")
        }

        override fun onCreateFailure(p0: String?) {
            super.onCreateFailure(p0)
            Log.d("VIEWER_APPSDPOBSERVER_STATUS", "CREATED FAILED")

        }
    }

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
        initVideo()
//        var handler = Handler()
//        handler.postDelayed(object:Runnable{
//            override fun run() {
////                TODO("Not yet implemented")
//                rtcClient.call(sdpObserver)
//                var due = CMDummyEvent()
//                due.dummyInfo = "REQUEST_STREAM_TO_STREAMER#"+cmClient.cmClientStub.myself.currentSession
//                cmClient.cmClientStub.send(due, streamerId)
//            }
//        },1000)

    }

    fun initVideo(){
        rtcClient = RTCClient(
            application,
            object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
//                    signallingClient.send(p0)
                    rtcClient.addIceCandidate(p0)
                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
                    p0?.videoTracks?.get(0)?.addSink(video_view)
                }
            }
        )
        rtcClient.initSurfaceView(video_view)
        signallingClient = SignallingClient(createSignallingClientListener())

        var stListener = object:CMClientEventHandler.cmStreamingListener{
            override fun toStreamer(sender: String) {
//                TODO("Not yet implemented")
            }

            override fun toViewer(sender: String) {
//                TODO("Not yet implemented")
                Log.d("STREAMING_PROTOCALL", "VIEWER_OK")
//                rtcClient.call(sdpObserver)

            }
        }

        cmClient.cmEventHandler.stListener = stListener
//        cmClient.cmEventHandler.

        call_video.setOnClickListener { rtcClient.call(sdpObserver) }
        var due = CMDummyEvent()
        due.dummyInfo = "REQUEST_STREAM_TO_STREAMER#"+cmClient.cmClientStub.myself.currentSession
        cmClient.cmClientStub.send(due, streamerId)
        rtcClient.call(sdpObserver)
    }

    private fun createSignallingClientListener() = object : SignallingClientListener {
        override fun onConnectionEstablished() {
//            call_button.isClickable = true
        }

        override fun onOfferReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            rtcClient.answer(sdpObserver)
//            remote_view_loading.isGone = true
        }

        override fun onAnswerReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
//            remote_view_loading.isGone = true
        }

        override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
            rtcClient.addIceCandidate(iceCandidate)
        }
    }

    fun setFullScreen(is_full:Boolean){
//        var params = LayoutPa

    }

    private fun init(){

        streamerId = intent.getStringExtra("streamerId")
        Log.d("v_streamer_id", streamerId)
        v_streamer_id.text = streamerId


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
            finish()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
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
