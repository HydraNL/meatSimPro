/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import main.CFG;
import meatEating.Home;
import meatEating.VegVenue;
import repast.simphony.space.grid.Grid;

/**
 * Description of PhysicalContext.
 * 
 * @author rijk
 */
public abstract class Location {
	private int ID;
	private Grid<Object> myGrid;
	private PhysicalContext myPhysicalContext;
	private boolean open;
	private double meatRatio;

	public double getMeatRatio() {
		return meatRatio;
	}
	public void setMeatRatio(double meatRatio) {
		this.meatRatio = meatRatio;
	}
	/**
	 * The constructor.
	 */
	public Location(Grid<Object> grid) {
		open = true;
		myGrid = grid;
		ID = CFG.getLocationID();
		setMyPhysicalContext(new PhysicalContext(this));
	}
	/*
	 * Constructor for affordance placeholder.
	 */
	public Location(){
		setMyPhysicalContext(new PhysicalContext(this));
	}
	
	public PhysicalContext getMyPhysicalContext(){
		return myPhysicalContext;
	}
	
	public void setMyPhysicalContext(PhysicalContext p){
		myPhysicalContext = p;
	}
	
	//This is a bridge
	public PContext getMyContext(){
		return myPhysicalContext.getMyPContext();
	}
	
	public boolean hasContext(){
		return getMyContext() != null;
	}
	public void removePContext() {
		myPhysicalContext.setMyPContext(null);
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public double dataMeatRatio(){
		if(getMyPhysicalContext() != null && hasContext()){
			double meatCount =0.0;
			double totalCount =0.0;
			
			for(Agent a:getMyPhysicalContext().getMyPContext().getMyAgents()){
				meatCount+=a.dataMeatAction();
				totalCount++;
			}
//			if((this instanceof VegVenue) &&getMyPhysicalContext().getMyPContext().getMyAgents().size() == 1 && 0 < (meatCount/totalCount)){
//				System.out.println("a");
//			}
			setMeatRatio((meatCount/totalCount));
		}
		if(getMeatRatio() != 0 && this instanceof VegVenue){
			System.out.println("err");
		}
		return getMeatRatio();
	}
	public double getID(){
		return ID;
	}
	
	public double dataAgentCount(){
		return (!(this instanceof Home) && getMyPhysicalContext() != null && hasContext()) ? getMyContext().getMyAgents().size():0;
	}
}

