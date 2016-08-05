package lejos.robotics.geometry;



/**
 * An abstract class representing a line in two dimensional space
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class Line2D implements Shape, Cloneable {
	/**
	 * A line in 2D space using float coordinates
	 */
	public static class Float extends Line2D {
		/**
		 * The x coordinate of the start of the line
		 */
		public float x1;
		/**
		 * The y coordinate of the start of the line
		 */
		public float y1;
		/**
		 * The x coordinate of the end of the line
		 */
		public float x2;
		/**
		 * The y coordinate of the end of the line
		 */
		public float y2;
		
		/**
		 * Creates a zero length line at (0,0)
		 */
		public Float() {}
		
		/**
		 * Create a line from (x1,y1) to (x2,y2)
		 * 
		 * @param x1 the x coordinate of the start of the line
		 * @param y1 the y coordinate of the start of the line
		 * @param x2 the x coordinate of the end of the line
		 * @param y2 the y coordinate of the end of the line
		 */
		public Float(float x1, float y1, float x2, float y2) {
			setLine(x1, y1, x2, y2);
		}
		
		/**
		 * Set the float coordinates of the start and end of the line
		 * 
		 * @param x1 the x coordinate of the start of the line
		 * @param y1 the y coordinate of the start of the line
		 * @param x2 the x coordinate of the end of the line
		 * @param y2 the y coordinate of the end of the line
		 */
		public void setLine(float x1, float y1, float x2, float y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		/**
		 * Get the bounds of the line as a Rectangle2D
		 */
		public Rectangle2D getBounds2D() {
            float x, y, w, h;
            if (x1 < x2) {
                x = x1;
                w = x2 - x1;
            } else {
                x = x2;
                w = x1 - x2;
            }
            if (y1 < y2) {
                y = y1;
                h = y2 - y1;
            } else {
                y = y2;
                h = y1 - y2;
            }
            return new Rectangle2D.Float(x, y, w, h);
		}
	
		@Override
        public double getX1() {
            return x1;
        }

		@Override
        public double getY1() {
            return y1;
        }

		@Override
        public Point2D getP1() {
            return new Point2D.Float(x1, y1);
        }

		@Override
        public double getX2() {
            return x2;
        }

		@Override
        public double getY2() {
            return y2;
        }

		@Override
        public Point2D getP2() {
            return new Point2D.Float(x2, y2);
        }

		@Override
		public void setLine(double x1, double y1, double x2, double y2) {
			this.x1 = (float) x1;
			this.y1 = (float) y1;
			this.x2 = (float) x2;
			this.y2 = (float) y2;			
		}
	}
	
	/**
	 * A line in 2D space using float coordinates
	 */
	public static class Double extends Line2D {
		/**
		 * the x coordinate of the start of the line
		 */
		public double x1;
		
		/**
		 * The y coordinate of the sztart of the line
		 */
		public double y1;
		
		/**
		 * The x coordinate of the end of the line
		 */
		public double x2;
		
		/**
		 * The y coordinate of the start of the line
		 */
		public double y2;
		
		/**
		 * Create a zero length line at (0,0) with double coordinates
		 */
		public Double() {}
		
		/**
		 * Create a line from (x1,y1) to (x2,y2) with double coordinate
		 * 
		 * @param x1 the x coordinate of the start of the line
		 * @param y1 the y coordinate of the start of the line
		 * @param x2 the x coordinate of the end of the line
		 * @param y2 the y coordinate of the end of the line
		 */
		public Double(double x1, double y1, double x2, double y2) {
			setLine(x1, y1, x2, y2);
		}
		
		@Override
	    public double getX1() {
	    	return x1;
	    }

		@Override
        public double getY1() {
            return y1;
        }

		@Override
        public Point2D getP1() {
            return new Point2D.Double(x1, y1);
        }

		@Override
        public double getX2() {
            return x2;
        }

		@Override
        public double getY2() {
            return y2;
        }

		@Override
        public Point2D getP2() {
            return new Point2D.Double(x2, y2);
        }

		@Override
		public void setLine(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		/**
		 * Get the bounds of the line as a Rectangle2D
		 */
		public Rectangle2D getBounds2D() {
            double x, y, w, h;
            if (x1 < x2) {
                x = x1;
                w = x2 - x1;
            } else {
                x = x2;
                w = x1 - x2;
            }
            if (y1 < y2) {
                y = y1;
                h = y2 - y1;
            } else {
                y = y2;
                h = y1 - y2;
            }
            return new Rectangle2D.Double(x, y, w, h);
		}
	}
	
	/**
	 * This is an abstract class that cannot be instantiated: use Line2D.Float or Line2D.Double.
	 */
    protected Line2D() {
    }

	/**
	 * Get the x coordinate of the start of the line
	 * 
	 * @return the x coordinate as a double
	 */
    public abstract double getX1();

    /**
     * Get the y coordinate of the start of the line
     * 
     * @return the y coordinate as a double
     */
    public abstract double getY1();

    /**
     * Get the start point of the line as a Point2D
     * 
     * @return the Point2D
     */
    public abstract Point2D getP1();

    /**
     * Get the x coordinate of the end of the line
     * 
     * @return the x coordinate as a double
     */
    public abstract double getX2();

    /**
     * Get the y coordinate of the end of the line
     * 
     * @return the y coordinate as a double
     */
    public abstract double getY2();

    /**
     * Get the end point of the line as a Point2D
     * 
     * @return the Point2D
     */
    public abstract Point2D getP2();

    
    /**
     * Sets the end points of the line using double coordinates.
     * 
     * @param x1 the x coordinate of the start point
     * @param y1 the y coordinate of the start point
     * @param x2 the x coordinate of the end point
     * @param y2 the y coordinate of the end point
     */
    public abstract void setLine(double x1, double y1, double x2, double y2);
    
    /**
     * Sets the end points of the line from a given start and end point
     * @param p1 the start point
     * @param p2 the end point
     */
    public void setLine(Point2D p1, Point2D p2) {
    	setLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    
    /**
     * Set the end points of a line to the same as a given line
     * 
     * @param line the given line
     */
    public void setLine(Line2D line) {
    	setLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }
    
    public boolean contains(double x, double y) {
        return false;
    }
    
	public boolean contains(Point2D p) {
		return false;
	}

	public boolean contains(double x, double y, double w, double h) {
		return false;
	}

	public boolean contains(Rectangle2D r) {
		return false;
	}
	
    public boolean intersects(double x, double y, double w, double h) {
        return intersects(new Rectangle2D.Double(x, y, w, h));
    }
    
    /**
     * Test if this line intersects a given line
     * 
     * @param x1 the x coordinate of the start of the given line
     * @param y1 the y coordinate of the start of the given line
     * @param x2 the x coordinate of the end of the given line
     * @param y2 the y coordinate of the end of the given line
     * @return true iff the lines intersect
     */
    public boolean intersectsLine(double x1, double y1, double x2, double y2) {
        return linesIntersect(x1, y1, x2, y2,
                              getX1(), getY1(), getX2(), getY2());
    }
    
    /**
     * Tests if this line intersects a given line
     * 
     * @param l the given line
     * @return true iff the lines intersect
     */
    public boolean intersectsLine(Line2D l) {
        return linesIntersect(l.getX1(), l.getY1(), l.getX2(), l.getY2(),
                              getX1(), getY1(), getX2(), getY2());
    }
    
    /**
     * Test if one line intersects another line
     * 
     * @param x1 the x coordinate of the start of the first line
     * @param y1 the y coordinate of the start of the first line
     * @param x2 the x coordinate of the end of the first line
     * @param y2 the y coordinate of the end of the first line
     * @param x3 the x coordinate of the start of the second line
     * @param y3 the y coordinate of the start of the second line
     * @param x4 the x coordinate of the end of the second line
     * @param y4 the y coordinate of the end of the second line
     * @return true iff the lines intersect
     */
    public static boolean linesIntersect(
    		double x1, double y1,
            double x2, double y2,
            double x3, double y3,
            double x4, double y4)
    {
        return ((relativeCCW(x1, y1, x2, y2, x3, y3) *
                 relativeCCW(x1, y1, x2, y2, x4, y4) <= 0) &&
                (relativeCCW(x3, y3, x4, y4, x1, y1) *
                 relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
    }

	public RectangleInt32 getBounds() {
		return getBounds2D().getBounds();
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
    
    /**
     * Returns an indicator of where the specified point
     * lies with respect to the line
     * 
     * @param x1 the x coordinate of the start of the line
     * @param y1 the y coordinate of the start of the line
     * @param x2 the x coordinate of the end of the line
     * @param y2 the y coordinate of the end of the line
     * @param px the x coordinate of the specified point
     * @param py the y coordinate of the specified point
     * @return 0 iff the point is on the line else 1 or -1 depending
     * on whether the point in to the left or ahead of the line, or to the right or behind
     * the line segment
     */
    public static int relativeCCW(
    		double x1, double y1,
            double x2, double y2,
            double px, double py)
	{
		double tx = x2 - x1;
		double ty = y2 - y1;
		double tpx  = px - x1;
		double tpy = py - y1;
		double ccw = tpx * ty - tpy * tx;
		if (ccw == 0) {
			ccw = tpx * tx + tpy * ty;
			if (ccw > 0) {
				tpx -= tx;
				tpy -= ty;
				ccw = tpx * tx + tpy * ty;
				if (ccw < 0) ccw = 0;
			}
		}
		return (ccw < 0) ? -1 : ((ccw > 0) ? 1 : 0);
	}
    
    /**
     * Returns an indicator of where the specified point
     * lies with respect to the line
     * 
     * @param p the specified point
     * @return 0 iff the point is on the line else 1 or -1 depending
     * on whether the point in to the left or ahead of the line, or to the right or behind
     * the line segment
     */
    public int relativeCCW(Point2D p) {
        return relativeCCW(getX1(), getY1(), getX2(), getY2(),
                           p.getX(), p.getY());
    }
    
    /**
     * Returns an indicator of where the specified point
     * lies with respect to the line.
     *  
     * @param px the x coordinate of the specified point
     * @param py the y coordinate of the specified point
     * @return 0 iff the point is on the line else 1 or -1 depending
     * on whether the point in to the left or ahead of the line, or to the right or behind
     * the line segment
     */
    public int relativeCCW(double px, double py) {
        return relativeCCW(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    public boolean intersects(Rectangle2D r) {
        return r.intersectsLine(getX1(), getY1(), getX2(), getY2());
    }

    /**
     * Measures the square of the shortest distance from the reference point
     * to a point on the line segment. If the point is on the segment, the
     * result will be 0.
     *
     * @param x1 the first x coordinate of the segment
     * @param y1 the first y coordinate of the segment
     * @param x2 the second x coordinate of the segment
     * @param y2 the second y coordinate of the segment
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @return the square of the distance from the point to the segment
     */
    public static double ptSegDistSq(double x1, double y1, double x2, double y2,
                                     double px, double py)
    {
      double pd2 = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);

      double x, y;
      if (pd2 == 0)
        {
          // Points are coincident.
          x = x1;
          y = y2;
        }
      else
        {
          double u = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / pd2;

          if (u < 0)
            {
              // "Off the end"
              x = x1;
              y = y1;
            }
          else if (u > 1.0)
            {
              x = x2;
              y = y2;
            }
          else
            {
              x = x1 + u * (x2 - x1);
              y = y1 + u * (y2 - y1);
            }
        }

      return (x - px) * (x - px) + (y - py) * (y - py);
    }

    /**
     * Measures the shortest distance from the reference point to a point on
     * the line segment. If the point is on the segment, the result will be 0.
     *
     * @param x1 the first x coordinate of the segment
     * @param y1 the first y coordinate of the segment
     * @param x2 the second x coordinate of the segment
     * @param y2 the second y coordinate of the segment
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @return the distance from the point to the segment
     */
    public static double ptSegDist(double x1, double y1, double x2, double y2,
                                   double px, double py)
    {
      return Math.sqrt(ptSegDistSq(x1, y1, x2, y2, px, py));
    }

    /**
     * Measures the square of the shortest distance from the reference point
     * to a point on this line segment. If the point is on the segment, the
     * result will be 0.
     *
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @return the square of the distance from the point to the segment
     * @see #ptSegDistSq(double, double, double, double, double, double)
     */
    public double ptSegDistSq(double px, double py)
    {
      return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    /**
     * Measures the square of the shortest distance from the reference point
     * to a point on this line segment. If the point is on the segment, the
     * result will be 0.
     *
     * @param p the point
     * @return the square of the distance from the point to the segment
     * @throws NullPointerException if p is null
     * @see #ptSegDistSq(double, double, double, double, double, double)
     */
    public double ptSegDistSq(Point2D p)
    {
      return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), p.getX(), p.getY());
    }

    /**
     * Measures the shortest distance from the reference point to a point on
     * this line segment. If the point is on the segment, the result will be 0.
     *
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @return the distance from the point to the segment
     * @see #ptSegDist(double, double, double, double, double, double)
     */
    public double ptSegDist(double px, double py)
    {
      return ptSegDist(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    /**
     * Measures the shortest distance from the reference point to a point on
     * this line segment. If the point is on the segment, the result will be 0.
     *
     * @param p the point
     * @return the distance from the point to the segment
     * @throws NullPointerException if p is null
     * @see #ptSegDist(double, double, double, double, double, double)
     */
    public double ptSegDist(Point2D p)
    {
      return ptSegDist(getX1(), getY1(), getX2(), getY2(), p.getX(), p.getY());
    }
    
}
