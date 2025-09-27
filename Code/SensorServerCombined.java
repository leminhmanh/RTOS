import java.io.*;
import java.net.*;
import java.util.concurrent.Semaphore;

public class SensorServerCombined {
    private static final Semaphore semaphore = new Semaphore(2);

    public static void main(String[] args) {
        int port = 12345;
        String host = "127.0.0.1";

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(host))) {
            System.out.println("Server dang cho ket noi tai " + host + ":" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> {
                    try {
                        semaphore.acquire();

                        try (
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                        ) {
                            String input;
                            while ((input = in.readLine()) != null) {
                                input = input.trim();
                                if (!input.isEmpty()) {
                                    System.out.println("Nhan du lieu tu sensor: " + input);
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Loi khi doc du lieu: " + e.getMessage());
                        } finally {
                            clientSocket.close();
                        }
                    } catch (InterruptedException | IOException e) {
                        System.out.println("Loi khi xu ly ket noi: " + e.getMessage());
                    } finally {
                        semaphore.release();
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Khong the khoi dong server: " + e.getMessage());
        }
    }
}
