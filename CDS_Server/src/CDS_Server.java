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
	private CDS_ServerEventHandler m_eventHandler;
	private boolean m_bRun;
	private CMSNSUserAccessSimulator m_uaSim;
	private Scanner m_scan = null;
	
	public CDS_Server()
	{
		m_serverStub = new CMServerStub();
		m_eventHandler = new CDS_ServerEventHandler(m_serverStub);
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
//		startTest();
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
	
	// 질문: 그룹은 프린트 할 일 없을 것 같은데 지울까요?
	public void printGroupInfo()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String strSessionName = null;
		
		System.out.println("====== print group information");
		System.out.print("Session name: ");
		try {
			strSessionName = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		CMSession session = interInfo.findSession(strSessionName);
		if(session == null)
		{
			System.out.println("Session("+strSessionName+") not found.");
			return;
		}
		
		System.out.println("------------------------------------------------------------------");
		System.out.format("%-20s%-20s%-10s%-10s%n", "group name", "multicast addr", "port", "#users");
		System.out.println("------------------------------------------------------------------");

		Iterator<CMGroup> iter = session.getGroupList().iterator();
		while(iter.hasNext())
		{
			CMGroup gInfo = iter.next();
			System.out.format("%-20s%-20s%-10d%-10d%n", gInfo.getGroupName(), gInfo.getGroupAddress()
					, gInfo.getGroupPort(), gInfo.getGroupUsers().getMemberNum());
		}

		System.out.println("======");
		return;
	}

	public void addChannel()
	{
		int nChType = -1;
		int nChKey = -1;
		String strServerName = null;
		String strChAddress = null;
		int nChPort = -1;
		String strSessionName = null;
		String strGroupName = null;
		CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		boolean bResult = false;
		String strBlock = null;
		boolean isBlock = false;
		SocketChannel sc = null;
		DatagramChannel dc = null;
		String strSync = null;
		boolean isSyncCall = false;
		
		System.out.println("====== add additional channel");

		// ask channel type, (server name), channel index (integer greater than 0), addr, port
		try{
			//System.out.print("Select channel type (SocketChannel:2, DatagramChannel:3, MulticastChannel:4): ");
			System.out.print("Select channel type (DatagramChannel:3, MulticastChannel:4): ");
			nChType = m_scan.nextInt();
			if(nChType == CMInfo.CM_SOCKET_CHANNEL)
			{
				System.err.println("socket channel not yet supported!");
				return;
				/*
				System.out.print("is it a blocking channel? (\"y\": yes, \"n\": no): ");
				strBlock = m_scan.next();
				if(strBlock.equals("y")) isBlock = true;
				else if(strBlock.equals("n")) isBlock = false;
				else
				{
					System.err.println("invalid answer! : "+strBlock);
					return;
				}
			
				if(isBlock)
				{
					System.out.print("Channel key(>=0): ");
					nChKey = m_scan.nextInt();
					if(nChKey < 0)
					{
						System.err.println("testAddChannel(), invalid blocking socket channel key ("+nChKey+")!");
						return;
					}
				}
				else
				{
					System.out.print("Channel key(integer greater than 0): ");
					nChKey = m_scan.nextInt();
					if(nChKey <= 0)
					{
						System.err.println("testAddChannel(), invalid nonblocking socket channel key ("+nChKey+")!");
						return;
					}
				}
				
				System.out.print("Is the addition synchronous? (\"y\": yes, \"n\": no): ");
				strSync = m_scan.next();
				if(strSync.equals("y")) isSyncCall = true;
				else if(strSync.equals("n")) isSyncCall =false;
				else
				{
					System.err.println("invalid answer! :" + strSync);
					return;
				}
				
				System.out.print("Server name(\"SERVER\" for the default server): ");
				strServerName = m_scan.next();
				*/
			}
			else if(nChType == CMInfo.CM_DATAGRAM_CHANNEL)
			{
				System.out.print("is it a blocking channel? (\"y\": yes, \"n\": no): ");
				strBlock = m_scan.next();
				if(strBlock.equals("y")) isBlock = true;
				else if(strBlock.equals("n")) isBlock = false;
				else
				{
					System.err.println("invalid answer! : "+strBlock);
					return;
				}
			
				if(isBlock)
				{
					System.out.print("Channel udp port: ");
					nChPort = m_scan.nextInt();
					if(nChPort < 0)
					{
						System.err.println("testAddChannel(), invalid blocking datagram channel key ("+nChPort+")!");
						return;
					}
				}
				else
				{
					System.out.print("Channel udp port: ");
					nChPort = m_scan.nextInt();
					if(nChPort <= 0)
					{
						System.err.println("testAddChannel(), invalid nonblocking datagram channel key ("+nChPort+")!");
						return;
					}
				}

			}
			else if(nChType == CMInfo.CM_MULTICAST_CHANNEL)
			{
				System.out.print("Target session name: ");
				strSessionName = m_scan.next();
				System.out.print("Target group name: ");
				strGroupName = m_scan.next();
				System.out.print("Channel multicast address: ");
				strChAddress = m_scan.next();
				System.out.print("Channel multicast port: ");
				nChPort = m_scan.nextInt();
			}
		}catch(InputMismatchException e){
			System.err.println("Invalid input type!");
			m_scan.next();
			return;
		}
					
		switch(nChType)
		{
		/*
		case CMInfo.CM_SOCKET_CHANNEL:
			if(isBlock)
			{
				if(isSyncCall)
				{
					sc = m_clientStub.syncAddBlockSocketChannel(nChKey, strServerName);
					if(sc != null)
						System.out.println("Successfully added a blocking socket channel both "
								+ "at the client and the server: key("+nChKey+"), server("+strServerName+")");
					else
						System.err.println("Failed to add a blocking socket channel both at "
								+ "the client and the server: key("+nChKey+"), server("+strServerName+")");					
				}
				else
				{
					bResult = m_clientStub.addBlockSocketChannel(nChKey, strServerName);
					if(bResult)
						System.out.println("Successfully added a blocking socket channel at the client and "
								+"requested to add the channel info to the server: key("+nChKey+"), server("
								+strServerName+")");
					else
						System.err.println("Failed to add a blocking socket channel at the client or "
								+"failed to request to add the channel info to the server: key("+nChKey
								+"), server("+strServerName+")");
					
				}
			}
			else
			{
				if(isSyncCall)
				{
					sc = m_clientStub.syncAddNonBlockSocketChannel(nChKey, strServerName);
					if(sc != null)
						System.out.println("Successfully added a nonblocking socket channel both at the client "
								+ "and the server: key("+nChKey+"), server("+strServerName+")");
					else
						System.err.println("Failed to add a nonblocking socket channel both at the client "
								+ "and the server: key("+nChKey+"), server("+strServerName+")");										
				}
				else
				{
					bResult = m_clientStub.addNonBlockSocketChannel(nChKey, strServerName);
					if(bResult)
						System.out.println("Successfully added a nonblocking socket channel at the client and "
								+ "requested to add the channel info to the server: key("+nChKey+"), server("
								+strServerName+")");
					else
						System.err.println("Failed to add a nonblocking socket channel at the client or "
								+ "failed to request to add the channel info to the server: key("+nChKey
								+"), server("+strServerName+")");					
				}
			}
				
			break;
		*/
		case CMInfo.CM_DATAGRAM_CHANNEL:
			if(isBlock)
			{
				dc = m_serverStub.addBlockDatagramChannel(nChPort);
				if(dc != null)
					System.out.println("Successfully added a blocking datagram socket channel: port("+nChPort+")");
				else
					System.err.println("Failed to add a blocking datagram socket channel: port("+nChPort+")");								
			}
			else
			{
				dc = m_serverStub.addNonBlockDatagramChannel(nChPort);
				if(dc != null)
					System.out.println("Successfully added a non-blocking datagram socket channel: port("+nChPort+")");
				else
					System.err.println("Failed to add a non-blocking datagram socket channel: port("+nChPort+")");				
			}
			
			break;
		case CMInfo.CM_MULTICAST_CHANNEL:
			bResult = m_serverStub.addMulticastChannel(strSessionName, strGroupName, strChAddress, nChPort);
			if(bResult)
			{
				System.out.println("Successfully added a multicast channel: session("+strSessionName+"), group("
						+strGroupName+"), address("+strChAddress+"), port("+nChPort+")");
			}
			else
			{
				System.err.println("Failed to add a multicast channel: session("+strSessionName+"), group("
						+strGroupName+"), address("+strChAddress+"), port("+nChPort+")");
			}
			break;
		default:
			System.out.println("Channel type is incorrect!");
			break;
		}
		
		System.out.println("======");
	}
	
	public void removeChannel()
	{
		int nChType = -1;
		int nChKey = -1;
		int nChPort = -1;
		String strChAddress = null;
		String strServerName = null;
		String strSessionName = null;
		String strGroupName = null;
		CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		boolean result = false;
		String strBlock = null;
		boolean isBlock = false;
		String strSync = null;
		boolean isSyncCall = false;
		
		System.out.println("====== remove additional channel");
		try{
			//System.out.print("Select channel type (SocketChannel:2, DatagramChannel:3, MulticastChannel:4): ");
			System.out.print("Select channel type (DatagramChannel:3, MulticastChannel:4): ");
			nChType = m_scan.nextInt();
			if(nChType == CMInfo.CM_SOCKET_CHANNEL)
			{
				System.err.println("socket channel not yet supported!");
				return;
				/*
				System.out.print("is it a blocking channel? (\"y\": yes, \"n\": no): ");
				strBlock = m_scan.next();
				if(strBlock.equals("y")) isBlock = true;
				else if(strBlock.equals("n")) isBlock = false;
				else
				{
					System.err.println("invalid answer! : "+strBlock);
					return;
				}
			
				if(isBlock)
				{
					System.out.print("Channel key(>=0): ");
					nChKey = m_scan.nextInt();
					if(nChKey < 0)
					{
						System.err.println("testRemoveChannel(), invalid socket channel key ("+nChKey+")!");
						return;
					}
					System.out.print("Is the removal synchronous? (\"y\": yes, \"n\": no); ");
					strSync = m_scan.next();
					if(strSync.equals("y")) isSyncCall = true;
					else if(strSync.equals("n")) isSyncCall = false;
					else
					{
						System.err.println("Invalid answer! : "+strSync);
						return;
					}
				}
				else
				{
					System.out.print("Channel key(integer greater than 0): ");
					nChKey = m_scan.nextInt();
					if(nChKey <= 0)
					{
						System.err.println("testRemoveChannel(), invalid socket channel key ("+nChKey+")!");
						return;
					}
				}
				System.out.print("Server name(\"SERVER\" for the default server): ");
				strServerName = m_scan.next();
				*/
			}
			else if(nChType ==CMInfo.CM_DATAGRAM_CHANNEL)
			{
				System.out.print("is it a blocking channel? (\"y\": yes, \"n\": no): ");
				strBlock = m_scan.next();
				if(strBlock.equals("y")) isBlock = true;
				else if(strBlock.equals("n")) isBlock = false;
				else
				{
					System.err.println("invalid answer! : "+strBlock);
					return;
				}

				System.out.print("Channel udp port: ");
				nChPort = m_scan.nextInt();			
			}
			else if(nChType == CMInfo.CM_MULTICAST_CHANNEL)
			{
				System.out.print("Target session name: ");
				strSessionName = m_scan.next();
				System.out.print("Target group name: ");
				strGroupName = m_scan.next();
				System.out.print("Multicast address: ");
				strChAddress = m_scan.next();
				System.out.print("Multicast port: ");
				nChPort = m_scan.nextInt();
			}
		}catch(InputMismatchException e){
			System.err.println("Invalid input type!");
			m_scan.next();
			return;
		}

		switch(nChType)
		{
		/*
		case CMInfo.CM_SOCKET_CHANNEL:
			if(isBlock)
			{
				if(isSyncCall)
				{
					result = m_clientStub.syncRemoveBlockSocketChannel(nChKey, strServerName);
					if(result)
						System.out.println("Successfully removed a blocking socket channel both "
								+ "at the client and the server: key("+nChKey+"), server ("+strServerName+")");
					else
						System.err.println("Failed to remove a blocking socket channel both at the client "
								+ "and the server: key("+nChKey+"), server ("+strServerName+")");					
				}
				else
				{
					result = m_clientStub.removeBlockSocketChannel(nChKey, strServerName);
					if(result)
						System.out.println("Successfully removed a blocking socket channel at the client and " 
								+ "requested to remove it at the server: key("+nChKey+"), server("+strServerName+")");
					else
						System.err.println("Failed to remove a blocking socket channel at the client or "
								+ "failed to request to remove it at the server: key("+nChKey+"), server("
								+strServerName+")");
				}
			}
			else
			{
				result = m_clientStub.removeNonBlockSocketChannel(nChKey, strServerName);
				if(result)
					System.out.println("Successfully removed a nonblocking socket channel: key("+nChKey
							+"), server("+strServerName+")");
				else
					System.err.println("Failed to remove a nonblocing socket channel: key("+nChKey
							+"), server("+strServerName+")");
			}
			
			break;
		*/
		case CMInfo.CM_DATAGRAM_CHANNEL:
			if(isBlock)
			{
				result = m_serverStub.removeBlockDatagramChannel(nChPort);
				if(result)
					System.out.println("Successfully removed a blocking datagram socket channel: port("+nChPort+")");
				else
					System.err.println("Failed to remove a blocking datagram socket channel: port("+nChPort+")");				
			}
			else
			{
				result = m_serverStub.removeNonBlockDatagramChannel(nChPort);
				if(result)
					System.out.println("Successfully removed a non-blocking datagram socket channel: port("+nChPort+")");
				else
					System.err.println("Failed to remove a non-blocking datagram socket channel: port("+nChPort+")");				
			}

			break;
		case CMInfo.CM_MULTICAST_CHANNEL:
			result = m_serverStub.removeAdditionalMulticastChannel(strSessionName, strGroupName, strChAddress, nChPort);
			if(result)
			{
				System.out.println("Successfully removed a multicast channel: session("+strSessionName+"), group("
						+strGroupName+"), address("+strChAddress+"), port("+nChPort+")");
			}
			else
			{
				System.err.println("Failed to remove a multicast channel: session("+strSessionName+"), group("
						+strGroupName+"), address("+strChAddress+"), port("+nChPort+")");
			}
			break;
		default:
			System.out.println("Channel type is incorrect!");
			break;
		}
		
		System.out.println("======");		
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
