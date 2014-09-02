package edu.xjtu.localization.localization.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.xjtu.localization.localization.particlefilter.StepMessage;
import edu.xjtu.localization.localization.wifiservice.WifiMessage;

public class MessageBufferManager {
	private static BlockingQueue<WifiMessage> wifiMessageQueue = new LinkedBlockingQueue<WifiMessage>();
	private static BlockingQueue<StepMessage> stepMessageQueue = new LinkedBlockingQueue<StepMessage>();
	public static void addWifiMessage(WifiMessage message){
		try {
			wifiMessageQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * retrieve latest wifi prediction message. If there are more than one message in this buffer, 
	 * this function just return the latest one and the others will be thrown.
	 */
	public static WifiMessage drainWifiMessage(){
		List<TimeMessageInterface> tmpList = new ArrayList<TimeMessageInterface>();
		wifiMessageQueue.drainTo(tmpList);
		wifiMessageQueue.clear();
		return (WifiMessage) findLatestMessage(tmpList);
	}
	
	public static void addStepMessage(StepMessage message){
		try {
			stepMessageQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static StepMessage drainStepMessage(){
		List<TimeMessageInterface> tmpList = new ArrayList<TimeMessageInterface>();
		stepMessageQueue.drainTo(tmpList);
		stepMessageQueue.clear();
		return (StepMessage) findLatestMessage(tmpList);
	}
	public static TimeMessageInterface findLatestMessage(List<TimeMessageInterface> messages){
		if (messages == null || messages.isEmpty()) {
			return null;
		}
		TimeMessageInterface tmp = messages.get(0);
		for (TimeMessageInterface m : messages) {
			if (m.getNanoTime() > tmp.getNanoTime()) {
				tmp = m;
			}
		}
		return tmp;
	}
}
