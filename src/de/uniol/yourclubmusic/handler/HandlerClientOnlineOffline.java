package de.uniol.yourclubmusic.handler;

import android.os.Handler;

public class HandlerClientOnlineOffline extends Handler{
	public static final int CLIENT_ONLINE=1;
	public static final int CLIENT_OFFLINE=0;
	
	public void sendClientOffLine(){
		sendEmptyMessage(CLIENT_OFFLINE);
	}
	public void sendClientOnline(){
		sendEmptyMessage(CLIENT_ONLINE);
	}
}
