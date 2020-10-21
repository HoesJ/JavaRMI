package agency;

import java.rmi.RemoteException;
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
	
	public ManagerSession(RentalAgency agency) {
		super(agency);
	}
	
	public int getNumberOfReservationsForCarType(String company, String type) throws RemoteException {
		return agency.getCompany(company).getNumberOfReservationsForCarType(type);
	}
	
	private Set<Reservation> getAllReservations() throws RemoteException {
    	Set<Reservation> reservations = new HashSet<>();
    	for (ICarRentalCompany company : agency.getCompanies().values())
    		reservations.addAll(company.getAllReservations());
    	
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
	
	public Set<String> getBestRenter() throws RemoteException {
		Map<String, Integer> numResByRenter = getNumResByRenter();
		int highestValue = numResByRenter.values().stream().reduce((Integer num1, Integer num2) -> (num1 < num2 ? num2 : num1)).get();
		return numResByRenter.keySet().stream().filter(name -> numResByRenter.get(name) == highestValue).collect(Collectors.toSet());
	}
	
	public int getNumResByRenter(String name) throws RemoteException {
		Integer numRes = getNumResByRenter().get(name);
		return numRes == null ? 0 : numRes;
	}
	
	public String getMostPopularCarType(Date date) throws RemoteException {
		Map<String, Integer> numResByCarType = new HashMap<>();
		for (Reservation reservation : getAllReservations().stream().filter(reservation -> date.before(reservation.getStartDate()))
																	.collect(Collectors.toSet())) {
			if (numResByCarType.containsKey(reservation.getCarType()))
				numResByCarType.put(reservation.getCarType(), numResByCarType.get(reservation.getCarType()) + 1);
			else
				numResByCarType.put(reservation.getCarType(), 1);
		}
		
		return numResByCarType.entrySet().stream().reduce((Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) ->
														  (entry1.getValue() < entry2.getValue() ? entry2 : entry1)).get().getKey();
	}
	
}
