package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

import agency.ManagerSession;
import agency.Quote;
import agency.ReservationSession;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.Reservation;
import rental.ReservationConstraints;

public class Client extends AbstractTestBooking<ReservationSession, ManagerSession> {

	/********
	 * MAIN *
	 ********/

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	
	private ICarRentalCompany crc;

	/**
	 * The `main` method is used to launch the client application and run the test
	 * script.
	 */
	public static void main(String[] args) throws Exception {
		System.setSecurityManager(null);
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;

		String carRentalCompanyName = "Hertz";

		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", carRentalCompanyName, localOrRemote);
		client.run();
	}

	/***************
	 * CONSTRUCTOR 
	 * @throws RemoteException 
	 * @throws NotBoundException *
	 ***************/

	public Client(String scriptFile, String carRentalCompanyName, int localOrRemote) throws RemoteException, NotBoundException {
		super(scriptFile);
		
		Registry registry = LocateRegistry.getRegistry();
		crc = (ICarRentalCompany) registry.lookup(carRentalCompanyName);
	}

	/**
	 * Check which car types are available in the given period (across all companies
	 * and regions) and print this list of car types.
	 *
	 * @param start start time of the period
	 * @param end   end time of the period
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {
		Set<CarType> cars = crc.getAvailableCarTypes(start, end);
		
		for (CarType car : cars) {
			System.out.println(car);
		}
	}

	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param clientName name of the client
	 * @param start      start time for the quote
	 * @param end        end time for the quote
	 * @param carType    type of car to be reserved
	 * @param region     region in which car must be available
	 * @return the newly created quote
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Quote createQuote(String clientName, Date start, Date end, String carType, String region)
			throws Exception {
		Quote quote = crc.createQuote(new ReservationConstraints(start, end, carType, region), clientName);
		System.out.println(quote);
		
		return quote;
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param quote the quote to be confirmed
	 * @return the final reservation of a car
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws Exception {
		Reservation reservation = crc.confirmQuote(quote);
		System.out.println(reservation);
		
		return reservation;
	}

	/**
	 * Get all reservations made by the given client.
	 *
	 * @param clientName name of the client
	 * @return the list of reservations of the given client
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsByRenter(String clientName) throws Exception {
		List<Reservation> reservations = crc.getReservationByRenter(clientName);
		
		for (Reservation reservation : reservations) {
			System.out.println(String.format("Reservation of car type %s, car ID %d from %s to %s at a price of %.2f.", 
	                reservation.getCarType(), reservation.getCarId(), reservation.getStartDate(), reservation.getEndDate(), reservation.getRentalPrice()));
		}
		return reservations;
	}

	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param carType name of the car type
	 * @return number of reservations for the given car type
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws Exception {
		int nbReservations = crc.getNumberOfReservationsForCarType(carType);
		System.out.println(String.format("There are %d reservations for car type %s", nbReservations, carType));
		
		return nbReservations;
	}
}