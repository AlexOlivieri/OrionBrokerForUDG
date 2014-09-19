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

public class ParsingPatientMovementsThread implements Runnable{
	
	private NotificationManager notificationManager;
	private JSONObject notification;
	
	private final String SubscriptionDeleted = "Deleted";
	
	public ParsingPatientMovementsThread(NotificationManager notificationManager, JSONObject notification){
		this.notificationManager = notificationManager;
		this.notification = notification;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void run() {
		
		Spreadsheet spreadsheet = new Spreadsheet("UDG");

		String subscriptionId = (String) notification.get("subscriptionId");
		
		if(notificationManager.getSubscriptionId() ==null || notificationManager.getSubscriptionId().equals("empty")){
			notificationManager.setSubscriptionId(subscriptionId);
		}
		
		
		JSONArray contextResponsesArray = (JSONArray) notification.get("contextResponses");
		JSONObject contextResponsesElement = new JSONObject();
		
		//JSONObject statusCode = new JSONObject();
		JSONObject contextElements = new JSONObject();
		
		JSONArray attributesArray = new JSONArray();
		JSONObject attributeElement = new JSONObject();
		
		//String attributeName = "";
		
		String id = "";
		String attributeName = "";
		String attributeContextValue = "";
		String elementType = "";
		
		String subscriptionName = "";
		
	
		
		if(contextResponsesArray != null){
		
			Iterator<JSONObject> contextResponsesIterator = contextResponsesArray.iterator();
			while (contextResponsesIterator.hasNext()) {
				
				contextResponsesElement = contextResponsesIterator.next();
				
				contextElements = (JSONObject) contextResponsesElement.get("contextElement");
				
				id = (String) contextElements.get("id");
				elementType = (String) contextElements.get("type");
				
				attributesArray = (JSONArray) contextElements.get("attributes");
				
				if(attributesArray != null){
					
					Iterator<JSONObject> attributeIterator = attributesArray.iterator();
					
					Date d = new Date();
					spreadsheet.setCell(1, 1, d.toGMTString());
					
					int cellRow = 1;
					
					CellEntry lastCellOccupied = spreadsheet.findLastOccupiedCell();
					if(lastCellOccupied != null){
						cellRow = lastCellOccupied.getCell().getRow();
					}
					
					////////////////////
					/////
					///// For testing purpose
					/////
					////////////////////
					
					while (attributeIterator.hasNext()) {
						
						attributeElement = attributeIterator.next();
						
						
						attributeName = (String) attributeElement.get("name");
						attributeContextValue = (String) attributeElement.get("value");	
						
						System.out.println(notificationManager.getSubscriptionId());
						
						spreadsheet.setCell(cellRow+1, 1,"Subscripion from Notification");
						spreadsheet.setCell(cellRow+1, 4,subscriptionId);
					}
				}
			}
		}
	}
}
