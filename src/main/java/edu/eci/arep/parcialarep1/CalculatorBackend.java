/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.eci.arep.parcialarep1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author maria.sanchez-m
 */
public class CalculatorBackend {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
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
            String requestLine = "";

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Backend recibió: " + inputLine);
                if (inputLine.startsWith("GET")) {
                    requestLine = inputLine;
                }
                if (!in.ready()) {
                    break;
                }
            }
            if (!requestLine.isEmpty()) {
                outputLine = processCalculation(requestLine);
            } else {
                outputLine = "HTTP/1.1 400 Bad Request\r\n\r\n{\"error\":\"invalid_request\"}";
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String processCalculation(String request) {
        try {
            String uri = request.split(" ")[1];
            System.out.println("Procesando URI: " + uri);

            if (uri.contains("/compute")) {
                String[] parts = uri.split("\\?")[1].split("&");
                String params = "";

                for (String part : parts) {
                    if (part.startsWith("params=")) {
                        params = part.substring(7);
                    }
                }

                if (params.isEmpty()) {
                    return "HTTP/1.1 400 Bad Request\r\n"
                            + "Content-Type: application/json\r\n"
                            + "\r\n{\"error\":\"No parameters provided\"}";
                }

                String[] paramArray = params.split(",");
                double sum = 0;
                for (String param : paramArray) {
                    sum += Double.parseDouble(param);
                }
                double media = sum / paramArray.length;

                String results = "{\"media\":" + media + ",\"status\":\"success\"}";
                return "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n" + results;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n{\"error\":\"Internal server error\"}";
        }
        return "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n{\"error\":\"Not found\"}";
    }

}
