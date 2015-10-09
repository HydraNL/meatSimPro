/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import main.CFG;
import framework.Agent;
import framework.PContext;
import framework.SocialPractice;
import framework.Value;

// End of user code

/**
 * Description of SelfEnhancement.
 * 
 * @author rijk
 */
public class SelfEnhancement extends Value {
	private double weightMeatEating;
	private Agent myAgent;
	
	/**
	 * The constructor.
	 */
	public SelfEnhancement(double strengthWeigth, Agent myAgent) {
		super(strengthWeigth, CFG.SELFE_beta(), CFG.SELFE_k()); //Different implementation of using constuctor than before.
		this.weightMeatEating = CFG.SELFE_actionWeight();
		this.myAgent = myAgent;
	}
	
	public void updateSatisfaction(PContext myContext, SocialPractice actionDone){
		double eatenMeat = (actionDone instanceof MeatEatingPractice) ? 1:0;
		double feature1 = weightMeatEating * eatenMeat;
		//Room for more features.
		double connectedFeaturesWeightedSum = feature1;
		
		super.updateSatisfactionFunction(connectedFeaturesWeightedSum, myContext);
	}
	
	

	@Override
	public double getStrengthAvarage() {
		return CFG.SELFE_AVG_STRENGTH();
	}

	@Override
	public double getMyEvaluation(PContext myContext) {
		double x=0;
		for(SocialPractice sp:myAgent.getMySocialPractices()){
			if(sp.getClass()==MeatEatingPractice.class) x=sp.calculateEvaluation(myContext, CFG.OUTSIDE_CONTEXT(myAgent.getOCweight()));
		}
		return x;
	}
}
