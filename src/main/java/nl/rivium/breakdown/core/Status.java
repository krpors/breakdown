package nl.rivium.breakdown.core;

/**
 * Status represents information regarding a run test step or test case. It encapsulates information about the run time,
 * errors and such.
 */
public class Status {
    /**
     * Amount of time a test step or the like took.
     */
    private long timeRun;

    private String message;

    public Status() {
        this(0, "");
    }

    public Status(long timeRun, String message) {
        this.timeRun = timeRun;
        this.message = message;
    }

    public long getTimeRun() {
        return timeRun;
    }

    public void setTimeRun(long timeRun) {
        this.timeRun = timeRun;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
