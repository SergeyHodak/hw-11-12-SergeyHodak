package hw11and12v1;

/*
    Напишите программу, которая каждую секунду отображает на экране данные о времени,
    прошедшем от начала сессии (запуска программы).

    Другой ее поток выводит каждые 5 секунд сообщение "Прошло 5 секунд".
    Предусмотрите возможность ежесекундного оповещения потока, воспроизводящего сообщение, потоком, отсчитывающим время.
 */


public class Task1 {
    public static void main(String[] args) {
        Thread fiveSeconds = new Thread(() -> { // поток оповещающий каждые5 секунд
            while (true) { // бесконечный цикл
                synchronized (Thread.currentThread()) { // синхронизированный блок для майн
                    try {
                        Thread.currentThread().wait(); // остановка потока
                    } catch (InterruptedException e) {
                        e.printStackTrace(); // исключение
                    }
                }
                System.out.println("Прошло 5 секунд");
            }
        });
        fiveSeconds.start(); // запустили поток


        Thread oneSecond = new Thread(() -> { // поток оповещающий каждую секунду
            int count = 0; // счетчик
            long starts = System.currentTimeMillis(); // время старта запуска программы

            while (true) { // бесконечный цикл
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(System.currentTimeMillis() - starts); // в консоль, сколько прошло от запуска миллисекунд
                //System.out.println("Прошла 1 секунда");
                count++; // повысить счетчик

                if (count == 5) { // если счетчик насчитал 5 секунд
                    count = 0; // сбросить счетчик
                    synchronized (fiveSeconds) {
                        fiveSeconds.notify();
                    }
                }
            }
        });
        oneSecond.start(); // запустили поток
    }
}