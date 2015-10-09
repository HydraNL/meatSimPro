/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import java.util.ArrayList;



/**
 * the Builder creates one context object per venue per round<br />
 * make sure the Builder does this before an agent moves
 * 
 * @author rijk
 */
public class PContext {

	private PhysicalContext physical;
	private SocialContext social =new SocialContext();
	private int timestep;

	
	/**
	 * The constructor.
	 */
	public PContext(Location location) {
		this.physical = location.getMyPhysicalContext();
		location.getMyPhysicalContext().setMyPContext(this);
	}


	public PhysicalContext getPhysical() {
		return physical;
	}


	public void setPhysical(PhysicalContext physical) {
		this.physical = physical;
	}
	
	public SocialContext getSocial() {
		return social;
	}
	
	//Wrappers
	public void addAgent(Agent a){
		getSocial().addAgent(a);
	}

	public ArrayList<Agent> getMyAgents() {
		return social.getMyAgents();
	}

	public Location getMyLocation() {
		return physical.getMyLocation();
	}

}
