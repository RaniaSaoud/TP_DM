package ScoketsWIthThread;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Repartitor extends Thread {
    private final Socket socket;
    private final int clientNumber;
    private final int secretNumber;
    private final ServerMT server;
    private PrintWriter printWriter;
    private boolean gameActive;

    public Repartitor(Socket socket, int clientNumber, ServerMT server) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.secretNumber = generateSecretNumber();
        this.server = server;
        this.gameActive = true;
    }

    private int generateSecretNumber() {
        Random random = new Random();
        return random.nextInt(100) + 1;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)) {
                
            printWriter.println("Welcome client number: " + clientNumber);
            printWriter.println("Guess the secret number between 1 and 100.");

            while (gameActive) {
                String guessStr = bufferedReader.readLine();
                if (guessStr == null) {
                    gameActive = false;
                    break;
                }

                int guess = Integer.parseInt(guessStr);
                if (guess == secretNumber) {
                    server.broadcastMessage("Client #" + clientNumber + " has won the game!");
                    printWinnerMessage();
                    server.shutDown();
                    break;
                } else {
                    provideHint(printWriter, guess);
                }
            }
        } catch (IOException e) {
            System.err.println("Error with client #" + clientNumber + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Received invalid number from client #" + clientNumber);
        } finally {
            server.removeClientHandler(this);
            closeConnection();
        }
                }

                

    private void printWinnerMessage() {
       
        printWriter.println("Congrats! You guessed the correct number: " + secretNumber);
        printWriter.println("Game ended.");
        gameActive = false;
       
    }
    

    private void provideHint(PrintWriter printWriter, int guess) {
        if (guess < secretNumber) {
            printWriter.println("Try again. The secret number is higher.");
        } else {
            printWriter.println("Try again. The secret number is lower.");
        }
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            // ...
        }
    }
}
