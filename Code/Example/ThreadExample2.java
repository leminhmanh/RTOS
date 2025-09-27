class MyRunnable implements Runnable {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("🔸 Thread 2 đang chạy: " + i);
            try {
                Thread.sleep(1000); // Tạm dừng 1 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Thread 2 kết thúc.");
    }
}

public class ThreadExample2 {
    public static void main(String[] args) {
        Thread t2 = new Thread(new MyRunnable()); // Tạo Thread từ Runnable
        t2.start(); // Chạy Thread

        // Main thread chạy song song
        for (int i = 1; i <= 5; i++) {
            System.out.println("➡️ Main thread đang chạy: " + i);
            try {
                Thread.sleep(1200); // Tạm dừng 1.2 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Main thread kết thúc.");
    }
}
