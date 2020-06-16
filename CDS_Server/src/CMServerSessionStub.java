
public class CMServerSessionStub {
	private String[] streamers;
	final int MAXSTREAMER = 5;
	final String initStr = ".";
	
	public CMServerSessionStub() {
		streamers = new String[MAXSTREAMER];
		for(int i = 0; i < MAXSTREAMER; ++i) {
			streamers[i] = initStr;
		}
	}
	
	public String getStreamerID() {
		String ret = "";
		for(int i = 0; i < MAXSTREAMER; ++i) {
			ret += streamers[i]+"@@";
		}
		System.out.println("getStreamerID: "+ret);
		return ret;
	}
	private void setStreamerID(int num, String streamerID) {
		this.streamers[num] = streamerID;
	}
	public String leaveSession(String streamerID) {
		for(int i = 0; i < MAXSTREAMER; ++i) {
			if(streamers[i].equals(streamerID)) {
				streamers[i] = initStr;
				i++;
				return "session"+i;
			}
		}
		return "";
	}
	
	public String getPossibleSession(String senderID) {
		String ret = initStr;
		for(int i = 0; i < MAXSTREAMER; ++i) {
			if(streamers[i].equals(initStr)) {
				System.out.println("streamers["+i+"] == .");
				setStreamerID(i, senderID);
				i++;
				ret = "session"+i;
				break;
			}
		}
		return ret;
	}
}
