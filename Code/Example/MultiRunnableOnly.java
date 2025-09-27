class Task1 implements Runnable {
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
                Thread.sleep(1200); // Dừng 1.2 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Task 2 hoàn thành.");
    }
}

class Task3 implements Runnable {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("🔔 Task 3 - Step " + i);
            try {
                Thread.sleep(800); // Dừng 0.8 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Task 3 hoàn thành.");
    }
}

public class MultiRunnableOnly {
    public static void main(String[] args) {
        System.out.println("🚦 Bắt đầu chạy 3 task song song...");

        Thread t1 = new Thread(new Task1());
        Thread t2 = new Thread(new Task2());
        Thread t3 = new Thread(new Task3());

        t1.start();
        t2.start();
        t3.start();

        // (Tuỳ chọn) Chờ các thread kết thúc trước khi main kết thúc
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("🏁 Tất cả các task đã hoàn thành. Main kết thúc.");
    }
}
