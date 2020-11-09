package no.kristiania.DAO;

import no.kristiania.HTTP.HttpClient;
import no.kristiania.HTTP.HttpServer;
import no.kristiania.HTTP.taskOptionsController;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskDaoTest {

    private TaskDao taskDao;
    private static Random random = new Random();
    private HttpServer server;



    @BeforeEach
    void setUp() throws IOException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new TaskDao(dataSource);
        // Declaring port 0 means: operative system will give us a "random"(unused) port
        server = new HttpServer(0, dataSource);
    }

    @Test
    void shouldListAllTasks() throws SQLException {
        Task task1 = exampleTask();
        Task task2 = exampleTask();
        taskDao.insert(task1);
        taskDao.insert(task2);
        assertThat(taskDao.list())
                .extracting(Task::getName)
                .contains(task1.getName(), task2.getName());
    }
    @Test
    void shouldPostNewTask() throws IOException {
        String requestBody = "taskName=Walking&statusColorCode=black";
        HttpClient postClient = new HttpClient("localhost", server.getPort(), "/api/newTask", "POST", requestBody);
        assertEquals(302, postClient.getStatusCode());

        HttpClient getClient = new HttpClient("localhost", server.getPort(), "/api/tasks");
        assertThat(getClient.getResponseBody()).contains("<hr> <article>\n" +
                "<h1> Task: Walking</h1>\n" +
                "<p><strong> Status:</strong> black</p>\n" +
                "<p><strong> Workers:</strong> </p>\n" +
                "\n" +
                "    </article><hr>");
    }

    @Test
    void shouldRetrieveAllTaskProperties() throws SQLException {
        taskDao.insert(exampleTask());
        taskDao.insert(exampleTask());
        Task task = exampleTask();
        taskDao.insert(task);
        assertThat(task).hasNoNullFieldsOrProperties();

        assertThat(taskDao.retrieve(task.getId()))
                .usingRecursiveComparison()
                .isEqualTo(task);
    }

    @Test
    void shouldReturnTasksAsOptions() throws SQLException {
        taskOptionsController controller = new taskOptionsController(taskDao);
        Task task = exampleTask();
        taskDao.insert(task);

        assertThat(controller.getBody())
                .contains("<option value=" + task.getId() +">" + task.getName() + "</option>");
    }

    public static Task exampleTask() {
        Task task = new Task();
        task.setName(exampleTaskName());
        task.setStatusColorCode("red");
        return task;
    }

    private static String exampleTaskName() {
        String[] options = {"Walking", "Coding", "Reading", "Handstands", "Excessive-drinking"};
        return options[random.nextInt(options.length)];
    }
}
