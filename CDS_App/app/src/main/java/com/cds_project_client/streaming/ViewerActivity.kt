package com.cds_project_client.streaming

import android.Manifest
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.cds_project_client.R
import com.cds_project_client.mApplication
import com.cds_project_client.util.CMClient
import com.cds_project_client.util.CMClientEventHandler
import kotlinx.android.synthetic.main.activity_streamer.*
import kotlinx.android.synthetic.main.activity_viewer.*
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription

class ViewerActivity : AppCompatActivity() {

    lateinit var cmClient: CMClient

    private lateinit var rtcClient: RTCClient
    private lateinit var signallingClient: SignallingClient

    private val sdpObserver = object : AppSdpObserver() {
        override fun onCreateSuccess(p0: SessionDescription?) {
            super.onCreateSuccess(p0)
            signallingClient.send(p0)
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
        call_video.setOnClickListener { rtcClient.call(sdpObserver) }
//        rtcClient.call(sdpObserver)
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
        chatting.movementMethod = ScrollingMovementMethod()

        // Send Message
        send_message.setOnClickListener {
            var strTarget = "/b"
            var strMsg = input_chatting.text.toString()
            cmClient.cmClientStub.chat(strTarget, strMsg)
            input_chatting.text.clear()
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
