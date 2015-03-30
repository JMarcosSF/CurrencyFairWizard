package com.skybabble.wizard.process;

import java.util.LinkedList;

/**
 * API for working with Thread Safe jobs to run from a queue.
 * @author JMarcos
 *
 */
public interface QueueProcessor extends Runnable {

	// Linked list which manages messages/jobs received.
	LinkedList<Runnable> procQueueList = new LinkedList<Runnable>();
	
	// Thread thread = new Thread(this);
	// thread.start();
	public void beginProcess();	// Begin listening to updates at queue
	
	/*
	 * Thread-safe method is called at start of application and listens for
	 * any incoming Message processing jobs to insert to MongoDB.
	 * (non-Javadoc)
	 * @see com.currencyfair.wizard.rabbitmq.proxy.job.Processor#process(java.lang.Runnable)
	 */
    public void process(Runnable runJob);
    
}