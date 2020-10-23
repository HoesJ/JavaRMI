package rental;

import java.io.Serializable;

public class Session implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8636680286674609205L;
	
	protected IRentalAgency agency;
	protected String sessionOwner;
	
	public Session(IRentalAgency agency, String owner) {
		this.agency = agency;
		this.sessionOwner = owner;
	}
	
}
