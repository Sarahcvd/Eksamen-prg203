package no.kristiania.DAO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkerTaskDao extends AbstractDao <WorkerTask>{

    public WorkerTaskDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected WorkerTask mapRow(ResultSet rs) throws SQLException {
        WorkerTask workerTask = new WorkerTask();
        workerTask.setTaskId(rs.getInt("task_id"));
        workerTask.setWorkerId(rs.getInt("worker_id"));

        return workerTask;
    }

    public List<WorkerTask> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM worker_task")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<WorkerTask> workerTasks = new ArrayList<>();
                    while (rs.next()) {
                        workerTasks.add(mapRow(rs));
                    }
                    return workerTasks;
                }
            }
        }
    }

    public void insert(Task task, Worker worker) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO worker_task (task_id, worker_id) VALUES (?, ?)"

            )) {
                statement.setInt(1, task.getId());
                statement.setInt(2, worker.getId());
                statement.executeUpdate();
            }
        }
    }
}
