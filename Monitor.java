/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	 private final int numPhilosophers;
	 private final boolean[] chopsticks;      // true if chopstick is available
	 private final Philosopher[] state;  // state of each philosopher
	 private boolean isTalking = false;       
	/**
	 * Constructor
	 */

	private enum Philosopher{
		thinking, hungry, eating
	}

	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		numPhilosophers = piNumberOfPhilosophers;
        chopsticks = new boolean[numPhilosophers];
        state = new Philosopher[numPhilosophers];

        // Initialize all philosophers as THINKING and all chopsticks as available
        for (int i = 0; i < numPhilosophers; i++)
        {
            chopsticks[i] = true;
            state[i] = Philosopher.thinking;
        }

	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */
	private boolean canEat(int philosopherID){
		int left = (philosopherID - 1 + numPhilosophers) % numPhilosophers;// ensures the last philosopher wraps to the first chopstick to the left
		int right = philosopherID % numPhilosophers;
		return chopsticks[left] && chopsticks[right];
	}

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		int philosopherID = piTID - 1;
		state[philosopherID] = Philosopher.hungry;
		while(!canEat(philosopherID)){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Correct circular indexing for chopstick access
		int left = (philosopherID - 1 + numPhilosophers) % numPhilosophers;
		int right = philosopherID % numPhilosophers;
	
		chopsticks[left] = false;
		chopsticks[right] = false;
		state[philosopherID] = Philosopher.eating;
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		int philosopherID = piTID - 1;

		// Correct circular indexing for chopstick access
		int left = (philosopherID - 1 + numPhilosophers) % numPhilosophers;
		int right = philosopherID % numPhilosophers;
	

        // Put down chopsticks
        chopsticks[left] = true;
        chopsticks[right] = true;
        state[philosopherID] = Philosopher.thinking;
        notifyAll();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk()
	{
		while(isTalking){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isTalking = true;
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk()
	{
		isTalking = false;
		notifyAll();
	}
}
// EOF