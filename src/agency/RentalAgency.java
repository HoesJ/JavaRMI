package agency;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import rental.ICarRentalCompany;

public class RentalAgency {
	
	private String name;
	
	private Map<String, ICarRentalCompany> companies = new HashMap<>();
	
	// TODO: Bind in rmi registry (main).
	public RentalAgency(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addCompany(ICarRentalCompany company) throws RemoteException {
		companies.put(company.getName(), company);
	}
	
	public void removeCompany(ICarRentalCompany company) throws RemoteException {
		companies.remove(company.getName());
	}
	
	public ICarRentalCompany getCompany(String name) {
		return companies.get(name);
	}
	
	public Map<String, ICarRentalCompany> getCompanies() {
		return new HashMap<>(companies);
	}
	
	public ReservationSession getNewReservationSession() {
		return new ReservationSession(this);
	}
	
	public ManagerSession getNewManagerSession() {
		return new ManagerSession();
	}
	
}
