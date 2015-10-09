package framework;

public class PhysicalContext {
	private Location myLocation;
	private PContext myPContext;
	
	public PhysicalContext(Location myLocation){
		this.setMyLocation(myLocation);
	}

	public Location getMyLocation() {
		return myLocation;
	}

	public void setMyLocation(Location myLocation) {
		this.myLocation = myLocation;
	}
	
	public PContext getMyPContext(){
		return myPContext;
	}
	
	public void setMyPContext(PContext myPContext){
		this.myPContext = myPContext;
	}
}
