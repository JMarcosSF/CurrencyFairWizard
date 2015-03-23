package com.currencyfair.wizard.app.dao;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;

import com.currencyfair.wizard.app.configuration.WizardConfiguration;
import com.currencyfair.wizard.app.model.Message;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class MessageDAOImpl implements MessageDAO {

	private final WizardConfiguration conf = new WizardConfiguration();
	private final String COLLECTION_NAME = "messages";

	Mongo mongo;
	DB db;
	JacksonDBCollection<Message, String> messages;

	public MessageDAOImpl() {
		try {
			this.mongo = new Mongo(conf.mongohost, conf.mongoport);
			this.db = mongo.getDB(conf.mongodb);
			this.messages =
					JacksonDBCollection.wrap(db.getCollection(COLLECTION_NAME), Message.class, String.class);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public List<Message> getAllMessages() {
		DBCursor<Message> cursor = messages
				.find();
		return cursor.toArray();
	}

	public Message getMessageById(String id) {
		Message message = messages.findOneById(id);
		return message;
	}

	public List<Message> getMessagesByUserId(String userId) {
		DBCursor<Message> cursor = messages.find(DBQuery.all("userId", userId));
		List<Message> messageList = cursor.toArray();
		return messageList;
	}

	public List<Message> getAllMessagesByOriginatingCountry(String countryCode) {
		DBCursor<Message> cursor = messages
				.find(DBQuery.all("originatingCountry", countryCode));
		List<Message> messageList = cursor.toArray();
		return messageList;
	}

}
