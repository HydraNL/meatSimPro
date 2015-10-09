/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import java.util.ArrayList;

import repast.simphony.random.RandomHelper;
import main.CFG;
import main.Helper;

// Start of user code (user defined imports)

// End of user code

/**
 * use instanceof or add compare method
 * 
 * @author rijk
 */
public abstract class Value {
	private double satisfaction;
	private double strengthWeight;
	private double beta;
	private double k;
	private double range;

	/**
	 * The constructor.
	 */
	public Value(double strengthWeight, double beta, double k){
		//System.out.println(strengthWeight);
		this.strengthWeight = strengthWeight; //ND and correlated over 1, 0.25
		RandomHelper.createNormal(1, beta/8); //Geeft errors door mogelijk negatief
		this.satisfaction = min() + (range/2.0);
		RandomHelper.createNormal(1, 0.25); //In case I fucked up somewhere.
		this.beta = beta;
		this.k = k;
		this.range = 3.4;
	}
	
	public double getStrengthWeight(){
		return strengthWeight;
	}
	public abstract double getStrengthAvarage(); //Normally also 1
	
	public abstract double getMyEvaluation(PContext myContext);
	
	public double getStrength(PContext myContext) {
		//if(160.0>CFG.getTime() > 140.0) System.out.println("Evaluation in context is"+ getMyEvaluation(myContext));
		if(CFG.isEvaluated() && myContext != null) return strengthWeight * getStrengthAvarage() *getMyEvaluation(myContext);
		else return strengthWeight * getStrengthAvarage();
	}
	
	public double getThreshold(PContext myContext){
		return getStrength(myContext) + (range/2.0);		//This is thus slightly different per agent and not all the same as startsat.	
	}

	/**
	 * Description of the method getNeed.
	 */
	public double getNeed(PContext myContext) {
		return getThreshold(myContext)/satisfaction;  //only works if satisfaction stays positive, else the Needs get lower when satisfaction gets lower
	}
	
	/**
	 * Description of the method equals.
	 */
	public void equals() {
		// Start of user code for method equals
		// End of user code
	}
	
	public void updateSatisfactionFunction(double connectedFeaturesSum, PContext myContext){
				double increment = Math.tanh( beta * (connectedFeaturesSum - getK(myContext))) /10; //beta = 0.03 //je kan de 0.05 eigenlijk wegstrepen, en beta wordt dan 1 //volgens mij heb ik dat nu dus ook gedaan
				//System.out.println("Increment: " + this + "by: "+ increment +"because of: " + 
				//"beta: "+ beta  +
				//"action: " + connectedFeaturesSum+
				//"k" + getK(myContext)+
				//"strength" + getStrength(myContext));
				
		 		satisfaction += increment;
			 		if(satisfaction > range + min()) satisfaction = range +min(); //zorgt voor verschillen
			 		if(satisfaction < min()) satisfaction = min();
		 	}
		 	
			private double getK(PContext myContext) {
				return getStrength(myContext) * k;
			}
		
	//Might give problems later as each value needs its own parameters when updating.
	public abstract void updateSatisfaction(PContext myContext, SocialPractice myAction);
	
	
	private double min(){
		return ((CFG.SELFE_AVG_STRENGTH()+CFG.SELFT_AVG_STRENGTH())/2);
	}
	protected void setBeta(double beta){
		this.beta = beta;
	}
	
	protected void setK(double k){
		this.k = k;
	}
	
	//for data purposes
	public double getSatisfaction() {
		return satisfaction;
	}

	
}
