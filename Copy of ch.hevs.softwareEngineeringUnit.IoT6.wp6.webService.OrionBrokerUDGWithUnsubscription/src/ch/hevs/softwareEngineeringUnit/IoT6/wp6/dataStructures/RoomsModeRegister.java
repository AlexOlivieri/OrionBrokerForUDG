package ch.hevs.softwareEngineeringUnit.IoT6.wp6.dataStructures;

import java.util.Hashtable;

public class RoomsModeRegister {
	
	public static RoomsModeRegister instance = null;
	
	private Hashtable<String, String> comfortModeRegister;

	public RoomsModeRegister(){
		
		this.comfortModeRegister = new Hashtable<String, String>();
	}
	
	public static RoomsModeRegister getInstance(){
			
		if(instance == null){
			instance = new RoomsModeRegister();
		}
		return instance;
	}
	
	public synchronized void addRoom(String roomIdentifier, String comfortMode){
		
		comfortModeRegister.put(roomIdentifier, comfortMode);
	}
	
	public synchronized void deleteSubscription(String roomIdentifier){
		
		comfortModeRegister.remove(roomIdentifier);
	}
	
	public synchronized String getRoomComfortMode(String roomIdentifier){
		
		String comfortMode = comfortModeRegister.get(roomIdentifier);
		
		return comfortMode;
	}
}
