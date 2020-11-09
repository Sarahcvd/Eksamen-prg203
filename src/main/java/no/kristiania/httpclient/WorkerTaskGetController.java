package no.kristiania.httpclient;

import no.kristiania.database.*;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;


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
        String body = "";
        StringBuilder bod = new StringBuilder();

        List<Task> tasks = taskDao.list();
        for(int i=0; i < tasks.size(); i++){
            String taskId = "" + tasks.get(i).getId();
            String sql = "select w.* from worker_task wt " +
                    "join worker w on wt.worker_id = w.id where wt.task_id = " + taskId;
            String workerName= "";

            List<Worker> workers = workerDao.list(sql);
            System.out.println("size: " + workers.size());
            for (int j = 0; j < workers.size(); j++) {
                workerName = workerName + workers.get(j).getFirstName() + ", ";

            }
            bod.append("<hr> <article>\n" + "<h1>" + tasks.get(i).getName() + "</h1>\n" +
                    "<h4> Workers: </h4>\n" +
                    "<div>" + workerName + "</div>\n" +
                    "\n" +
                    "</article> ");
        }
        body += bod;

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
