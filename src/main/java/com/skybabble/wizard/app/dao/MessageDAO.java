package com.skybabble.wizard.app.dao;

import java.util.List;

import org.json.JSONObject;

import com.skybabble.wizard.app.model.Message;

public interface MessageDAO {
	
	public List<Message> getAllMessages();
	
	public Message getMessageById(String id);
	
	public List<Message> getMessagesByUserId(String userId);
	
	public List<Message> getAllMessagesByOriginatingCountry(String countryCode);
	
}
