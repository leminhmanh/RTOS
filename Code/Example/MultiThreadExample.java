class Task1 extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("🌟 Task 1 - Step " + i);
            try {
                Thread.sleep(1000); // Dừng 1 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Task 1 hoàn thành.");
    }
}

class Task2 implements Runnable {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("🚀 Task 2 - Step " + i);
            try {
                Thread.sleep(1000); // Dừng 1 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Task 2 hoàn thành.");
    }
}

public class MultiThreadExample {
    public static void main(String[] args) {
        System.out.println("🚦 Bắt đầu chạy đa luồng...");

        Task1 t1 = new Task1();                  // Tạo Thread từ Task1
        Thread t2 = new Thread(new Task2());     // Tạo Thread từ Task2

        t1.start(); // Chạy Task1
        t2.start(); // Chạy Task2

        // Có thể thêm logic cho main thread nếu cần
        for (int i = 1; i <= 5; i++) {
            System.out.println("🔧 Main thread đang chạy: Bước " + i);
            try {
                Thread.sleep(900); // Dừng 0.9 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Main thread hoàn thành.");
    }
}
