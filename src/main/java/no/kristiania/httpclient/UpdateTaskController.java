package no.kristiania.httpclient;

import no.kristiania.database.*;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateTaskController implements HttpController{
    private TaskDao taskDao;

    public UpdateTaskController(TaskDao taskDao) {
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

        int taskId = Integer.parseInt(requestedParameter.getParameter("taskId"));
        String colorcode = String.valueOf(requestedParameter.getParameter("colorcode"));

        Task task = taskDao.retrieve(taskId);
        task.setColorCode(colorcode);

        taskDao.update(task);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/editColorcode.html");
        return redirect;
    }

}
