package com.skybabble.wizard.proxy;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.skybabble.wizard.app.model.Message;

/**
 * A Class which creates a runnable object
 * @author JMarcos
 *
 */
public class ProxyJob implements Runnable {

	private final static String QUEUE_NAME = "currencyfair_wizardQ";
	private String message;
	
	public ProxyJob(String message) {
		this.message = message;
	}
	
	/*
	 * Processed in ProcessorImpl's procQueue.
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			sendMessageToQueue(this.message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void sendMessageToQueue(String msg) throws IOException{

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		System.out.println("Sending message...");
		System.out.println(msg);
		channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
		System.out.println(" [x] Sent '" + msg + "'");
		System.out.println("Done sending message");
		
		channel.close();
		connection.close();

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
