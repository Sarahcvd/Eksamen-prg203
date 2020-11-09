package no.kristiania.httpclient;

import no.kristiania.database.*;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class WorkerTaskGetController implements HttpController {
    private TaskDao taskDao;
    private WorkerDao workerDao;
    private WorkerTaskDao workerTaskDao;

    public WorkerTaskGetController(TaskDao taskDao, WorkerDao workerDao, WorkerTaskDao workerTaskDao) {
        this.taskDao = taskDao;
        this.workerDao = workerDao;
        this.workerTaskDao = workerTaskDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for(Task task : taskDao.list()) {
            body += "<li colorCode="+ task.getColorCode() +">" + task.getName() + "</br>   Current status:   " + task.getColorCode() + "</li>";
        }

        for(WorkerTask workerTask : workerTaskDao.list() ) {
            body += "<li>" + "Worker ID: " + workerTask.getWorkerId() +"</li>";
        }

        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;


        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
        return request;
    }
}
