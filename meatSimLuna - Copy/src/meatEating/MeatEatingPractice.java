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
 * Description of MeatEatingPractice.
 * 
 * @author rijk
 */
public class MeatEatingPractice extends SocialPractice {
	// Start of user code (user defined attributes for MeatEatingPractice)
	
	// End of user code
	
	/**
	 * The constructor.
	 */
	public MeatEatingPractice(Agent myAgent) {
		super(myAgent);
		addAffordance(new PContext(new MeatVenue()));
		addAffordance(new PContext(new MixedVenue()));
		addAffordance(new PContext(new Home()));
		addAffordance(new PContext(new MeetUpLocation()));
		addPurpose(SelfEnhancement.class);
	}
	
	/**
	 * Description of the method embodiment.
	 * -waar is het
	 *	-extra restricties
	 *	-wat behelst het
	 *		-values get satisfied
	 */
	public void embodiment() {
	}
	 
	// Start of user code (user defined methods for MeatEatingPractice)
	
	// End of user code


}
