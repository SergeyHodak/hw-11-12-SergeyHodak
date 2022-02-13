package hw11and12v1;

/*
    Напишите программу, которая каждую секунду отображает на экране данные о времени,
    прошедшем от начала сессии (запуска программы).

    Другой ее поток выводит каждые 5 секунд сообщение "Прошло 5 секунд".
    Предусмотрите возможность ежесекундного оповещения потока, воспроизводящего сообщение, потоком, отсчитывающим время.
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Task1 {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2); // потоки

    public void beepForAnHour() {
        long start = System.currentTimeMillis(); // время старта запуска программы

        final Runnable passedIn1Second = new Runnable() {
            public void run() {
                System.out.println(System.currentTimeMillis() - start); // сколько прошло времени от старта программы
                //System.out.println("Прошла 1 секунда");
            }
        };

        final Runnable fiveSecondsHavePassed = new Runnable() {
            public void run() {
                System.out.println("Прошло 5 секунд");
            }
        };

        scheduler.scheduleAtFixedRate(passedIn1Second, 1, 1, SECONDS); // 1`й поток
        scheduler.scheduleAtFixedRate(fiveSecondsHavePassed, 5, 5, SECONDS); // 2`й поток

    }

    public static void main(String[] args) {
        new Task1().beepForAnHour();
    }
}