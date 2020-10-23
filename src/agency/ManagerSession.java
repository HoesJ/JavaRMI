package agency;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import rental.Car;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.Reservation;

public class ManagerSession extends Session {
	
	public ManagerSession(IRentalAgency agency, String owner) {
		super(agency, owner);
	}
	
	public void registerCRC(String crcName) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry();
		agency.addCompany((ICarRentalCompany) registry.lookup(crcName));
	}
	
	public void unRegisterCRC(ICarRentalCompany company) throws RemoteException {
		agency.removeCompany(company);
	}
	
	public int getNumberOfReservationsForCarType(String company, String type) throws RemoteException {
		return agency.getCompany(company).getNumberOfReservationsForCarType(type);
	}
	
	private Set<Car> getAllCars(String companyName) throws RemoteException {
		return agency.getCompany(companyName).getAllCars();
	}
	
	private Set<Car> getAllCars() throws RemoteException {
		Set<Car> cars = new HashSet<>();
		for (ICarRentalCompany company : agency.getCompanies()) {
			cars.addAll(company.getAllCars());
		}
		
		return cars;
	}
	
	private Set<Reservation> getAllReservations() throws RemoteException {
    	Set<Reservation> reservations = new HashSet<>();
    	for (Car car : getAllCars())
    		reservations.addAll(car.getAllReservations());
    	
    	return reservations;
    }
	
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
	
	public Set<String> getBestRenters() throws RemoteException {
		Map<String, Integer> numResByRenter = getNumResByRenter();
		int highestValue = numResByRenter.values().stream().reduce((Integer num1, Integer num2) -> (num1 < num2 ? num2 : num1)).get();
		return numResByRenter.keySet().stream().filter(name -> numResByRenter.get(name) == highestValue).collect(Collectors.toSet());
	}
	
	public int getNumResByRenter(String name) throws RemoteException {
		Integer numRes = getNumResByRenter().get(name);
		return numRes == null ? 0 : numRes;
	}
	
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
