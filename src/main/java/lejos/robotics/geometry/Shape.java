package lejos.robotics.geometry;



/**
 * Shape interface without getPathIterator methods
 * 
 * @author Lawrie Griffiths
 *
 */
public interface Shape {
	
	/**
	 * Get the bounding Rectangle for the shape
	 * 
	 * @return the bounding Rectangle
	 */
	public RectangleInt32 getBounds();
	
	/**
	 * Get the bounding Rectangle2D for the shape
	 * 
	 * @return the bounding Rectangle2D
	 */
	public Rectangle2D getBounds2D();
	
	/**
	 * Test if the shape contains the point (x,y)
	 * 
	 * @param x the x co-ordinate of the point
	 * @param y the y co-ordinate of the point
	 * @return true iff the shape contains the point
	 */
	public boolean contains(double x, double y);
	
	/**
	 * Test if the shape contains the Point2D
	 * 
	 * @param p the Point2D
	 * @return true iff the shape contains the point
	 */
	public boolean contains(Point2D p);
	
	/**
	 * Test if the shape intersects the rectangle with top left at (x,y), width w and height h.
	 * 
	 * @param x the x-coordinate of the top left point of the rectangle
	 * @param y the y-coordinate of the top left point of the rectangle
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 * @return true iff the shape intersects the rectangle
	 */
	public boolean intersects(double x, double y, double w, double h);
	
	/**
	 * Test if the shape intersects the Rectangle2D r
	 * 
	 * @param r the Recangle2D
	 * @return true iff the shape intersects the Rectangle2D
	 */
	public boolean intersects(Rectangle2D r);
	
	/**
	 * Test if the shape contains the rectangle with top left at (x,y), width w and height h.
	 * 
	 * @param x the x-coordinate of the top left point of the rectangle
	 * @param y the y-coordinate of the top left point of the rectangle
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 * @return true iff the shape contains the rectangle
	 */
	public boolean contains(double x, double y, double w, double h);
	
	/**
	 * Test if the shape contains the Rectangle2D
	 * 
	 * @param r the Rectangle2D
	 * @return true iff the shape contains the Rectangle2D
	 */
	public boolean contains(Rectangle2D r);
}
