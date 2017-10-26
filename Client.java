/*
Abdulellah Abualshour
Krunal Patel
*/

import java.io.*;
import java.net.*;
import java.util.concurrent.*; 

public class Client {
	public static boolean hostAvailabilityCheck(int port) { 
	    try (DatagramSocket s = new DatagramSocket(port)) {
	        return false;
	    } catch (IOException ex) {
	        /* ignore */
	    }
	    return true;
	}

	public static void tcp_pure(String host, int port) throws Exception{
		String line; // user input
		double messageSize;
		String power;
		int count = 0; 
		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
		Socket sock = new Socket(host, port); 	// connect to localhost port 12345
		DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		System.out.println("\n*Client is running.\n ");
		System.out.println("Please enter power (e.g. if 5 is chosen, message size will be 2^5) | Maximum value of 16:");
		power = userdata.readLine(); // assume messageSize was 100
		int value = Integer.parseInt(power);
		if((value > 16) || (value < 1)){
			System.out.println("Error: Power input has to be between 1 and 16.");
			return;
		}
		messageSize = Math.pow(2.0, Double.parseDouble(power));
		toServer.writeBytes(String.valueOf(messageSize) + '\n');
		String data = "";
		double messageCount = 10485760 / messageSize; // 10 MB divided by message size gives us how many messages need to be sent
		while(count < messageSize){
			data = data + "D";
			count++;
		}
		count = 0;
		System.out.println("Sending messages using TCP pure streaming... ");
		TimeUnit.SECONDS.sleep(1);
		final long startTime = System.currentTimeMillis();
		while(count < messageCount){
			//System.out.println("*Sending message to server...");
			toServer.writeBytes(data + '\n'); // send the line to the server
			//String result = fromServer.readLine(); // read a one-line result
			//System.out.println("Data sent.\n");
			count++;
		}
		final long endTime = System.currentTimeMillis();
		TimeUnit.SECONDS.sleep(1);
		System.out.println("Messages sent: " + messageCount);
		System.out.println("Bytes sent: " + messageCount * messageSize);
		long totalTime = endTime - startTime;
		double totalSec =  totalTime / 1000.0;
		System.out.println("Total execution time: " + (totalSec) );
		// total transmit time will be here
		sock.close(); // and we’re done
	}

	public static void tcp_stop_and_wait(String host, int port) throws Exception {
		String line; // user input
		double messageSize;
		String power;
		int count = 0; 
		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
		Socket sock = new Socket(host, port); 	// connect to localhost port 12345
		DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		System.out.println("\n*Client is running.\n ");
		System.out.println("Please enter power (e.g. if 5 is chosen, message size will be 2^5) | Maximum value of 16:");
		power = userdata.readLine(); // assume messageSize was 100
		int value = Integer.parseInt(power);
		if((value > 16) || (value < 1)){
			System.out.println("Error: Power input has to be between 1 and 16.");
			return;
		}
		messageSize = Math.pow(2.0, Double.parseDouble(power));
		toServer.writeBytes(String.valueOf(messageSize) + '\n');
		String data = "";
		double messageCount = 3145728 / messageSize; // 3 MB divided by message size gives us how many messages need to be sent
		while(count < messageSize){
			data = data + "D";
			count++;
		}
		count = 0;
		System.out.println("Sending messages... ");
		TimeUnit.SECONDS.sleep(1);
		final long startTime = System.currentTimeMillis();
		while(count < messageCount){
			toServer.writeBytes(data + '\n'); // send the line to the server
			String result = fromServer.readLine(); // read a one-line result
			count++;
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("Messages sent: " + messageCount);
		System.out.println("Bytes sent: " + messageCount * messageSize);
		long totalTime = endTime - startTime;
		double totalSec =  totalTime / 1000.0;
		System.out.println("Total execution time: " + (totalSec) );
		TimeUnit.SECONDS.sleep(3);
		sock.close(); // and we’re done
	}

	public static void udp_stop_and_wait(String host, int port) throws Exception {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      	InetAddress IPAddress = InetAddress.getByName(host);
      	DatagramSocket clientSocket = new DatagramSocket();
      	double messageSize;
      	int count = 0;
		System.out.println("Please enter power (if 5 is chosen, message size will be 2^5) | Maximum value of 15:");
		String power = inFromUser.readLine();
		int value = Integer.parseInt(power);
		if((value > 15) || (value < 1)){
			System.out.println("Error: Power input has to be between 1 and 15.");
			return;
		}
      	messageSize = Math.pow(2.0, Double.parseDouble(power));
      	byte[] sendData = new byte[(int)messageSize];
      	byte[] receiveData = new byte[1024];
      	byte[] sendSize = new byte[1024];
      	sendSize = power.getBytes("UTF-8");
      	double messageCount = 10485760 / messageSize; // 1 GB divided by message size gives us how many messages need to be sent
      	DatagramPacket sizePacket = new DatagramPacket(sendSize, sendSize.length, IPAddress, port);
	    clientSocket.send(sizePacket);
      	System.out.println("Sending packets...");
      	final long startTime = System.currentTimeMillis();
      	while(true){
	      	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
	      	clientSocket.send(sendPacket);
	      	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      	clientSocket.receive(receivePacket);
	      	count++;
	      	if(count>=messageCount){
	      		break;
	      	}
      	}
      	final long endTime = System.currentTimeMillis();
      	System.out.println("Messages sent: " + messageCount);
		System.out.println("Bytes sent: " + messageCount * messageSize);
		long totalTime = endTime - startTime;
		double totalSec =  totalTime / 1000.0;
		System.out.println("Total execution time: " + (totalSec) );
      	clientSocket.close();
	}

	public static void udp_pure(String host, int port) throws Exception {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      	InetAddress IPAddress = InetAddress.getByName(host);
      	DatagramSocket clientSocket = new DatagramSocket();
      	double messageSize;
      	int count = 0;
		System.out.println("Please enter power (e.g. if 5 is chosen, message size will be 2^5) | Maximum value of 15:");
      	String power = inFromUser.readLine();
      	int value = Integer.parseInt(power);
		if((value > 15) || (value < 1)){
			System.out.println("Error: Power input has to be between 1 and 15.");
			return;
		}
      	messageSize = Math.pow(2.0, Double.parseDouble(power));
      	byte[] sendData = new byte[(int)(messageSize)];
      	byte[] receiveData = new byte[1024];
      	byte[] sendSize = new byte[1024];
      	sendSize = power.getBytes("UTF-8");
      	double messageCount = 10485760 / messageSize; // 1 GB divided by message size gives us how many messages need to be sent
      	DatagramPacket sizePacket = new DatagramPacket(sendSize, sendSize.length, IPAddress, port);
	    clientSocket.send(sizePacket);
      	System.out.println("Sending packets...");
      	final long startTime = System.currentTimeMillis();
      	while(hostAvailabilityCheck(port)){
	      	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
	      	clientSocket.send(sendPacket);
      	}
      	final long endTime = System.currentTimeMillis();
      	System.out.println("Messages sent: " + messageCount);
		System.out.println("Bytes sent: " + messageCount * messageSize);
		long totalTime = endTime - startTime;
		double totalSec =  totalTime / 1000.0;
		System.out.println("Total execution time: " + (totalSec) );
      	clientSocket.close();
	}

	public static void main(String args []) throws Exception {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String transport_protocol = args[2];
		String ack_protocol = args[3];
		if(transport_protocol.equals("tcp") && ack_protocol.equals("stop-and-wait")){
			tcp_stop_and_wait(host, port);
		}
		else if(transport_protocol.equals("udp") && ack_protocol.equals("stop-and-wait")){
			udp_stop_and_wait(host, port);
		}
		else if(transport_protocol.equals("tcp") && ack_protocol.equals("pure")){
			tcp_pure(host, port);
		}
		else if(transport_protocol.equals("udp") && ack_protocol.equals("pure")){
			udp_pure(host, port);
		}
		else{
			System.out.println("Input not valid.\n");
		}
	}

}