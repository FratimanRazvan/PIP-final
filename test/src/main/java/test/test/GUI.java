package test.test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class GUI {
	JFrame frame;
	private JTextField SubscribeText;
	String topic="topictest";
	int flag=0;
	Server client = new Server();

	public class CustomOutputStream extends OutputStream {
		private JTextArea textArea;

		public CustomOutputStream(JTextArea textArea) {
			this.textArea = textArea;
		}

		@Override
		public void write(int b) throws IOException {
			textArea.append(String.valueOf((char)b));
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.update(textArea.getGraphics());
		}
	}

	public GUI() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setVisible(true);  
		frame.setBounds(100, 100, 941, 543);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.BLACK);

		final JTextArea Chatlog = new JTextArea(20, 40);
		Chatlog.setBounds(1, 1, 324, 364);
		Chatlog.setText("ChatLog...\n");
		Chatlog.setEditable(false);
		frame.getContentPane().add(Chatlog);
		frame.getContentPane().setLayout(null);
		PrintStream printStream = new PrintStream(new CustomOutputStream(Chatlog));
		System.setOut(printStream);
		System.setErr(printStream);


		JScrollPane  scrollableTextArea = new JScrollPane(Chatlog);  
		scrollableTextArea.setBounds(10, 5, 649, 383);
		scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
		scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
		frame.getContentPane().add(scrollableTextArea);

		final JTextField Message = new JTextField(20);
		Message.setBounds(10, 403, 485, 77);
		Message.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Message.setText(null);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				Message.setText(null);
			}
		});
		Message.setText("Enter Message..");
		frame.getContentPane().add(Message);

		JButton Publish = new JButton("Publish");
		Publish.setBackground(Color.ORANGE);



		//pt a lua textul din text field message si a-l afisa in text area Chatlog
		Publish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String s="Fratica "+Message.getText();//ia mesajul din casuta jtext publish si il pune in chatlog

				if(flag==1){//verificare pentru server conectat sau nu
					try {
						client.send(s, topic);
					} catch (MqttException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					System.out.println("Server deconectat");
				}


			}
		});

		Publish.setBounds(519, 403, 140, 77);
		frame.getContentPane().add(Publish);
		Message.setColumns(10);
		//Connect		
		JButton Connect = new JButton("Connect");
		Connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.Server("Fratica");
				try {
					client.subscribe(topic);
				} catch (MqttException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Connected");
				System.out.println("Standard topic: "+topic);
				flag=1;//flag pus pe 1 cand server conectat

			}
		});
		Connect.setBounds(684, 10, 84, 47);
		frame.getContentPane().add(Connect);

		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//disconect
				try {
					client.close();
					flag=0;//flag pus pe 0 cand server deconectat
				} catch (MqttException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		btnDisconnect.setBounds(799, 10, 84, 47);
		frame.getContentPane().add(btnDisconnect);
		//Subscribe		
		JButton btnSubscribe = new JButton("Subscribe");
		btnSubscribe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				topic=SubscribeText.getText();
				try {
					client.subscribe(topic);
				} catch (MqttException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("New topic: "+topic);

			}
		});
		btnSubscribe.setBounds(746, 115, 84, 47);
		frame.getContentPane().add(btnSubscribe);
		//	subscribe	
		SubscribeText = new JTextField(10);
		SubscribeText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SubscribeText.setText(null);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				SubscribeText.setText(null);
			}
		});
		SubscribeText.setText("Enter Topic..");
		SubscribeText.setBounds(669, 178, 234, 210);
		frame.getContentPane().add(SubscribeText);
	}
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					GUI window = new GUI();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
}
