// Java implementation for multithreaded chat client 
// Save file as Client.java 
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;

public class Client extends JFrame
{ 
	final static int ServerPort = 2003; 
	InetAddress target_ip;
	int target_port;
	InetAddress ip;
	JTextArea msgbox;
	JFrame gui;
	JTextField text;
	int port;
	Socket s;
	DataOutputStream dos;
	DataInputStream dis;
	public static void main(String[] args) throws UnknownHostException, IOException 
	{
		new Client();

	}

	Client() throws UnknownHostException, IOException 
	{
		
		try
		{
			ip = InetAddress.getByName("localhost"); 
			System.out.println(ip);
		}
		catch
		(UnknownHostException er)
		{;}
		s = new Socket(ip, ServerPort); 

		dos = new DataOutputStream(s.getOutputStream()); 
		dis = new DataInputStream(s.getInputStream()); 
		gui = new JFrame();

		msgbox= new JTextArea(100, 200);
		msgbox.setEditable(false);
		JScrollPane scroll= new JScrollPane(msgbox);

		scroll.setBounds(100, 150, 300, 500);
		gui.add(scroll);

		JLabel l4 = new JLabel("Enter:");
		l4.setBounds(20, 30, 60, 30);
		gui.add(l4);

		text = new JTextField();
		text.setBounds(100, 20, 300, 50);
		gui.add(text);

		JButton send = new JButton("Send");
		send.setBounds(200, 80, 100, 50);
		gui.add(send);
		gui.getRootPane().setDefaultButton(send);

		send.addActionListener(new SendingListener());

		Server obj = new Server();
		Thread t = new Thread(obj);
		t.start();
		
		gui.setSize(500, 800);
		gui.setLayout(null);
		gui.setVisible(true);

	
	}

	class SendingListener implements ActionListener 
	{
    	public void actionPerformed(ActionEvent ae)
    	{
			try
			{
				byte[] data = new byte[1024];			
				data = text.getText().getBytes();
				msgbox.append("\n" + "User- " + text.getText());
				dos.writeUTF(text.getText() + "\n"); 
				text.setText(""); 
			}
			catch(Exception er)
			{;}    			  				
  		}
    	
    }

    class Server implements Runnable
    {
    	public void run()
    	{
    		try
    		{
    			while(true)
    			{
    				try
    				{
	    				byte[] receive = new byte[1024];
						String msg = dis.readUTF(); 
						msgbox.append("\n" + "JARVIC- " + msg);
    				}
					catch(Exception er)
    				{;}
    			}
    		}
    		catch(Exception er)
    		{;}
    	}

    }
}
