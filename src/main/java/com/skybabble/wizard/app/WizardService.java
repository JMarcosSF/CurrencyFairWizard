package com.skybabble.wizard.app;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import net.vz.mongodb.jackson.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.skybabble.wizard.app.configuration.WizardConfiguration;
import com.skybabble.wizard.app.model.Message;
import com.skybabble.wizard.app.resource.WizardResource;
import com.skybabble.wizard.process.*;
import com.skybabble.wizard.process.processor.MessageProcessor;
import com.skybabble.wizard.proxy.ProxyResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * Entry point for application
 * @author JMarcos
 *
 */
public class WizardService extends Service<WizardConfiguration>{

	public static void main(String[] args) throws Exception {
		new WizardService().run(args);

		// Starting ProxyResource Queue
		ProcessorImpl proxy = new ProxyResource();
		proxy.beginProcess();

		// Starting processor for messages from rabbitMQ
		MessageProcessor rabbitMQMessageProcessor = new MessageProcessor();

	}

	public String getName() {
		return "hello-world";
	}

	@Override
	public void initialize(Bootstrap<WizardConfiguration> bootstrap) {
		bootstrap.setName("skybabble_wizard");
	}

	@Override
	public void run(WizardConfiguration configuration, Environment environment)
			throws Exception {
		final String template = configuration.getTemplate();

		Mongo mongo = new Mongo(configuration.mongohost, configuration.mongoport);
		DB db = mongo.getDB(configuration.mongodb);
		JacksonDBCollection<Message, String> messages =
				JacksonDBCollection.wrap(db.getCollection("messages"), Message.class, String.class);
		MongoManaged mongoManaged = new MongoManaged(mongo);

		environment.manage(mongoManaged);
		environment.addHealthCheck(new MongoHealthCheck(mongo)); 
		environment.addResource(new WizardResource());
		environment.addResource(new ProxyResource());
		environment.addFilter(CrossOriginFilter.class, "/*")
		.setInitParam("allowedOrigins", "*")
		.setInitParam("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin")
		.setInitParam("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

	}

}
