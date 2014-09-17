package ch.hevs.softwareEngineeringUnit.IoT6.wp6.parsingManagement;

import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ch.hevs.softwareEngineeringUnit.IoT6.wp6.dataStructures.RoomsModeRegister;
import ch.hevs.softwareEngineeringUnit.IoT6.wp6.dataStructures.SubscriptionRegister;
import ch.hevs.softwareEngineeringUnit.IoT6.wp6.googleSpreadsheet.Spreadsheet;

import com.google.gdata.data.spreadsheet.CellEntry;

public class ParsingPatientMovementsThread extends Thread{
	
	private JSONObject notification;
	
	private final String SubscriptionDeleted = "Deleted";
	
	public ParsingPatientMovementsThread(JSONObject notification){
		this.notification = notification;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void run() {
		
		Spreadsheet spreadsheet = new Spreadsheet("UDG");

		String subscriptionId = (String) notification.get("subscriptionId");
		
		SubscriptionRegister subscription = SubscriptionRegister.getInstance();
		
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
					
					spreadsheet.setCell(cellRow+1, 1,"Subscripion from Notification");
					spreadsheet.setCell(cellRow+1, 4,subscriptionId);
					
					while (attributeIterator.hasNext()) {
						
						attributeElement = attributeIterator.next();
						
						
						attributeName = (String) attributeElement.get("name");
						attributeContextValue = (String) attributeElement.get("value");
						
						subscriptionName = attributeName.concat("_").concat(elementType).concat("_").concat(id);
						
						String subscriptionValue = subscription.getSubscription(subscriptionName);
						
						if(subscriptionValue == null){
							
							subscription.addSubscription(subscriptionName, subscriptionId);
							subscriptionValue = subscription.getSubscription(subscriptionName);
							
							spreadsheet.setCell(cellRow+3, 1,"Subscription Inserted into Hashmap");
							spreadsheet.setCell(cellRow+4, 1,"Subscription from Hashmap");
							spreadsheet.setCell(cellRow+4, 4,subscriptionValue);
							spreadsheet.setCell(cellRow+5, 1,"ContextValue");
							spreadsheet.setCell(cellRow+5, 4, attributeContextValue);
						
						}else if(!subscriptionValue.contains(SubscriptionDeleted)){
							
							RoomsModeRegister comfortModeRegister = RoomsModeRegister.getInstance();
							String roomIdentifier = elementType.concat("_").concat(id);
							String comfortModeValue = comfortModeRegister.getRoomComfortMode(roomIdentifier);
							if(comfortModeValue != null){
								spreadsheet.setCell(cellRow+3, 1, roomIdentifier);
								spreadsheet.setCell(cellRow+3, 4, comfortModeValue);
								spreadsheet.setCell(cellRow+4, 1,"Subscripion from Hashmap");
								spreadsheet.setCell(cellRow+4, 4,subscriptionValue);

								if(subscriptionValue.equals(subscriptionId) && comfortModeValue.equals("true")){
								
									spreadsheet.setCell(cellRow+4, 1,"Subscription already presents in the Hashmap");							
									spreadsheet.setCell(cellRow+6, 1, attributeContextValue);
									
									//TODO: ///To insert here your code to manage LED ALARM
									// You must use for it the variable: attributeContextValue
								}
							}
						}
					}
				}
			}
		}		
	}
}
