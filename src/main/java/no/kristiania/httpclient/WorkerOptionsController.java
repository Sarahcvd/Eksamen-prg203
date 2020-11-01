package no.kristiania.httpclient;

import no.kristiania.database.Worker;
import no.kristiania.database.WorkerDao;
import no.kristiania.database.WorkerTaskDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class WorkerOptionsController implements HttpController{
    private WorkerDao workerDao;

    public WorkerOptionsController(WorkerDao workerDao) {
        this.workerDao = workerDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "";
        for(Worker worker : workerDao.list()){
            body += "<option value=" + worker.getId() +">" + worker.getFirstName() + "</option>";
        }

        return body;
        /*return workerDao.list()
                .stream().map(w -> "<option value=" + w.getId() +">" + w.getFirstName() + "</option>")
                .collect(Collectors.joining());*/
    }
}