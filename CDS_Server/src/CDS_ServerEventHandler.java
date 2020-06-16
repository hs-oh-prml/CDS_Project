import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMDBInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class CDS_ServerEventHandler implements CMAppEventHandler{

	private CMServerStub m_serverStub;
	private CMServerSessionStub m_sessionStub;
	private int m_nCheckCount;	// for internal forwarding simulation
	private boolean m_bDistFileProc;	// for distributed file processing
	CMDBManager dbManager;
	public CDS_ServerEventHandler(CMServerStub serverStub, CMServerSessionStub sessionStub)
	{
		m_serverStub = serverStub;
		m_sessionStub = sessionStub;
		m_nCheckCount = 0;
		m_bDistFileProc = false;
	}
	
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType()) {
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		case CMInfo.CM_INTEREST_EVENT:
			processInterestEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		default:
			return;
		}
	}
	private void processSessionEvent(CMEvent cme) {
		CMSessionEvent se = (CMSessionEvent) cme;
		CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
		CMInfo ci = m_serverStub.getCMInfo();
//		CMDBInfo di = ci.getDBInfo();
//		String url = di.getDBURL();
//		System.out.println("DB: " + url);
//		CMDBManager.init(ci);
//		CMDBManager.connectDB(ci);
		
		// 질문: 로그아웃은 필요없나요?
		switch(se.getID()){
		case CMSessionEvent.LOGIN:
			System.out.println("[" + se.getUserName() + "] requests login");
//			System.out.println(confInfo.isLoginScheme());
			if(confInfo.isLoginScheme()) {
				
				boolean ret = CMDBManager.authenticateUser(se.getUserName(), se.getPassword(), m_serverStub.getCMInfo());
//				System.out.println("TEST" + se.getUserName() + " " + se.getPassword() + " " + m_serverStub.getCMInfo());
//				boolean ret = true;
				System.out.println(ret);
				if(!ret) {
					System.out.println("[" + se.getUserName() + "] authentication fails");
					m_serverStub.replyEvent(se, 0);
				} else {
					System.out.println("[" + se.getUserName() + "] authentication succeeded");
					m_serverStub.replyEvent(se, 1);
				}
			}
			break;		
		case CMSessionEvent.REGISTER_USER:
			System.out.println("User registration requested by user["+se.getUserName()+"].\n");
			break;
			
		case CMSessionEvent.REQUEST_SESSION_INFO:
			System.out.println("["+se.getUserName()+"] requests session information.");
			String req = "RESPONSE_STREAMER_ID" + "#" + m_sessionStub.getStreamerID();
			CMDummyEvent sendDue = new CMDummyEvent();
			sendDue.setDummyInfo(req);
//			m_serverStub.broadcast(sendDue);
			m_serverStub.send(sendDue, se.getUserName());
			System.out.println("보낸 메세지: "+sendDue.getDummyInfo());
			break;

		case CMSessionEvent.JOIN_SESSION:
			System.out.println("["+se.getUserName()+"] requests to join session("+se.getSessionName()+").");
			break;
			
		case CMSessionEvent.LEAVE_SESSION:
			System.out.println("["+se.getUserName()+"] leaves a session("+se.getSessionName()+").");
			break;

		default:
				return;
		}
	}
	private void processInterestEvent(CMEvent cme) {
		CMInterestEvent ie = (CMInterestEvent) cme;
		CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
		CMInfo ci = m_serverStub.getCMInfo();
		switch(ie.getID()) {
		case CMInterestEvent.USER_TALK:
			System.out.println("(" + ie.getHandlerSession() + ", " + ie.getHandlerGroup() + ")");
			System.out.println("[" + ie.getUserName() + "] : " + ie.getTalk());
			break;
		default:
			break;
		}
		
	}
	private void processDummyEvent(CMEvent cme)
	{
		/*
		 * ==DummyEvent Protocol==
		 * Event_Type#SenderID#Contents 
		 * 
		 * ==Event_Type
		 * 
		 * ===STREAMERID
		 * 정의: 현재 스트리밍 중인 유저의 로그인 아이디 요청 
		 * Send: 현재 스트리밍 중인 유저의 로그인 아이디를 @@로 구분해서 SenderID에게 제공 
		 *		 세션 순서대로 스트리머의 아이디가 제공되며
		 *		 해당 세션의 스트리머가 없을 경우 null값으로 제공 
		 *
		 * ===VIDEO
		 * 정의: 실시간 스트리밍 비디오 제공 
		 * Send: 없음 
		 * 동작: Contents에 담긴 비디오 정보를 디코딩해서 화면에 송출 
		 * 
		 * ===STREAMINGSTART
		 * 정의: 스트리밍 시작 요청 
		 * Send: 성공하면 가능한 세선 이름, 실패하면 ""을 제공 
		 * 동작: 
		 */
		
		CMDummyEvent due = (CMDummyEvent) cme;
		String[] req = due.getDummyInfo().split("#");
		CMDummyEvent sendDue = new CMDummyEvent();
		switch(req[0]) {
		case "STREAMINGSTART":
			sendDue.setDummyInfo("RESPONSE_STREAMER_START" + "#" + m_sessionStub.getPossibleSession(req[1]));
			m_serverStub.send(sendDue, req[1]);
			System.out.println("보낸 메세지: "+sendDue.getDummyInfo());
			due = null;
			
			String req2 = "RESPONSE_STREAMER_ID" + "#" + m_sessionStub.getStreamerID();
			sendDue = new CMDummyEvent();
			sendDue.setDummyInfo(req2);
			m_serverStub.broadcast(sendDue);
			
			break;
		case "STREAMINGEND":
			String sessionName = m_sessionStub.leaveSession(req[1]);
			sendDue.setDummyInfo("RESPONSE_STREAMER_END" + "#" + "1");
			System.out.println(sessionName);
			System.out.println("보낸 메세지: "+sendDue.getDummyInfo()+", "+ due.getHandlerSession());
			if(sessionName != "") m_serverStub.cast(sendDue, due.getHandlerSession(), null);
			due = null;
			
			String req3 = "RESPONSE_STREAMER_ID" + "#" + m_sessionStub.getStreamerID();
			sendDue = new CMDummyEvent();
			sendDue.setDummyInfo(req3);
			m_serverStub.broadcast(sendDue);
			
			break;
		default:
			break;
		}
		
	}
}
