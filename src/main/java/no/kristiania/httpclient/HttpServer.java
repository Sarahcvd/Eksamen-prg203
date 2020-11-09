package no.kristiania.httpclient;

import no.kristiania.database.Worker;
import no.kristiania.database.WorkerDao;
import no.kristiania.database.TaskDao;
import no.kristiania.database.WorkerTaskDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private static String headerClose = "Connection: close";

    private Map<String, HttpController> controllers;

    private static WorkerDao workerDao;
    private ServerSocket serverSocket;
    private final TaskDao taskDao;

    public HttpServer(int port, DataSource dataSource) throws IOException {
        workerDao = new WorkerDao(dataSource);
        taskDao = new TaskDao(dataSource);
        WorkerTaskDao workerTaskDao = new WorkerTaskDao(dataSource);
        controllers = Map.of(
                "/api/newTask", new WorkerTaskPostController(taskDao),
                "/api/tasks", new WorkerTaskGetController(taskDao, workerDao, workerTaskDao),
                "/api/taskOptions", new taskOptionsController(taskDao),
                "/api/workerOptions", new WorkerOptionsController(workerDao),
                "/api/updateWorker", new UpdateWorkerController(workerDao, taskDao, workerTaskDao),
                "/api/updateStatus", new UpdateTaskController(taskDao)
        );
        // Open an entry point to our program for network clients
        serverSocket = new ServerSocket(port);
        logger.warn("Server started on port {}", serverSocket.getLocalPort());

        // New threads executes the code in a separate "thread", that is: In parallel
        new Thread(() -> {  // Anonymous function with code that will be executed in parallel (INFINITE LOOP!!)
            while (true){
                try(Socket clientSocket = serverSocket.accept()){
                    // Accept waits for a client to try and connect - blocks until a connection is successful
                    handleRequest(clientSocket);
                }catch (IOException | SQLException e) {
                    // If something went wrong with the connection - print out exception and try again
                    e.printStackTrace();
                }
            }
        }).start();  // Start the threads, so the code inside executes without blocking the current thread
        // Now the test does NOT have to wait for someone to connect
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    // This code will be executed for each client (connection)
    private void handleRequest(Socket clientSocket) throws IOException, SQLException {
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        // Example "GET /echo?body=hello HTTP/1.1"  (this is what the browser writes)

        // Example GET, POST, PUT, DELETE etc
        String requestMethod = requestLine.split(" ")[0];
        String requestTarget = requestLine.split(" ")[1];
        // Example "GET /echo?body=hello"

        int questionPos = requestTarget.indexOf('?');
        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        if(requestMethod.equals("POST")){
            if(requestPath.equals("/api/newWorker")){
                handlePostWorker(clientSocket, request);
            }else {
                getController(requestPath).handle(request, clientSocket);
            }
        } else {
            if (requestPath.equals("/echo")) {
                handleEchoRequest(clientSocket, requestTarget, questionPos);
            } else if (requestPath.equals("/api/worker")){
                handleGetWorkers(clientSocket);
            } else {
                HttpController controller = controllers.get(requestPath);
                if(controller != null){
                    controller.handle(request, clientSocket);
                }else{
                    handleFileRequest(clientSocket, requestPath);
                }
            }
        }
    }

    public HttpController getController(String requestPath) {
        return controllers.get(requestPath);
    }

    public static void handlePostWorker(Socket clientSocket, HttpMessage request) throws SQLException, IOException {
        QueryString requestedParameter = new QueryString(request.getBody());
        String decodedEmailAddress = URLDecoder.decode(requestedParameter.getParameter("email_address"), StandardCharsets.UTF_8);

        Worker worker = new Worker();
        worker.setFirstName(requestedParameter.getParameter("first_name"));
        worker.setLastName(requestedParameter.getParameter("last_name"));
        worker.setEmailAddress(decodedEmailAddress);

        workerDao.insert(worker);
        String body = "Wait a second, redirecting....";
        String response = "HTTP/1.1 302 REDIRECT\r\n" +
                "Location: http://localhost:8080/newWorker.html\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                headerClose + "\r\n" +
                "\r\n" +
                body;
        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void handleFileRequest(Socket clientSocket, String requestPath) throws IOException {
        try (InputStream inputStream = HttpServer.class.getResourceAsStream(requestPath)) {
            if(inputStream == null){
                String body = requestPath + " does not exist";
                String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        headerClose + "\r\n" +
                        "\r\n" +
                        body;
                clientSocket.getOutputStream().write(response.getBytes());
                return;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            }
            if (requestPath.endsWith(".css")){
                contentType = "text/css";
            }

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
        }
    }

    public static void handleGetWorkers(Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (Worker worker : workerDao.list()) {
            body += "<li>" + worker.getFirstName() + " " + worker.getLastName() + " " + worker.getEmailAddress() + "</li>";
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
    }

    public static void handleEchoRequest(Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String statusCode = "200";
        String body = "Hello <strong>World</strong>!";
        if (questionPos != -1) {
            // "body=hello"
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                body = queryString.getParameter("body");
            }
        }
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Contention: close\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        } catch (Exception e) {
            System.out.println(e);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        logger.info("Using database {}", dataSource.getUrl());
        Flyway.configure().dataSource(dataSource).load().migrate();

        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Started on http://localhost:8080/index.html: {}", 8080);
    }


    public List<Worker> getWorkers() throws SQLException {
        return workerDao.list();
    }
}