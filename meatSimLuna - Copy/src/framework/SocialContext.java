/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import framework.Agent;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * Description of SocialContext.
 * 
 * @author rijk
 */
public class SocialContext {
	private ArrayList<Agent> myAgents = new ArrayList<Agent>();
	
	/**
	 * The constructor.
	 */
	public SocialContext() {
		super();
	}
	
	public ArrayList<Agent> getMyAgents() {
		return this.myAgents;
	}

	public void addAgent(Agent a){
		myAgents.add(a);
	}

}
