package rental;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import company.CRCServer;
import company.ReservationException;

public class RentalServer {
	
	public static void main(String[] args) throws ReservationException,
			NumberFormatException, IOException, NotBoundException {
		//System.setSecurityManager(null);
		
		String rentalAgencyname = "G&H rental service";
		RentalAgency rental = new RentalAgency(rentalAgencyname);
		
		// Add companies to this rental server.
		// Note: in a realistic setup these would not be run by the same process.
		new CRCServer("Hertz", "hertz.csv");
		new CRCServer("Dockx", "dockx.csv");
		
		rental.addCompany("Hertz");
		rental.addCompany("Dockx");
		
		IRentalAgency rentalStub = (IRentalAgency) UnicastRemoteObject.exportObject(rental, 0);
		Registry registry = LocateRegistry.getRegistry(14540);
		registry.rebind(rentalAgencyname, rentalStub);
	}
	
}
