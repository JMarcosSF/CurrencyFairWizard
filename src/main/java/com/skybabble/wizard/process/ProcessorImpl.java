package com.skybabble.wizard.process;

import com.google.common.util.concurrent.RateLimiter;

public abstract class ProcessorImpl implements QueueProcessor {
	
	// For demonstration purposes, rate is "5 permits per second"
	protected final RateLimiter rateLimiter = RateLimiter.create(5.0);

	// Starts thread at main() to handle Thread safe jobs in a queue
	public void beginProcess() {
		// Begin listening to updates at queue
		Thread thread = new Thread(this);
		thread.start();
	}

	/*
	 * Thread-safe method is called at start of application and listens for
	 * any incoming Message processing jobs to insert to MongoDB.
	 * (non-Javadoc)
	 * @see com.skybabble.wizard.rabbitmq.proxy.job.QueueProcessor#process(java.lang.Runnable)
	 */
	public void process(Runnable runJob) {
		synchronized (procQueueList) {
			procQueueList.add(runJob);
			// Wake up the thread which is currently awaiting the monitor
			procQueueList.notify();
		}
	}

}
