package agency;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import rental.ICarRentalCompany;

public class RentalAgency implements IRentalAgency {
	
	private String name;
	
	private Set<ICarRentalCompany> companies = new HashSet<>();
	
	public RentalAgency(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void addCompany(ICarRentalCompany company) throws RemoteException {
		companies.add(company);
	}
	
	@Override
	public void removeCompany(ICarRentalCompany company) throws RemoteException {
		companies.remove(company);
	}
	
	@Override
	public ICarRentalCompany getCompany(String name) throws RemoteException {
		for (ICarRentalCompany company : companies) {
			if (company.getName().equals(name))
				return company;
		}
		
		return null;
	}
	
	@Override
	public Set<ICarRentalCompany> getCompanies() {
		return new HashSet<>(companies);
	}
	
	@Override
	public ReservationSession getNewReservationSession(String owner) {
		return new ReservationSession(this, owner);
	}
	
	@Override
	public ManagerSession getNewManagerSession(String owner) {
		return new ManagerSession(this, owner);
	}
	
}
