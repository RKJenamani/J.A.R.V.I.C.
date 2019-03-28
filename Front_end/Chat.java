import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;


public class Chat extends JFrame
{

	InetAddress target_ip;
	int target_port;
	InetAddress ip;
	JTextArea msgbox;
	JFrame gui;
	JTextField text;
	int port;


	public static void main(String[] args)
	{
		new Chat();
	}

	Chat()
	{

		try
		{
			ip=InetAddress.getLocalHost();
			System.out.println(ip);
		}
		catch
		(UnknownHostException er)
		{;}

		gui =new JFrame();

		JLabel l1=new JLabel("Enter target's IP:");
		l1.setBounds(50,100,250,30);
		gui.add(l1);

		JTextField t1 = new JTextField();
		t1.setBounds(300,100,300,50);
		gui.add(t1);

		JLabel l2=new JLabel("Enter your listening port:");
		l2.setBounds(50,150,250,30);
		gui.add(l2);

		JTextField t2 = new JTextField();
		t2.setBounds(300,150,300,50);
		gui.add(t2);

		JLabel l3=new JLabel("Enter target's listening port:");
		l3.setBounds(50,200,250,30);
		gui.add(l3);

		JTextField t3 = new JTextField();
		t3.setBounds(300,200,300,50);
		gui.add(t3);

		msgbox= new JTextArea(300,400);
		msgbox.setEditable(false);
		JScrollPane scroll= new JScrollPane(msgbox);

		scroll.setBounds(100,300,350,500);
		gui.add(scroll);

		JLabel l4 =new JLabel("Enter message:");
		l4.setBounds(20,820,200,50);
		gui.add(l4);

		text= new JTextField();
		text.setBounds(250,820,300,50);
		gui.add(text);

		JButton send = new JButton("Send");
		send.setBounds(250,900,100,50);
		gui.add(send);

		send.addActionListener(new SendingListener());

		JButton b1=new JButton("Submit");
		b1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				try
				{
					target_ip=InetAddress.getByName(t1.getText());
				}
				catch
				(UnknownHostException er)
				{;}

				port=Integer.parseInt(t2.getText());
				target_port=Integer.parseInt(t3.getText());

				Server obj = new Server();
				Thread t=new Thread(obj);
				t.start();
				//talk();
			}
		});

		b1.setBounds(200,250,80,30);
		gui.add(b1);

		gui.setSize(600,1000);
		gui.setLayout(null);
		gui.setVisible(true);

	
	}

	class SendingListener implements ActionListener 
	{
    	public void actionPerformed(ActionEvent ae)
    	{
			try
			{
				DatagramSocket client =new DatagramSocket();

				byte[] data= new byte[1024];			
				data= text.getText().getBytes();
				//System.out.println(text.getText());
				msgbox.append("\n"+"SENT- "+text.getText());
				DatagramPacket sp= new DatagramPacket(data,data.length,target_ip,target_port);
				client.send(sp);
				client.close();  
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
    					DatagramSocket rec=new DatagramSocket(port);

	    				byte[] receive= new byte[1024];

	    				DatagramPacket rp= new DatagramPacket(receive, receive.length);
						rec.receive(rp);
						String str= new String(rp.getData());
						msgbox.append("\n"+"Recieved- " + str);
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