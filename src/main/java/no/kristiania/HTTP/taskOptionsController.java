package no.kristiania.HTTP;

import no.kristiania.DAO.Task;
import no.kristiania.DAO.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class taskOptionsController implements HttpController{
    private TaskDao taskDao;

    public taskOptionsController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);

        return new HttpMessage();
    }

    public String getBody() throws SQLException {
        String body = "";
        for(Task task : taskDao.list()){
            body += "<option value=" + task.getId() +">" + task.getName() + "</option>";
        }

        return body;

    }
}
