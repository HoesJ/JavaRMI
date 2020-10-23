package rental;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import company.Car;
import company.CarType;
import company.ICarRentalCompany;
import company.Reservation;

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
	private Set<Car> getAllCars(String companyName) throws RemoteException {
		return agency.getCompany(companyName).getAllCars();
	}
	
	/**
	 * Get the set of all cars of all companies.
	 */
	private Set<Car> getAllCars() throws RemoteException {
		Set<Car> cars = new HashSet<>();
		for (ICarRentalCompany company : agency.getCompanies()) {
			cars.addAll(company.getAllCars());
		}
		
		return cars;
	}
	
	/**
	 * Get the set of all reservations of all companies.
	 */
	private Set<Reservation> getAllReservations() throws RemoteException {
    	Set<Reservation> reservations = new HashSet<>();
    	for (Car car : getAllCars())
    		reservations.addAll(car.getAllReservations());
    	
    	return reservations;
    }
	
	/**
	 * Get a map the gives the number of reservations per renter.
	 */
	private Map<String, Integer> getNumResByRenter() throws RemoteException {
		Map<String, Integer> numResByRenter = new HashMap<>();
		for (Reservation reservation : getAllReservations()) {
			if (numResByRenter.containsKey(reservation.getCarRenter()))
				numResByRenter.put(reservation.getCarRenter(), numResByRenter.get(reservation.getCarRenter()) + 1);
			else
				numResByRenter.put(reservation.getCarRenter(), 1);
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
		Map<CarType, Integer> numResByCarType = new HashMap<>();
		for (Car car : getAllCars(carRentalCompanyName)) {
			long nbReservations = car.getAllReservations().stream().filter(reservation -> start.before(reservation.getStartDate()) &&
																						  end.after(reservation.getStartDate())).count();
			
			if (numResByCarType.containsKey(car.getType()))
				numResByCarType.put(car.getType(), numResByCarType.get(car.getType()) + (int)nbReservations);
			else
				numResByCarType.put(car.getType(), (int)nbReservations);
		}
		
		return numResByCarType.entrySet().stream().reduce((Map.Entry<CarType, Integer> entry1, Map.Entry<CarType, Integer> entry2) ->
														  (entry1.getValue() < entry2.getValue() ? entry2 : entry1)).get().getKey();
	}
	
}