package de.uniol.yourclubmusic.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HandlerClientOnlineOffline extends Handler{
	public static final int CLIENT_ONLINE=1;
	public static final int CLIENT_OFFLINE=0;
	
	public static final String REASON="reason";
	
	public void sendClientOffLine(String reason){
		Message msg = Message.obtain(this, CLIENT_OFFLINE);
		Bundle bundle = new Bundle();
	 	bundle.putString(REASON, reason);
	 	msg.setData(bundle);
	 	sendMessage(msg);
	}
	public void sendClientOnline(){
		sendEmptyMessage(CLIENT_ONLINE);
	}
}
