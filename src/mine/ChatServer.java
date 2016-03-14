package mine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.net.*;

import javax.swing.*;
import javax.swing.border.Border;

public class ChatServer extends JFrame 
{

	JButton startButton, stopButton;
	JTextField textBox;
	JTextArea textWindow;
	JLabel label;
	JPanel upperPanel, lowerPanel;	
	Border border;
	
	ServerSocket serverSocket;
	Socket clientSocket;
	ObjectOutputStream outputStream;
	ObjectInputStream inputStream;
	boolean keepRunning;
	boolean talking;
	
	
	public ChatServer()
	{
		super("Server");
		setSize(350, 400);
		setLayout(new BorderLayout());
		border = BorderFactory.createLineBorder(Color.BLACK);
		
		//components
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");
		textBox = new JTextField(20);
		textWindow = new JTextArea(20,20);
		label = new JLabel("Enter Text: ");
		
		//add border to textWindow
		border = BorderFactory.createLineBorder(Color.BLACK, 2);
		textWindow.setBorder(border);
		textBox.setBorder(border);
		
		//panels
		lowerPanel = new JPanel();
		upperPanel = new JPanel();
		
		//add components to panels
		lowerPanel.add(startButton);
		lowerPanel.add(stopButton);
		upperPanel.add(label);
		upperPanel.add(textBox);
		
		//add components to main window
		add(upperPanel, BorderLayout.NORTH);
		add(textWindow, BorderLayout.CENTER);
		add(lowerPanel, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		ableToType(false);
		
		//startbutton action
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						keepRunning = true;
						talking = true;
						startServer();
						
					}
				});
				thread.start();
				
			}
		});
		
		//textBox action
		textBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String temp = e.getActionCommand();
				sendMessageThroughSocket(temp);
				clearTextBox();
				
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				closeConnections();
				
			}
		});
		
	}
	
	public static void main(String[] args) 
	{
		new ChatServer();

	}
	
	public void startServer()
	{
		try {
			serverSocket = new ServerSocket(8888);
			while(keepRunning)
			{
				waitForConnections();
				setupStreams();
				ableToType(true);
				listenForIncomingMessages();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			closeConnections();
		}
		
	}
	
	public void waitForConnections() throws IOException
	{
		addMessageToWindow("Waiting for connections...");
		clientSocket = serverSocket.accept();
	}
	
	private void setupStreams() throws IOException
	{
		addMessageToWindow("Starting to setup streams...");
		outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		outputStream.flush();
		inputStream = new ObjectInputStream(clientSocket.getInputStream());
		addMessageToWindow("Streams are setup...");
		
	}
	
	
	public void addMessageToWindow(String message)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				textWindow.append(message + "\n");
				
			}
		});
	}
	
	public void sendMessageThroughSocket(String message)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					String temp = "Server: " + message;
					outputStream.writeObject(temp);
					outputStream.flush();
					addMessageToWindow(temp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	public void listenForIncomingMessages() 
	{
		while(talking)
		{
			String incomingMessage = "";
			try {
				incomingMessage = (String) inputStream.readObject();
				addMessageToWindow(incomingMessage);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void closeConnections()
	{
		try {
			keepRunning = false;
			talking = false;
			addMessageToWindow("Closing connections...");
			outputStream.close();
			inputStream.close();
			clientSocket.close();
			addMessageToWindow("Connections closed...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ableToType(boolean canType)
	{
		textBox.setEditable(canType);
	}
	
	public void clearTextBox()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				textBox.setText("");
				
			}
		});
	}
	
	public void test()
	{
		Thread test = new Thread(){
			public void run(){
				
			}
		};
	}
	

}
