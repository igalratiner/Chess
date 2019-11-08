public class Main {
    public static void main(String[] args) {
        while(true) {
            try {
                //sending the actual Thread of execution to sleep X milliseconds
                Thread.sleep(3000);
            } catch(InterruptedException ie) {}
            System.out.println("Hello from Computer!");
        }
    }
}
