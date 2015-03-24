package com.currencyfair.wizard.app.resource;

import com.currencyfair.wizard.app.configuration.WizardConfiguration;
import com.currencyfair.wizard.app.dao.MessageDAOImpl;
import com.currencyfair.wizard.app.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.google.common.base.Optional;
import com.yammer.dropwizard.assets.ResourceNotFoundException;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
public class WizardResource {

	private MessageDAOImpl messageDaoImpl = new MessageDAOImpl();
	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

	@GET
	@Timed
	@Path("/countries/messages/count")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCountryMessageCount() {
		countAllCountryMessages();
		return countAllCountryMessages().toString();
	}
	
	@GET
	@Timed
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMessageById(@PathParam("id") String id) {
		Message msg = messageDaoImpl.getMessageById(id);
		JSONObject jso;
		try {
			System.out.println(ow.writeValueAsString(msg) + "!!!");
			jso = new JSONObject(ow.writeValueAsString(msg));
			return jso.toString();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		}
		return null;
	}
	
	@GET
	@Timed
	@Path("/users/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMessageByUserId(@PathParam("userId") String userId) {
		System.out.println("userId: " + userId);
		List<Message> messages = messageDaoImpl.getMessagesByUserId(userId);
		return getMessageHelper(messages);
	}
	
	@GET
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllMessages() {
		List<Message> messages = messageDaoImpl.getAllMessages();
		return getMessageHelper(messages);
	}
	
	protected JSONArray countAllCountryMessages() {
		JSONObject curr;
		JSONArray metricArray = new JSONArray();
		JSONArray metricArrayEl;
		try {
			JSONArray countryList = new JSONArray(WizardConfiguration.countryArrayString);
			curr = new JSONObject();
			int count = 0;
			String countryName;
			for(int i = 0; i < countryList.length(); i++) {
				metricArrayEl = new JSONArray();
				curr = countryList.getJSONObject(i);
				countryName = curr.getString("name");
				count = messageDaoImpl.getAllMessagesByOriginatingCountry(curr.getString("code")).size();
				
				metricArrayEl.put(countryName);
				metricArrayEl.put(count);
				
				metricArray.put(metricArrayEl);
			}
			return metricArray;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getMessageHelper(List<Message> list) {
		JSONArray allMessagesJSONArray = new JSONArray();
		try {			
			for(Message msg:list) {
				allMessagesJSONArray.put(new JSONObject(ow.writeValueAsString(msg)));
			}
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (JSONException jse) {
			jse.printStackTrace();
		}
		return allMessagesJSONArray.toString();
	}

}
