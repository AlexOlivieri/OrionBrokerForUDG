package ch.hevs.softwareEngineeringUnit.IoT6.wp6.googleSpreadsheet;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

public class Spreadsheet {

	private SpreadsheetService service;
	private URL cellFeedUrl;
	private CellFeed cellFeed;
	private WorksheetEntry worksheet;
	private SpreadsheetEntry spreadsheet;
	
	public Spreadsheet(String worksheetName) {
		
		String USERNAME = "iigiot6@gmail.com";
		String PASSWORD = "iot62014";
		
		try {

		    service = new SpreadsheetService("MySpreadsheetIntegration-v1");
		    service.setUserCredentials(USERNAME, PASSWORD);
			
			// Define the URL to request.  This should never change.
		    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			
		    // Make a request to the API and get all spreadsheets.
		    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
		    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		    
		    if (spreadsheets.size() == 0) {
		      // TODO: There were no spreadsheets, act accordingly.
		    }

		    // TODO: Choose a spreadsheet more intelligently based on your app's needs.
		    spreadsheet = spreadsheets.get(0);
		    
		    // Get the first worksheet of the first spreadsheet.
		    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
		    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
		    

		    int worksheetIndex = getWorksheetIndex(worksheetName, worksheets);
		    
		    if(worksheetIndex != -1) {
		    	// select worksheet
		    	worksheet = worksheets.get(worksheetIndex);
		    	
		    	// Empty all filled cells
		    	URL cellFeedUrl = worksheet.getCellFeedUrl();
			    cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
			    
/*			    for (CellEntry cell : cellFeed.getEntries()) {
			    	cell.delete();
			    }
*/		    }
		    else {
		    	// create new worksheet locally
		    	WorksheetEntry newWorksheet = new WorksheetEntry();
		    	newWorksheet.setColCount(10);
		    	newWorksheet.setRowCount(20);
		    	newWorksheet.setTitle(TextConstruct.plainText(worksheetName));
		    	
		    	// Send new worksheet
		    	URL wsFeed = spreadsheet.getWorksheetFeedUrl();
		    	service.insert(wsFeed, newWorksheet);
		    	
		    	// Update worksheets
		    	worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    worksheets = worksheetFeed.getEntries();
		    	
		    	worksheet = worksheets.get(getWorksheetIndex(worksheetName, worksheets));
		    }

		    // Fetch the cell feed of the worksheet.
		    cellFeedUrl = worksheet.getCellFeedUrl();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int getWorksheetIndex(String worksheetName, List<WorksheetEntry> worksheets) {
		
		for(int i=0; i<worksheets.size(); i++) {
			
	    	if(worksheets.get(i).getTitle().getPlainText().equals(worksheetName)) {
	    		return i;
	    	}
	    }
		
		return -1;
	}

	public void setCell(int row, int col, String formulaOrValue) {
		CellEntry newEntry = new CellEntry(row, col, formulaOrValue);
		
		try {
			
			if(row > worksheet.getRowCount()) {
				worksheet.setRowCount(worksheet.getRowCount()+1000);
				worksheet.update();
			}
			
			service.insert(cellFeedUrl, newEntry);
		} catch (IOException | ServiceException e) {
			//e.printStackTrace();
		}
	}
	
	public CellEntry findLastOccupiedCell() {
		
		CellEntry emptyCell = null;
		
		try {	
			
		    for (CellEntry cellEntry : cellFeed.getEntries()) {
		    	emptyCell = cellEntry;
		    }
		}
		catch (Exception e) {
			System.err.println(e);
		}
		return emptyCell;
	}
}
