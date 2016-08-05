package lejos.robotics.geometry;


/**
 * An abstract base class for shapes based on a rectangular frame.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class RectangularShape implements Shape, Cloneable {
	
	/**
	 * Get the x coordinate as a double
	 * 
	 * @return the x coordinate
	 */
    public abstract double getX();
    
    /**
     * Get the y coordinate as a double
     * 
     * @return the y coordinate
     */
    public abstract double getY();
    
    /**
     * Get the width as a double
     * 
     * @return the width
     */
    public abstract double getWidth();
    
    /**
     * Get the height as a double
     * 
     * @return the height
     */
    public abstract double getHeight();
    
    /**
     * Get the minimum value of the x x coordinate
     * 
     * @return the minimum x coordinate
     */
    public double getMinX() {
        return getX();
    }
    
    /**
     * Get the minimum value of the y coordinate
     * 
     * @return the minimum y coordinate
     */
    public double getMinY() {
        return getY();
    }
    
    /**
     * Get the maximum value of the x coordinate
     * 
     * @return the maximum y coordinate
     */
    public double getMaxX() {
        return getX() + getWidth();
    }
    
    /**
     * Get the maximum value of the y coordinate
     * 
     * @return the maximim y coordinate
     */
    public double getMaxY() {
        return getY() + getHeight();
    }
    
    /**
     * Get the x coordinate of the center of the shape
     * 
     * @return the x coordinate of the center
     */
    public double getCenterX() {
        return getX() + getWidth() / 2.0;
    }
    
    /**
     * Get the y coordinate of the center of the shape
     * 
     * @return the y coordinate of the center
     */
    public double getCenterY() {
        return getY() + getHeight() / 2.0;
    }
    
    /**
     * Get the framing rectangle
     * 
     * @return the framing rectangle
     */
    public Rectangle2D getFrame() {
        return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }
    
    /**
     * Test if the rectangular shape is empty
     * 
     * @return true iff the shape is empty
     */
    public abstract boolean isEmpty();
    
    /**
     * Set the frame for the rectangular shape
     * 
     * @param x the x coordinate of the top left corner
     * @param y the y coordinate iof the top left corner
     * @param w the width
     * @param h the height
     */
    public abstract void setFrame(double x, double y, double w, double h);
    
    /**
     * Set the frame of the rectangular shape
     * 
     * @param r the framing rectangle
     */
    public void setFrame(Rectangle2D r) {
    	setFrame(r.getX(),r.getY(),r.getWidth(),r.getHeight());
    }
    
    /**
     * Test if the shape contains a Point2D
     * @param p the Point2D
     * @return true iff this shape contains the Point2D
     */
    public boolean contains(Point2D p) {
    	return contains(p.getX(), p.getY()); 	
    }
    
    /**
     * Test if this shape intersects a given Rectangle2D
     * 
     * @param r the Rectangle2D
     * @return true iff this shape intersects the given Rectangle2D
     */
    public boolean intersects(Rectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
    
    /**
     * Test if this shape contains a given Rectangle2D
     * 
     * @param r the Rectangle2D
     * @return true iff this shape contains the given Rectangle2D
     */
    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
    
    /**
     * Get the bounds of this rectangular shape as a Rectangle
     * @return the bounds as a Rectangle
     */
    public RectangleInt32 getBounds() {
        double width = getWidth();
        double height = getHeight();
        if (width < 0 || height < 0) return new RectangleInt32(0, 0, 0, 0);
        double x = getX();
        double y = getY();
        double x1 = Math.floor(x);
        double y1 = Math.floor(y);
        double x2 = Math.ceil(x + width);
        double y2 = Math.ceil(y + height);
        return new RectangleInt32((int) x1, (int) y1,(int) (x2 - x1), (int) (y2 - y1));
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
