/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package main;

import java.util.ArrayList;

import meatEating.SelfEnhancement;
import meatEating.SelfTranscendence;
import framework.Value;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.random.RandomHelper;

// Start of user code (user defined imports)

// End of user code

/**
 * Configuration file to store global variables.
 * Maybe don't need it due to paramfile
 * @author rijk
 */
public class CFG {
	public static boolean GUI(){
		return RunEnvironment.getInstance().getParameters().getBoolean("GUI");
	}
	
	/*Changes in deliberation*/
	public static boolean chooseContext(){
		return RunEnvironment.getInstance().getParameters().getBoolean("chooseContext");
	}
	public static boolean isFilteredOnAffordances(){
		return RunEnvironment.getInstance().getParameters().getBoolean("filterOnAffordances");
	}
	public static boolean isFilteredOnHabits(){
		return RunEnvironment.getInstance().getParameters().getBoolean("filterOnHabits");
	}
	public static boolean isEvaluated(){
		return RunEnvironment.getInstance().getParameters().getBoolean("isEvaluated");
	}
	public static boolean isIntentional() {
		return RunEnvironment.getInstance().getParameters().getBoolean("isIntentional");
	}	
	public static boolean isUpdatedPerformanceHistory() {
		return RunEnvironment.getInstance().getParameters().getBoolean("isUpdatedPHistory");
	}
	public static boolean complexEvaluation() {
		return RunEnvironment.getInstance().getParameters().getBoolean("isComplexEvaluated");
	}
	
	/*Agent and Venue Counts*/
	public static int agentCount(){
		return RunEnvironment.getInstance().getParameters().getInteger("eater1Count");
	}
	public static int mixedVenueCount(){
		return RunEnvironment.getInstance().getParameters().getInteger("mixedVenueCount") +CFG.getExtraMixed();
	}
	private static int ExtraMixed;
	private static int ExtraMeat;
	private static int ExtraVeg;
	
	public static int meatVenueCount(){
		return RunEnvironment.getInstance().getParameters().getInteger("meatVenueCount") + CFG.getExtraMeat();
	}
	public static int vegetarianVenueCount(){
		return RunEnvironment.getInstance().getParameters().getInteger("vegetarianVenueCount") + CFG.getExtraVeg();
	}
	public static int venueCount(){
		return mixedVenueCount() + meatVenueCount() + vegetarianVenueCount();
	}
	
	/*Habitual Parameters*/
	public static double HTR(double habitWeight){
		return Helper.bound(0,1, habitWeight * RunEnvironment.getInstance().getParameters().getDouble("HABIT_THRESHOLD_RELATIVE")); //An action has to be done 70% in this context to become habitual.
	}
	public static double OUTSIDE_CONTEXT(double OCWeight){
		return Helper.bound(0,1,OCWeight *RunEnvironment.getInstance().getParameters().getDouble("OUTSIDE_CONTEXT"));
	}
	public static double HTA(double habitWeight) {
		return habitWeight *RunEnvironment.getInstance().getParameters().getDouble("HABIT_THRESHOLD_ABSOLUTE");
	}
		
	/*Value Strength*/
	public static double SELFT_AVG_STRENGTH(){
		if(getTime() > VALUE_CHANGE_TIME()) return RunEnvironment.getInstance().getParameters().getDouble("SELFT_INTERVENTION_STRENGTH");
		return RunEnvironment.getInstance().getParameters().getDouble("SELFT_AVG_STRENGTH");
	}
	
	private static double VALUE_CHANGE_TIME() {
		return RunEnvironment.getInstance().getParameters().getDouble("VALUE_CHANGE_TIME");
	}

	public static double SELFE_AVG_STRENGTH(){
		if(getTime() > VALUE_CHANGE_TIME()) return RunEnvironment.getInstance().getParameters().getDouble("SELFE_INTERVENTION_STRENGTH");
		return RunEnvironment.getInstance().getParameters().getDouble("SELFE_AVG_STRENGTH");
	}
	
	public static double OPENNESS_AVG_STRENGTH(){
		return RunEnvironment.getInstance().getParameters().getDouble("OPENNESS_AVG_STRENGTH");
		
	}
	public static double CONSERVATION_AVG_STRENGTH(){
		return RunEnvironment.getInstance().getParameters().getDouble("CONSERVATION_AVG_STRENGTH");
	}
	
	/*Satisfaction parameters*/ //Changed to 1
	public static double SELFT_k(){
		return RunEnvironment.getInstance().getParameters().getDouble("selfTk");
	}
	public static double SELFT_beta(){ //achievementdrive
		return RunEnvironment.getInstance().getParameters().getDouble("selfEbeta"); //NB: changed to selFEBETA!!!
	}
	public static double SELFT_actionWeight(){
		return RunEnvironment.getInstance().getParameters().getDouble("weightVegEatingAction");
	}
	public static double SELFE_k(){
		return RunEnvironment.getInstance().getParameters().getDouble("selfEk");
	}
	public static double SELFE_beta(){ //achievementdrive
		return RunEnvironment.getInstance().getParameters().getDouble("selfEbeta");
	}
	public static double SELFE_actionWeight(){
		return RunEnvironment.getInstance().getParameters().getDouble("weightMeatEatingAction");
	}
	
	//Evaluation parameters
	//Could make it degrading over time as they get older.
	public static double LEARN_RATE(double learnWeight) {
		return Helper.bound(0,1, learnWeight *RunEnvironment.getInstance().getParameters().getDouble("LEARN_RATE"));
	}
	
	//SocialEvaluation
	public static double a;
	public static double b; 
	
	public static void setA() {
		//a = 0.5 * (agentCount -1) * (1/ (double) venueCount);
		a= 0;
	}
	
	public static void setB() {
		//b = Math.sqrt(0.5 *a);
		b = Math.sqrt(0.5 * 0.5 * (agentCount() -1) * (1 / (double) venueCount()) *2 );
	}
	
	public static int getAgentID() {
		agentID++;
		return agentID%agentCount();
	}
	public static int getLocationID() {
		return locationID++;
	}

	//How to reset
	private static int agentID = 0;
	private static int locationID = 0;

	public static int diningOutPercent() {
		return RunEnvironment.getInstance().getParameters().getInteger("diningOutPercent");
	}
	
	//Simulation parameters
	public static double endTime() {
		return RunEnvironment.getInstance().getParameters().getDouble("endTime");
	}
	public static double INTERVENTION_TIME() {
		return RunEnvironment.getInstance().getParameters().getDouble("INTERVENTION_TIME");
	}
	public static double getTime(){
		return RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	
	//Chooce context para's
	public static int getHomesCount() {
		//http://www.abs.gov.au/ausstats/abs@.nsf/Latestproducts/3236.0Main%20Features42011%20to%202036?opendocument&tabname=Summary&prodno=3236.0&issue=2011%20to%202036&num=&view=
		return (int) Math.round(agentCount()/2.5);
	}
	
	public static ArrayList<Double> ratios;
	
	//Gives errors for low amount of agents, prob because you round the low percentages away.
 	public static void createDiningOutDistribution(){
		ratios=new ArrayList<Double>();
		int times = (int) Math.round(agentCount()/2.0);
		add(ratios,times,
				((double) 1- 
						((29*
								(Math.pow(0.5, CFG.INCREASE_DINING_OUT())))/30))); 
		times  = (int) Math.round(agentCount()/3.0);
		add(ratios,times,((double) 1- ((6*(Math.pow(0.5, CFG.INCREASE_DINING_OUT())))/7)));
		times  = (int)Math.round( agentCount()/10.0);
		add(ratios,times,((double) 1- ((4.5*(Math.pow(0.5, CFG.INCREASE_DINING_OUT())))/7)));
		times  = (int)Math.round(agentCount()/25.0);
		add(ratios,times,((double) 1- ((4*(Math.pow(0.5, CFG.INCREASE_DINING_OUT())))/7)));
		times  = (int)Math.round(agentCount()/33.0);
		add(ratios,times,((double) 1- ((3*(Math.pow(0.5, CFG.INCREASE_DINING_OUT())))/7)));
		times  = (int)Math.round(agentCount()/50.0);
		add(ratios,times,((double) 1- ((2*(Math.pow(0.5, CFG.INCREASE_DINING_OUT())))/7)));
		times  = (int)Math.round(agentCount()/80.0);
		add(ratios,times,((double) 1- ((29*(Math.pow(0.5, CFG.INCREASE_DINING_OUT())))/30)));
	}
 	
 	private static double INCREASE_DINING_OUT() {
		return RunEnvironment.getInstance().getParameters().getDouble("INCREASE_DINING_OUT");
	}

	//	public static void createDiningOutDistribution(){
//		ratios=new ArrayList<Double>();
//		int times = (int) Math.round(agentCount()/2.0);
//		add(ratios,times,((double) 1/30)); 
//		times  = (int) Math.round(agentCount()/3.0);
//		add(ratios,times,((double) 1/7));
//		times  = (int)Math.round( agentCount()/10.0);
//		add(ratios,times,((double) 2.5/7));
//		times  = (int)Math.round(agentCount()/25.0);
//		add(ratios,times,((double) 3/7));
//		times  = (int)Math.round(agentCount()/33.0);
//		add(ratios,times,((double) 4/7));
//		times  = (int)Math.round(agentCount()/50.0);
//		add(ratios,times,((double) 5/7));
//		times  = (int)Math.round(agentCount()/80.0);
//		add(ratios,times,((double) 29/30));
//	}
	public static double getDiningOutRatio() {
		if(CFG.allDineIn()) return 0.0;
		if(ratios.isEmpty()) return 1.0/30.0; //If due to rounding mistakes there are not eonugh values.
		int randomIndex = RandomHelper.nextIntFromTo(0, ratios.size()-1);
		//System.out.println("Ratio:" + ratios.get(randomIndex));
		
		return ratios.remove(randomIndex);
	}
	
	private static boolean allDineIn() {
		return RunEnvironment.getInstance().getParameters().getBoolean("ALL_DINE_IN");
	}

	public static void add(ArrayList<Double> l, int times, double ratio){
		for(int i =0; i < times; i++){
			l.add(ratio);
		}
	}
	
	//TODO: look in paper
	public static int inviteDistribution() {
		int x=RandomHelper.getPoisson().nextInt();
		if(x==0){
			x=RandomHelper.getPoisson().nextInt();
		}
		
		
		//System.out.println(x);
		return x;
	}

	public static double habitSD() {
		return RunEnvironment.getInstance().getParameters().getDouble("HABIT_SD");
	}

	public static int getExtraMixed() {
		return ExtraMixed;
	}

	public static void setExtraMixed(int extraMixed) {
		ExtraMixed = extraMixed;
	}

	public static int getExtraMeat() {
		return ExtraMeat;
	}

	public static void setExtraMeat(int extraMeat) {
		ExtraMeat = extraMeat;
	}

	public static int getExtraVeg() {
		return ExtraVeg;
	}

	public static void setExtraVeg(int extraVeg) {
		ExtraVeg = extraVeg;
	}

	public static double OPEN_VEG_RESTAURANTS() {
		return RunEnvironment.getInstance().getParameters().getDouble("OPEN_VEG_RESTAURANTS");
	}
	public static boolean CHANGE_AFFORDANCES() {
		return RunEnvironment.getInstance().getParameters().getBoolean("CHANGE_AFFORDANCES");
	}


	public static int meetUpPlacesCount() {
		return 2;
	}

	

	public static boolean useValues() {
		return true;
	}

	public static double HABIT_LEARN_RATE(double learnWeight) {
		return RunEnvironment.getInstance().getParameters().getDouble("HABIT_LEARN_RATE");
	}

	public static double habitlearnSD() {
		return RunEnvironment.getInstance().getParameters().getDouble("HABIT_LEARN_RATE_SD");
	}

	public static boolean individualEvualuation() {
		return true;
	}

	public static boolean useComplexValues() {
		return true;
	}

	public static double INT_VEGDAY() {
		return (getTime() > INTERVENTION_TIME()) ? RunEnvironment.getInstance().getParameters().getDouble("INT_VEGDAY"):0;
	}
	
	public static double MeetUpIterator(){
		return RunEnvironment.getInstance().getParameters().getDouble("MEET_UP_ITERATOR");
	}
	public static boolean isMeetUp() {
		boolean meetUp = false;
		if(getTime()> INTERVENTION_TIME()){
			double meetUpCount = (MeetUpIterator()==0.0)? RunEnvironment.getInstance().getParameters().getDouble("MEET_UP_COUNT"):
								(MeetUpIterator()<4.0) ? 1:2;
			for(double i =1.0; i <= meetUpCount; i++){
				if(getTime()%7 ==i -1.0) meetUp |= true;
			}
		}
		return  meetUp;
	}
	public static boolean MEETUP_VEG() {
		return (MeetUpIterator()==0.0)? RunEnvironment.getInstance().getParameters().getBoolean("MEETUP_VEG"):
			(MeetUpIterator()==3.0 |MeetUpIterator()==6.0);
	}
	public static boolean MEETUP_INVITE() {
		return (MeetUpIterator()==0.0)? RunEnvironment.getInstance().getParameters().getBoolean("MEETUP_INVITE"):
			(MeetUpIterator()==2.0 | MeetUpIterator()==3.0 |MeetUpIterator()==5.0 |MeetUpIterator()==6.0);
	}
//	private static List<Integer> meetUpDays;
//	}

	public static double getEvaluationCorrelation() {
		return -0.5;
	}

	public static double intentionModifier(Value value) {
		
		if(value instanceof SelfEnhancement) return RunEnvironment.getInstance().getParameters().getDouble("MEAT_INTENTION_MOD");
		else return RunEnvironment.getInstance().getParameters().getDouble("VEG_INTENTION_MOD");
	
	}

	
}
