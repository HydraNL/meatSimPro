/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package main;

import java.util.ArrayList;

import framework.Agent;
import framework.Location;
import framework.PContext;
import framework.PhysicalContext;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.PriorityType;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

// Start of user code (user defined imports)

// End of user code

/**
 * Description of AbstractBuilder.
 * 
 * @author rijk
 */
public abstract class AbstractBuilder implements ContextBuilder<Object> {
	public Context<Object> context;
	public Grid<Object> grid;
	public GridFactory myGridFactory;
	public Parameters params;	
	public ArrayList<Agent> agents;
	public ArrayList<Location> locations;
	public ArrayList<PContext>	pContexts; 
	public DataCollector myDataCollector;
	public ArrayList<Location> homes;
	public ArrayList<Location> meetUpPlaces;
	/**
	 * Description of the method build.
	 */
	@Override
	public Context build(Context<Object> context) {
		this.context = context;
		params = RunEnvironment.getInstance().getParameters(); /*retrieves GUI-made parameters*/
		agents=new ArrayList<Agent>();
		locations=new ArrayList<Location>();
		pContexts=new ArrayList<PContext>();
		homes = new ArrayList<Location>(); //Could add it MeatSimBuilder, but scared of not properly cleaning up list
		meetUpPlaces= new ArrayList<Location>();
		
		RandomHelper.createNormal(1, 0.25);
		RandomHelper.createPoisson(2);
		CFG.createDiningOutDistribution();
		
		setCFG();
		setID();
		makeGrid();
		
		runBuilder();
		addEnvironment();
		addAgents();
		
		
		//after making agents
		myDataCollector=new DataCollector(this);
		context.add(myDataCollector);
		
		//schedule a check for change context each round
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters params1 = ScheduleParameters.createOneTime(CFG.INTERVENTION_TIME(), 102); //Intervention is first
		schedule.schedule(params1, this, "contextIntervention");
		
		ScheduleParameters params = ScheduleParameters.createRepeating(1, 1, 100); //100 = prob first priority, except for intervention
		schedule.schedule(params, this, "changeContext");
		schedule.schedule(params, this, "cleanUpContext"); //set Agents context to null
		
		//schedule a clean up after each round
		//TODO: do cleanup schedule to some schedule?
		ScheduleParameters params2 = ScheduleParameters.createRepeating(1, 1, ScheduleParameters.LAST_PRIORITY);
		schedule.schedule(params2, this, "cleanUp"); //erase agents from restaurants
		
		/*Schedules a performance context task each timestep.*/
		/* Is not needed because I make the Context now per agent*/
//		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
//		ScheduleParameters params = ScheduleParameters.createRepeating(1, 1, ScheduleParameters.FIRST_PRIORITY);
//		schedule.schedule(params, this, "createPContexts");
		
		/*Specifies simulation endTime*/
		RunEnvironment.getInstance().endAt(CFG.endTime());
		
		return context;
	}
	 
	public abstract void setCFG();
	/**
	 * Description of the method setID.
	 */
	public abstract void setID();
	 
	/**
	 * Description of the method makeGrid.
	 */
	public abstract void contextIntervention();
	
	public void makeGrid() {
		myGridFactory = GridFactoryFinder.createGridFactory(null);
		
		grid = myGridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
				new RandomGridAdder<Object>(),
				true, 50, 50));
	}	
	
	public abstract void runBuilder();
	/**
	 * Description of the method addAgents.
	 */
	public abstract void addAgents();
	 
	/**
	 * Description of the method addEnvironment.
	 */
	public abstract void addEnvironment();
	
	/**
	 * If you want to change the context (i.e. locations) during runtime.
	 */
	public abstract void changeContext();

	/**
	 * Removes all previous performanceContexts
	 * Creates one performance context per location per timestep.
	 * Adds it to the list of pContexts and to the Repast context and (thus) grid.
	 * Moves the pContext to the point in the Grid of the location.
	 */
	
	public void cleanUp(){
		cleanUpPContext();
		setAgentsToUnLocated();
	}
	
	//remove physicalcontext as well?
	public void cleanUpPContext(){
		for(Location l:locations){
			l.removePContext();
		}
		for(Location l:homes){
			l.removePContext();
		}
		for(Location l:meetUpPlaces){
			l.removePContext();
		}
	}
	
	public void setAgentsToUnLocated(){
		for(Agent a:agents){
			a.setLocated(false);
		}
	}
	//Do this first so it doesnt mess with possible data.
	//But at the same point agents dont have a context before they do something.
	public void cleanUpContext(){
		for(Agent a:agents){
			a.setContext(null);
		}
	}
	//@ScheduledMethod(start = 1, interval = 1) /dont think you run this anymore
	public void createPContexts() {
		pContexts.clear(); //List doesn't become infinite, there are new pContexts every time step.
		
		for(int i =0; i < locations.size(); i++){
			Location location = locations.get(i);
			PContext newPContext=new PContext(location);
			pContexts.add(newPContext);
			context.add(newPContext);
			Helper.moveToObject(grid, newPContext, location);
		}
	}
}
