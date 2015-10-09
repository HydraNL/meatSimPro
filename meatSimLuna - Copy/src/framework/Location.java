/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import main.CFG;
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
	
	
}

