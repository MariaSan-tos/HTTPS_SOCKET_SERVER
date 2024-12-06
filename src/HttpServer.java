import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class HttpServer {
    private static final int PORT = 8080;
    private static final Map<Integer, String> alunos = new ConcurrentHashMap<>();
    private static int idCounter = 1;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor HTTP rodando na porta " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String requestLine = reader.readLine();
                if (requestLine == null) return;

                String[] requestParts = requestLine.split(" ");
                String method = requestParts[0];
                String uri = requestParts[1];

                if (method.equals("GET")) {
                    handleGet(uri, writer);
                } else if (method.equals("DELETE")) {
                    handleDelete(uri, writer);
                } else if (method.equals("POST")) {
                    handlePost(writer);
                } else {
                    sendResponse(writer, 405, "Method Not Allowed", "Método não permitido");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleGet(String uri, PrintWriter writer) {
            String[] pathParts = uri.split("/");
            if (pathParts.length == 3 && pathParts[1].equals("aluno")) {
                try {
                    int id = Integer.parseInt(pathParts[2]);
                    String aluno = alunos.get(id);
                    if (aluno != null) {
                        sendResponse(writer, 200, "OK", "<h1>Aluno " + id + "</h1><p>" + aluno + "</p>");
                    } else {
                        sendResponse(writer, 404, "Not Found", "<h1>Aluno não encontrado</h1>");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(writer, 400, "Bad Request", "<h1>ID inválido</h1>");
                }
            } else {
                sendResponse(writer, 400, "Bad Request", "<h1>Requisição inválida</h1>");
            }
        }

        private void handleDelete(String uri, PrintWriter writer) {
            String[] pathParts = uri.split("/");
            if (pathParts.length == 3 && pathParts[1].equals("aluno")) {
                try {
                    int id = Integer.parseInt(pathParts[2]);
                    if (alunos.containsKey(id)) {
                        alunos.remove(id);
                        sendResponse(writer, 200, "OK", "<h1>Aluno " + id + " excluído com sucesso</h1>");
                    } else {
                        sendResponse(writer, 404, "Not Found", "<h1>Aluno não encontrado</h1>");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(writer, 400, "Bad Request", "<h1>ID inválido</h1>");
                }
            } else {
                sendResponse(writer, 400, "Bad Request", "<h1>Requisição inválida</h1>");
            }
        }

        private void handlePost(PrintWriter writer) {
            // Criando um aluno aleatório
            int id = generateUniqueId();
            String aluno = "Aluno " + id;
            alunos.put(id, aluno);
            sendResponse(writer, 201, "Created", "<h1>Aluno criado com sucesso</h1><p>ID: " + id + "</p>");
        }

        private void sendResponse(PrintWriter writer, int statusCode, String statusMessage, String content) {
            writer.println("HTTP/1.1 " + statusCode + " " + statusMessage);
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println("Connection: close");
            writer.println();
            writer.println(content);
        }

        private int generateUniqueId() {
            int id;
            synchronized (alunos) {
                id = idCounter++;
            }
            return id;
        }
    }
}
