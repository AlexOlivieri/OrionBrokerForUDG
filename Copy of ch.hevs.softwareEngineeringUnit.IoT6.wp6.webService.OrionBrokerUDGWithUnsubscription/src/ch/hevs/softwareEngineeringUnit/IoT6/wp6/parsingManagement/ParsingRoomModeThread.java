package ch.hevs.softwareEngineeringUnit.IoT6.wp6.parsingManagement;

import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ch.hevs.se.IoT6.T6_5.OrionInterface.Boundary.SubscriberEntity;
import ch.hevs.se.IoT6.T6_5.OrionInterface.Entity.UnsubscriptionData;
import ch.hevs.softwareEngineeringUnit.IoT6.wp6.OrionBroker.NotificationManager;
import ch.hevs.softwareEngineeringUnit.IoT6.wp6.googleSpreadsheet.Spreadsheet;

import com.google.gdata.data.spreadsheet.CellEntry;
import com.sun.jersey.api.client.WebResource;

public class ParsingRoomModeThread implements Runnable{
	
	private NotificationManager notificationManager;
	private JSONObject notification;
	
	public ParsingRoomModeThread(NotificationManager notificationManager, JSONObject notification){
		this.notificationManager = notificationManager;
		this.notification = notification;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void run() {
		
		Spreadsheet spreadsheet = new Spreadsheet("UDG");
		
		JSONArray contextResponsesArray = (JSONArray) notification.get("contextResponses");
		JSONObject contextResponsesElement = new JSONObject();
		
		JSONObject contextElement = new JSONObject();
		
		JSONArray attributesArray = new JSONArray();
		JSONObject attributeElement = new JSONObject();
		
		String attributeContextValue = "";
		
		if(contextResponsesArray != null){
		
			Iterator<JSONObject> contextResponsesIterator = contextResponsesArray.iterator();
			while (contextResponsesIterator.hasNext()) {
				
				contextResponsesElement = contextResponsesIterator.next();
				
				contextElement = (JSONObject) contextResponsesElement.get("contextElement");

				attributesArray = (JSONArray) contextElement.get("attributes");
				
				if(attributesArray != null){
					
					Iterator<JSONObject> attributeIterator = attributesArray.iterator();
					
					Date d = new Date();
					spreadsheet.setCell(1, 1, d.toGMTString());
					
					int cellRow = 1;
					CellEntry lastCellOccupied = spreadsheet.findLastOccupiedCell();
					if(lastCellOccupied != null){
						cellRow = lastCellOccupied.getCell().getRow();
					}
					
					while (attributeIterator.hasNext()) {
						
						attributeElement = attributeIterator.next();

						//System.out.println(attributeElement);
						
						/////
						///// Important Value for decide the subscription
						/////
						attributeContextValue = (String) attributeElement.get("contextValue");
						
						checkAndEventuallySetTheSubscription(attributeContextValue);	
					}
				}
			}
		}
	}
	
	
	private void checkAndEventuallySetTheSubscription(String notificationValue){
		
		boolean isSubscribed = notificationManager.getSubscriptionToMovementValue();
		
		System.out.println("Subscription Value = " +isSubscribed);
		
		System.out.println(notificationManager.getSubscriptionId());
				
		if(isSubscribed == false){
			
			if(notificationValue.equals("MeetingMode")){
				
				System.out.println("Enter in false - MeetingMode");
				
				notificationManager.setSubscriptionToMovementValue(true);
				//notificationManager.setSubscriptionId(notificationManager.getSubscriptionId());
			}
		}else{
			if(!notificationValue.equals("MeetingMode")){
				notificationManager.setSubscriptionToMovementValue(false);
				String subscriptionId = notificationManager.getSubscriptionId();
				unsubscribe(subscriptionId);
			}
		}
	}
	
	private void unsubscribe(String subscriptionId){
		
		String url = "http://130.206.82.228:1026/NGSI10";
		
		SubscriberEntity subscriberEntity = new SubscriberEntity();
		WebResource webResource = subscriberEntity.connectEntity(url);
		
		unsubscribe(subscriberEntity, webResource, subscriptionId);
	}
	
	private void unsubscribe(SubscriberEntity subscriberEntity, WebResource webResource, String subscriptionId){
		
		UnsubscriptionData unsubscriptionData = new UnsubscriptionData();
		JSONObject contextPayload = unsubscriptionData.getData();
		
		subscriberEntity.unsubscribe(webResource, "unsubscribeContext", contextPayload);
	}
}