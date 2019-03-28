// A Java program for a Client 
import java.net.*; 
import java.io.*; 

public class MyClient 
{ 
	// initialize socket and input output streams 
	private Socket socket	= null; 
	private DataInputStream input = null; 
	private DataInputStream input_stream = null; 
	private DataOutputStream out = null; 

	// constructor to put ip address and port 
	public MyClient(String address, int port) 
	{ 
		// establish a connection 
		try
		{ 
			socket = new Socket(address, port); 
			System.out.println("Connected"); 

			// takes input from terminal 
			input = new DataInputStream(System.in); 

			// takes input from socket
			input_stream = new DataInputStream(socket.getInputStream());

			// sends output to the socket 
			out = new DataOutputStream(socket.getOutputStream()); 
		} 
		catch(UnknownHostException u) 
		{ 
			System.out.println(u); 
		} 
		catch(IOException i) 
		{ 
			System.out.println(i); 
		} 
	}
	

	public void send_message()
	{
		// string to read message from input 
		String line = ""; 
		System.out.println("Sending...");
		try
		{ 
			System.out.println("Send message :");
			line = input.readLine(); 
			// System.out.println(line);
			out.writeUTF(line); 
			out.flush();
		} 
		catch(IOException i) 
		{ 
			System.out.println(i); 
		} 
	} 


	public void receive_message()
	{
		String line = "";
		try 
		{
			// System.out.println("I'm here!"); 
			line = input_stream.readLine();
			System.out.println("Server :" + line);
		}
		catch(IOException i)
		{
			System.out.println(i); 
		}
	}


	public void close_connection()
	{
		// close the connection 
		try
		{ 
			input.close(); 
			out.close(); 
			socket.close(); 
		} 
		catch(IOException i) 
		{ 
			System.out.println(i); 
		} 
	} 


	public static void main(String args[]) 
	{ 
		MyClient client = new MyClient("localhost", 2006); 
		DataInputStream input = new DataInputStream(System.in);
		String ch = "1";
		while (ch != "Exit" || ch != "exit") 
		{ 
			System.out.println("Send message?(Y/N):");
			try
			{
				ch = input.readLine();
			}
			catch(IOException i) 
			{ 
				System.out.println(i); 
			} 
			if(ch == "Y" || ch == "y")
			{
				System.out.println(ch); 
				client.send_message();
			}
			client.receive_message();
		}
		client.close_connection();
	} 

}



// // Java implementation for multithreaded chat client 
// // Save file as Client.java 

// import java.io.*; 
// import java.net.*; 
// import java.util.Scanner; 

// public class MyClient 
// { 
// 	final static int ServerPort = 2004; 

// 	public static void main(String args[]) throws UnknownHostException, IOException 
// 	{ 
// 		Scanner scn = new Scanner(System.in); 
		
// 		// getting localhost ip 
// 		InetAddress ip = InetAddress.getByName("localhost"); 
		
// 		// establish the connection 
// 		Socket s = new Socket(ip, ServerPort); 
		
// 		// obtaining input and out streams 
// 		DataInputStream dis = new DataInputStream(s.getInputStream()); 
// 		DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

// 		// sendMessage thread 
// 		Thread sendMessage = new Thread(new Runnable() 
// 		{ 
// 			@Override
// 			public void run() { 
// 				while (true) { 

// 					// read the message to deliver. 
// 					String msg = scn.nextLine(); 
					
// 					try { 
// 						// write on the output stream 
// 						dos.writeUTF(msg); 
// 					} catch (IOException e) { 
// 						e.printStackTrace(); 
// 					} 
// 				} 
// 			} 
// 		}); 
		
// 		// readMessage thread 
// 		Thread readMessage = new Thread(new Runnable() 
// 		{ 
// 			@Override
// 			public void run() { 

// 				while (true) { 
// 					try { 
// 						// read the message sent to this client 
// 						String msg = dis.readUTF(); 
// 						System.out.println(msg); 
// 					} catch (IOException e) { 

// 						e.printStackTrace(); 
// 					} 
// 				} 
// 			} 
// 		}); 

// 		sendMessage.start(); 
// 		readMessage.start(); 

// 	} 
// } 
