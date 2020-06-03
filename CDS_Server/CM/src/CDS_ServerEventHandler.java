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
	private int m_nCheckCount;	// for internal forwarding simulation
	private boolean m_bDistFileProc;	// for distributed file processing
	CMDBManager dbManager;
	public CDS_ServerEventHandler(CMServerStub serverStub)
	{
		m_serverStub = serverStub;
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
		switch(se.getID()){
		case CMSessionEvent.LOGIN:
			System.out.println("[" + se.getUserName() + "] requests login");
//			System.out.println(confInfo.isLoginScheme());
			if(confInfo.isLoginScheme()) {
				
//				boolean ret = CMDBManager.authenticateUser(se.getUserName(), se.getPassword(), m_serverStub.getCMInfo());
//				System.out.println("TEST" + se.getUserName() + " " + se.getPassword() + " " + m_serverStub.getCMInfo());
				boolean ret = true;
				if(!ret) {
					System.out.println("[" + se.getUserName() + "] authentication fails");
					m_serverStub.replyEvent(se, 0);
				} else {
					System.out.println("[" + se.getUserName() + "] authentication succeeded");
					m_serverStub.replyEvent(se, 1);
				}
			}
			break;
		case CMSessionEvent.LOGIN_ACK:
			System.out.println("[" + se.getUserName() + "] requests syncLogin");
//			System.out.println(confInfo.isLoginScheme());
			if(se.isValidUser() == 0) {
				System.out.println("[" + se.getUserName() + "] authentication fails");				
				m_serverStub.replyEvent(se, 0);
			} else if(se.isValidUser() == -1) {
				System.out.println("[" + se.getUserName() + "] already login");								
				m_serverStub.replyEvent(se, -1);
			} else {
				System.out.println("[" + se.getUserName() + "] authentication succeeded");
				m_serverStub.replyEvent(se, 1);
			}
			break;
		case CMSessionEvent.SESSION_TALK:
			
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

}
