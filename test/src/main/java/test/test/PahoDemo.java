package test.test;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PahoDemo implements MqttCallback {

	private MqttClient sampleClient;
	private String topic = "TestPIP";
	private int qos = 2;
	private int received = 0;

	public static void main(String[] args) throws MqttException, InterruptedException {
		PahoDemo client;
//		client = new PahoDemo("c1");
//		client.close();
//
//		client = new PahoDemo("c2");
//		client.send();
//		client.close();
		
		client = new PahoDemo("Fratica");
		client.send();
		client.subscribe();
		client.waitFor(100);
		client.close();
	}

	public PahoDemo(String clientId) {
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

		send("Fratica: eu sunt fratica");

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
		System.out.println("got msg:" + message);
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

}
