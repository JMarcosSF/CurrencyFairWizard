package com.currencyfair.wizard.process.processor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;

import org.json.JSONException;
import org.json.JSONObject;

import com.currencyfair.wizard.app.configuration.WizardConfiguration;
import com.currencyfair.wizard.app.model.Message;
import com.google.common.util.concurrent.RateLimiter;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class MessageProcessor {

	// For demonstration purposes, rate is "5000 permits per second"
	protected final RateLimiter rateLimiter = RateLimiter.create(5000.0);
	private final static String QUEUE_NAME = "currencyfair_wizardQ";

	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private QueueingConsumer consumer;
	
	private Message msg;

	public MessageProcessor() {
		try{			
			factory = new ConnectionFactory();
			factory.setHost("localhost");
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			consumer = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, consumer);
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		}catch(IOException e) {
			e.printStackTrace();
		}

		while (true) {
			System.out.println("IN WHILE!!!!!!!!!!");
			try {
				QueueingConsumer.Delivery delivery;
				System.out.println("IN TRY!!!!!!!!!!");
				delivery = consumer.nextDelivery();
				String recMessage = new String(delivery.getBody());
				System.out.println("\n[x] recMsg: " + recMessage);
				
				rateLimiter.acquire();
				System.out.println("\n Creating MESSAGE!!!!!!");
				createMessage(recMessage);
				
				System.out.println("MESSAGE WRITE TO MONGODB COMPLETE!!!!!!\n");
			} catch (ShutdownSignalException e) {
				e.printStackTrace();
			} catch (ConsumerCancelledException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	void createMessage(String message) {
		WizardConfiguration conf = new WizardConfiguration();
		Mongo mongo;
		
		JSONObject json;
//		Format: 24-JAN-15 10:27:44 
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
		Date date;
		try {
			json = new JSONObject(message);
			date = formatter.parse(json.getString("timePlaced"));
			msg = new Message();
			msg.setUserId(json.getString("userId"));
			msg.setCurrencyFrom(json.getString("currencyFrom"));
			msg.setCurrencyTo(json.getString("currencyTo"));
			msg.setAmmountSell(json.getInt("amountSell"));
			msg.setAmmountBuy(json.getInt("amountBuy"));
			msg.setRate(json.getDouble("rate"));
			msg.setTimePlaced(date);
			msg.setOriginatingCountry(json.getString("originatingCountry"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mongo = new Mongo(conf.mongohost, conf.mongoport);
			DB db = mongo.getDB(conf.mongodb);
			JacksonDBCollection<Message, String> messages =
					JacksonDBCollection.wrap(db.getCollection("messages"), Message.class, String.class);
			WriteResult<Message, String> result = messages.insert(msg);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
