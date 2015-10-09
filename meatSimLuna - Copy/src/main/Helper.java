/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import framework.Agent;
import framework.Location;
import framework.PContext;
import framework.SocialPractice;

// Start of user code (user defined imports)

// End of user code

/**
 * the Builder creates one context object per venue per round<br />
 * make sure the Builder does this before an agent moves
 * 
 * @author rijk
 */
public class Helper {
	// Start of user code (user defined attributes for Helper)
	
	// End of user code
	
	/**
	 * The constructor.
	 */
	public Helper() {
		// Start of user code constructor for Helper)
		super();
		// End of user code
	}
	/**
	 * Move one object to the same location as another on the grid.
	 * 
	 * @param grid Grid objects move on
	 * @param movingObject object that moves
	 * @param targetObject object where movingObject moves to
	 */
	public static void moveToObject(Grid<Object> grid, Object movingObject,
			Object targetObject) {
		GridPoint pt = grid.getLocation(targetObject);
		
		Helper.moveTo(grid, movingObject, pt.getX(), pt.getY());
	}
	
	public static void moveTo(Grid<Object> grid, Object movingObject, int x, int y){
		Iterator<Object> iter = grid.getObjectsAt(x,y).iterator();
		boolean hasAgent = false;
		int dx = 0;
		int dy = 0;
		while(iter.hasNext()){
			Object obje = iter.next();
			if(obje instanceof Agent) hasAgent = true;
		}
		if(hasAgent){
			moveTo(grid, movingObject, x+1,y); //Maybe getNeighbor or something?
		}else{
			grid.moveTo(movingObject, x,y);
		}
	}
	/**
	 * Move one object to the same location as another on the grid.
	 * 
	 * @param grid Grid objects move on
	 * @param movingObject object that moves
	 * @param targetObject object where movingObject moves to
	 */
	public void moveNextToObject(Grid<Object> grid, Object movingObject,
			Object targetObject) {
		//TODO: make
		//GridPoint pt = grid.getLocation(targetObject);
		//grid.moveTo(movingObject, pt.getX(), pt.getY());
	}
	
	
	public static void filter(
			ArrayList<SocialPractice> candidateSocialPractices,
			HashMap<SocialPractice, Double> habitStrengths, double HABIT_THRESHOLD) {
		Iterator<SocialPractice> iter = candidateSocialPractices.iterator();
		
		while(iter.hasNext()){
			SocialPractice sp = iter.next();
			if(habitStrengths.get(sp) < HABIT_THRESHOLD) iter.remove(); 
		}
		
	}
	public static int sum(Collection<Integer> values) {
		int sum = 0;
		for(Integer i:values){
			sum = sum + i;
		}
		return sum;
	}
	
	public static double sumDouble(Collection<Double> values) {
		double sum = 0;
		for(Double i:values){
			sum = sum + i;
		}
		return sum;
	}
	
	public static double avarageDouble(Collection<Double> values) {
		return sumDouble(values)/values.size();
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValue( Map<K, V> map )
{
      Map<K,V> result = new LinkedHashMap<>();
     Stream <Entry<K,V>> st = map.entrySet().stream();

     st.sorted(Comparator.comparing(e -> e.getValue()))
          .forEach(e ->result.put(e.getKey(),e.getValue()));
//Doesn't work yet
     return result;
}
	
	
	//Wrapper
	public static void mapAdd(HashMap<Object, Double> map,
			Object key){
		mapAdd(map, key, 1);
	}
	
	/*
	 * Adds 'add'-points to HashMap entry;
	 */
	public static void mapAdd(HashMap<Object, Double> map,
			Object key, double add) {
		Double currentValue = map.get(key);
		if(currentValue == null){		//no Entry yet
			map.put(key, add);
		}
		else{
			currentValue+=add;
			map.put(key, currentValue);
		}
	}
	
	public static void mapLearn(boolean habitMap, HashMap<Object, Double> map,
			Object key, double newValue, double learnWeight) {
		Double nextValue;
		Double currentValue = map.get(key);
		if(currentValue == null){		//no Entry yet
			if(habitMap) currentValue = 0.5;
			else currentValue = 1.0;
		}
		nextValue =  (1.0-CFG.LEARN_RATE(learnWeight)) * currentValue + CFG.LEARN_RATE(learnWeight) * newValue;
		map.put(key, nextValue);
	}
	
	public static double normalize(double value, double muOrigin, double sdOrigin){
		//System.out.println("from" + value + "to:" +normalize(value,muOrigin,sdOrigin, 1, 0.25));
		return Math.max(0, Math.min(2, normalize(value,muOrigin,sdOrigin, 1, 0.25)));
	}
	public static double normalize(double value, double muOrigin, double sdOrigin, double muTarget, double sdTarget){
		double normalized = (value - muOrigin)/sdOrigin;
		return normalized * sdTarget + muTarget;
	}
	
	public static double bound(double min, double max, double value){
		return Math.max(min, Math.min(max, value));
	}
	
	
	// Start of user code (user defined methods for Helper)
	
	// End of user code


}
