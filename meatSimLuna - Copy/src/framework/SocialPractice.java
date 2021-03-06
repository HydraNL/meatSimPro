/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import main.CFG;
import main.Helper;









import meatEating.Home;
import meatEating.MeatEatingPractice;
import meatEating.MeatVenue;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
// Start of user code (user defined imports)
import org.jblas.DoubleMatrix;

// End of user code

/**
 * use Hashmap with agents and venue ID
 * Make affordances a list of context objects. Each object represent a context precondition (i.e. Meat Venue).<br />
 * Less than ideal is that these objects are thus fake, they only represent a class type that will be used in checkAffordances.<br />
 * Another option would be to make a seperate checkAffordances method for each SocialPractice
 * 
 * @author rijk
 */
public abstract class SocialPractice {
	private Agent myAgent;
	private Class<? extends Value> purpose;
	private ArrayList<PContext> affordances=new ArrayList<PContext>();
	//private ArrayList<PContext> performanceHistory = new ArrayList<PContext>();�߮
	private HashMap<Object, Double>  performanceHistoryMap = new HashMap<Object, Double>();
	
	public HashMap<Object, Double> getPerformanceHistoryMapSP() {
		return performanceHistoryMapSP;
	}

	//private DoubleMatrix performanceHistoryMatrix;
	private HashMap<Object, Double> evaluationHistoryMap = new HashMap<Object, Double>();
	private HashMap<Object, Double>  performanceHistoryMapSP = new HashMap<Object, Double>();
	
	//private ArrayList<Evaluation> evaluations = new ArrayList<Evaluation>(); //Iig nu niet nodig en erg veel ruimte
	private Evaluation lastEvaluation;
	private double evaluationAvarage = 1;
	//TODO: add a lastgrade option to reduce runningtime?

	/**
	 * The constructor.
	 */
	public SocialPractice(Agent a) {
		myAgent = a;
	}
	
	/**
	 * Description of the method embodiment.
	 * Abstract or not?
	 */
	public void embodiment(){
		//satisfy values
	}
	 
	/**
	 * Description of the method updatePerformanceHistory.
	 */

	public void updatePerformanceHistory(PContext currentContext, boolean isEnacted, double learnWeight) {
		//performanceHistory.add(currentContext); <- requires space
		updatePerformanceHistoryMap(currentContext, isEnacted, learnWeight);
		
		//We stoppen de supermap in de meateatingpractice
		if(this instanceof MeatEatingPractice) updatePerformanceHistoryMapSP(currentContext,learnWeight);
	}
	
	public void updateEvaluationHistoryMap(PContext currentContext, double grade, double learnWeight) {
		Helper.mapLearn(false, evaluationHistoryMap, currentContext.getMyLocation(), grade, learnWeight);

		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
		double newValue = grade; //agents.size()?
		for(Agent a: agents){
			Helper.mapLearn(false, evaluationHistoryMap, a, newValue, learnWeight);
		}	
	}
	
	//Don't know if this is still neccesary.
	//Note that you use the last grade not the avarage
//	public void updatePerformanceHistoryEvaluative(PContext currentContext){
//		//performanceHistory.add(currentContext); <- requires space
//		double grade = lastEvaluation.getGrade();
//		updatePerformanceHistoryMap(currentContext, grade);
//	}
	 
	
	private void updatePerformanceHistoryMap(PContext currentContext, boolean isEnacted, double learnWeight) {
		double newValue = (isEnacted) ? 1:0;
		Helper.mapLearn(true, performanceHistoryMap, currentContext.getMyLocation(), newValue, learnWeight);

		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
		for(Agent a: agents){
			Helper.mapLearn(true, performanceHistoryMap, a, newValue, learnWeight);
		}	
	}
	
	private void updatePerformanceHistoryMapSP(PContext currentContext, double learnWeight) {
		//And learn 0 voor die niet in de context zitten.
		
		//Niet als je in Home bent, oftewel dit wordt een SP voor'dining out' anders, gaat de HI zo hard omlaag
				Map<Object, Double> temp =new HashMap<Object, Double>(performanceHistoryMapSP);
		if(!(currentContext.getMyLocation() instanceof Home)){
			Helper.mapLearn(true, performanceHistoryMapSP, currentContext.getMyLocation(), 1.0, learnWeight);
			temp.remove(currentContext.getMyLocation());
			for(Object o: temp.keySet()){
				Helper.mapLearn(true, performanceHistoryMapSP, o, 0.0, learnWeight);
			}
		}
		//agents
		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
		for(Agent a: agents){
			Helper.mapLearn(true, performanceHistoryMapSP, a, 1.0, learnWeight);
		}
		for(Object o: currentContext.getMyAgents()){
			temp.remove(o);
		}
		
	}
	
	//Dont know if this is still neccesary.
//	private void updatePerformanceHistoryMap(PContext currentContext, double grade){
//		Helper.mapAdd(performanceHistoryMap, currentContext.getMyLocation(), grade);
//
//		ArrayList<Agent> agents = currentContext.getMyAgents(); //Something wrong with adding?
//		for(Agent a: agents){
//			double add = grade/agents.size();
//			Helper.mapAdd(performanceHistoryMap, a, add);
//		}	
//	}
	
	public void addAffordance(PContext affordance){
		getAffordances().add(affordance);
	}
	
	public void removeAffordance(PContext affordance){
		PContext affordanceToRemove=null;
		for(PContext affordance1:getAffordances()){
			if(affordance1.getClass() == affordance.getClass()){
				affordanceToRemove = affordance1;
			}
		}
		getAffordances().remove(affordanceToRemove);
	}
	protected void addPurpose(Class<? extends Value> purpose){
		this.purpose = purpose;
	}
	
	public Class<? extends Value> getPurpose(){
		return purpose;
	}

	public ArrayList<PContext> getAffordances() {
		//if vegday, only allow meat in meatvenues and home;
		ArrayList<PContext> tempAffordances=new ArrayList<PContext>();
		tempAffordances.add(new PContext(new Home()));
		tempAffordances.add(new PContext(new MeatVenue()));
		if(CFG.INT_VEGDAY() ==1.0 && this instanceof MeatEatingPractice) return tempAffordances;
		return affordances;
	}

	/*
	 * Lazy evaluation?
	 */
	public double calculateFrequency(PContext myContext, double OCweight) {
		double weightedFrequency = (1- CFG.OUTSIDE_CONTEXT(OCweight)) *getFreqInsideContext(myContext) + 
				CFG.OUTSIDE_CONTEXT(OCweight) * getFreqOutsideContext(myContext);
		return weightedFrequency;
	}
	
	//For filtering locations on habits.
	public double calculateFrequencyL(Location l){
		return performanceHistoryMapSP.getOrDefault(l, 0.5);
	}
	public double calculateFrequencyA(Agent a){
		return performanceHistoryMapSP.getOrDefault(a, 0.5);
	}
	
	private double getFreqInsideContext(PContext myContext) {
		double frequencyLocation = performanceHistoryMap.getOrDefault(myContext.getMyLocation(), 0.5);
		double frequencyAgents = 0;
		for(Agent a: myContext.getMyAgents()){
			frequencyAgents+=performanceHistoryMap.getOrDefault(a, 0.5);
		}
		return 0.5 * frequencyLocation + 0.5 *(frequencyAgents/myContext.getMyAgents().size());
	}

	private double getFreqOutsideContext(PContext myContext){
		Map<Object, Double> temp =new HashMap<Object, Double>(performanceHistoryMap);
		temp.remove(myContext.getMyLocation());
		for(Object o: myContext.getMyAgents()){
			temp.remove(o);
		}
		if(temp.values().isEmpty()) return 0.5;
		
		//Filter alle locaties eruit
		Map<Object, Double> locationOC = 
				temp.entrySet()
				.stream()
				.filter(p -> p.getKey() instanceof Location)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		
		
		Map<Object, Double> agentOC = 
				temp.entrySet()
				.stream()
				.filter(p -> p.getKey() instanceof Agent)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		
				
		double hiLocation = locationOC.isEmpty()? 0.5:Helper.sumDouble(locationOC.values())/locationOC.size();
		double hiAgents = agentOC.isEmpty() ? 0.5:Helper.sumDouble(agentOC.values())/agentOC.size();
		
		return 0.5 * hiLocation + 0.5 * hiAgents;
	}
	
	public double calculateEvaluation(PContext myContext, double OCweight){
		if(myContext == null){
		//	System.out.println("Called without context");
			return getEvaluationAvarage(); //Abstraheer over context als je er geen hebt.
		}
		
		double weightedEvaluation = (1- CFG.OUTSIDE_CONTEXT(OCweight)) *getEVInsideContext(myContext)+
				CFG.OUTSIDE_CONTEXT(OCweight) * getEVOutsideContext(myContext);
		return weightedEvaluation;
	}
	
	private double getEVInsideContext(PContext myContext) {
		double evLocation = evaluationHistoryMap.getOrDefault(myContext.getMyLocation(), 1.0);
		double sumAgents = 0;
		for(Agent a: myContext.getMyAgents()){
			sumAgents+=evaluationHistoryMap.getOrDefault(a, 1.0);
		}
		return 0.5 * evLocation + 0.5 *(sumAgents/myContext.getMyAgents().size());
	}
	
	private double getEVOutsideContext(PContext myContext){
		HashMap<Object, Double> temp =new HashMap<Object, Double>(evaluationHistoryMap);
		temp.remove(myContext.getMyLocation());
		for(Object o: myContext.getMyAgents()){
			temp.remove(o);
		}
		if(temp.values().isEmpty()) return 1;
		
		//Filter alle locaties eruit
		Map<Object, Double> locationOC = 
				temp.entrySet()
				.stream()
				.filter(p -> p.getKey() instanceof Location)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		
		
		Map<Object, Double> agentOC = 
				temp.entrySet()
				.stream()
				.filter(p -> p.getKey() instanceof Agent)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		
				
		double evLocation = locationOC.isEmpty()? 1.0:Helper.sumDouble(locationOC.values())/locationOC.size();
		double evAgents = agentOC.isEmpty() ? 1.0:Helper.sumDouble(agentOC.values())/agentOC.size();
		
		return 0.5 * evLocation + 0.5 * evAgents;
	}
	

	
	//Don't know if these our neccessary. Maybe for data or something.
	public void addEvaluation(Evaluation ev, double learnWeight) {
		//System.out.println("Evaluation: "+ev.getGrade());
		lastEvaluation = ev; 
		//evAvarage goes over all evaluation for this action
		evaluationAvarage = (1 - CFG.LEARN_RATE(learnWeight)) *evaluationAvarage + CFG.LEARN_RATE(learnWeight) * ev.getGrade();
		updateEvaluationHistoryMap(ev.getContext(),ev.getGrade(), learnWeight);
		//if(getID()%30 ==3)System.out.println("grade"+ ev.getGrade);
	}

	public double getEvaluationAvarage(){
		return evaluationAvarage;
	}
	/*
	 * If Evaluation isn't use return a 0-Evaluation.
	 */
	public Evaluation getLastEvaluation(){
		if(lastEvaluation == null) return new Evaluation(0,0,0,0, null);
		else{
			return lastEvaluation;
		}
	}
}
