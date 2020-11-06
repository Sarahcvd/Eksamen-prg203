package no.kristiania.database;

public class WorkerTask {
    private Long workerId;
    private Long taskId;

    public Long getWorkerId() {

        return workerId;
    }

    public void setWorkerId(Long workerId) {

        this.workerId = workerId;
    }

    public long getTaskId() {

        return taskId;
    }

    public void setTaskId(Long taskId) {

        this.taskId = taskId;
    }
}
