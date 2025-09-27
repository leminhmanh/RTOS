class MyThread extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("🔹 Thread 1 đang chạy: " + i);
            try {
                Thread.sleep(1000); // Tạm dừng 1 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Thread 1 kết thúc.");
    }
}

public class ThreadExample1 {
    public static void main(String[] args) {
        MyThread t1 = new MyThread(); // Tạo Thread
        t1.start(); // Chạy Thread

        // Main thread cũng làm gì đó song song (tùy chọn)
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
