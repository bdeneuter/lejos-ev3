package lejos.robotics.navigation;

public interface LineFollowingMoveController extends ArcRotateMoveController {
  /** Moves the robot forward while making a curve specified by <code>turnRate</code>. <p>
   * 
   * This move is suited for line following as it executes immediately without stopping the move that the robot is currently executing. 
   * It is also non blocking, control goes back to the main program right way. 
   * <p>
   * The <code>turnRate</code> specifies the sharpness of the turn. Use values
   * between -200 and +200.<br>
   * A positive value means that center of the turn is on the left. If the
   * robot is traveling toward the top of the page the arc looks like this:
   * <b>)</b>. <br>
   * A negative value means that center of the turn is on the right so the arc
   * looks this: <b>(</b>. <br>
   * . In this class, this parameter determines the ratio of inner wheel speed
   * to outer wheel speed <b>as a percent</b>.<br>
   * <I>Formula:</I> <code>ratio = 100 - abs(turnRate)</code>.<br>
   * When the ratio is negative, the outer and inner wheels rotate in opposite
   * directions. Examples of how the formula works:
   * <UL>
   * <LI><code>steer(0)</code> -> inner and outer wheels turn at the same
   * speed, travel straight
   * <LI><code>steer(25)</code> -> the inner wheel turns at 75% of the speed
   * of the outer wheel, turn left
   * <LI><code>steer(100)</code> -> the inner wheel stops and the outer wheel
   * is at 100 percent, turn left
   * <LI><code>steer(200)</code> -> the inner wheel turns at the same speed as
   * the outer wheel - a zero radius turn.
   * </UL>
   * <p>
   * Note: If you have specified a drift correction in the constructor it will
   * not be applied in this method.
   * 
   * @param turnRate
   *            If positive, the left side of the robot is on the inside of
   *            the turn. If negative, the left side is on the outside.
   */
  public void steer(double turnRate);
  
  /** Moves the robot backward while making a curve specified by <code>turnRate</code>. <p>
   * 
   * This move is suited for line following as it executes immediately without stopping the move that the robot is currently executing. 
   * It is also non blocking, control goes back to the main program right way. 
   * <p>
   * The <code>turnRate</code> specifies the sharpness of the turn. Use values
   * between -200 and +200.<br>
   * A positive value means that center of the turn is on the left. If the
   * robot is traveling toward the top of the page the arc looks like this:
   * <b>)</b>. <br>
   * A negative value means that center of the turn is on the right so the arc
   * looks this: <b>(</b>. <br>
   * . In this class, this parameter determines the ratio of inner wheel speed
   * to outer wheel speed <b>as a percent</b>.<br>
   * <I>Formula:</I> <code>ratio = 100 - abs(turnRate)</code>.<br>
   * When the ratio is negative, the outer and inner wheels rotate in opposite
   * directions. Examples of how the formula works:
   * <UL>
   * <LI><code>steer(0)</code> -> inner and outer wheels turn at the same
   * speed, travel straight
   * <LI><code>steer(25)</code> -> the inner wheel turns at 75% of the speed
   * of the outer wheel, turn left
   * <LI><code>steer(100)</code> -> the inner wheel stops and the outer wheel
   * is at 100 percent, turn left
   * <LI><code>steer(200)</code> -> the inner wheel turns at the same speed as
   * the outer wheel - a zero radius turn.
   * </UL>
   * <p>
   * Note: If you have specified a drift correction in the constructor it will
   * not be applied in this method.
   * 
   * @param steerRatio
   *            If positive, the left side of the robot is on the inside of
   *            the turn. If negative, the left side is on the outside.
   */
  public void steerBackward(double steerRatio);
  
}
