import java.io.*;
import java.net.*;
import java.util.concurrent.*; 

public class Server {
	public static void tcp_pure(int port) throws Exception {	
		ServerSocket svc = new ServerSocket(port, 5); // listen on port 12345
		for (;;) {
			Socket conn = svc.accept(); // wait for a connection
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());
			String line; // read the data from the client
			String size;
			int messagesReceived = 0;
			System.out.println("\n*Server is running.\n ");
			while((size = fromClient.readLine()) != null){
				String result = size + "\n"; // do the work
				toClient.writeBytes(result); // send the result
				break;
			}
			double totalExpected = Double.parseDouble(size);
			double messageCount = 10485760 / totalExpected; // 1 GB divided by message size gives us how many messages need to be sent
			while ((line = fromClient.readLine()) != null) {
				messagesReceived++;
				if(messagesReceived>=messageCount){
					break;
				}
			}
			System.out.println("\nTransport protocol used: TCP\n");
			System.out.println("\nAcknowledgement protocol used: pure streaming\n");
			System.out.println("Messages read: " + messagesReceived + "\n");
			double bytesRead = messagesReceived * Double.parseDouble(size);
			System.out.println("Bytes read: " + bytesRead + "\n");
			System.out.println("\n");
			System.out.println("Server exiting\n");
			conn.close(); // close connection
			svc.close(); // stop listening
		}
	}
	public static void tcp_stop_and_wait(int port) throws Exception {	
		ServerSocket svc = new ServerSocket(port, 5); // listen on port 12345
		for (;;) {
			Socket conn = svc.accept(); // wait for a connection
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());
			String line; // read the data from the client
			String size;
			int messagesReceived = 0;
			System.out.println("\n*Server is connected to client.\n ");
			while((size = fromClient.readLine()) != null){
				String result = size + "\n"; // do the work
				toClient.writeBytes(result); // send the result
				break;
			}
			double totalExpected = Double.parseDouble(size);
			double messageCount = 3145728 / totalExpected; // 3 MB divided by message size gives us how many messages need to be sent
			while ((line = fromClient.readLine()) != null) {
				String result = "D\n"; // do the work
				toClient.writeBytes(result); // send the result
				messagesReceived++;
				if(messagesReceived>=messageCount){
					break;
				}
			}
			System.out.println("\nTransport protocol used: TCP\n");
			System.out.println("\nAcknowledgement protocol used: stop-and-wait\n");
			System.out.println("Messages read: " + messagesReceived + "\n");
			double bytesRead = messagesReceived * Double.parseDouble(size);
			System.out.println("Bytes read: " + bytesRead + "\n");
			System.out.println("\n");
			System.out.println("Server exiting\n");
			TimeUnit.SECONDS.sleep(1);
			conn.close(); // close connection
			svc.close(); // stop listening
		}
	}

	public static void udp_stop_and_wait(int port) throws Exception {
		DatagramSocket serverSocket = new DatagramSocket(port);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        int messagesReceived = 0;
        DatagramPacket sizePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(sizePacket);
        String size = new String( sizePacket.getData());
        double messageSize = Math.pow(2.0, Double.parseDouble(size));
        System.out.println("Size: " + messageSize + "\n");
        double totalExpected = messageSize;
        double messageCount = 10485760 / totalExpected; // 10 MB divided by message size gives us how many messages need to be sent
        System.out.println("\n*Server is running.\n ");
        while(true){
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String( receivePacket.getData());
			//System.out.println("RECEIVED: " + sentence);
			InetAddress IPAddress = receivePacket.getAddress();
			int portData = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portData);
			serverSocket.send(sendPacket);
			messagesReceived++;
			if(messagesReceived>=messageCount){
				break;
			}
        }
        System.out.println("\nTransport protocol used: UDP\n");
        System.out.println("\nAcknowledgement protocol used: stop-and-wait\n");
		System.out.println("Messages read: " + messagesReceived + "\n");
		double bytesRead = messagesReceived * messageSize;
		System.out.println("Bytes read: " + bytesRead + "\n");
		System.out.println("\n");
		System.out.println("Server exiting\n");
        serverSocket.close();
	}

	public static void udp_pure(int port) throws Exception {
		DatagramSocket serverSocket = new DatagramSocket(port);
        byte[] receiveData = new byte[1024];
        int messagesReceived = 0;
        DatagramPacket sizePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(sizePacket);
        String size = new String( sizePacket.getData());
        double messageSize = Math.pow(2.0, Double.parseDouble(size));
        System.out.println("Size: " + messageSize + "\n");
        double totalExpected = messageSize;
        double messageCount = 10485760 / totalExpected; // 10 MB divided by message size gives us how many messages need to be sent
        System.out.println("\n*Server is running.\n ");
        while(true){
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);			
			messagesReceived++;
			if(messagesReceived>=messageCount){
				break;
			}
        }
        System.out.println("\nTransport protocol used: UDP\n");
        System.out.println("Acknowledgement protocol used: pure streaming\n");
		System.out.println("Messages read: " + messagesReceived + "\n");
		double bytesRead = messagesReceived * messageSize;
		System.out.println("Bytes read: " + bytesRead + "\n");
		System.out.println("\n");
		System.out.println("Server exiting\n");
        serverSocket.close();
	}

	public static void main(String args[]) throws Exception {	
		int port = Integer.parseInt(args[0]);
		String transport_protocol = args[1];
		String ack_protocol = args[2];
		if(transport_protocol.equals("tcp") && ack_protocol.equals("stop-and-wait")){
			tcp_stop_and_wait(port);
		}
		else if(transport_protocol.equals("udp") && ack_protocol.equals("stop-and-wait")){
			udp_stop_and_wait(port);
		}
		else if(transport_protocol.equals("tcp") && ack_protocol.equals("pure")){
			tcp_pure(port);
		}
		else if(transport_protocol.equals("udp") && ack_protocol.equals("pure")){
			udp_pure(port);
		}
		else{
			System.out.println("Input not valid.\n");
		}
	}
} 
