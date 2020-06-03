package com.cds_project_client.util

import android.content.Context
import android.util.Log
import com.cds_project_client.R
import kr.ac.konkuk.ccslab.cm.manager.CMConfigurator
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class CMClient(
    var context: Context
) {
    lateinit var cmClientStub:CMClientStub
    lateinit var cmEventHandler: CMClientEventHandler

    init{
        initCM()
    }


    fun initCM(){
        cmClientStub = CMClientStub()

        var strInterPath = context.filesDir.absolutePath
        var interPath = Paths.get(strInterPath)
        var confPath = interPath.resolve("cm-client.conf")

        if (!Files.exists(confPath)) {
            // get cm-client.conf in the asset folder
            var `is`: InputStream? = null
            `is` = try {
                context.assets.open("cm-client.conf")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("CONFIG_PATH", "Copy configuration file error!")
                return
            }
            try {
                // copy cm-client.conf from the asset folder to the internal storage
                Files.copy(
                    `is`,
                    confPath,
                    StandardCopyOption.REPLACE_EXISTING
                )
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("CONFIG_PATH", "Copy configuration file error!")
            }
        }

        cmClientStub.configurationHome = interPath


        Log.d("CONFIG_PATH", confPath.toString())
        val strCurServerAddress = cmClientStub.serverAddress
        val nCurServerPort = cmClientStub.serverPort
        Log.d("ServerInfo", "${strCurServerAddress}, $nCurServerPort")
        val testPath = cmClientStub.configurationHome.resolve("cm-client.conf")
        var strConfigurations = CMConfigurator.getConfigurations(testPath.toString())
        Log.d("ConfigInfo", cmClientStub.configurationHome.toString())
        for (strConf in strConfigurations) {
            println(strConf)
        }

        cmEventHandler = CMClientEventHandler(cmClientStub)
        cmClientStub.appEventHandler = cmEventHandler
//        val addr = "192.168.254.1"
        val addr = context.resources.getString(R.string.server_address)
        cmClientStub.serverAddress = addr

        val bRet = cmClientStub.startCM()
//        Log.d("bRet", bRet.toString())
        if(!bRet){
            Log.d("START_CM", "CM initialization error!")
        } else {
            Log.d("START_CM", "Client CM starts.")
        }

    }
}