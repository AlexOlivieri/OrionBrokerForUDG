package ch.hevs.softwareEngineeringUnit.IoT6.wp6.parsingManagement;

import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ch.hevs.softwareEngineeringUnit.IoT6.wp6.dataStructures.RoomsModeRegister;
import ch.hevs.softwareEngineeringUnit.IoT6.wp6.dataStructures.SubscriptionRegister;
import ch.hevs.softwareEngineeringUnit.IoT6.wp6.googleSpreadsheet.Spreadsheet;

import com.google.gdata.data.spreadsheet.CellEntry;

public class ParsingComfortCommandThread extends Thread{
	
	private JSONObject notification;
	
	public ParsingComfortCommandThread(JSONObject notification){
		this.notification = notification;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void run() {
		
		SubscriptionRegister subscription = SubscriptionRegister.getInstance();
		
		RoomsModeRegister comfortModeRegister = RoomsModeRegister.getInstance();
		
		Spreadsheet spreadsheet = new Spreadsheet("UDG");
		
		JSONArray contextResponsesArray = (JSONArray) notification.get("contextResponses");
		JSONObject contextResponsesElement = new JSONObject();
		
		JSONObject contextElement = new JSONObject();
		
		JSONArray attributesArray = new JSONArray();
		JSONObject attributeElement = new JSONObject();
		
		String id = "";
		String name = "";
		String attributeContextValue = "";
		String elementType = "";
		String subscriptionName = "";
		
		if(contextResponsesArray != null){
		
			Iterator<JSONObject> contextResponsesIterator = contextResponsesArray.iterator();
			while (contextResponsesIterator.hasNext()) {
				
				contextResponsesElement = contextResponsesIterator.next();
				
				contextElement = (JSONObject) contextResponsesElement.get("contextElement");
				
				id = (String) contextElement.get("id");
				elementType = (String) contextElement.get("type");
				String roomIdentifier = elementType.concat("_").concat(id);

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

						name = "PatientMovements";

						subscriptionName = name.concat("_").concat(elementType).concat("_").concat(id);
						
						attributeContextValue = (String) attributeElement.get("value");
						
						//If there is not yet CommandMode subscription, create it
						if(comfortModeRegister.getRoomComfortMode(roomIdentifier) == null){
							comfortModeRegister.addRoom(roomIdentifier, attributeContextValue);
						}else{
							String comfortModeValue = comfortModeRegister.getRoomComfortMode(roomIdentifier);
							if(comfortModeValue.equals("true")){
								if(attributeContextValue.equals("false")){
									comfortModeRegister.addRoom(roomIdentifier, attributeContextValue);
									String subscriptionValue = subscription.getSubscription(subscriptionName);
									spreadsheet.setCell(cellRow+2, 1,"Subscripion from Notification changed to false");
									spreadsheet.setCell(cellRow+2, 4,subscriptionValue);
									if(subscriptionValue != null && !subscriptionValue.contains("Deleted")){
										spreadsheet.setCell(cellRow+3, 1,"Subscripion Name is not Deleted");
										spreadsheet.setCell(cellRow+3, 4,subscriptionName);
										subscriptionValue = subscriptionValue.concat("-");
										subscriptionValue = subscriptionValue.concat("Deleted");
										
										subscription.addSubscription(subscriptionName, subscriptionValue);
									}
								}
							}else{
								if(attributeContextValue.equals("true")){
									comfortModeRegister.addRoom(roomIdentifier, attributeContextValue);
									String subscriptionValue = subscription.getSubscription(subscriptionName); 
									spreadsheet.setCell(cellRow+2, 1,"Subscripion from Notification changed to true");
									spreadsheet.setCell(cellRow+2, 4,subscriptionValue);
									if(subscriptionValue != null && subscriptionValue.contains("Deleted")){
										spreadsheet.setCell(cellRow+3, 1,"Subscripion Name is Deleted");
										spreadsheet.setCell(cellRow+3, 4,subscriptionName);
										subscriptionValue = subscriptionValue.replace("-", "");
										subscriptionValue = subscriptionValue.replace("Deleted", "");
										
										subscription.addSubscription(subscriptionName, subscriptionValue);
									}
								}
							}
						}						
					}
				}
			}
		}
	}
}