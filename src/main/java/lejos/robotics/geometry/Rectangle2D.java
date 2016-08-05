package lejos.robotics.geometry;

/**
 * An abstract class for a Rectangle.
 * Subclasses use float, double or integer coordinates.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class Rectangle2D extends RectangularShape {
    /**
     * The bitmask that indicates that a point lies to the left of
     * this rectangle.
     */
    public static final int OUT_LEFT = 1;

    /**
     * The bitmask that indicates that a point lies above this rectangle.
     */
    public static final int OUT_TOP = 2;

    /**
     * The bitmask that indicates that a point lies to the right of this rectangle.
     */
    public static final int OUT_RIGHT = 4;

    /**
     * The bitmask that indicates that a point lies below this rectangle.
     */
    public static final int OUT_BOTTOM = 8;

	/**
	 * A Rectangle2D with float coordinates.
	 */
	public static class Float extends Rectangle2D {
		/**
		 * The x coordinate of the top left corner
		 */
		public float x;
		
		/**
		 * The y coordinate of the top right corner
		 */
		public float y;
		
		/**
		 * The width of the rectangle
		 */
		public float width;
		
		/**
		 * The height of the rectangle;
		 */
		public float height;
		
		
		/**
		 * Create an empty rectangle at (0,0)
		 */
		public Float() {
			x = y = width = height = 0;
		}
		
		/**
		 * Create a rectangle with float coordinates
		 * 
		 * @param x the x coordinate of the top left corner
		 * @param y the y coordinate of the top left corner
		 * @param width the width of the rectangle
		 * @param height the height of the rectangle
		 */
		public Float(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
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
		public double getWidth() {
			
			return width;
		}
		@Override
		public double getHeight() {
			return height;
		}

		@Override
		public boolean isEmpty() {
            return (width <= 0) || (height <= 0);
		}

		/**
		 * Get the bounds as a Rectangle2D with float coordinates
		 * @return the bounding rectangle
		 */
		public Rectangle2D getBounds2D() {
			return new Float(x, y, width, height);
		}
	
		/**
		 * Set the rectangle using float coordinates
		 * 
		 * @param x the x coordinate of the top left corner
		 * @param y the y coordinate of the top left corner
		 * @param w the width
		 * @param h the height
		 */
	    public void setRect(float x, float y, float w, float h) {
	        this.x = x;
	        this.y = y;
	        this.width = w;
	        this.height = h;
	    }
	    
	    @Override
        public void setRect(Rectangle2D r) {
            this.x = (float) r.getX();
            this.y = (float) r.getY();
            this.width = (float) r.getWidth();
            this.height = (float) r.getHeight();
        }
        
	    @Override
        public void setRect(double x, double y, double w, double h) {
            this.x = (float) x;
            this.y = (float) y;
            this.width = (float) w;
            this.height = (float) h;
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
	}
	
	/**
	 * A Rectangle2D with double coordinates
	 */
	public static class Double extends Rectangle2D {
		/**
		 * The x coordinate of the top left corner
		 */
		public double x;
		
		/**
		 * The y coordinate of the top right corner
		 */
		public double y;
		
		/**
		 * The width of the rectangle
		 */
		public double width;
		
		/**
		 * The height of the rectangle;
		 */
		public double height;
		
		/**
		 * Create an empty rectangle at (0,0)
		 */
		public Double() {
			x = y = width = height = 0;
		}
		
		public Double(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
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
		public double getWidth() {
			return width;
		}
		
		@Override
		public double getHeight() {
			return height;
		}

		@Override
		public boolean isEmpty() {
            return (width <= 0) || (height <= 0);
		}

		@Override
		public void setFrame(double x, double y, double w, double h) {
			setRect(x, y, w, h);			
		}

		public Rectangle2D getBounds2D() {
			return new Double(x, y, width, height);
		}
		
		@Override
	    public void setRect(double x, double y, double w, double h) {
	        this.x = x;
	        this.y = y;
	        this.width = w;
	        this.height = h;
	    }
	    
		@Override
        public void setRect(Rectangle2D r) {
            this.x = r.getX();
            this.y = r.getY();
            this.width = r.getWidth();
            this.height = r.getHeight();
        }
		
	    @Override
		public int outcode(double x, double y) {
	        int out = 0;
	        if (this.width <= 0) {
	            out |= OUT_LEFT | OUT_RIGHT;
	        } else if (x < this.x) {
	            out |= OUT_LEFT;
	        } else if (x > this.x + this.width) {
	            out |= OUT_RIGHT;
	        }
	        if (this.height <= 0) {
	            out |= OUT_TOP | OUT_BOTTOM;
	        } else if (y < this.y) {
	            out |= OUT_TOP;
	        } else if (y > this.y + this.height) {
	            out |= OUT_BOTTOM;
	        }
	        return out;
	    }
	}
	
	/**
	 * This is an abstract class which cannot be instantiated: use Rectangle2D.Float, Rectangle2D.Double, or Rectangle.
	 */
	protected Rectangle2D() {
	}
	
	public boolean contains(double x, double y, double w, double h) {
        if (isEmpty() || w <= 0 || h <= 0) return false;       
        double x0 = getX();
        double y0 = getY();
        return (x >= x0 && y >= y0 &&
                (x + w) <= x0 + getWidth() &&
                (y + h) <= y0 + getHeight());
	}
	
	/**
	 * Set this rectangle to a rectangle defined by double coordinates
	 * 
	 * @param x the x coordinate of the top left corner
	 * @param y the y coordinate of the top right corner
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 */
    public abstract void setRect(double x, double y, double w, double h);
	
    /**
     * Set this Rectangle2D to be the same as a given Rectangle2D
     * 
     * @param r the Rectangle2D
     */
    public void setRect(Rectangle2D r) {
        setRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
	
	@Override
    public void setFrame(double x, double y, double w, double h) {
        setRect(x, y, w, h);
    }
	
	/**
	 * Test if this Rectangle2D contains a rectangle defined by double coordinates
	 */
    public boolean contains(double x, double y) {
        double x0 = getX();
        double y0 = getY();
        return (x >= x0 && y >= y0 &&
                x < x0 + getWidth() && y < y0 + getHeight());
    }
    
    /**
     * Test if this Rectangle2D intersects a rectangle defined by double coordinates
     */
    public boolean intersects(double x, double y, double w, double h) {
        if (isEmpty() || w <= 0 || h <= 0) return false;
        double x0 = getX();
        double y0 = getY();
        return (x + w > x0 && y + h > y0 &&
                x < x0 + getWidth() && y < y0 + getHeight());
    }  
    
    /**
     * Test if this rectangle intersects a given line
     * 
     * @param x1 the x coordinate of the start of the given line
     * @param y1 the y coordinate of the start of the given line
     * @param x2 the x coordinate of the end of the given line
     * @param y2 the y coordinate of the end of the given line
     * @return true iff the rectangle intersects the line
     */
    public boolean intersectsLine(double x1, double y1, double x2, double y2) {
        int out1, out2 = outcode(x2, y2);
        if (out2 == 0) return true;
        
        while ((out1 = outcode(x1, y1)) != 0) {
            if ((out1 & out2) != 0) return false;
            
            if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
                double x = getX();
                if ((out1 & OUT_RIGHT) != 0) {
                    x += getWidth();
                }
                y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
                x1 = x;
            } else {
                double y = getY();
                if ((out1 & OUT_BOTTOM) != 0) {
                    y += getHeight();
                }
                x1 = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
                y1 = y;
            }
        }
        return true;
    }
    
    /**
     * Returns a mask value that specifies where a given point lies with respect
     * to this rectangle.
     * 
     * @param p the given point
     * @return the mask value
     */
    public int outcode(Point2D p) {
        return outcode(p.getX(), p.getY());
    }
    
    /**
     * Returns a mask value that specifies where a point lies with respect
     * to this rectangle.
     * 
     * @param x the x coordinate of the given point
     * @param y the y coordinate of the given point
     * @return the mask value
     */
    public abstract int outcode(double x, double y);
    
    /**
     * Test if the rectangle is equal to a given object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Rectangle2D) {
            Rectangle2D r2d = (Rectangle2D) obj;
            return ((getX() == r2d.getX()) &&
                    (getY() == r2d.getY()) &&
                    (getWidth() == r2d.getWidth()) &&
                    (getHeight() == r2d.getHeight()));
        }
        return false;
    }
}
