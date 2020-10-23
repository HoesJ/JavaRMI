package agency;

public class Session {
	
	protected IRentalAgency agency;
	protected String sessionOwner;
	
	public Session(IRentalAgency agency, String owner) {
		this.agency = agency;
		this.sessionOwner = owner;
	}
	
}
