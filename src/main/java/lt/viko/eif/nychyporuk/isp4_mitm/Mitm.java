package lt.viko.eif.nychyporuk.isp4_mitm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Mitm {
    public static void main(String[] args) {

        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_PURPLE = "\u001B[35m";

        try (ServerSocket serverSocket = new ServerSocket(1337);
             Socket socketServer = new Socket("localhost", 1338)) {
            System.out.println(ANSI_PURPLE +
                    "Listening on port 1337. Waiting for connection...\n" +
                    ANSI_RESET);

            Socket clientSocket = serverSocket.accept();
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            System.out.println(ANSI_GREEN +
                    "Connection from client established!" +
                    ANSI_RESET);

            DataOutputStream dos = new DataOutputStream(socketServer.getOutputStream());

            while (true) {
                // Receive data from client
                String receivedPublicKey = dis.readUTF();
                System.out.println(ANSI_PURPLE + "Received public key:\n"
                        + ANSI_GREEN + receivedPublicKey);

                String receivedSignature = dis.readUTF();
                System.out.println(ANSI_PURPLE + "Received signature:\n"
                        + ANSI_GREEN + receivedSignature);

                String receivedMessage = dis.readUTF();
                System.out.println(ANSI_PURPLE + "Received message:\n"
                        + ANSI_GREEN + receivedMessage);

                // Optionally modify the signature
                System.out.print(ANSI_PURPLE + "Do you want to modify signature? (y/n): " + ANSI_RESET);
                Scanner scanner = new Scanner(System.in);
                char choice = scanner.next(".").charAt(0);
                scanner.nextLine();

                String modifiedSignature = receivedSignature;
                if (choice == 'y') {
                    System.out.println(ANSI_PURPLE + "Enter new signature:" + ANSI_RESET);
                    modifiedSignature = scanner.nextLine();
                }

                // Send (modified) data to the server
                dos.writeUTF(receivedPublicKey);
                dos.writeUTF(modifiedSignature);
                dos.writeUTF(receivedMessage);

                System.out.println(ANSI_GREEN + "Data was send to the server.\n" + ANSI_RESET);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}