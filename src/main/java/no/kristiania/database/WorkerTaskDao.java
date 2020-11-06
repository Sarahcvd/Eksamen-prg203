package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;

public class WorkerTaskDao extends AbstractDao <WorkerTask>{

    public WorkerTaskDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected WorkerTask mapRow(ResultSet rs) throws SQLException {
        WorkerTask workerTask = new WorkerTask();
        workerTask.setTaskId((Long) rs.getObject("task_id"));
        workerTask.setWorkerId((Long) rs.getObject("worker_id"));

        return workerTask;
    }

    public void update(Task task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE worker_task SET worker_id = ? WHERE task_id = ?" //Denne er åpenbart feil

            )) {
                statement.setLong(1, task.getId());
                statement.executeUpdate();
            }
        }
    }

    public void insert(Task task, Worker worker) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO worker_task (task_id, worker_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS

            )) {
                statement.setLong(1, task.getId());
                statement.setLong(2, worker.getId());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    WorkerTask workerTask = new WorkerTask();
                    workerTask.setTaskId((long) generatedKeys.getInt("task_id"));
                    workerTask.setWorkerId((long) generatedKeys.getInt("worker_id"));
                }
            }
        }
    }
}
