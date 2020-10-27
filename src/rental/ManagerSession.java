package rental;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Set;

import company.CarType;
import company.ICarRentalCompany;

public class ManagerSession extends Session {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 383901569956994876L;

	public ManagerSession(IRentalAgency agency, String owner) {
		super(agency, owner);
	}
	
	/**
	 * Register a new car rental company by its name.
	 */
	public void registerCRC(String crcName) throws RemoteException, NotBoundException {
		agency.addCompany(crcName);
	}
	
	/**
	 * Unregister a car rental company.
	 */
	public void unRegisterCRC(ICarRentalCompany company) throws RemoteException {
		agency.removeCompany(company);
	}
	
	/**
	 * Get the number of reservations for a specific company and a car type.
	 */
	public int getNumberOfReservationsForCarType(String company, String type) throws RemoteException {
		return agency.getNumberOfReservationsForCarType(company, type);
	}
	
	/**
	 * Get the set of all renters that have the highest number of reservations.
	 */
	public Set<String> getBestRenters() throws RemoteException {
		return agency.getBestRenters();
	}
	
	/**
	 * Get the number of reservations for a specific renter.
	 */
	public int getNumResByRenter(String name) throws RemoteException {
		return agency.getNumResByRenter(name);
	}
	
	/**
	 * Get the most popular car type for a given car rental company in a specific period.
	 */
	public CarType getMostPopularCarType(Date start, Date end, String carRentalCompanyName) throws RemoteException {
		return agency.getMostPopularCarType(start, end, carRentalCompanyName);
	}
	
}
