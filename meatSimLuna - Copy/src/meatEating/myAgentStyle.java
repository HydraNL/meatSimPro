package meatEating;

import java.awt.Color;

import framework.Agent;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class myAgentStyle extends DefaultStyleOGL2D {

		

		@Override
		public Color getColor(Object o) {
			Agent agent = (Agent)o;
			if (agent.getMyAction() instanceof MeatEatingPractice)	
				return Color.RED;
			else 
				return Color.GREEN;
		}
}
