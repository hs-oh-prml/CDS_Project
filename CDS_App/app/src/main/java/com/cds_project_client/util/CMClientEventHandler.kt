package com.cds_project_client.util

import android.app.Activity
import android.util.Log
import kr.ac.konkuk.ccslab.cm.event.CMEvent
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler
import kr.ac.konkuk.ccslab.cm.event.handler.CMEventHandler
import kr.ac.konkuk.ccslab.cm.info.CMInfo
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub

class CMClientEventHandler(
    var clientStub:CMClientStub
): CMAppEventHandler {

    override fun processEvent(p0: CMEvent?) {
//        TODO("Not yet implemented")
        when(p0?.type){
            CMInfo.CM_SESSION_EVENT->{
                processSessionEvent(p0)
            }
            CMInfo.CM_SESSION_JOIN->{
            }
            else->{
            }
        }
    }

    fun processSessionEvent(cme:CMEvent){
        var lDelay = 0
        var se = cme as CMSessionEvent
        when(se.id){
            CMSessionEvent.LOGIN_ACK->{
                if(se.isValidUser == 0){
                    // INVALID USER
                    Log.d("LOGIN_ACK", "${se.isValidUser} LOGIN FAIL INVALID USER")
                } else if(se.isValidUser == -1){
                    // ALREADY LOGIN
                    Log.d("LOGIN_ACK", "${se.isValidUser} LOGIN FAIL ALREADY LOGIN USER")
                } else {
                    // SUCCESS LOGIN
                    var interInfo = clientStub.cmInfo.interactionInfo
                    Log.d("LOGIN_ACK", "${se.isValidUser} LOGIN SUCCESS")
                    Log.d("LOGIN_ACK", "NAME: ${interInfo.myself.name}")
                }
            }


        }
    }

}