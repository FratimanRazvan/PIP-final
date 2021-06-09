package test.test;
import javax.swing.*; 
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class App implements MqttCallback {

	private MqttClient sampleClient;
	private String topic = "mihalache";
	private int qos = 0;
	private int received = 0;
	
	public App(String clientId) {
		String broker = "tcp://broker.hivemq.com:1883";
		//      
		MqttClientPersistence persistence = new MemoryPersistence();

		try {
			sampleClient = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(false);
			sampleClient.setCallback(this);

			System.out.println("Connecting to broker: " + broker);
			IMqttToken tok = sampleClient.connectWithResult(connOpts);
			System.out.println("Connected sessionPresent:" + tok.getSessionPresent() + " complete:" + tok.isComplete());

		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
	}

	public void close() throws MqttException {
		sampleClient.disconnect();
		System.out.println("Disconnected\n");
	}

	private void subscribe() throws MqttException {
		sampleClient.subscribe(topic, qos);
	}

	private void send() throws MqttException {

		send("Gherman: Mesaj din ceruri");
		System.out.println("Message published");
	}

	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void send(String msg) throws MqttException {
		System.out.println("Publishing message: " + msg);
		MqttMessage message = new MqttMessage(msg.getBytes());
		message.setQos(qos);
		sampleClient.publish(topic, message);
	}

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("lost connection");
	}

	@Override
	public synchronized void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println(message);
		received();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("delivery complete");
	}

	private synchronized void waitFor(int nr) throws InterruptedException {
		do {
			System.out.println("waitFor:" + nr + " received:" + received);
			wait();
			if (received >= nr) {
				System.out.println("waitFor ret received:" + received);
				return;
			}
		} while (true);
	}

	private synchronized void received() {
		received++;
		notifyAll();
	}
	////////////////////////////////////////////////////////////////////////////////
	

	public static void main(String[] args) throws MqttException, InterruptedException {
		JFrame gui = new JFrame(); // 1
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //2
		gui.setSize(500, 300); //3
		gui.setTitle("Test GUI");
		gui.getContentPane().setBackground(new Color(0, 0, 0));
		
		JButton btn = new JButton("Apasa!");
		// btn.setText("Apasa!");
		gui.add(btn);
		gui.add(new JButton("aba"));
		
		gui.setLayout(new FlowLayout());
		//gui.setLayout(null);
		//btn.setBounds(50, 200, 100, 30);
		gui.setVisible(true);
		
		App client;
		client = new App("Gherman");
		client.send();
		client.subscribe();
		client.waitFor(100);
		client.close();
		
		
		
	
	}
	
      

	
}
