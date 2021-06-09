package test.test;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

class Server implements MqttCallback {

	private MqttClient sampleClient;
	private int qos = 2;
	private int received = 0;
	public String msg_Server="1";
	public MqttMessage msg1;

	public void Server(String clientId) {
		String broker = "tcp://broker.hivemq.com:1883";
		//      
		MqttClientPersistence persistence = new MemoryPersistence();

		try {
			sampleClient = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			sampleClient.setCallback(this);

			IMqttToken tok = sampleClient.connectWithResult(connOpts);

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

	void subscribe(String topic) throws MqttException {
		sampleClient.subscribe(topic, qos);
	}


	void send(String msg,String topic) throws MqttException {
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
		System.out.println("" + message);

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}

}
