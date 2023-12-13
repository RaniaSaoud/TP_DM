package ScoketsWIthThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final String host = "localhost";
        final int port = 1900;

        try (Socket socket = new Socket(host, port);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server at " + host + ":" + port);

            String serverMessage;
            while ((serverMessage = bufferedReader.readLine()) != null) {
                System.out.println(serverMessage);

                if (serverMessage.contains("Game ended") || serverMessage.contains("Congrats")) {
                    break;
                }

                System.out.print("Enter your guess: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number.");
                    scanner.next(); 
                    System.out.print("Enter your guess: ");
                }
                int userGuess = scanner.nextInt();
                printWriter.println(userGuess);
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

