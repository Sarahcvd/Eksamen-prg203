package no.kristiania.httpclient;

import no.kristiania.database.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateWorkerController implements HttpController{
    private final TaskDao taskDao;
    private WorkerDao workerDao;
    private DataSource dataSource;

    public UpdateWorkerController(WorkerDao workerDao, TaskDao taskDao) {
        this.workerDao = workerDao;
        this.taskDao = taskDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
        return response;
    }

    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestedParameter = new QueryString(request.getBody());

        Long workerId = Long.valueOf(requestedParameter.getParameter("workerId"));
        Long taskId = Long.valueOf(requestedParameter.getParameter("taskId"));
        Worker worker = workerDao.retrieve(workerId);
        Task task = taskDao.retrieve(taskId);
        WorkerTask workerTask = new WorkerTask();

        workerTask.setTaskId(taskId);
        workerTask.setWorkerId(workerId);

        WorkerTaskDao workerTaskDao = new WorkerTaskDao(dataSource);
        workerTaskDao.insert(task, worker);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/editWorker.html");
        return redirect;
    }
}
