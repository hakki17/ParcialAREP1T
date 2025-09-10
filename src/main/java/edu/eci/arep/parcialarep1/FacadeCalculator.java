/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.eci.arep.parcialarep1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author maria.sanchez-m
 */
public class FacadeCalculator {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "https://localhost:36000/compute";

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;

            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine = "";
            String requestLine = ""; // Solo la primera línea nos interesa

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("GET")) { // Solo procesar la línea GET
                    requestLine = inputLine;
                }
                System.out.println("Recibí: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            // Procesar solo si tenemos una línea de petición válida
            if (!requestLine.isEmpty()) {
                try {
                    URI uri = new URI(requestLine.split(" ")[1]);
                    String path = uri.getPath();
                    String query = uri.getQuery();

                    if (path.startsWith("/compute")) {
                        outputLine = invokeRestService(query);
                    } else if (path.startsWith("/calculadora")) {
                        outputLine = webClient();
                    } else {
                        outputLine = "HTTP/1.1 404 Not Found\r\n\r\nNot Found";
                    }
                } catch (Exception e) {
                    outputLine = "HTTP/1.1 500 Internal Server Error\r\n\r\nError: " + e.getMessage();
                }
            }
            out.println(outputLine);

            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    public static String invokeRestService(String query) throws IOException {
        String outputLine = "";

        String fullURL = "http://localhost:36000/compute?" + query;
        URL obj = new URL(fullURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n" + response;
            in.close();
        } else {
            outputLine = "HTTP/1.1 500 Internal Server Error\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n{\"error\":\"Backend service failed\"}";
        }
        return outputLine;
    }

    public static String webClient() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "    <head>\n"
                + "        <title>Calculadora</title>\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <h1>Calculadora de Media</h1>\n"
                + "        <form>\n"
                + "            <label for=\"params\">Números (separados por comas):</label><br>\n"
                + "            <input type=\"text\" id=\"params\" name=\"params\" placeholder=\"1,2,3\"><br><br>\n"
                + "            <input type=\"button\" value=\"Calcular\" onclick=\"loadGetMsg()\">\n"
                + "        </form> \n"
                + "        <h3>Resultado</h3>\n"
                + "        <div id=\"getrespmsg\"></div>\n"
                + "\n"
                + clientJS()
                + "    </body>\n"
                + "</html>";
    }

    public static String clientJS() {
        return "        <script>\n"
                + "            function loadGetMsg() {\n"
                + "                let paramsVar = document.getElementById(\"params\").value;\n"
                + "                const xhttp = new XMLHttpRequest();\n"
                + "                xhttp.onload = function() {\n"
                + "                    document.getElementById(\"getrespmsg\").innerHTML =\n"
                + "                    this.responseText;\n"
                + "                }\n"
                + "                xhttp.open(\"GET\", \"/compute?params=\" + paramsVar);\n"
                + "                xhttp.send();\n"
                + "            }\n"
                + "        </script>\n";
    }
}
