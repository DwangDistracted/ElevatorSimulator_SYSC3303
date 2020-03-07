package elevatorsim;

import elevatorsim.scheduler.Scheduler;

public class SchedulerProcess {
	public static void main(String[] args) {
		Scheduler scheduler = Scheduler.getInstance();
		scheduler.start();
	}
}
