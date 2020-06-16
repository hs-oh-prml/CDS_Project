package com.cds_project_client.util

import android.util.Log
import com.cds_project_client.data.ItemStreaming
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent
import kr.ac.konkuk.ccslab.cm.event.CMEvent
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler
import kr.ac.konkuk.ccslab.cm.info.CMInfo
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub

class CMClientEventHandler(
    var clientStub:CMClientStub,
    var cmSessions: ArrayList<ItemStreaming>
): CMAppEventHandler {
    lateinit var cListener:cmChatListener
    lateinit var rListener:cmRegisterListener
    lateinit var stListener:cmStreamingListener
    lateinit var eListener:cmEndStreamListener

    var sListener:cmSessListener? = null


    var sessionNums: ArrayList<String>

    init{
        sessionNums = ArrayList()
        for(i in 0..4){
            sessionNums.add("0")
        }
    }

    interface cmChatListener{
        fun printChat(u_id:String, msg:String)
    }
    interface cmRegisterListener{
        fun registerUser(ret:Int)
    }
    interface cmStreamingListener{
        fun toStreamer(sender:String)
        fun toViewer(sender:String)

    }
    interface cmSessListener{
        fun sessionRefresh(cmSessions: ArrayList<String>)
    }
    interface cmEndStreamListener{
        fun endSession()
    }

    override fun processEvent(p0: CMEvent?) {
//        TODO("Not yet implemented")
        when(p0?.type){
            CMInfo.CM_SESSION_EVENT->{
                processSessionEvent(p0)
            }
            CMInfo.CM_DUMMY_EVENT->{
                processDummyEvent(p0)
            }
            else->{
            }
        }
    }

    private fun processDummyEvent(cme: CMEvent) {
        var due = cme as CMDummyEvent
        var req = due.dummyInfo.split("#".toRegex())
        when(req[0]) {
            "RESPONSE_STREAMER_START"->{
                if(req[1] != ".") {
                    clientStub.joinSession(req[1])
                }
            }
            "RESPONSE_STREAMER_END"->{
                clientStub.leaveSession()
                eListener.endSession()
                Log.d("RES_S_END", "sessionEND")
            }
            "RESPONSE_STREAMER_ID"->{
                cmSessions.clear()
                var streamers = req[1].split("@@".toRegex())
                for(i in 0..streamers.size-2){
                    Log.d("STREAMERID", streamers[i])
                    if(streamers[i] != ".") cmSessions.add(ItemStreaming(streamers[i], sessionNums[i]))
                }

                Log.d("EVENTHANDLER_STREAMER_INFO", sessionNums.toString())
                sListener?.sessionRefresh(sessionNums)
            }
            "REQUEST_STREAM_TO_STREAMER"->{
                stListener.toStreamer(due.sender)
            }
            "REQUEST_STREAM_TO_VIEWER"->{
                stListener.toViewer(due.sender)
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
            CMSessionEvent.RESPONSE_SESSION_INFO->{
                processRESPONSE_SESSION_INFO(se);
            }
        }
    }

    private fun processRESPONSE_SESSION_INFO(se: CMSessionEvent) {
        val iter: Iterator<CMSessionInfo> = se.sessionInfoList.iterator()

        System.out.format(
            "%-60s%n",
            "------------------------------------------------------------"
        )
        System.out.format("%-20s%-20s%-10s%-10s%n", "name", "address", "port", "user num")
        System.out.format(
            "%-60s%n",
            "------------------------------------------------------------"
        )
        var i = 0
        while (iter.hasNext()) {
            val tInfo: CMSessionInfo = iter.next()
            System.out.format(
                "%-20s%-20s%-10d%-10d%n", tInfo.getSessionName(), tInfo.getAddress(),
                tInfo.getPort(), tInfo.getUserNum()
            )
            sessionNums[i] = tInfo.getUserNum().toString()
            sListener?.sessionRefresh(sessionNums)
        }
    }

}