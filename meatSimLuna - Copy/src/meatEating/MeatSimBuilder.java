/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package meatEating;

import java.util.ArrayList;
import java.util.List;

import framework.Agent;
import framework.Location;
import framework.PContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.random.RandomHelper;
import main.AbstractBuilder;
import main.CFG;

// Start of user code (user defined imports)

// End of user code

/**
 * Description of MeatSimBuilder.
 * 
 * @author rijk
 */
public class MeatSimBuilder extends AbstractBuilder {
	List<MeatVenue> meatVenues;
	List<VegVenue> vegVenues;
	List<MixedVenue> mixedVenues;
	
	
	//Method that gets run as soon as Builder is made
	//But here we make context specific additions
	@Override
	public void runBuilder(){
		meatVenues = new ArrayList<MeatVenue>();
		vegVenues = new ArrayList<VegVenue>();
		mixedVenues = new ArrayList<MixedVenue>();
	}

	
	@Override
	public void setID() {
		context.setId("meatSim");
	}

	@Override
	public void addAgents() {
		int eater1Count = (Integer) params.getValue("eater1Count");
		for (int i=0; i < eater1Count; i++){
			int randomIndex = RandomHelper.nextIntFromTo(0,
					meetUpPlaces.size() - 1);
			Location meetUpPlace = meetUpPlaces.get(randomIndex);
			Eater1 newAgent=new Eater1(agents, locations, homes, grid, meetUpPlace);
			context.add(newAgent);
			agents.add(newAgent);
		}
	}
	
	@Override
	public void setCFG(){
		
		/*Values to calculate Social Norm*/
		CFG.setA();
		CFG.setB();
		CFG.setExtraVeg(0);
		
	}
	
	@Override
	public void addEnvironment(){
		int mixedVenueCount = (Integer) params.getValue("mixedVenueCount");
		for (int i=0; i < mixedVenueCount; i++){
			MixedVenue newMixedVenue=new MixedVenue(grid);
			addVenue(newMixedVenue, mixedVenues);
		}
		
		int meatVenueCount = (Integer) params.getValue("meatVenueCount");
		for (int i=0; i < meatVenueCount; i++){
			MeatVenue newMeatVenue=new MeatVenue(grid);
			addVenue(newMeatVenue, meatVenues);
		}
		
		int vegetarianVenueCount = (Integer) params.getValue("vegetarianVenueCount");
		for (int i=0; i < vegetarianVenueCount; i++){
			VegVenue newVegVenue=new VegVenue(grid);
			addVenue(newVegVenue, vegVenues);
		}
		
		//Always add, so we can change to chooseCOntext in between.
			int homesCount = CFG.getHomesCount();
			for (int i=0; i < homesCount; i++){
				Home newHome=new Home(grid);
				context.add(newHome);
				homes.add(newHome);
			}
			int meetUpPlacesCount = CFG.meetUpPlacesCount();
			for (int i=0; i < meetUpPlacesCount; i++){
				MeetUpLocation newMeetUP=new MeetUpLocation(grid); //TODO: make something else than home
				context.add(newMeetUP);
				meetUpPlaces.add(newMeetUP);
			}
			

	}
	
	public <T extends Location> void addVenue(T venue, List<T> venueList){
		context.add(venue);
		locations.add(venue);
		venueList.add(venue);
	}
	
	//Note that locations this hold all venues, while specific list only hold venues that are open
	public <T extends Location> void removeVenue(List<T> venueList){
		if(venueList.size() > 0){
			int randomIndex = RandomHelper.nextIntFromTo(0, venueList.size()-1);
			venueList.get(randomIndex).setOpen(false);
			venueList.remove(randomIndex);
		}
	}
	
	@Override 
	public void contextIntervention(){
		if(CFG.getTime() >= CFG.INTERVENTION_TIME()){
			CFG.setExtraVeg(CFG.getExtraVeg() + (int)CFG.OPEN_VEG_RESTAURANTS());//meatToVeg();

			if(CFG.CHANGE_AFFORDANCES()){
				for(Agent a:agents){
					a.getVegPractice().addAffordance(new PContext(new MeatVenue()));
				}
			}
		}
	}
	
	
	public void meatToVeg(){
		if(CFG.meatVenueCount() >0) CFG.setExtraMeat(CFG.getExtraMeat()-1);
		else if (CFG.mixedVenueCount() >0) CFG.setExtraMixed(CFG.getExtraMixed()-1);
		CFG.setExtraVeg(CFG.getExtraVeg() + 1);
	}
	@Override
	public void changeContext(){
		changeVenues();
	}
	
	public void changeVenues(){
		if(mixedVenues.size() > CFG.mixedVenueCount()){
			int difference = mixedVenues.size() - CFG.mixedVenueCount();
			for(int i =0; i < difference; i++){
				removeVenue(mixedVenues);
			}
		}
		if(mixedVenues.size() < CFG.mixedVenueCount()){
			int difference = CFG.mixedVenueCount() - mixedVenues.size();
			for(int i =0; i < difference; i++){
				addVenue(new MixedVenue(grid), mixedVenues);
			}
		}
		if(vegVenues.size() > CFG.vegetarianVenueCount()){
			int difference = vegVenues.size() - CFG.vegetarianVenueCount();
			for(int i =0; i < difference; i++){
				removeVenue(vegVenues);
			}
		}
		if(vegVenues.size() < CFG.vegetarianVenueCount()){
			int difference = CFG.vegetarianVenueCount() - vegVenues.size();
			for(int i =0; i < difference; i++){
				addVenue(new VegVenue(grid), vegVenues);
			}
		}
		if(meatVenues.size() > CFG.meatVenueCount()){
			int difference = meatVenues.size() - CFG.meatVenueCount();
			for(int i =0; i < difference; i++){
				removeVenue(meatVenues);
			}
		}
		if(meatVenues.size() < CFG.meatVenueCount()){
			int difference = CFG.meatVenueCount() - meatVenues.size();
			for(int i =0; i < difference; i++){
				addVenue(new MeatVenue(grid), meatVenues);
			}
		}
	}
}
