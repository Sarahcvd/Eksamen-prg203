package no.kristiania.HTTP;

import no.kristiania.DAO.Task;
import no.kristiania.DAO.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class WorkerTaskPostController implements HttpController {
    private TaskDao taskDao;

    public WorkerTaskPostController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestedParameter = new QueryString(request.getBody());


        String decodedTaskName = URLDecoder.decode(requestedParameter.getParameter("taskName"), StandardCharsets.UTF_8);
        String decodedTaskStatusColor = URLDecoder.decode(requestedParameter.getParameter("statusColorCode"), StandardCharsets.UTF_8);

        Task task = new Task();
        task.setName(decodedTaskName);
        task.setStatusColorCode(decodedTaskStatusColor);
        taskDao.insert(task);

        String body = "Wait a second, redirecting....";
        String response = "HTTP/1.1 302 REDIRECT\r\n" +
                "Location: http://localhost:8080/newTask.html\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;
        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
        return request;
    }

}
