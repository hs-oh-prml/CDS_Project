package com.cds_project_client.streaming

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ConfigurationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.Camera
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.HandlerThread
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cds_project_client.MainActivity
import com.cds_project_client.R
import com.cds_project_client.mApplication
import com.cds_project_client.util.CMClient
import com.cds_project_client.util.CMClientEventHandler
import kotlinx.android.synthetic.main.activity_streamer.*
import kr.ac.konkuk.ccslab.cm.event.CMDataEvent
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent
import kr.ac.konkuk.ccslab.cm.event.CMEvent
import java.io.*

class StreamerActivity : AppCompatActivity() {

    lateinit var cmClient: CMClient
    var textureListener: TextureView.SurfaceTextureListener = object: TextureView.SurfaceTextureListener{
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
//                TODO("Not yet implemented")
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
//                TODO("Not yet implemented")
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
//                TODO("Not yet implemented")
            return false
        }

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
//                TODO("Not yet implemented")
            openCamera()
        }
    }


    var ratioWidth = 0
    var ratioHeight = 0
    fun setAspectRatio(width:Int, height:Int){
        if(width < 0|| height < 0){
            Log.e("Aspect_Error", "Negative Number cannot be value")
            return
        }
        ratioWidth = width
        ratioHeight = height
        requestLayout()
    }
    fun requestLayout(){

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
//                    chatting.append(str)
                }
            }
        }
        cmClient.cmEventHandler.cListener = listener

        video_view.surfaceTextureListener = textureListener

        streamer_leave_btn.setOnClickListener {
            val due: CMDummyEvent = CMDummyEvent()
            due.dummyInfo = "STREAMINGEND"+"#"+cmClient.cmClientStub.myself.name
            cmClient.cmClientStub.send(due, "SERVER");

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }


    var faceCamera = false
    var cameraId = ""
    lateinit var imageDimension:Size
    lateinit var videoDimension:Size
    var map: StreamConfigurationMap? = null
    val stateCallback = object:CameraDevice.StateCallback(){
        override fun onOpened(p0: CameraDevice) {
//            TODO("Not yet implemented")
            cameraDevice = p0
            createCameraPreviewSession()
        }

        override fun onDisconnected(p0: CameraDevice) {
//            TODO("Not yet implemented")
            cameraDevice!!.close()
        }

        override fun onError(p0: CameraDevice, p1: Int) {
//            TODO("Not yet implemented")
            cameraDevice!!.close()
            cameraDevice = null
        }
    }
    fun openCamera(){
        Log.e("OpenCamera", "openCamera() method called")
        var manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try{
            cameraId = if(faceCamera){
                manager.cameraIdList[1]
            } else{
                manager.cameraIdList[0]
            }
            val characteristics = manager.getCameraCharacteristics(cameraId)
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            imageDimension = map!!.getOutputSizes<SurfaceTexture>(SurfaceTexture::class.java)[0]
            videoDimension = map!!.getOutputSizes<MediaRecorder>(MediaRecorder::class.java)[0]

//            var orientation = resources.configuration.orientation
//            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
//
//            }

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 100)
                return
            }
            manager.openCamera(cameraId!!, stateCallback, null)
        } catch (e: CameraAccessException){
            e.printStackTrace()
        }
    }
    var cameraDevice: CameraDevice? = null
    lateinit var texture:SurfaceTexture
    lateinit var captureRequestBuilder:CaptureRequest.Builder
    var cameraCaptureSessions: CameraCaptureSession? = null

    fun createCameraPreviewSession(){
        Log.d("CameraPreviewSession", "Preview Start")
        try{
            texture = video_view.surfaceTexture
            texture.setDefaultBufferSize(imageDimension.width, imageDimension.height)
            var surfaces = ArrayList<Surface>()
            var surface = Surface(texture)
            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            surfaces.add(surface)

            var jpegSizes: Array<Size>? = null;
            if (map != null) {
                jpegSizes = map!!.getOutputSizes(ImageFormat.JPEG);
            }
            var width = 640;
            var height = 480;
//            if (jpegSizes != null && 0 < jpegSizes.size) {
//                width = jpegSizes[0].getWidth();
//                height = jpegSizes[0].getHeight();
//            }
            Log.d("Image_Size", "${width}, ${height}")
            var reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
            var readerListener = ImageReader.OnImageAvailableListener { p0 ->
                var image: Image? = null;
                try {
                    image = p0!!.acquireNextImage()
                    var buffer = image.planes[0].buffer
                    var bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)
                    var cme = CMDataEvent()
//                    cme.

//                    cmClient.cmClientStub.send(cme, String(bytes))

                    var matrix = Matrix()
//                    matrix.setScale(-1f, 1f)
                    matrix.postRotate(90f)
                    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
                    var canvas = captured_view.lockCanvas()
                    canvas.drawBitmap(bitmap, 0f, 0f,null)
                    captured_view.unlockCanvasAndPost(canvas)
                    image.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            captureRequestBuilder.addTarget(reader.surface)
            surfaces.add(reader.surface)

            var backgroundThread = HandlerThread("streaming")
            backgroundThread.start()
            var backgroundHandler = Handler(backgroundThread.looper)
            reader.setOnImageAvailableListener(readerListener, backgroundHandler)


            cameraDevice!!.createCaptureSession(surfaces, object: CameraCaptureSession.StateCallback(){
                override fun onConfigureFailed(p0: CameraCaptureSession) {
//                    TODO("Not yet implemented")
                }

                override fun onConfigured(p0: CameraCaptureSession) {
//                    TODO("Not yet implemented")
                    if(cameraDevice == null){
                        return
                    }
                    cameraCaptureSessions = p0
                    updatePreview()

                }
            }, null)

        } catch(e:CameraAccessException){
            e.printStackTrace()
        }

    }


    fun updatePreview(){
        if(cameraDevice == null){
            Log.d("Update_Preview","Error: Camera Device is null")
            return
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        var thread = HandlerThread("preview")
        thread.start()
        var bgHandler = Handler(thread.looper)
        try{
            cameraCaptureSessions!!.setRepeatingRequest(captureRequestBuilder.build(), null, bgHandler)
//            takePicture()
        }catch (e:CameraAccessException){
            e.printStackTrace()
        }


    }

    fun closeCameraPreviewSession(){
        if(cameraCaptureSessions != null){
            cameraCaptureSessions!!.close()
            cameraCaptureSessions = null
        }
    }

    fun closeCamera(){
        if(null != cameraDevice){
            cameraDevice!!.close()
            cameraDevice = null
        }
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
