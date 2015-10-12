/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import framework.Agent;
import framework.PContext;
import framework.PhysicalContext;
import framework.SocialPractice;

// End of user code

/**
 * Description of VegEatingPractice.
 * 
 * @author rijk
 */
public class VegEatingPractice extends SocialPractice {
	
	/**
	 * The constructor.
	 */
	public VegEatingPractice(Agent myAgent) {
		super(myAgent);
		addAffordance(new PContext(new MixedVenue()));
		addAffordance(new PContext(new VegVenue()));
		addAffordance(new PContext(new Home()));
		addAffordance(new PContext(new MeetUpLocation()));
		addPurpose(SelfTranscendence.class);
	}
	
	/**
	 * Description of the method embodiment.
	 * Overwrite?
	 */
	public void embodiment() {
	}
	 


}
