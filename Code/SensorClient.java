import java.io.*;
import java.net.*;
import java.util.Random;

class SensorThread extends Thread {
    private final String sensorName;
    private final int intervalMillis;
    private Socket socket;
    private PrintWriter out;

    public SensorThread(String sensorName, int intervalMillis, String serverAddress, int serverPort) {
        this.sensorName = sensorName;
        this.intervalMillis = intervalMillis;
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Khong the ket noi den Server tu " + sensorName);
        }
    }

    private int getRandomValue(String sensor) {
        Random rand = new Random();
        switch (sensor) {
            case "DHT11": return 20 + rand.nextInt(16);     // 20 - 35 do C
            case "MQ2": return rand.nextInt(101);           // 0 - 100 (gas)
            default: return 0;
        }
    }

    public void run() {
        if (socket == null || out == null) return;
        try {
            for (int i = 0; i < 5; i++) {
                int value = getRandomValue(sensorName);
                String message = sensorName + ": " + value;
                System.out.println("Gui du lieu -> " + message);
                out.println(message);
                Thread.sleep(intervalMillis);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Loi khi dong socket: " + e.getMessage());
            }
        }
    }
}

public class SensorClient {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";     
        int serverPort = 12345;

        // Chi con 2 cam bien
        SensorThread dht11 = new SensorThread("DHT11", 2000, serverAddress, serverPort); // moi 2 giay
        SensorThread mq2 = new SensorThread("MQ2", 1000, serverAddress, serverPort);     // moi 1 giay

        dht11.start();
        mq2.start();
    }
}
