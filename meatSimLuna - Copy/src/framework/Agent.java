/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
// Start of user code (user defined imports)





import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import main.SocialPracticeConverter;
import main.CFG;
import main.Helper;
import meatEating.Conservation;
import meatEating.MeatEatingPractice;
import meatEating.MeetUpLocation;
import meatEating.MixedVenue;
import meatEating.Openness;
import meatEating.SelfEnhancement;
import meatEating.SelfTranscendence;
import meatEating.VegEatingPractice;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameter;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/*
 * 
 * POssible speedincrease:
 * Only filter on openLocations once per timestep, not twice per agent
 */
// End of user code

/**
 * myAction is a Social Practice and a Social Practice has an embodiment variable, if we choose Social Practices to represent a multiple of actions we should make an action class, myAction should be an Action and action has an embodiment
 * chooseAction needs a temporary variable named candidateActions()
 * 
 * @author rijk
 */
public abstract class Agent {
	private Grid<Object> myGrid;
	private ArrayList<Agent> agents;
	private ArrayList<Location> candidateLocations; //Does not include Homes.
	private PContext myContext;
	private ArrayList<SocialPractice> mySocialPractices; //check with reseting model
	int i =0;
	

	public HashMap<Class, Value> myValues;
	
	//Weights
	private double habitWeight;
	private double OCweight;
	private double learnWeight;
	private double Iweight; 
	private double Sweight;
	private double acceptRate;
	private double chooseOnPhysicalRate;
	
	
	//For chosing context
	private Location myMeetUpPlace;
	private Location myHome;
	private double diningOutRatio;
	private boolean isLocated;
	public double generalEvaluation;
	
	//For Data Projection
	private int ID;
	private SocialPractice myAction;
	HashMap<SocialPractice, Double> frequencies; //TODO: Might be nicer to put it in the SocialPractice
	HashMap<SocialPractice, Double> habitStrengths;
	HashMap<SocialPractice, Double> habitStrengthsWeighted;

	HashMap<Location, Double> frequenciesL;
	HashMap<Location, Double> habitStrengthsL;

	HashMap<Agent, Double> frequenciesA;
	HashMap<Agent, Double> habitStrengthsA;
	public HashMap<Double, Double> habitStrengthsMeat;
	
	ActionType actionType;
	private enum ActionType{
		AFFORDED,
		HABITUAL,
		INTENTIONAL,
		NOACTION,
		RANDOM
	}
	private boolean isEating;
	private ArrayList<Location> homes;
	
	

	public Agent(ArrayList<Agent> agents, ArrayList<Location> candidateLocations, ArrayList<Location> homes, Grid<Object> grid, Location meetUpPlace) {
		this.myGrid = grid;
		this.candidateLocations = candidateLocations;
		this.agents = agents;
		this.ID = CFG.getAgentID(); //for repast
		
		mySocialPractices=new ArrayList<SocialPractice>();
		myValues =new HashMap<Class, Value>();
		
		frequencies=new HashMap<SocialPractice, Double>();
		habitStrengths=new HashMap<SocialPractice, Double>();
		habitStrengthsWeighted=new HashMap<SocialPractice, Double>();
		frequenciesL =new HashMap<Location, Double>();
		habitStrengthsL =new HashMap<Location, Double>();
		frequenciesA=new HashMap<Agent, Double>();
		habitStrengthsA =new HashMap<Agent, Double>();
		habitStrengthsMeat =new HashMap<Double, Double>();
		
		int randomIndex = RandomHelper.nextIntFromTo(0,
				homes.size() - 1);
		myHome= homes.get(randomIndex);
		this.homes = homes;
		diningOutRatio = CFG.getDiningOutRatio();
		myMeetUpPlace = meetUpPlace;
		generalEvaluation = 1.0;
	}
	
	
	public double getGeneralEvaluation() {
		return generalEvaluation;
	}

	//See Update General Ev (EV of agent for Eating-practice)
	public void setGeneralEvaluation(double generalEvaluation) {
		this.generalEvaluation = generalEvaluation;
	}


	/**
	 * Description of the method step.
	 * How is this done per agent?
	 */
	
	@ScheduledMethod(start = 1, interval =1, priority = 7)
	public void updateWeights(){
		if(CFG.useComplexValues()){
			setHabitWeight(Helper.normalize(myValues.get(Conservation.class).getStrength(null) -myValues.get(Openness.class).getStrength(null), 0, 0.47, 1, CFG.habitSD()));
			setOCweight(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()));
			setIweight(Helper.normalize(myValues.get(SelfEnhancement.class).getStrength(null),1,0.25));
			setSweight(Helper.normalize((myValues.get(Conservation.class).getStrength(null)+myValues.get(SelfTranscendence.class).getStrength(null))/2,1,0.125));
			setAcceptRate(Helper.normalize(myValues.get(SelfEnhancement.class).getStrength(null)+ myValues.get(Conservation.class).getStrength(null) 
					-(myValues.get(Openness.class).getStrength(null)+ myValues.get(SelfTranscendence.class).getStrength(null)),0,0.47));
			setChooseOnPhysicalRate(Helper.normalize(myValues.get(SelfEnhancement.class).getStrength(null) -myValues.get(SelfTranscendence.class).getStrength(null), 0, 0.47));
			setLearnWeight(Helper.normalize(myValues.get(Openness.class).getStrength(null) -myValues.get(Conservation.class).getStrength(null), 0, 0.47,1,CFG.habitlearnSD()));
	}
	}
	
	//This is if you want to let only a percentage of the agents eat.
	@ScheduledMethod(start = 1, interval = 1, priority = 6)
	public void determineIfEating() {
		isEating = (RandomHelper.nextIntFromTo(0,100) <= CFG.diningOutPercent());
	}
	
	
	
	//Either attribute people randomly to the set of locations.
	@ScheduledMethod(start = 1, interval = 1, priority = 5)
	public void randomContext() {
		if(!CFG.chooseContext() && isEating){
			//Note that you don't add PContexts to the grid, nor move their location
			//When making a Pcontext the constructer automaticly sets the pcontext of the location.
			List<Location> openLocations = new ArrayList<Location>(candidateLocations);
			openLocations.addAll(homes);
			openLocations = filterOnAffordancesL(openLocations);
			int randomIndex = RandomHelper.nextIntFromTo(0,
					openLocations.size() - 1);
			Location randomLocation = openLocations.get(randomIndex);
			goTo(randomLocation);
		}
	}
	
	@ScheduledMethod(start= 1, interval =1, priority=5.5)
	public void meetUp(){
		if(CFG.MEETUP_VEG()){
			PContext affordanceToRemove =null;
			for(PContext affordance:getMeatPractice().getAffordances()){
				if(affordance.getMyLocation().getClass() == myMeetUpPlace.getClass()) affordanceToRemove = affordance;
			}
			 if(affordanceToRemove != null) getMeatPractice().removeAffordance(affordanceToRemove); //After one time it isnt there anymore
		}
		if(CFG.isMeetUp() &&!isLocated){
			if(CFG.MEETUP_INVITE()){
				if(acceptInvitation(myMeetUpPlace)) diningOutToMeetUp(); //Only if you want to eat Veg you organize a dining party.
			}
			else goTo(myMeetUpPlace);
		//	System.out.println("Agents MEETUP");
		}
	}
	


	//Or let them choose their Context
	@ScheduledMethod(start =1, interval = 1, priority = 5)
	public void diningIn() {
		//i = 0;
		if(CFG.chooseContext() &&isEating &&!isLocated){
			//if(CFG.isMeetUp()) System.out.println("Something wrent wrong");
			if(RandomHelper.nextDoubleFromTo(0, 1) > diningOutRatio)
				goTo(myHome);
		}
	}
	
	//When you go to something you create or join a Pcontext.
	public void goTo(Location l){
		//System.out.println("Agents:" + i++);
		if(!l.hasContext()) new PContext(l);
		myContext = l.getMyContext();
		myContext.addAgent(this);
		if(CFG.GUI()) Helper.moveToObject(myGrid, this, l);
		setLocated(true);
	}
	
	public void diningOutToMeetUp(){
		if(CFG.chooseContext() &&isEating && !isLocated){
			Location chosenLocation;
			ArrayList<Agent> diningGroup =new ArrayList<Agent>();
			List<Agent> candidateAgents=new ArrayList<Agent>(agents);
			candidateAgents = filterOnAffordancesA(candidateAgents);
			
			diningGroup.add(this);
			candidateAgents.remove(this);
			
			
				chosenLocation = myMeetUpPlace;
				for(int i = 0; i < CFG.inviteDistribution(); i++){
					Agent a = pickEatBuddy(candidateAgents); //TODO: maakt lijst van eetbuddies elke keer opnieuw, pak een lijst, maak op, pak nieuw lijst
					if(a != null){
						candidateAgents.remove(a); //Remove from candidates!
						if(a.acceptInvitation(chosenLocation)) diningGroup.add(a); 
					}
				}
			
			for(Agent a:diningGroup){
				a.goTo(chosenLocation);
			}
		}
	}
	
	//Others diningOut
	@ScheduledMethod(start =1, interval = 1, priority = 4)
	public void diningOut(){
		if(CFG.chooseContext() &&isEating && !isLocated){
			Location chosenLocation;
			ArrayList<Agent> diningGroup =new ArrayList<Agent>();
			List<Agent> candidateAgents=new ArrayList<Agent>(agents);
			candidateAgents = filterOnAffordancesA(candidateAgents);
			
			diningGroup.add(this);
			candidateAgents.remove(this);
			boolean chooseOnPhysical = false;
			if(RandomHelper.nextDoubleFromTo(0, 1) < getChooseOnPhysicalRate()){
				chooseOnPhysical = true;
			}
			
			if(chooseOnPhysical){
				chosenLocation = pickLocation(candidateLocations);
				for(int i = 0; i < CFG.inviteDistribution(); i++){
					Agent a = pickEatBuddy(candidateAgents); //TODO: maakt lijst van eetbuddies elke keer opnieuw, pak een lijst, maak op, pak nieuw lijst
					if(a != null){
						candidateAgents.remove(a); //Remove from candidates!
						if(a.acceptInvitation(chosenLocation)) diningGroup.add(a); 
					}
				}
			}
			else{ //chooseOnSocial
				for(int i = 0; i < CFG.inviteDistribution(); i++){
					Agent a = pickEatBuddy(candidateAgents);
					if(a != null){
						candidateAgents.remove(a);
						diningGroup.add(a); //Everybody accepts!
					}
				}
				List<Location> affordedLocations = filterOnGroupsPreference(diningGroup, candidateLocations);
				chosenLocation = pickLocation(affordedLocations); //Lijst kan leeg zijn als er geen mixed zijn. Je zou dan een willekeurige kunnen pakken ofzo.
			}
			
			for(Agent a:diningGroup){
				a.goTo(chosenLocation);
			}
		}
	}
	
	

	private List<Location> filterOnGroupsPreference(
			ArrayList<Agent> diningGroup,
			ArrayList<Location> candidateLocations2) {
		List<Location> newCandidates=new ArrayList<Location>();
		
		//De kans dat iedereen in de groep een veg restaurant accept is erg laag
		//Al helemaal als we niet op intenties filteren, i.e. je gewoon per agent 50% kans hebt dat die accepteert.
		for(Location l:candidateLocations2){
			boolean accepted = true;
			for(Agent a:diningGroup){
				accepted &= a.acceptInvitation(l); //Geeft vast errors als je geen mixed restaurants, meer hebt, omdat lijst dan leeg is.
			}
			if(accepted) newCandidates.add(l);
		}
		return newCandidates;
	}


	//Might extend to choice on values.

	
	private boolean isHomogenous(List<Location> list){
		boolean homogenous = true;
		Location proto = list.get(0);
		for(Location l:list){
			if(proto.getClass() != l.getClass()) homogenous = false;
		}
		return homogenous;
	}
	
	private Location pickLocation(List<Location> locations){
		//You could add an affordance variable for open and close.
		List<Location> temp =new ArrayList<Location>(locations); 
		
		temp = filterOnAffordancesL(temp); //Easier to give a new list back and change ref.
		List<Location> aFiltered = new ArrayList<Location>(temp);
		if(isHomogenous(aFiltered)) return pickRandomly(temp);
		
		if(CFG.isFilteredOnHabits()){
		temp = filterOnHabitsL(temp);
		List<Location> hFiltered = new ArrayList<Location>(temp);
		if(hFiltered.isEmpty()) temp = aFiltered; //Reroll if empty
		else if(isHomogenous(hFiltered)) return pickRandomly(temp);
		}
		
		if(CFG.isIntentional()){
		temp = filterOnIntentionsL(temp);
		List<Location> iFiltered = new ArrayList<Location>(temp);
		}
		
		return pickRandomly(temp);
	}
	
	private List<Location> filterOnAffordancesL(List<Location> temp) {
		List<Location> newCandidates=new ArrayList<Location>();
		for(Location l:temp){
			if(l.isOpen()) newCandidates.add(l);
		}
		return newCandidates;
	}
	private List<Location> filterOnHabitsL(List<Location> aFiltered) {

		List<Location> newCandidates=new ArrayList<Location>();
		habitStrengthsL.clear();
		RandomHelper.createNormal(1, 0.5);
		double randomAttention = Math.min(2, Math.max(0, RandomHelper.getNormal().nextDouble()));
		RandomHelper.createNormal(1, 0.25);
		
		for(Location l: candidateLocations){
			double HI = mySocialPractices.get(0).calculateFrequencyL(l);
			double ro = CFG.getEvaluationCorrelation();
			double Attention = 2-(
					ro * getGeneralEvaluation()+
						Math.sqrt(1-(ro*ro)) * randomAttention);
			double habitThreshold = CFG.HTR(getHabitWeight());
			
			if(HI > Attention * habitThreshold) newCandidates.add(l);
			
			habitStrengthsL.put(l, HI);
		}
		return newCandidates;
	}
	
	private List<Agent> filterOnHabitsA(List<Agent> aFiltered) {
		List<Agent> newCandidates=new ArrayList<Agent>();
		habitStrengthsA.clear();
		RandomHelper.createNormal(1, 0.5);
		double randomAttention = Math.min(2, Math.max(0, RandomHelper.getNormal().nextDouble()));
		RandomHelper.createNormal(1, 0.25);
		
		for(Agent a: agents){
			double HI = mySocialPractices.get(0).calculateFrequencyA(a);
			double ro = CFG.getEvaluationCorrelation();
			double Attention = 2- (ro * getGeneralEvaluation()+
						Math.sqrt(1-(ro*ro)) * randomAttention);
			double habitThreshold = CFG.HTR(getHabitWeight());
			
			if(HI > Attention * habitThreshold) newCandidates.add(a);
			habitStrengthsA.put(a, HI);
		}
		return newCandidates;
	}
	/*Filter candidate Social Practices on their relative Habit Strength by:
	 * 1. Calculate frequency per Social Practice
	 * 2. Calculate totalFrequency
	 * 3. Calculate Habit Strength per Social Practice
	 * //Thus your HI * evaluation > HTR * HW * Attention
		//i.e. if you have done the action more often
		//or you evaluate it higher
		//you have more chance to go in a habit
		
		//if the general threshold is higher
		//if you have a higher disposition to go into a habit
		//if you have more attention for it
		//you have less chance of going into the habit
	 */
	private ArrayList<SocialPractice> filterOnTriggers(
			ArrayList<SocialPractice> candidateSocialPractices) {
		ArrayList<SocialPractice> newCandidates=new ArrayList<SocialPractice>();
		habitStrengths.clear();
		habitStrengthsWeighted.clear();
		
		RandomHelper.createNormal(1, 0.5);
		double randomAttention = Math.min(2, Math.max(0, RandomHelper.getNormal().nextDouble()));
		RandomHelper.createNormal(1, 0.25);
		
		for(SocialPractice sp:mySocialPractices){
				double HI =sp.calculateFrequency(myContext,getOCweight());
				double ro = CFG.getEvaluationCorrelation();
				//Ro moet nog negatief!
				double evaluation = sp.calculateEvaluation(myContext, CFG.OUTSIDE_CONTEXT(getOCweight()));
				double EVvalue =
							((ro * evaluation)+
							(Math.sqrt(1-(ro*ro)) * randomAttention));
				double Attention = 2- Helper.normalize(EVvalue, 1, 0.5);
				double habitThreshold = CFG.HTR(getHabitWeight());
				
				if(HI > Attention * habitThreshold) newCandidates.add(sp);
				
				//System.out.println(sp.getClass() + "HI"+ HI + "Thr:" + Attention*habitThreshold);
				//System.out.println(sp.getClass() + "EV"+ evaluation + "Attention:" + Attention + "HW"+ getHabitWeight());
				//data
				habitStrengths.put(sp, HI);
				habitStrengthsWeighted.put(sp, habitStrengths.get(sp) - CFG.HTR(getHabitWeight()));//data
			}
		//Data
		habitStrengthsMeat.put(CFG.getTime(),dataHabitStrength(MeatEatingPractice.class));//For Data purposes
		return newCandidates;
		
		
		
		
		//System.out.println("HI"+ pair.getValue() + "EV:" + getEvaluationInContext());
		//System.out.println("HTR"+ CFG.HTR(1) + "HW"+ getHabitWeight() + "Attention" + attention);
		
		
		//Entry<ArrayList<SocialPractice>, Double> pair = relativeHabitFilter(habitStrengths);
//		if(pair.getValue() * 
//				pair.getKey().get(0).calculateEvaluation(myContext, CFG.OUTSIDE_CONTEXT(getOCweight())) //NB:Kan alleen nu omdat er twee practices zijn.
//				> CFG.HTR(getHabitWeight())*attention){
//			
//			//System.out.println("I agentnr. " + getID() + " am doing a" + pair.getKey().get(0) + "habit at tick: " + CFG.getTime());
//			
//			newCandidates = pair.getKey();
//		}
//		return newCandidates;
	}
	
	private List<Location> filterOnIntentionsL(List<Location> hFiltered) {
		List<Location> newCandidates=new ArrayList<Location>();
		SocialPractice chosenAction= chooseOnIntentions(mySocialPractices);
		for(Location l:hFiltered){
			for (PContext affordance : chosenAction.getAffordances()) {
				if(l.getClass() == affordance.getMyLocation().getClass()) newCandidates.add(l);
			}
		}
		return newCandidates;
	}
	
	
	private <T> T pickRandomly(List<T> list){
		return list.get(RandomHelper.nextIntFromTo(0, list.size()-1));
	}
	
	
	
	private Agent pickEatBuddy(List<Agent> candidateAgents){
		if(candidateAgents.isEmpty()) return null;
		List<Agent> temp =new ArrayList<Agent>(candidateAgents); 
		List<Agent> original=new ArrayList<Agent>(candidateAgents);
		//temp = filterOnAffordancesA(temp); //Easier to give a new list back and change ref.
		//List<Agent> aFiltered = new ArrayList<Agent>(temp);
		//if(aFiltered.isEmpty()) return null; //Niemand available (van de candidates)
		
		if(CFG.isFilteredOnHabits()){
		temp = filterOnHabitsA(temp);
		List<Agent> hFiltered = new ArrayList<Agent>(temp);
		if(hFiltered.isEmpty()) temp = original; //Reroll if empty
		}
		
		return pickRandomly(temp);

	}
	private List<Agent> filterOnAffordancesA(List<Agent> temp) {
		List<Agent> newCandidates=new ArrayList<Agent>();
		for(Agent a:temp){
			if(!a.isLocated()) newCandidates.add(a);
		}
		return newCandidates;
	}
	
	
	
	private boolean acceptInvitation(Location chosenLocation) {
		boolean accept = false;
		if (chosenLocation instanceof MixedVenue){
			accept = true;
		}
		else{
			SocialPractice temp;
			temp = (CFG.isIntentional()) ? //Get a random practice if intentions aren't used.
					chooseOnIntentions(mySocialPractices):
					(RandomHelper.nextIntFromTo(0, 1) ==1) ?
							new MeatEatingPractice(this):
							new VegEatingPractice(this);
			for(PContext affordance: temp.getAffordances()){
				if(chosenLocation.getClass() == affordance.getMyLocation().getClass()) accept = true;
			}
			if(!accept){ //Sometimes even accept if it does not cater values
				if(RandomHelper.nextDoubleFromTo(0, 1) < getAcceptRate()){
					accept = true;
				}
			}
		}
		return accept;
	}
	
	
	
	
	
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void chooseFood() {
		if(isEating) myAction = chooseAction();
		else{
			myAction = new NoAction(this); //Maybe change to just new Social Practice which will not be an instance of either;
			actionType = ActionType.NOACTION;
		}
		if(myAction == null) System.out.println("No action is chosen");
		act();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step3(){
		if(isEating) learn();
	}
	
	
	 
	/**
	 * Moves agent to random context.
	 * Maybe put in Eater instead.
	 */
//	private void randomContext() {
		
//		if(CFG.chooseContext()){
//			if(!located){
//				ArrayList<Agent> diningGroup = new ArrayList<Agent>();
//				Location chosenRestaurant;
//				
//				System.out.print(RandomHelper.nextDouble());
//				
//				/*Eat at home*/
//				if(RandomHelper.nextDoubleFromTo(0, 1) <diningOutRatio){
//					chosenRestaurant = myHome;
//					for(int i = 0; i < CFG.nrOfPeople(); i++){
//						diningGroup.add(socialDefault());
//					}
//				}
//				/*Eat out*/
//				else{
//					/*Physical First*/
//					if(RandomHelper.nextDoubleFromTo(0, 1) <exploreRatio){
//						chosenRestaurant = physicalDefault();
//					}
//					else chosenRestaurant = exploreNew();
//				}
//				
//				
//			}
//		}
		
//		//Note that you don't add PContexts to the grid, nor move their location
//		int randomIndex = RandomHelper.nextIntFromTo(0,
//				candidateLocations.size() - 1);
//		Location randomLocation = candidateLocations.get(randomIndex);
//		myContext = (randomLocation.hasContext()) ? randomLocation.getMyContext():new PContext(candidateLocations.get(randomIndex));
//		myContext.addAgent(this);
//		Helper.moveToObject(myGrid, this, randomLocation);
//	}
	 
	/**
	 * Description of the method chooseAction.
	 */
	private SocialPractice chooseAction() {
		ArrayList<SocialPractice> candidateSocialPractices = (ArrayList<SocialPractice>) mySocialPractices
				.clone();
		ArrayList<SocialPractice> previousCandidates;
		SocialPractice chosenAction;
		
		if(CFG.INT_VEGDAY() == 1.0){
			if(getMyLocation() == myHome){
				if(RandomHelper.nextIntFromTo(1,7) ==1){
					return chosenAction = getVegPractice();
				}
			}
		}
		if(CFG.INT_VEGDAY() == 2.0){
			if(getMyLocation() == myHome){
				if(RandomHelper.nextIntFromTo(1,3) ==1){
					return chosenAction = getVegPractice();
				}
			}
		}
		
		if (CFG.isFilteredOnAffordances()) {
			filterOnAffordances(candidateSocialPractices);
			if (candidateSocialPractices.size() == 1) {
				actionType = ActionType.AFFORDED;
				return candidateSocialPractices.get(0);
			}
		}

		if (CFG.isFilteredOnHabits()) {
			previousCandidates = (ArrayList<SocialPractice>) candidateSocialPractices
					.clone();
			candidateSocialPractices = filterOnTriggers(candidateSocialPractices);
			if (candidateSocialPractices.size() == 1) {
				actionType = ActionType.HABITUAL;
				return candidateSocialPractices.get(0);
			}
			if (candidateSocialPractices.size() < 1)
				candidateSocialPractices = previousCandidates; // Return to
																// Afforded
		}

		if (CFG.isIntentional()) {
			actionType = ActionType.INTENTIONAL;
			chosenAction = chooseOnIntentions(candidateSocialPractices);
		} else {
			actionType = ActionType.RANDOM;// Choose Randomly
			chosenAction = candidateSocialPractices.get(RandomHelper
					.nextIntFromTo(0, candidateSocialPractices.size() - 1));
		}
		return chosenAction;
	}
	 
	


	/**
	 * Description of the method checkAffordances.
	 */
	private void filterOnAffordances(
			ArrayList<SocialPractice> candidateSocialPractices) {
		Iterator<SocialPractice> iter = candidateSocialPractices.iterator();
		while (iter.hasNext()) {
			SocialPractice sp = iter.next();
			boolean contextAffordsSP = false;

			for (PContext affordance : sp.getAffordances()) {

				/* check affordance location matches with current location */
				Location locationToCheck = affordance.getPhysical()
						.getMyLocation();
				Location l = getMyLocation();
				if (getMyLocation().getClass() == locationToCheck.getClass())
					contextAffordsSP = true;
				// TODO: change affordances to list of classes. Problem though,
				// it is a context object right now.
				/* check affordance social matches with current socialcontext */
			}
			if (!contextAffordsSP)
				iter.remove();
		}
	}
	
	
	//Maybe errors if list is size 1;
	//Only makes subsets seperating high from low.
	public static <T> Pair<ArrayList<T>, Double> relativeHabitFilter(Map<T, Double> habitStrength){
		//System.out.print("rHabitFilter is called on size:" + habitStrength.size());
		Map<T, Double> sorted= Helper.sortByValue(habitStrength);
		ArrayList<T> keys =new ArrayList<T>(sorted.keySet());
		
		//System.out.println(habitStrength);
		//System.out.println(sorted);
		
		double bestDifference = 0;
		double bestSplit = 0;
		for(int i = 0; i < keys.size()-1; i++){
			Map<T, Double> left = new HashMap<T, Double>();
			Map<T, Double> right =new HashMap<T, Double>(habitStrength);
			for(int j =0; j <= i; j++){ //Fill maps left and right
				T key = keys.get(j);
				left.put(key, sorted.get(key));
				right.remove(key);
				//System.out.print("loopmakemaps");
			}
			
			
			double difference = Helper.avarageDouble(right.values()) - Helper.avarageDouble(left.values());
			if(difference > bestDifference){
				bestDifference = difference;
				bestSplit = i;
			}
			//System.out.print("loopfindbest");
		}
		
		ArrayList<T> newCandidates =new ArrayList<T>();
		for(int j = keys.size()-1; j > bestSplit; j--){
			newCandidates.add(keys.get(j));
			//System.out.print("loophere");
		}
		
		//System.out.println("I made it! Difference:" + bestDifference + "newCandidates" + newCandidates);
		
		return new ImmutablePair<ArrayList<T>, Double>(newCandidates, bestDifference);
	}
	/*
	 * 
	 * If the practice has never been done before return 1.
	 */
	private double habitStrength(Double spFrequency, double totalFrequency) {
		return totalFrequency ==0 ? 1:spFrequency/totalFrequency;
	}
	 
	/**
	 * Description of the method checkIntentions.
	 * Choose random double in between 0 and total need.
	 * Choose socialpractice based on this double and need per sp.
	 */
	private SocialPractice chooseOnIntentions(
			ArrayList<SocialPractice> candidateSocialPractices) {
		SocialPractice chosenAction = null; //temp
		HashMap<SocialPractice, Double> needs=new HashMap<SocialPractice, Double>();
		for(SocialPractice sp: candidateSocialPractices){
			needs.put(sp, myValues.get(sp.getPurpose()).getNeed(myContext)); 
		}
		double totalNeed = Helper.sumDouble(needs.values()); //satisfaction can get <0, so need as well, so maybe not getting in while loop
		double randomDeterminer = RandomHelper.nextDoubleFromTo(0, totalNeed);
		//System.out.println(myContext);
		//System.out.println("Needs:" + needs);
		
		Iterator it = needs.entrySet().iterator();
		while(randomDeterminer > 0) {
			HashMap.Entry pair = (HashMap.Entry) it.next();
			chosenAction = (SocialPractice) pair.getKey();
			randomDeterminer = randomDeterminer - (double) pair.getValue();
		}
		return chosenAction;
	}
	 
	/**
	 * Description of the method act.
	 */
	private void act() {
		myAction.embodiment();
	}
	
	private void updateValues(){
		for(Value val: myValues.values()){
			val.updateSatisfaction(myContext, myAction);
		}
	}
	 
	/**
	 * Description of the method learn.
	 */
	private void learn() {
		updateValues();
		if(CFG.isUpdatedPerformanceHistory()){
			updateHistory();
		}
		if(CFG.isEvaluated()) evaluate();
	}
	//Dunno if still needed
	//private void updateValuesEvaluative() {
	//	for(Value val: myValues.values()){
	//		val.updateSatisfactionEvaluative(myAction);
	//	}
	//}


	/**
	 * Description of the method evaluate.
	 * 
	 * Gives all information, but in evaluate it is decided what is used.
	 */
	private void evaluate() { //All factors are avarage 1
		
	//	System.out.println("Evaluation :" + Iweight + " " + individualEvaluation() + " " + Sweight + " " + socialEvaluation());
		Evaluation ev=new Evaluation(getIweight(), individualEvaluation(), getSweight(), socialEvaluation(), myContext);
		myAction.addEvaluation(ev, getEvaluationLearnRatio());
		updateGeneralEvaluation(ev, getEvaluationLearnRatio());
	}
	

	//GeneralEvaluation is an agents general evaluation of the Eatin-practice, it is iniated on 1?
	private void updateGeneralEvaluation(Evaluation ev, double learnRatio) {
		generalEvaluation = (1-learnRatio)*generalEvaluation + learnRatio *ev.getGrade();
	}


	private double socialEvaluation() {
		double simAgents = 0;	//amount of similar agents
		for(Agent a: myContext.getMyAgents()){
			if(a.myAction.getClass() == myAction.getClass() && a != this) simAgents++;
		}
		double dissimAgents = myContext.getMyAgents().size() - simAgents; //amount of dissimilar agents
		double x = simAgents - dissimAgents;
		//System.out.print("socev: "+ x + "at tick:" + CFG.getTime());
		//return 1 + 0.5 * Math.tanh((x-CFG.a)/CFG.b);
		return Helper.normalize(x, 0, 0.86);
	}
	
	//Strength not need or someting! If need, you have to think about order.
	private double individualEvaluation() {
		//System.out.println("indE"+ myValues.get(myAction.getPurpose()).getStrength(null));
		return myValues.get(myAction.getPurpose()).getStrength(null);
	}
	public void updateHistory(){
			myAction.updatePerformanceHistory(myContext, true, getHabitLearnRatio());
			for(SocialPractice action:mySocialPractices){
				if(action!=myAction) action.updatePerformanceHistory(myContext, false, getHabitLearnRatio());
			}
	}
//	private void updateHistoryEvaluative() {
//		myAction.updatePerformanceHistoryEvaluative(myContext);
//		
//	}
	protected void addSocialPractice(SocialPractice sp){
		mySocialPractices.add(sp);
	}
	protected void addValue(Value val){
		myValues.put(val.getClass(), val);
	}
	
	@Parameter(usageName="myAction", displayName="Action", converter = "main.SocialPracticeConverter")
	public SocialPractice getMyAction() {
		return this.myAction;
	}
	
	public Location getMyLocation(){
		return myContext.getPhysical().getMyLocation();
	}
	
	/*Datacollectors*/
	/*Aggregate*/
	public int dataMeatAction(){
		return (getMyAction() instanceof MeatEatingPractice) ? 1:0;
	}
	public int dataVegAction(){
		return (getMyAction() instanceof VegEatingPractice) ? 1:0;
	}
	public int dataAffAction(){
		return (actionType == ActionType.AFFORDED) ? 1:0;
	}
	public int dataHabitual(){
		return (actionType == ActionType.HABITUAL) ? 1:0;
	}
	public int dataIntentional(){
		return (actionType == ActionType.INTENTIONAL) ? 1:0;
	}

	/*Graph1*/
	public int dataEatingType(){
		return dataMeatAction() - dataVegAction(); 
	}
	
	/*Crosspoints*/
	public int dataMeatAfforded(){
		return (dataMeatAction() + dataAffAction() == 2) ? 1:0;
	}
	public int dataMeatHabitual(){
		return (dataMeatAction() + dataHabitual() == 2) ? 1:0;
	}
	public int dataMeatIntentional(){
		return (dataMeatAction() + dataIntentional() == 2) ? 1:0;
	}
	
	public int dataVegIntentional(){
		return (dataVegAction() + dataIntentional() == 2) ? 1:0;
	}
	public int dataVegHabitual(){
		return (dataVegAction() + dataHabitual() == 2) ? 1:0;
	}
	public int dataVegAfforded(){
		return (dataVegAction() + dataAffAction() == 2) ? 1:0;
	}
	
	/*Individual*/
	/*Graph 6*/
	public int dataOneAgent(){
		if(dataMeatAfforded() == 1) return 1;
		if(dataMeatHabitual() == 1) return 2;
		if(dataMeatIntentional() == 1) return 3;
		if(dataVegAfforded() == 1) return -1;
		if(dataVegHabitual() == 1) return -2;
		if(dataVegIntentional() == 1) return -3;
		return 0; //Should never get here.
	}
	
	/*Habitual Params*/
	//Now only returns habitStrength if agent has just done a habitualaction.
	//
	public double dataHabitStrength(Class spClass){
//		for(SocialPractice sp: habitStrengths.keySet()){
//			if(actionType == ActionType.HABITUAL && sp.getClass()==spClass) return habitStrengths.get(sp);
//		}
//		return -1.0;
		double a = 0;
		for(SocialPractice sp: habitStrengths.keySet()){
		if(sp.getClass()==spClass) return  a= habitStrengths.get(sp);
		}
		return a;
	}
	
	public double dataHabitStrengthWeighted(Class spClass){
//		for(SocialPractice sp: habitStrengths.keySet()){
//			if(actionType == ActionType.HABITUAL && sp.getClass()==spClass) return habitStrengths.get(sp);
//		}
//		return -1.0;
		double a = 0;
		for(SocialPractice sp: habitStrengthsWeighted.keySet()){
		if(sp.getClass()==spClass) return  a= habitStrengthsWeighted.get(sp);
		}
		return a;
	}
	
	
	public double dataFrequencyIndex(Class spClass){
		for(SocialPractice sp: frequencies.keySet()){
			if(sp.getClass()==spClass) return frequencies.get(sp);
		}
		return 0.0;
	}
	
	/*Intentional Params*/
	public double dataNeed(Class spClass){
		double need = myValues.get(spClass).getNeed(myContext);
		return need;
	}
	public double dataThreshold(Class spClass){
		return myValues.get(spClass).getThreshold(myContext);
	}
	public double dataSatisfaction(Class spClass){
		return myValues.get(spClass).getSatisfaction();
	}
	
	/*Evaluation params*/
	public double dataEvIndividual(){
		return myAction.getLastEvaluation().getIndE();// *myAction.getLastEvaluation().getIweight();
	}
	public double dataEvSocial(){
		return myAction.getLastEvaluation().getSocE();// *myAction.getLastEvaluation().getSweight();
	}
	public double dataEvaluation(){
		return myAction.getLastEvaluation().getGrade();
	}
	public int getID() {
		return ID;
	}


	public boolean isLocated() {
		return isLocated;
	}


	public void setLocated(boolean isLocated) {
		this.isLocated = isLocated;
	}


	public double getOCweight() {
		return OCweight;
	}


	public void setOCweight(double oCweight) {
		OCweight = oCweight;
	}

	/*Has context weight in there*/
	public double getHabitWeight() {
		return habitWeight; //TODO: letting opennes and conservation having their effect.
	}
	
	public double getEvaluationInContext(){
		double evInContext = 0;
		if(CFG.isEvaluated()){
			for(SocialPractice sp: getMySocialPractices()){
				evInContext+= sp.calculateEvaluation(myContext, CFG.OUTSIDE_CONTEXT(getOCweight()));
			}
			evInContext = evInContext/getMySocialPractices().size();
		}else{
			evInContext = 1;
		}
		return evInContext;
	}
	
	public double getEvaluationInContextMeat(){
		return getMySocialPractices().get(0).calculateEvaluation(myContext, CFG.OUTSIDE_CONTEXT(getOCweight()));
	}
	public double getEvaluationInContextVeg(){
		return getMySocialPractices().get(1).calculateEvaluation(myContext, CFG.OUTSIDE_CONTEXT(getOCweight()));
	}
	

	public void setHabitWeight(double habitWeight) {
		this.habitWeight = habitWeight;
	}
	public ArrayList<SocialPractice> getMySocialPractices() {
		return mySocialPractices;
	}


	public void setMySocialPractices(ArrayList<SocialPractice> mySocialPractices) {
		this.mySocialPractices = mySocialPractices;
	}


	public void setContext(PContext newContext) {
		this.myContext = null;
		
	}

	private double getEvaluationLearnRatio() {
		return CFG.LEARN_RATE(getLearnWeight());
	}
	
	private double getHabitLearnRatio() {
		return CFG.HABIT_LEARN_RATE(getLearnWeight());
	}
	
	public double getLearnWeight() {
		return learnWeight;
	}


	public void setLearnWeight(double learnWeight) {
		this.learnWeight = learnWeight;
	}


	public double getIweight() {
		return Iweight;
	}


	public void setIweight(double iweight) {
		Iweight = iweight;
	}


	public double getSweight() {
		return Sweight;
	}


	public void setSweight(double sweight) {
		Sweight = sweight;
	}


	public double getAcceptRate() {
		return acceptRate;
	}


	public void setAcceptRate(double acceptRate) {
		this.acceptRate = acceptRate;
	}


	public double getChooseOnPhysicalRate() {
		return chooseOnPhysicalRate;
	}


	public void setChooseOnPhysicalRate(double chooseOnPhysicalRate) {
		this.chooseOnPhysicalRate = chooseOnPhysicalRate;
	}


	public SocialPractice getVegPractice() {
		SocialPractice spVeg= null;
		for(SocialPractice sp:getMySocialPractices()){
			if(sp.getClass()==VegEatingPractice.class) spVeg= sp;
		}
		return spVeg;
	}
	public SocialPractice getMeatPractice() {
		SocialPractice spMeat= null;
		for(SocialPractice sp:getMySocialPractices()){
			if(sp.getClass()==MeatEatingPractice.class) spMeat= sp;
		}
		return spMeat;
	}
	
}