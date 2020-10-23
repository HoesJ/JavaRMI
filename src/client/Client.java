package client;

import java.rmi.NotBoundException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import agency.IRentalAgency;
import agency.ManagerSession;
import agency.ReservationSession;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;

public class Client extends AbstractTestManagement<ReservationSession, ManagerSession> {

	/********
	 * MAIN *
	 ********/

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	
	private IRentalAgency agency;

	/**
	 * The `main` method is used to launch the client application and run the test
	 * script.
	 */
	public static void main(String[] args) throws Exception {
		System.setSecurityManager(null);
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;

		String rentalAgencyName = "G&H rental service";

		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", rentalAgencyName, localOrRemote);
		client.run();
	}

	/***************
	 * CONSTRUCTOR 
	 * @throws RemoteException 
	 * @throws NotBoundException *
	 ***************/

	public Client(String scriptFile, String rentalAgencyName, int localOrRemote) throws RemoteException, NotBoundException {
		super(scriptFile);
		
		Registry registry = LocateRegistry.getRegistry();
		agency = (IRentalAgency) registry.lookup(rentalAgencyName);
	}
	
	/**
     * Create a new reservation session for the user with the given name.
     *
     * @param name name of the client (renter) owning this session
     * @return the new reservation session
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected ReservationSession getNewReservationSession(String name) throws Exception {
		return agency.getNewReservationSession(name);
	}
	
	/**
     * Create a new manager session for the user with the given name (there is only one manager for all car rental companies).
     * @param name of the user (i.e. manager) using this session
     * @return the new manager session
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected ManagerSession getNewManagerSession(String name) throws Exception {
		return agency.getNewManagerSession(name);
	}

	/**
     * Check which car types are available in the given period and print them.
     *
     * @param session the session to do the request from
     * @param start start time of the period
     * @param end end time of the period
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws Exception {
		session.checkForAvailableCarTypes(start, end);
	}

	/**
     * Add a quote for a given car type to the session.
     *
     * @param session the session to add the reservation to
     * @param name the name of the client owning the session
     * @param start start time of the reservation
     * @param end end time of the reservation
     * @param carType type of car to be reserved
     * @param region region for which the car shall be reserved
     * should be done
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected void addQuoteToSession(ReservationSession session, String name, Date start,
    		Date end, String carType, String region) throws Exception {
		session.createQuote(new ReservationConstraints(start, end, carType, region), name);
	}

	/**
     * Confirm the quotes in the given session.
     *
     * @param session the session to finalize
     * @param name the name of the client owning the session
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected List<Reservation> confirmQuotes(ReservationSession session, String name) throws Exception {
		return session.confirmQuotes();
	}
	
	/**
     * Get the number of reservations made by the given renter (across whole
     * rental agency).
     *
     * @param	ms manager session
     * @param clientName name of the renter
     * @return	the number of reservations of the given client (across whole
     * rental agency)
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected int getNumberOfReservationsByRenter(ManagerSession ms, String clientName) throws Exception {
		return ms.getNumResByRenter(clientName);
	}
	
	/**
     * Get the number of reservations for a particular car type.
     *
     * @param ms manager session
     * @param carRentalName name of the rental company managed by this session
     * @param carType name of the car type
     * @return number of reservations for this car type
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType) throws Exception {
		return ms.getNumberOfReservationsForCarType(carRentalName, carType);
	}
	
	/**
     * Get the (list of) best clients, i.e. clients that have highest number of
     * reservations (across all rental agencies).
     *
     * @param ms manager session
     * @return set of best clients
     * @throws Exception if things go wrong, throw exception
     */
	@Override
	protected Set<String> getBestClients(ManagerSession ms) throws Exception {
		return ms.getBestRenters();
	}
	
	/**
     * Find a cheapest car type that is available in the given period and region.
     *
     * @param session the session to do the request from
     * @param start start time of the period
     * @param end end time of the period
     * @param region region of interest (if null, no limitation by region)
     *
     * @return name of a cheapest car type for the given period
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws Exception {
		return session.getCheapestCarType(start, end, region).getName();
	}
	
	/**
     * Get the most popular car type in the given car rental company.
     *
     * @param ms manager session
     * @param	carRentalCompanyName The name of the car rental company.
     * @param year year in question
     * @return the most popular car type in the given car rental company
     *
     * @throws Exception if things go wrong, throw exception
     */
	@Override
    protected CarType getMostPopularCarTypeInCRC(ManagerSession ms, String carRentalCompanyName, int year) throws Exception {
		return ms.getMostPopularCarType(new GregorianCalendar(year, Calendar.JANUARY, 1).getTime(),
										new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime(), carRentalCompanyName);
	}

}