package company;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rental.Quote;

public class CarRentalCompany implements ICarRentalCompany {

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

	private List<String> regions;
	private String name;
	private List<Car> cars;
	private Map<String, CarType> carTypes = new HashMap<String, CarType>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		setRegions(regions);
		for (Car car : cars)
			carTypes.put(car.getType().getName(), car.getType());
		logger.log(Level.INFO, this.toString());
	}

	/********
	 * NAME *
	 ********/

	@Override
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	/***********
	 * Regions *
	 **********/
	private void setRegions(List<String> regions) {
		this.regions = regions;
	}

	@Override
	public List<String> getRegions() {
		return this.regions;
	}

	@Override
	public boolean operatesInRegion(String region) {
		return this.regions.contains(region);
	}

	/*************
	 * CAR TYPES *
	 *************/

	@Override
	public Collection<CarType> getAllCarTypes() {
		return carTypes.values();
	}

	@Override
	public CarType getCarType(String carTypeName) {
		if (carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}

	// mark
	@Override
	public boolean isAvailable(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[] { name, carTypeName });
		if (carTypes.containsKey(carTypeName)) {
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		} else {
			throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
		}
	}

	@Override
	public synchronized Set<CarType> getAvailableCarTypes(Date start, Date end) {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}

	/*********
	 * CARS *
	 *********/

	private Car getCar(int uid) {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}

	private synchronized List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}

	/****************
	 * RESERVATIONS *
	 ****************/

	@Override
	public synchronized Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
				new Object[] { name, client, constraints.toString() });

		if (!operatesInRegion(constraints.getRegion())
				|| !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
			throw new ReservationException("<" + name + "> No cars available to satisfy the given constraints.");

		CarType type = getCarType(constraints.getCarType());

		double price = calculateRentalPrice(type.getRentalPricePerDay(), constraints.getStartDate(),
				constraints.getEndDate());

		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(),
				constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24D));
	}

	@Override
	public synchronized Reservation confirmQuote(Quote quote) throws ReservationException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[] { name, quote.toString() });
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if (availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
					+ " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = availableCars.get((int) (Math.random() * availableCars.size()));

		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		return res;
	}

	@Override
	public synchronized void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[] { name, res.toString() });
		getCar(res.getCarId()).removeReservation(res);
	}

	@Override
	public synchronized List<Reservation> getReservationByRenter(String clientName) throws RemoteException {
		List<Reservation> lst = new ArrayList<>();

		for (Car car : cars)
			lst.addAll(car.getReservationForRenter(clientName));
		return lst;
	}

	@Override
	public synchronized int getNumberOfReservationsForCarType(String carType) throws RemoteException {
		return cars.stream()
			.filter(car -> car.getType().getName().equals(carType))
			.map(car -> car.getNumberReservations())
			.reduce((x, y) -> x + y).get();
	}

	@Override
	public String toString() {
		return String.format("<%s> CRC is active in regions %s and serving with %d car types", name,
				listToString(regions), carTypes.size());
	}

	private static String listToString(List<? extends Object> input) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < input.size(); i++) {
			if (i == input.size() - 1) {
				out.append(input.get(i).toString());
			} else {
				out.append(input.get(i).toString() + ", ");
			}
		}
		return out.toString();
	}
	
	/**
	 * Get a map that gives the number of reservations per renter.
	 */
	@Override
	public synchronized Map<String, Integer> getNumResByRenter() throws RemoteException {
		Map<String, Integer> numResByRenter = new HashMap<>();
		for (Car car : cars) {
			for (Reservation reservation : car.getAllReservations())
				numResByRenter.put(reservation.getCarRenter(), numResByRenter.getOrDefault(reservation.getCarRenter(), 0) + 1);
		}
		
		return numResByRenter;
	}
	
	/**
	 * Get the most popular car type in a specific period.
	 */
	@Override
	public synchronized CarType getMostPopularCarType(Date start, Date end) throws RemoteException {
		Map<CarType, Integer> numResByCarType = new HashMap<>();
		for (Car car : cars) {
			long nbReservations = car.getAllReservations().stream()
				.filter(reservation -> start.before(reservation.getStartDate()) && end.after(reservation.getStartDate()))
				.count();
			
			numResByCarType.put(car.getType(), numResByCarType.getOrDefault(car.getType(), 0) + (int)nbReservations);
		}
		
		return numResByCarType.entrySet().stream()
			.reduce((Map.Entry<CarType, Integer> entry1, Map.Entry<CarType, Integer> entry2) ->
				(entry1.getValue() < entry2.getValue() ? entry2 : entry1))
			.get().getKey();
	}

}