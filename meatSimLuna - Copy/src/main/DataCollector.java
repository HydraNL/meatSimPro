package main;


import repast.simphony.random.RandomHelper;
import meatEating.MeatEatingPractice;
import meatEating.VegEatingPractice;
import framework.Agent;

//Needs to be added to the context to be able to collect data from.
public class DataCollector {
	AbstractBuilder main;
	Agent oneAgent;
	
	public DataCollector(AbstractBuilder abstractBuilder) {
		main = abstractBuilder;
		oneAgent = main.agents.get(RandomHelper.nextIntFromTo(0, main.agents.size()-1));
	}
	
	public int countVegAction(){
		int c=0;
		for(Agent a:main.agents){
			if(a.getMyAction() instanceof VegEatingPractice) c++;
		}
		return c;
	}
	
	public int countMeatAction(){
		int c=0;
		for(Agent a:main.agents){
			if(a.getMyAction() instanceof MeatEatingPractice) c++;
		}
		return c;
	}
	
	public int eatingTypeOneAgent(){
		return (int) oneAgent.dataEatingType();
	}
	
	public int deliberativeTypeOneAgent(){
		return oneAgent.dataOneAgent();
	}
	
	public double evaluationMeatAggr(){
		double x =0;
		for(Agent a:main.agents){
			x+=a.dataEvaluation() *a.dataMeatAction();
		}
		return x/countMeatAction();
	}
	
	public double evaluationVegAggr(){
		double x =0;
		for(Agent a:main.agents){
			x+=a.dataEvaluation() *a.dataVegAction();
		}
		return x/countVegAction();
	}
}
