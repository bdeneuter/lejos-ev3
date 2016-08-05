package lejos.robotics.geometry;

/**
 * An abstract class for a point.
 * Subclasses implement float, double and integer coordinates.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class Point2D implements Cloneable {
	/**
	 * A point with float coordinates.
	 */
	public static class Float extends Point2D {
		/**
		 * The x coordinate of the point
		 */
		public float x;
		/**
		 * The y coordinate of the point
		 */
		public float y;
		
		/**
		 * Create a point at (0,0) with float coordinates
		 */
		public Float() {}
		
		/**
		 * Create a point at (x,y) with float coordinates
		 * 
		 * @param x the x coordinate
		 * @param y the y coordinate
		 */
		public Float(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public void setLocation(double x, double y) {
			this.x = (float) x;
			this.y = (float) y;			
		}
		
		/**
		 * Set the location of the point
		 * 
		 * @param x the new x coordinate
		 * @param y the new y coordinate
		 */
		public void setLocation(float x, float y) {
			this.x = x;
			this.y = y;			
		}

		/**
		 * Represent the Point2SD.Float as a String
		 */
		@Override
        public String toString() {
            return "Point2D.Float["+x+", "+y+"]";
        }
	}
	
	/**
	 * A point with double coordinates.
	 */
	public static class Double extends Point2D {
		/**
		 * The x coordinate of the point
		 */
		public double x;
		/**
		 * The y coordinate of the point
		 */
		public double y;
		
		/**
		 * Create a point at (0,0) with double coordinates
		 */
		public Double() {}
		
		/**
		 * Create a point at (x,y) with double coordinates
		 * 
		 * @param x the x coordinate
		 * @param y the y coordinate
		 */
		public Double(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public void setLocation(double x, double y) {
			this.x = x;
			this.y = y;			
		}
		
		/**
		 * Represent the Point2D.Double as a String
		 */
		@Override
        public String toString() {
            return "Point2D.Double["+x+", "+y+"]";
        }
	}
	
	/**
	 * This is abstract class that cannot be instantiated.
	 * Use one of its subclasses.
	 */
    protected Point2D() {
    }
    
	/**
	 * Get the x coordinate as a double
	 * 
	 * @return the x co-ordinate (as a double) 
	 */
	public abstract double getX();
	
	/**
	 * Get the y coordinate as a double
	 * 
	 * @return the y coordinate (as a double)
	 */
	public abstract double getY();
	
	/**
	 * Set the location of this Point2D using double coordinates
	 * 
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 */
	public abstract void setLocation(double x, double y);
	
	/**
	 * Set the location of this Point2D to the same as a specified Point2D
	 * 
	 * @param p the specified Point2D
	 */
    public void setLocation(Point2D p) {
        setLocation(p.getX(), p.getY());
    }
    
    /**
     * Get the square of the distance between two points
     * 
     * @param x1 the x coordinate of the first point
     * @param y1 the y coordinate of the first point
     * @param x2 the x coordinate of the second point
     * @param y2 the y coordinate of the second point
     * @return the square of the distance between the points as a double
     */
    public static double distanceSq(double x1, double y1, double x2, double y2) {
    	double tx = x1 - x2;
    	double ty = y1 - y2;
    	return (tx * tx + ty * ty);
    }
    
    /**
     * Get the the distance between two points
     * 
     * @param x1 the x coordinate of the first point
     * @param y1 the y coordinate of the first point
     * @param x2 the x coordinate of the second point
     * @param y2 the y coordinate of the second point
     * @return the distance between the points as a double
     */
    public static double distance(double x1, double y1, double x2, double y2) {
    	return Math.sqrt(distanceSq(x1,y1,x2,y2));
    }
    
    /**
     * Get the square of the distance between two points
     * 
     * @param px the first point
     * @param py the second point
     * @return the square of the distance between the points as a double
     */
    public double distanceSq(double px, double py) {
        double tx = px - getX();
        double ty = py - getY();
        return (tx * tx + ty * ty);
    }
    /**
     * Get the square of the distance of this point to a given point
     * 
     * @param pt the given point
     * @return the square of the distance to the given point as a double
     */
    public double distanceSq(Point2D pt) {
        return distanceSq(pt.getX(), pt.getY());
    }
    
    /**
     * Get the distance from this point to a given point as a double
     * 
     * @param px the x coordinate of the given point
     * @param py the y coordinate of the given point
     * @return the distance to the given point as a double
     */
    public double distance(double px, double py) {
    	return Math.sqrt(distanceSq(px,py));
    }
    
    /**
     * Get the distance from this point to a given point asa double
     * 
     * @param pt the given point
     * @return the distance to the given point as a double
     */
    public double distance(Point2D pt) {
    	return Math.sqrt(distanceSq(pt));
    }
    
    /**
     * Test if this point is equal to a given object
     */
    @Override
    public boolean equals(Object obj) {
	    if (obj instanceof Point2D) {
	        Point2D p2d = (Point2D) obj;
	        return (getX() == p2d.getX()) && (getY() == p2d.getY());
	    }
	    return super.equals(obj);
    }
    
    @Override
	public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new RuntimeException();
        }
    }
}
