package lejos.robotics.subsumption;


/**
 *An  Arbitrator object  manages a behavior control system by starting and stopping individual  behaviors  
 *<br>  by the calling the <code>action()</code> and <code>suppress()</code> methods on them. 
 *<br>  These Behavior objects are stored in an array, in order of increasing priority. 
 *<br>  Arbitrator  has three major responsibilities: <br> 
 * 1. Determine the highest priority  behavior among those that returns <b> true </b> to <code>takeControl() </code>. <br>   
 * 2. Suppress the active behavior if its priority is less than highest
 * priority.   These two taska are performed the Arbitrator's internal Monitor thread.<br>   
 * 3. When the <code>action()</code> method exits, call <code> action() </code>on the Behavior of highest priority. <br>
 *       This task is  performed by the Arbitrator main thread. 
 * <br>  The Arbitrator assumes that a Behavior is no longer active when <code>action()</code> exits,
 * <br>  therefore it will only call <code>suppress()</code> on the active Behavior i.e.  whose <code>action()</code> method is running.
 * <br>  It can make consecutive calls of <code> action() </code>on the same Behavior.
 * <br>  Requirements for a Behavior:
 * <br>    When <code>suppress()</code> is called, terminate <code> action() </code>immediately.
 * <br>    When<code> action() </code>exits, the robot is in a safe state (e.g. motors stopped)
 * <br>    When the behavior should take control,  the <code> takeControl() </code> should continue to return <b> true </b>
 * <br>    until its action starts. 
 * <br> After your code instantiates the Arbitrator,  it should call <code>go() </code>to start it running.
 * <br>    
 * @see Behavior
 * @author Roger Glassey
 */
public class Arbitrator
{

  private final int NONE = -1;
  private Behavior[] _behavior;
  // highest priority behavior that wants control ; set by start() used by monitor
  private int _highestPriority = NONE;
  private int _active = NONE; //  active behavior; set by monitor, used by start();
  private boolean _returnWhenInactive;
  public boolean keepRunning = true;
  /**
   * Monitor is an inner class.  It polls the behavior array to find the behavior of hightst
   * priority.  If higher than the active behavior, it calls active.suppress()
   */
  private Monitor monitor;

  /**
   * Allocates an Arbitrator object and initializes it with an array of
   * Behavior objects. The index of a behavior in this array is its priority level, so 
   * the behavior of the largest index has the highest the priority level. 
   * The behaviors in an Arbitrator can not
   * be changed once the arbitrator is initialized.<BR>
   * <B>NOTE:</B> Once the Arbitrator is initialized, the method go() must be
   * called to begin the arbitration.
   * @param behaviorList an array of Behavior objects.
   * @param returnWhenInactive if <B>true</B>, the <B>go()</B> method returns when no Behavior is active.
   */
  public Arbitrator(Behavior[] behaviorList, boolean returnWhenInactive)
  {
    _behavior = behaviorList;
    _returnWhenInactive = returnWhenInactive;
    monitor = new Monitor();
    monitor.setDaemon(true);
	System.out.println("Arbitrator created");
  }

  /**
   * Same as Arbitrator(behaviorList, false) Arbitrator start() never exits
   * @param behaviorList An array of Behavior objects.
   */
  public Arbitrator(Behavior[] behaviorList)
  {
    this(behaviorList, false);
  }

  /**
   * This method starts the arbitration of Behaviors and runs an endless loop.  <BR>
   * Note: Arbitrator does not run in a separate thread. The go()
   * method will not return unless <br>1. <code> no action() </code>method is running  and
   * <br>2. no behavior <code> takeControl() </code> returns <B> true </B>  and  
   * <br> 3. the <B>returnWhenInacative </B> flag is true,
   */
  public void go()
  {

    monitor.start();
    while (_highestPriority == NONE)
    {
      Thread.yield();//wait for some behavior to take control                    
    }
    while (true)
    {
      synchronized (monitor)
      {
        if (_highestPriority > NONE)
        {
          _active = _highestPriority;
        }
        else if (_returnWhenInactive)
        {// no behavior wants to run
          monitor.more = false;//9 shut down monitor thread
          return;
        }
      }// monitor released before action is called
      if (_active != NONE)  //_highestPrioirty could be NONE
      {
        _behavior[_active].action();
        _active = NONE;  // no active behavior at the moment
      }
      Thread.yield();
    }
  }

  public void stop() {
	  keepRunning = false;
  }
  
  /**
   * Finds the highest priority behavior that returns <B>true </B> to <code> takeControl()</code>;
   * If this priority is higher than the active behavior, it calls active.suppress().
   */
  private class Monitor extends Thread
  {

    boolean more = true;
    int maxPriority = _behavior.length - 1;

    public void run()
    {
      while (keepRunning)
      {
        //FIND HIGHEST PRIORITY BEHAVIOR THAT WANTS CONTROL
        synchronized (this)
        {
           _highestPriority = NONE; // -1
          for (int i = maxPriority; i > _active; i--) // only behaviors with higher priority are interesting
          {
            if (_behavior[i].takeControl())
            {
              _highestPriority = i;
              break;
            }
          }
          int active = _active; // local copy in case _active is set to NONE by the primary thread
          if (_active != NONE && _highestPriority > _active)
          {
            _behavior[active].suppress();
          }
        }// end synchronize block - main thread can run now
        Thread.yield();
      }
    }
  }
}
  
