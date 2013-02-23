package com.bluetooth.manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class BluetoothComunication extends Thread {
	 
	private int whatMsgBT;
	private int whatMsgNotice;
	
	private Handler handler;
	
	private BluetoothSocket socket;
	private DataInputStream dataInputStream = null;
	private DataOutputStream dataOutputStream = null;
	
	public BluetoothComunication(Handler handler, int whatMsgBT, int whatMsgNotice){
		this.handler = handler;
		this.whatMsgBT = whatMsgBT;
		this.whatMsgNotice = whatMsgNotice;
	}
	
	public void openComunication(BluetoothSocket socket){
		this.socket = socket;
		start();
	}
	 
	@Override
	public void run() {
		 super.run();
		
		 String nameBluetooth;
		
		 try {
			 nameBluetooth = socket.getRemoteDevice().getName();
			 dataInputStream = new DataInputStream(socket.getInputStream());
			 dataOutputStream = new DataOutputStream(socket.getOutputStream());
			
			 sendHandler(whatMsgNotice, "Baﬂar›yla ba€land›");
			 
			 while (true) {
				 if(dataInputStream.available() > 0){
					 byte[] msg = new byte[dataInputStream.available()];
					 dataInputStream.read(msg, 0, dataInputStream.available());
					 
					 sendHandler(whatMsgBT, nameBluetooth + ": " + new String(msg));
				 }
			 }
		 } catch (IOException e) {
			 e.printStackTrace(); 
			 
			 dataInputStream = null;
			 dataOutputStream = null;
			 
			 sendHandler(whatMsgNotice, "Ba€lant› bulunamad›");
		 }
	}
	
	public void sendMessageByBluetooth(String msg){
		try {
			if(dataOutputStream != null){
				dataOutputStream.write(msg.getBytes());
			}else{
				sendHandler(whatMsgNotice, "Ba€lant› yok");
			}
			
		} catch (IOException e) {
			e.printStackTrace(); 
			 
			sendHandler(whatMsgNotice, "Mesaj gönderilemedi");
		}
	}
	
	public void sendHandler(int what, Object object){
		handler.obtainMessage(what, object).sendToTarget();
	}
           
	 public void stopComunication(){ 
		try {
			if(dataInputStream != null && dataOutputStream != null){
				dataInputStream.close();
				dataOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
 }