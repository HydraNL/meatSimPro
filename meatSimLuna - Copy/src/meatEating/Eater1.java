/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.CFG;
import main.Helper;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import framework.Agent;
import framework.Location;
import framework.PContext;
import framework.SocialPractice;

// End of user code

/**
 * Description of Eater1.
 * 
 * @author rijk
 */
public class Eater1 extends Agent {
	double meatEatCount;
	double lowestPlateau;
	
	/**
	 * The constructor.
	 */
	public Eater1(ArrayList<Agent> agents, ArrayList<Location> candidateLocations, ArrayList<Location> homes, Grid<Object> grid, Location meetUpPlace) {
		super(agents, candidateLocations, homes, grid, meetUpPlace);
		addSocialPractice(new MeatEatingPractice());
		addSocialPractice(new VegEatingPractice());
		double[] sampleValues = correlated();
		addValue(new SelfEnhancement(sampleValues[0], this)); //ND, 1, 0.25 (maybe higher sigma?)
		addValue(new SelfTranscendence(sampleValues[1], this));
		double[] sampleValues2 = correlated();
		addValue(new Openness(sampleValues2[0])); //ND defined in abstractbuilder as 1,0.25
		addValue(new Conservation(sampleValues2[1]));
		
		
		
		
		 
		RandomHelper.createNormal(1, 0.25);
		setHabitWeight(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()));
		setOCweight(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()));
		setIweight(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()));
		setSweight(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()));
		if(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()) >1){
			setAcceptRate(0.8);
		}else{
			setAcceptRate(0.20);
		}
		if(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()) >1){
			setChooseOnPhysicalRate(0.8);
		}else{
			setChooseOnPhysicalRate(0.2);
		}
			setLearnWeight(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()));
	
		//Reset it to the following
			
			
			if(CFG.useValues()){
				setHabitWeight(Helper.normalize(myValues.get(Conservation.class).getStrengthWeight() -myValues.get(Openness.class).getStrengthWeight(), 0, 0.47, 1, CFG.habitSD()));
				setOCweight(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()));
				setIweight(Helper.normalize(myValues.get(SelfEnhancement.class).getStrengthWeight(),1,0.25));
				setSweight(Helper.normalize((myValues.get(Conservation.class).getStrengthWeight()+myValues.get(SelfTranscendence.class).getStrengthWeight())/2,1,0.125));
				setAcceptRate(Helper.normalize(myValues.get(SelfEnhancement.class).getStrengthWeight()+ myValues.get(Conservation.class).getStrengthWeight() 
						-(myValues.get(Openness.class).getStrengthWeight()+ myValues.get(SelfTranscendence.class).getStrengthWeight()),0,0.47));
				setChooseOnPhysicalRate(Helper.normalize(myValues.get(SelfEnhancement.class).getStrengthWeight() -myValues.get(SelfTranscendence.class).getStrengthWeight(), 0, 0.47));
				setLearnWeight(Helper.normalize(myValues.get(Openness.class).getStrengthWeight() -myValues.get(Conservation.class).getStrengthWeight(), 0, 0.47,1,CFG.habitlearnSD()));
				
//		double conservativeSchale = sampleValues2[1] -sampleValues2[0];
//		double conservativeSchaleN //= 0.5 + (conservativeSchale+2)/4; //Normalized to ND 1 0.25
//								= Helper.normalize(conservativeSchale, 0, 0.47, 1, CFG.habitSD());
//		
//		double opennessSchaleN = Helper.normalize(sampleValues2[0] -sampleValues2[1], 0, 0.47) ;
//		double enhanceConserveN = Helper.normalize(sampleValues[1]+ sampleValues2[0] -(sampleValues[0]+ sampleValues2[1]),0,0.47);
//		
//		double enhanceSchaleN = Helper.normalize(sampleValues[0] -sampleValues[1], 0, 0.47);
//		if(CFG.useValues()){
//	setHabitWeight(conservativeSchaleN);
//	setOCweight(Helper.bound(0,2,RandomHelper.getNormal().nextDouble()));
//	setIweight(sampleValues[0]);
//	setSweight(Helper.normalize((sampleValues2[1]+sampleValues[1])/2,1,0.125));
//	setAcceptRate(enhanceConserveN);
//	setChooseOnPhysicalRate(enhanceSchaleN);
//	setLearnWeight(opennessSchaleN);
		}
		
	
		
		
		
		
		RandomHelper.createNormal(1, 0.25);
		
		meatEatCount = 0;
		//Or iniate everything with RandomHelper.getNormal().nextDouble()
	}
	
	
	public double[] correlated(){
		double[] means = {1,1};
		double variance = 0.0625;
		double correlation = -0.8; //Could be normally distributed
		//double correlation = Math.min(0, Math.max(-1, RandomHelper.getNormal().nextDouble() - 1.5)); //I'm not sure if the correlation, should differ, that seems strange.
		double[][] covariance_matrix = {{variance, variance * correlation},{variance*correlation, variance}};
		MultivariateNormalDistribution m= new MultivariateNormalDistribution(means,covariance_matrix);
		double[] l =m.sample();
		l[0] = Math.max(0, Math.min(2, l[0]));
		l[1] = Math.max(0, Math.min(2, l[1]));
		//System.out.println("correlation: " + correlation + "gives strengtWeight:" + l[0] + "and"+ l[1]);
		return l;
		/*
		double correlation = RandomHelper.getNormal().nextDouble() -1.5;
		double x2= RandomHelper.getNormal().nextDouble();
		double y1= correlation * x1 + Math.sqrt(1 - (correlation * correlation))* x2;
		
		System.out.println("x1: " + x1);
		System.out.println("x2: " + x2);
		System.out.println("correlation: "+correlation);
		System.out.println("y1: "+y1);
		
		return y1;
		*/
	}
	
	/*DataCollectors*/
	public double individualMeatRatio(){
		meatEatCount += dataMeatAction();
		return meatEatCount/RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
//	public double plateau(){
//		return (RunEnvironment.getInstance().getCurrentSchedule().getTickCount() >20 &&dataHabitStrengthMeat()> 0.95) ? 1:0;
//	}
//	
//	@ScheduledMethod(start = 1, interval = 1, priority = 0.5)
//	public void adaptPlateau(){
//		if(plateau() ==1.0 && lowestPlateau == 0) lowestPlateau = CFG.getTime();
//	}
	
	public double plateauTime(){
		if(habitStrengthsMeat.containsKey(250.0) &&
				habitStrengthsMeat.containsKey(499.0) &&
				habitStrengthsMeat.get(250.0) > 
				0.85 * habitStrengthsMeat.get(499.0)){
					double plateauValue = habitStrengthsMeat.get(499.0);
					double plateauTime=1.0;
					while(plateauTime <500.0){
						if(!habitStrengthsMeat.containsKey(plateauTime)) plateauTime++;
						else if(plateauValue*0.95 > habitStrengthsMeat.get(plateauTime)) plateauTime++;
						else break;
					}
							
					return (plateauTime == 1.0) ? 0.0: plateauTime;
		}else return 0.0;
	}
	
	public double plateauValue(){
		//Als de agent in een meatHabit raakt, kijk dan vanaf wanneer die plataeud.
		//Anders return 0.0
		if(habitStrengthsMeat.containsKey(250.0) &&
				habitStrengthsMeat.containsKey(499.0) &&
				habitStrengthsMeat.get(250.0) > 
				0.85 * habitStrengthsMeat.get(499.0)){
					double plateauValue = habitStrengthsMeat.get(499.0);
					double plateauTime=1.0;
					while(plateauTime <500.0){
						if(!habitStrengthsMeat.containsKey(plateauTime)) plateauTime++;
						else if(plateauValue*0.95 > habitStrengthsMeat.get(plateauTime)) plateauTime++;
						else break;
					}
							
					return (plateauTime == 1.0) ? 0.0: habitStrengthsMeat.get(plateauTime);
		}else return 0.0;
	}
	
	public double dataHabitStrengthMeat(){
		return dataHabitStrength(MeatEatingPractice.class);
	}
	public double dataHabitStrengthVeg(){
		return dataHabitStrength(VegEatingPractice.class);
	}
	public double dataHabitStrengthWeightedMeat(){
		return dataHabitStrengthWeighted(MeatEatingPractice.class);
	}
	public double dataHabitStrengthWeightedVeg(){
		return dataHabitStrengthWeighted(VegEatingPractice.class);
	}
	public double dataFrequencyIndexMeat(){
		return dataFrequencyIndex(MeatEatingPractice.class);
	}
	public double dataFrequencyIndexVeg(){
		return dataFrequencyIndex(VegEatingPractice.class);
	}
	public double dataMeatEvaluation(){
		return dataMeatAction() * dataEvaluation();
	}
	public double dataVegEvaluation(){
		return dataVegAction() *dataEvaluation();
	}
	public double dataSatisfactionSelfEnhancement(){
		return dataSatisfaction(SelfEnhancement.class);
	}
	public double dataSatisfactionSelfTranscendence(){
		return dataSatisfaction(SelfTranscendence.class);
	}
	public double dataThresholdSelfEnhancement(){
		return dataThreshold(SelfEnhancement.class);
	}
	public double dataThresholdSelfTranscendence(){
		return dataThreshold(SelfTranscendence.class);
	}
	public double dataNeedSelfEnhancement(){
		return dataNeed(SelfEnhancement.class);
	}
	public double dataNeedSelfTranscendence(){
		return dataNeed(SelfTranscendence.class);
	}
}
