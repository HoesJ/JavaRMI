package agency;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class ManagerSession {
	
	private RentalAgency agency;
	
	public ManagerSession(RentalAgency agency) {
		this.agency = agency;
	}
	
	public int getNumberOfReservationsForCarType(String company, String type) throws RemoteException {
		return agency.getCompany(company).getNumberOfReservationsForCarType(type);
	}
	
	// TODO: afmaken
	public String getBestRenter() {
		Map<String, Integer> numResByRenter = new HashMap<>();
		for ()
	}
	
}
