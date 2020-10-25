package rental;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;

import company.ICarRentalCompany;

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
	
	/**
	 * Add a car rental company to this rental agency by looking it up in the registry.
	 */
	@Override
	public void addCompany(String crcName) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(14540);	// TODO: remove
		companies.add((ICarRentalCompany) registry.lookup(crcName));
	}
	
	/**
	 * Remove a car rental company from this rental agency.
	 */
	@Override
	public void removeCompany(ICarRentalCompany company) throws RemoteException {
		companies.remove(company);
	}
	
	/**
	 * Get the car rental company of this rental agency with the specified name.
	 */
	@Override
	public ICarRentalCompany getCompany(String name) throws RemoteException {
		for (ICarRentalCompany company : companies) {
			if (company.getName().equals(name))
				return company;
		}
		
		return null;
	}
	
	/**
	 * Get the set of all car rental companies of this rental agency.
	 */
	@Override
	public Set<ICarRentalCompany> getCompanies() {
		return new HashSet<>(companies);
	}
	
	/**
	 * Create a new reservation session.
	 */
	@Override
	public ReservationSession getNewReservationSession(String owner) {
		return new ReservationSession(this, owner);
	}
	
	/**
	 * Create a new manager session.
	 */
	@Override
	public ManagerSession getNewManagerSession(String owner) {
		return new ManagerSession(this, owner);
	}
	
}
