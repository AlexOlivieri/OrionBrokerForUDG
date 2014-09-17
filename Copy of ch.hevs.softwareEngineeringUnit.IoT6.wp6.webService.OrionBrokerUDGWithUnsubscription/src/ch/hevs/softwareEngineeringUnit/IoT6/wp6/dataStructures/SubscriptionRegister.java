package ch.hevs.softwareEngineeringUnit.IoT6.wp6.dataStructures;

import java.util.Hashtable;

public class SubscriptionRegister {
	
	public static SubscriptionRegister instance = null;
	
	private Hashtable<String, String> subscriptionsActive;

	public SubscriptionRegister(){
		
		this.subscriptionsActive = new Hashtable<String, String>();
	}
	
	public static SubscriptionRegister getInstance(){
			
		if(instance == null){
			instance = new SubscriptionRegister();
		}
		return instance;
	}
	
	public synchronized void addSubscription(String subscriptionContext, String subscriptionId){
		
		subscriptionsActive.put(subscriptionContext, subscriptionId);
	}
	
	public synchronized void deleteSubscription(String subscriptionContext){
		
		subscriptionsActive.remove(subscriptionContext);
	}
	
	public synchronized String getSubscription(String subscriptionContext){
		
		String subscription = subscriptionsActive.get(subscriptionContext);
		return subscription;
	}

}
