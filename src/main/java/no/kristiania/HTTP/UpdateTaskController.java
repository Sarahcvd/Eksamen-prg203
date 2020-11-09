package no.kristiania.HTTP;

import no.kristiania.DAO.*;

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
        String statusColorCode = String.valueOf(requestedParameter.getParameter("statusColorCode"));

        Task task = taskDao.retrieve(taskId);
        task.setStatusColorCode(statusColorCode);

        taskDao.update(task);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/editStatusColorCode.html");
        return redirect;
    }

}
