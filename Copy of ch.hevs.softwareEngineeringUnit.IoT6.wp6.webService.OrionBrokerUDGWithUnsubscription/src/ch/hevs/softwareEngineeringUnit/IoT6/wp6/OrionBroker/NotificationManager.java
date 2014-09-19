package ch.hevs.softwareEngineeringUnit.IoT6.wp6.OrionBroker;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ch.hevs.softwareEngineeringUnit.IoT6.wp6.parsingManagement.ParsingRoomModeThread;
import ch.hevs.softwareEngineeringUnit.IoT6.wp6.parsingManagement.ParsingPatientMovementsThread;

@Path("/notificationManager")
public class NotificationManager {
	
	private static AtomicBoolean subscriptionToMovement;
	private static String storedSubscriptionId;
	
	public NotificationManager(){
		System.out.println("Constructor");
//		subscriptionToMovement = new AtomicBoolean(false);
//		storedSubscriptionId = "empty";
	}

	@Path("/participantPosition")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPatientMovements(String jsonString){
		
		System.out.println("NotificationString: " +jsonString);

		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		
		try {
			jsonObject = (JSONObject) parser.parse(jsonString);
			new Thread(new ParsingPatientMovementsThread(this, jsonObject)).start();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(201).entity("OK").build();
	}
	
	@Path("/roomMode")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getComfortCommand(String jsonString){
		
		System.out.println("NotificationString: " +jsonString);

		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		
		try {
			jsonObject = (JSONObject) parser.parse(jsonString);
			new Thread(new ParsingRoomModeThread(this, jsonObject)).start();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(201).entity("Ok").build();
	}

	//Method called if HTML is requested
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHTMLHello(){
		
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
		        + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	}
	
	@Path("getSubscription")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
/*	public Response getSubscription(String jsonString){
		
		JSONParser parser = new JSONParser();
		JSONObject object = null;
		
		String subscriptionID = "";
		try {
			object = (JSONObject) parser.parse(jsonString);
			subscriptionID = (String) object.get("subscriptionId");
			System.out.println("Subscription ID: " +subscriptionID);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SubscriptionRegister subscription = SubscriptionRegister.getInstance();
		String subscriptionId = subscription.getSubscription(subscriptionID);
		
		if(subscriptionId == null){
			System.out.println("SubscriptionID null");
		}else{
			System.out.println("SubscriptionID: " +subscriptionId);
		}
		
		return Response.status(201).entity(subscriptionId).build();
		
	}
*/	
	
	public synchronized void setSubscriptionToMovementValue(boolean value){
		
		System.out.println(subscriptionToMovement.get());
		System.out.println("SetTheValueOfTheSubscription");
		
		subscriptionToMovement.getAndSet(value);
		
		System.out.println(subscriptionToMovement.get());
	}

	public synchronized boolean getSubscriptionToMovementValue(){
		
		if(subscriptionToMovement == null){
			System.out.println("Subscription is null");
			subscriptionToMovement = new AtomicBoolean(false);
		}
		
		return subscriptionToMovement.get();
	}
	
	public synchronized void setSubscriptionId(String subscriptionId){
		storedSubscriptionId = subscriptionId;
	}
	
	public synchronized String getSubscriptionId(){
		return storedSubscriptionId;
	}
	
}
