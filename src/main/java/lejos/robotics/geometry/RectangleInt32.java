package lejos.robotics.geometry;



/**
 * A rectangle with integer coordinates.
 * 
 * @author Lawrie Griffiths
 *
 */
public class RectangleInt32 extends Rectangle2D {
    /**
     * The height of the rectangle
     */
    public int height;
    /**
     * The width of the rectangle
     */
    public int width;
    /**
     * The x coordinate of the top left of the rectangle
     */
    public int x;
    /**
     * The y coordinate of the top right of the rectangle
     */
    public int y;
    
    /**
     * Creates a rectangle with top left corner at (x,y) and with specified
     * width and height.
     * @param x the x coordinate of the top left corner
     * @param y the y coordinate of the top left corner
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public RectangleInt32(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Creates a rectangle with top left corner at (0,0) and with specified
     * width and height.
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public RectangleInt32(int width, int height) {
        this(0,0,width,height);
    }
    
    /**
     * Creates an empty rectangle at (0,0).
     */
    public RectangleInt32() {
        this(0,0);
    }
    
    /**
     * Create an empty rectangle at the given point
     * @param p trhe point
     */
    public RectangleInt32(Point p) {
        this((int)p.x, (int)p.y, 0, 0);
    }
    
    /**
     * Get the x coordinate as a double
     * @return the x coordinate
     */
    @Override
    public double getX() {
        return x;
    }
    
    /**
     * Get the y coordinate as a double
     * @return the y coordinate
     */
    @Override
    public double getY() {
        return y;
    }
    
    /**
     * Get the width as a double
     * @return the width
     */
    @Override
    public double getWidth() {
        return width;
    }
    
    /**
     * Get the height as a double
     * @return the height
     */
    @Override
    public double getHeight() {
        return height;
    }
    
    /**
     * Move the rectangle to (x,y)
     * @param x the new x coordinate
     * @param y the new y coordinate
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Set the location of this point to the location of a given point
     * 
     * @param p the given point
     */
    public void setLocation(Point p) {
        x = (int)p.x;
        y = (int)p.y;
    }
    
    /**
     * Test if the rectangle is empty
     * @return true iff the rectangle is empty
     */
    @Override
    public boolean isEmpty() {
        return (width <= 0 || height <= 0);
    }
    
    /**
     * Test if a point given by (x,y) coordinates is within the rectangle
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true iff the point is within the rectangle
     */
    public boolean contains(int x, int y) {
        return inside(x,y);
    }
    
    /**
     * Test if a point is within the rectangle
     * @param p the point
     * @return true iff the point is within the rectangle
     */
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }
    
    /**
     * Test if this rectangle contains a specified rectangle
     * @param r the specified rectangle
     * @return true iff the specified rectangle is contained within this rectangle
     */
    public boolean contains(RectangleInt32 r) {
        if (isEmpty() || r.isEmpty()) return false;
        return contains(r.x, r.y) && contains(r.x + r.width, r.y + r.height);
    }
    
    /** 
     * Test if this rectangle intersects a specified rectangle
     * @param r the given rectangle
     * @return true iff this rectangle intersects the given rectangle
     */
    public boolean intersects(RectangleInt32 r) {
        int tw = width, th = height, rw = r.width, rh = r.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) return false;
        int tx = x, ty = y, rx = r.x, ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        return ((rw < rx || rw > tx) && (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) && (th < ty || th > ry));
    }
    
    @Override
    public RectangleInt32 getBounds() {
        return new RectangleInt32(x, y, width, height);
    }
    
    public Rectangle2D getBounds2D() {
        return new Rectangle(x, y, width, height);
    }
    
    /**
     * Set the bounds of this rectangle
     * 
     * @param x the new x coordinate
     * @param y the new y coordinate
     * @param width the new width
     * @param height the new height
     */
    public void setBounds(int x, int y, int width, int height) {
        reshape(x, y, width, height);
    }
    /**
     * Set the bounds of this rectangle to the given rectangle
     * 
     * @param r the new rectangle
     */
    public void setBounds(RectangleInt32 r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    @Override
    public void setRect(double x, double y, double width, double height) {
        int newx, newy, neww, newh;

        if (x > 2.0 * Integer.MAX_VALUE) {
            // Cannot be sensibly represented with integers
            newx = Integer.MAX_VALUE;
            neww = -1;
        } else {
            newx = doubleToInt(x, false);
            if (width >= 0) width += x-newx;
            neww = doubleToInt(width, width >= 0);
        }
        if (y > 2.0 * Integer.MAX_VALUE) {
            // Cannot be sensibly represented with integers
            newy = Integer.MAX_VALUE;
            newh = -1;
        } else {
            newy = doubleToInt(y, false);
            if (height >= 0) height += y-newy;
            newh = doubleToInt(height, height >= 0);
        }
        reshape(newx, newy, neww, newh);        
    }
    
    /**
     * Use setBounds.
     */
    @Deprecated
    public void reshape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    private static int doubleToInt(double x, boolean high) {
        //Manual clipping not needed: Cast to int also returns MIN/MAX_VALUE for small/large values
        //Keep it for performance?
        if (x <= Integer.MIN_VALUE) return Integer.MIN_VALUE;  
        if (x >= Integer.MAX_VALUE) return Integer.MAX_VALUE;

        return (int) (high ? Math.ceil(x) : Math.floor(x));
    }
    
    /**
     * Test if the Rectangle is equal to a given object
     * 
     * @param obj the object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle r = (Rectangle)obj;
            return ((x == r.x) && (y == r.y) &&
                    (width == r.width) && (height == r.height));
        } else {
            return super.equals(obj);
        }
    }
    
    /**
     * Get the location of the rectangle
     * 
     * @return the (x,y) coordinate of the top left corner
     */
    public Point getLocation() {
        return new Point(x, y);
    }
    
    /**
     * Set the size of the rectangle
     * 
     * @param width the new width
     * @param height the new height
     */
    public void setSize(int width, int height) {
        resize(width, height);
    }
    

    /**
     * Use setSize
     */
    @Deprecated
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Returns a String representing this rectangle.
     */
    @Override
    public String toString() {
        return "Rectangle[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }
    
    @Override
    public int outcode(double x, double y) {
        int out = 0;
        if (this.width <= 0) {
            out |= OUT_LEFT | OUT_RIGHT;
        } else if (x < this.x) {
            out |= OUT_LEFT;
        } else if (x > this.x + (double) this.width) {
            out |= OUT_RIGHT;
        }
        if (this.height <= 0) {
            out |= OUT_TOP | OUT_BOTTOM;
        } else if (y < this.y) {
            out |= OUT_TOP;
        } else if (y > this.y + (double) this.height) {
            out |= OUT_BOTTOM;
        }
        return out;
    }
    
    @Deprecated
    public boolean inside(int x, int y) {
        int w = this.width;
        int h = this.height;
        if (w < 0 || h < 0) return false;
        if (x < this.x || y < this.y) return false;
        w += this.x;
        h += this.y;
        return ((w < this.x || w > x) && (h < this.y || h > y));
    }    
}
