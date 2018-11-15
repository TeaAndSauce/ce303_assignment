package testing;

import java.util.Timer;
import java.util.TimerTask;

class TimerTesting
{
    public static void main(String[] args) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("..world!");
            }
        };
        System.out.println("Hello..");
        timer.schedule(task, 5000);
    }
}
