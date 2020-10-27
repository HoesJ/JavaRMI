package rental;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
		return agency.getCompany(company).getNumberOfReservationsForCarType(type);
	}
	
	/**
	 * Get the set of all cars of a specific company.
	 */
	/*private Set<Car> getAllCars(String companyName) throws RemoteException {
		return agency.getCompany(companyName).getAllCars();
	}*/
	
	/**
	 * Get the set of all cars of all companies.
	 */
	/*private Set<Car> getAllCars() throws RemoteException {
		Set<Car> cars = new HashSet<>();
		for (ICarRentalCompany company : agency.getCompanies()) {
			cars.addAll(company.getAllCars());
		}
		
		return cars;
	}*/
	
	/**
	 * Get the set of all reservations of all companies.
	 */
	/*private Set<Reservation> getAllReservations() throws RemoteException {
    	Set<Reservation> reservations = new HashSet<>();
    	for (Car car : getAllCars())
    		reservations.addAll(car.getAllReservations());
    	
    	return reservations;
    }*/
	
	/**
	 * Get a map the gives the number of reservations per renter.
	 */
	private Map<String, Integer> getNumResByRenter() throws RemoteException {
		Map<String, Integer> numResByRenter = new HashMap<>();
		for (ICarRentalCompany company : agency.getCompanies()) {
			Map<String, Integer> companyNumResByRenter = company.getNumResByRenter();
			for (Map.Entry<String, Integer> entry : companyNumResByRenter.entrySet())
				numResByRenter.put(entry.getKey(), numResByRenter.getOrDefault(entry.getKey(), 0) + entry.getValue());
		}
		
		return numResByRenter;
	}
	
	/**
	 * Get the set of all renters that have the highest number of reservations.
	 */
	public Set<String> getBestRenters() throws RemoteException {
		Map<String, Integer> numResByRenter = getNumResByRenter();
		int highestValue = numResByRenter.values().stream().reduce((Integer num1, Integer num2) -> (num1 < num2 ? num2 : num1)).get();
		return numResByRenter.keySet().stream().filter(name -> numResByRenter.get(name) == highestValue).collect(Collectors.toSet());
	}
	
	/**
	 * Get the number of reservations for a specific renter.
	 */
	public int getNumResByRenter(String name) throws RemoteException {
		Integer numRes = getNumResByRenter().get(name);
		return numRes == null ? 0 : numRes;
	}
	
	/**
	 * Get the most popular car type for a given car rental company in a specific period.
	 */
	public CarType getMostPopularCarType(Date start, Date end, String carRentalCompanyName) throws RemoteException {
		return agency.getCompany(carRentalCompanyName).getMostPopularCarType(start, end);
	}
	
}
