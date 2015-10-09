package meatEating;

import java.awt.Color;

import framework.Agent;
import framework.Location;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class myLocationStyle extends DefaultStyleOGL2D {
		
		@Override
		public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		    return shapeFactory.createRectangle(50, 50);
		  }
		
		@Override
		public Color getColor(Object o) {
			Location l = (Location)o;
			if(!l.isOpen())
				return Color.BLACK;
			else{
				if (l instanceof MeatVenue)	
					return Color.ORANGE;
				else if(l instanceof MixedVenue)
					return Color.BLUE;
				else 
					return Color.MAGENTA;
			}
		}
}
