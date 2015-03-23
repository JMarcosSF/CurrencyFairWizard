package com.currencyfair.wizard.proxy;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.currencyfair.wizard.process.ProcessorImpl;
/**
 * This class would normally be it's own Stand Alone component within the
 * stack. For the sake of simplicity and for this test run project, I
 * have decided to keep in within the same project as the component which
 * processes the received data.
 * 
 * Otherwise, it could easily be configured and deployed for multiple nodes
 * which point to the same endpoint.
 * 
 * It is responsible for receiving POST with a valid JSON String, validates,
 * does no further processing, then sends the String to RabbitMQ.
 * 
 * Although this is meant to be in it's own project, it still shares common classes
 * with the CurrencyFairWizard app.
 * 
 * @author JMarcos
 *
 */
@Path("/proxy")
@Produces(MediaType.APPLICATION_JSON)
public class ProxyResource extends ProcessorImpl{

	/*
	 * Handles POST requests by taking the data and sending to 
	 * rabbitMQ  via Thread Safe process()
	 */
	@POST
	@Path("/message")
	public Response sendToQueue(String message) {
		System.out.println("MESSAGE" + message);
		try {
			// Using JSONObject here to validate the JSON String.
			// Not directly sending the message to rabbitMQ.
			JSONObject msgReceived = new JSONObject(message);
			process(new ProxyJob(msgReceived.toString()));
		} catch (JSONException e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("'" + e.getMessage()).type(MediaType.APPLICATION_JSON).build();
		}
		
		return Response.status(Response.Status.OK).build();
	}
	
	/*
	 * Waits if queue is empty. Otherwise processes given jobs from
	 * within the LinkedList. Processing is rate limited to keep
	 * from overloading the environment.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true) {  
			while(procQueueList.isEmpty()) {
				synchronized (procQueueList) {
					try {
						// Queue is empty.
						// Waiting for further input from user
						procQueueList.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			// Guava rate limiter implementation
			rateLimiter.acquire();
			// Gets the head of the LinkedList, and runs it
			ProxyJob curr = (ProxyJob) procQueueList.remove();
			curr.run();

		}

	}

}