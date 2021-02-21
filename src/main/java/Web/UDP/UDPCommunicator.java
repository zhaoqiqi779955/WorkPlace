package Web.UDP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPCommunicator {

	int port;//关联端口号
	DatagramSocket mail;
	public UDPCommunicator(int p1)
	{
		port = p1;

		try {
			mail = new DatagramSocket(port);

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void communicate(String adr,int remotePort)//对方地址及端口号
	{
		
		Thread talk=new Thread(new Runnable() {//创建一个线程可以讲话
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte [] buf=null;
				String message;//发送消息内容
				DatagramPacket data=null;//
			
				InetAddress address=null;


				while(true) {
					Scanner input=new Scanner(System.in);
					message=input.next();
					try {
						buf=message.trim().getBytes("utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					try {
						address=InetAddress.getByName(adr);
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					data=new DatagramPacket(buf, buf.length,address,remotePort);//ָ创建发送给对方的包
					try {
						
						mail.send(data);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
		});
		
		
		Thread hear=new Thread(new Runnable() {//接受消息
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte [] buf=new byte[1024];//接受消息不超过1KB
				String message;
				DatagramPacket data=null;//数据包
				data=new DatagramPacket(buf, buf.length);
				while(true) {
				
					try {
						mail.receive(data);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					System.out.println("收到地址为"+data.getAddress()+"的主机,端口号为"+data.getPort()+"的信息\n信息内容："+new String(data.getData(),0,data.getLength()));
				}
			}
		});
		talk.start();
		hear.start();
				
		
	}
	
	
	

}
