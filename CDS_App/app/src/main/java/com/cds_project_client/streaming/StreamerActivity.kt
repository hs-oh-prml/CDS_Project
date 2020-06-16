package com.cds_project_client.streaming

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cds_project_client.R
import com.cds_project_client.mApplication
import com.cds_project_client.util.CMClient
import com.cds_project_client.util.CMClientEventHandler
import kotlinx.android.synthetic.main.activity_streamer.*
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription

class StreamerActivity : AppCompatActivity() {

    lateinit var cmClient: CMClient

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }

    private lateinit var rtcClient: RTCClient
    private lateinit var signallingClient: SignallingClient

    private val sdpObserver = object : AppSdpObserver() {
        override fun onCreateSuccess(p0: SessionDescription?) {
            super.onCreateSuccess(p0)
            signallingClient.send(p0)
        }

        override fun onSetFailure(p0: String?) {
            super.onSetFailure(p0)
            Log.d("STREAMER_APPSDPOBSERVER_STATUS", "SET FAILED")
        }

        override fun onCreateFailure(p0: String?) {
            super.onCreateFailure(p0)
            Log.d("STREAMER_APPSDPOBSERVER_STATUS", "CREATED FAILED")

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streamer)
        initPermission()
        init()
    }
    fun init(){
        cmClient = (application as mApplication).cmClient
        var listener = object: CMClientEventHandler.cmChatListener {
            override fun printChat(u_id: String, msg: String) {
//                TODO("Not yet implemented")
                runOnUiThread {
                    var str = "${u_id}: $msg\n"
                    s_chatting.append(str)
                }
            }
        }
        cmClient.cmEventHandler.cListener = listener
        call_btn.setOnClickListener { rtcClient.call(sdpObserver) }
//        rtcClient.call(sdpObserver)

        onCameraPermissionGranted()
        streamer_leave_btn.setOnClickListener {
            val due = CMDummyEvent()
            due.dummyInfo = "STREAMINGEND"+"#"+cmClient.cmClientStub.myself.name
            cmClient.cmClientStub.send(due, "SERVER");
            finish()
        }

        var stListener = object:CMClientEventHandler.cmStreamingListener{
            override fun toStreamer(sender:String) {
//                TODO("Not yet implemented")
                Log.d("STREAMING_PROTOCALL", "STREAMER_OK")
                rtcClient.answer(sdpObserver)
                sdpObserver.onSetSuccess()
                var due = CMDummyEvent()
                due.dummyInfo = "REQUEST_STREAM_TO_VIEWER"
                cmClient.cmClientStub.send(due, sender)
            }

            override fun toViewer(sender: String) {

            }
        }
        cmClient.cmEventHandler.stListener = stListener
    }


    private fun onCameraPermissionGranted() {
//        var pubnub = PubNub()
        rtcClient = RTCClient(
            application,
            object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    signallingClient.send(p0)
//                    rtcClient.addIceCandidate(p0)
                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
//                    p0?.videoTracks?.get(0)?.addSink(remote_view)
                }
            }
        )
//        rtcClient.initSurfaceView(remote_view)
        rtcClient.initSurfaceView(local_view)
        rtcClient.startLocalVideoCapture(local_view)
        signallingClient = SignallingClient(createSignallingClientListener())
//        rtcClient.call(sdpObserver)
//        call_btn.setOnClickListener { rtcClient.call(sdpObserver) }
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

    private fun requestCameraPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION) && !dialogShown) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Required")
            .setMessage("This app need the camera to function")
            .setPositiveButton("Grant") { dialog, _ ->
                dialog.dismiss()
                requestCameraPermission(true)
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
//                onCameraPermissionDenied()
            }
            .show()
    }


    fun initPermission(){
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "권한 승인이 필요합니다.", Toast.LENGTH_SHORT).show()
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )){
                Toast.makeText(this, "스트리밍을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "스트리밍을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), 100);
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            100->{
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "권한 허가", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "권한 거부", Toast.LENGTH_SHORT).show()
                }
                return;
            }
        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}
