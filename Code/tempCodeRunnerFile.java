import java.io.*;
import java.net.*;
import java.util.concurrent.Semaphore;

public class SensorServerCombined {
    private static int countMessages = 0;
    private static final Semaphore semaphore = new Semaphore(2);

    public static synchronized void tangDem() {
        countMessages++;
    }

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
                                tangDem();

                                int temp = -1;
                                int gas = -1;

                                String[] parts = input.split(" ");
                                for (String part : parts) {
                                    if (part.startsWith("TEMP:")) {
                                        try {
                                            temp = Integer.parseInt(part.substring(5));
                                        } catch (NumberFormatException e) {
                                            System.out.println("Gia tri nhiet do khong hop le: " + part);
                                        }
                                    } else if (part.startsWith("GAS:")) {
                                        try {
                                            gas = Integer.parseInt(part.substring(4));
                                        } catch (NumberFormatException e) {
                                            System.out.println("Gia tri khi gas khong hop le: " + part);
                                        }
                                    }
                                }

                                System.out.print("Nhan du lieu tu sensor: " + input + " | Tong tin nhan: " + countMessages);

                                // LUÔN kiểm tra cảnh báo mỗi lần
                                boolean canhBao = false;
                                if (temp > 50) {
                                    System.out.print(" | CANH BAO: Nhiet do qua cao");
                                    canhBao = true;
                                }
                                if (gas > 8500) {
                                    System.out.print(" | CANH BAO: Khi gas nguy hiem");
                                    canhBao = true;
                                }

                                if (!canhBao) {
                                    System.out.print(" | OK");
                                }

                                System.out.println(); // Xuống dòng
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
