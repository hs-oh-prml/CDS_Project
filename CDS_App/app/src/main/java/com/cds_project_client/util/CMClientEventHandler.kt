package com.cds_project_client.util

import android.util.Log
import kr.ac.konkuk.ccslab.cm.event.CMEvent
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler
import kr.ac.konkuk.ccslab.cm.info.CMInfo
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub

class CMClientEventHandler(
    var clientStub:CMClientStub
): CMAppEventHandler {
    lateinit var cListener:cmChatListener
    lateinit var rListener:cmRegisterListener
    interface cmChatListener{
        fun printChat(u_id:String, msg:String)
    }
    interface cmRegisterListener{
        fun registerUser(ret:Int)
    }
    interface datagramListener{
        fun streaming(bytes:ByteArray)
    }
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
            CMSessionEvent.REGISTER_USER_ACK->{
                if( se.returnCode == 1 )
                {
                    Log.d("Register User","User["+se.userName+"] successfully registered at time["
                            +se.creationTime+"].\n");
                }
                else
                {
                    Log.d("Register User","User["+se.userName+"] failed to register!\n");
                }
                rListener.registerUser(se.returnCode)
            }
            CMSessionEvent.SESSION_TALK->{
                System.out.println("("+se.getHandlerSession()+")");
                System.out.println("<"+se.getUserName()+">: "+se.getTalk());
                cListener.printChat(se.userName, se.talk)
            }
        }
    }

}