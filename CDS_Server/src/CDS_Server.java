import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.sns.CMSNSUserAccessSimulator;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class CDS_Server {
	private CMServerStub m_serverStub;
	private CMServerSessionStub m_sessionStub;
	private CDS_ServerEventHandler m_eventHandler;
	private boolean m_bRun;
	private CMSNSUserAccessSimulator m_uaSim;
	private Scanner m_scan = null;
	
	public CDS_Server()
	{
		m_serverStub = new CMServerStub();
		m_sessionStub = new CMServerSessionStub();
		m_eventHandler = new CDS_ServerEventHandler(m_serverStub, m_sessionStub);
		m_bRun = true;
		m_uaSim = new CMSNSUserAccessSimulator();
	}

	public CMServerStub getServerStub()
	{
		return m_serverStub;
	}
	
	public CDS_ServerEventHandler getServerEventHandler()
	{
		return m_eventHandler;
	}
	public void startCM()
	{
		// get current server info from the server configuration file
		String strSavedServerAddress = null;
		String strCurServerAddress = null;
		int nSavedServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		int nNewServerPort = -1;
		
		strSavedServerAddress = m_serverStub.getServerAddress();
		strCurServerAddress = CMCommManager.getLocalIP();
		nSavedServerPort = m_serverStub.getServerPort();
		
		// ask the user if he/she would like to change the server info
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("========== start CM");
		System.out.println("detected server address: "+strCurServerAddress);
		System.out.println("saved server port: "+nSavedServerPort);
		
		try {
			System.out.print("new server address (enter for detected value): ");
			strNewServerAddress = br.readLine().trim();
			if(strNewServerAddress.isEmpty()) strNewServerAddress = strCurServerAddress;

			System.out.print("new server port (enter for saved value): ");
			strNewServerPort = br.readLine().trim();
			try {
				if(strNewServerPort.isEmpty()) 
					nNewServerPort = nSavedServerPort;
				else
					nNewServerPort = Integer.parseInt(strNewServerPort);				
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
			// update the server info if the user would like to do
			if(!strNewServerAddress.equals(strSavedServerAddress))
				m_serverStub.setServerAddress(strNewServerAddress);
			if(nNewServerPort != nSavedServerPort)
				m_serverStub.setServerPort(Integer.parseInt(strNewServerPort));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		boolean bRet = m_serverStub.startCM();
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
		startTest();
	}
	
	public void startTest()
	{
		System.out.println("Server application starts.");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		m_scan = new Scanner(System.in);
		String strInput = null;
		int nCommand = -1;
		while(m_bRun)
		{
			System.out.println("Type \"0\" for menu.");
			System.out.print("> ");
			try {
				strInput = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			try {
				nCommand = Integer.parseInt(strInput);
			} catch (NumberFormatException e) {
				System.out.println("Incorrect command number!");
				continue;
			}
			
			switch(nCommand)
			{
			case 0:
				printAllMenus();
				break;
			case 100:
				startCM();
				break;
			case 999:
				terminateCM();
				return;
			case 1: // print session information
				printSessionInfo();
				break;
			case 5:	// print current channels information
				printCurrentChannelInfo();
				break;
			case 6: // print current login users
				printLoginUsers();
				break;
			default:
				System.err.println("Unknown command.");
				break;
			}
		}
		
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		m_scan.close();
		
	}
	
	public void printAllMenus()
	{
		System.out.print("---------------------------------- Help\n");
		System.out.print("0: show all menus\n");
		System.out.print("---------------------------------- Start/Stop\n");
		System.out.print("100: strat CM, 999: terminate CM\n");
		System.out.print("---------------------------------- Information\n");
		System.out.print("1: show session information\n");
		System.out.print("5: show current channels, 6: show login users\n");
	}
	
	public void terminateCM()
	{
		m_serverStub.terminateCM();
		m_bRun = false;
	}
	
	public void printSessionInfo()
	{
		System.out.println("------------------------------------------------------");
		System.out.format("%-20s%-20s%-10s%-10s%n", "session name", "session addr", "port", "#users");
		System.out.println("------------------------------------------------------");
		
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		Iterator<CMSession> iter = interInfo.getSessionList().iterator();
		while(iter.hasNext())
		{
			CMSession session = iter.next();
			System.out.format("%-20s%-20s%-10d%-10d%n", session.getSessionName(), session.getAddress()
					, session.getPort(), session.getSessionUsers().getMemberNum());
		}
		return;
	}
	
	public void printCurrentChannelInfo()
	{
		System.out.println("========== print current channel info");
		String strChannels = m_serverStub.getCurrentChannelInfo();
		System.out.println(strChannels);
	}

	public void printLoginUsers()
	{
		System.out.println("========== print login users");
		CMMember loginUsers = m_serverStub.getLoginUsers();
		if(loginUsers == null)
		{
			System.err.println("The login users list is null!");
			return;
		}
		
		System.out.println("Currently ["+loginUsers.getMemberNum()+"] users are online.");
		Vector<CMUser> loginUserVector = loginUsers.getAllMembers();
		Iterator<CMUser> iter = loginUserVector.iterator();
		int nPrintCount = 0;
		while(iter.hasNext())
		{
			CMUser user = iter.next();
			System.out.print(user.getName()+" ");
			nPrintCount++;
			if((nPrintCount % 10) == 0)
			{
				System.out.println();
				nPrintCount = 0;
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CDS_Server server = new CDS_Server();
//		server.getServerStub().setServerAddress("192.168.66.71");
		CMServerStub cmStub = server.getServerStub();
		cmStub.setAppEventHandler(server.getServerEventHandler());
//		cmStub.setServerAddress("192.168.66.71");
		server.startCM();
		
		System.out.println("Server application is terminated.");
	}
}
