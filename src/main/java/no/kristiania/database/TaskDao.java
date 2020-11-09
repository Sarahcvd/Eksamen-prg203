package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao extends AbstractDao<Task>{

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Task task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO task (name, statusColorCode) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, task.getName());
                statement.setString(2, task.getStatusColorCode());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    task.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public Task retrieve(int id) throws SQLException {
        return retrieve(id, "SELECT * FROM task WHERE id = ?");
    }

    public List<Task> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM task")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<Task> workers = new ArrayList<>();
                    while (rs.next()) {
                        workers.add(mapRow(rs));
                    }
                    return workers;
                }
            }
        }
    }

    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setName(rs.getString("name"));
        task.setStatusColorCode(rs.getString("statusColorCode"));
        return task;
    }

    public void update(Task task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE task SET statusColorCode = ? WHERE id = ?"
            )) {
                statement.setString(1, task.getStatusColorCode());
                statement.setInt(2,task.getId());
                statement.executeUpdate();
            }
        }
    }
}
