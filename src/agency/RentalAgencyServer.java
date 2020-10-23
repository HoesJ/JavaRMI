package agency;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rental.CarRentalCompany;
import rental.ICarRentalCompany;
import rental.RentalServer;
import rental.ReservationException;

public class RentalAgencyServer {
	
	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	
	public static void main(String[] args) throws ReservationException,
			NumberFormatException, IOException {
		System.setSecurityManager(null);
		
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
		
		String rentalAgencyname = "G&H rental service";
		RentalAgency rental = new RentalAgency(rentalAgencyname);
		
		IRentalAgency rentalStub = (IRentalAgency) UnicastRemoteObject.exportObject(rental, 0);
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(rentalAgencyname, rentalStub);
	}
	
}