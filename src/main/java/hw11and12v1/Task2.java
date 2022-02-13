package hw11and12v1;

/*
    Напишите программу, которая выводит в консоль строку, состоящую из чисел от 1 до n, но с заменой некоторых значений:
        1) если число делится на 3 - вывести "fizz"
        2) если число делится на 5 - вывести "buzz"
        3) если число делится на 3 и на 5 - вывести "fizzbuzz"

    Например, для n = 15, ожидаемый результат:
        1, 2, fizz, 4, buzz, fizz, 7, 8, fizz, buzz, 11, fizz, 13, 14, fizzbuzz.

    Программа должна быть многопоточной, работать с 4 потоками:
        1) Поток A вызывает fizz() чтобы проверить делимость на 3 и вывести fizz.
        2) Поток B вызывает buzz() чтобы проверить делимость на 5 и вывести buzz.
        3) Поток C вызывает fizzbuzz() чтобы проверить делимость на 3 и 5 и вывести fizzbuzz.
        4) Поток D вызывает number() чтобы вывести число.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Task2 {
    public static void main(String[] args) {
        // Создадим потоки
        ProcessThread threadA = new ProcessThread((n) -> {if (n%3 == 0) {
            if (Objects.equals(ProcessThread.getResult(), "")) {
                ProcessThread.setResult("fizz");
            }}});
        ProcessThread threadB = new ProcessThread((n) -> {if (n%5 == 0) {
            if (Objects.equals(ProcessThread.getResult(), "")) {
                ProcessThread.setResult("buzz");
            }}});
        ProcessThread threadC = new ProcessThread((n) -> {if (n%3 == 0 & n%5 == 0) {
            ProcessThread.setResult("fizzBuzz");
        }});
        ProcessThread threadD = new ProcessThread((n) -> {if (n%3 != 0 && n%5 != 0) {
            if (Objects.equals(ProcessThread.getResult(), "")) {
                ProcessThread.setResult(n + "");
            }}});

        // Запихнем их в коллекцию
        List<ProcessThread> threads = new ArrayList<>();
        threads.add(threadA);
        threads.add(threadB);
        threads.add(threadC);
        threads.add(threadD);

        // Запустим все потоки
        for (ProcessThread thread : threads) {
            thread.start();
        }

        // Генератор чисел и роботы над ними
        for (int i = 1; i <= 15; i++) { // От 1 до 15
            for (ProcessThread thread : threads) { // В каждый поток
                thread.process(i); // Загнать число
            }

            while (true) { // Выполнять пока тру
                int processedCount = 0; // Счетчик процессов
                for (ProcessThread thread : threads) { // Пробежка по потокам
                    if (thread.isProcessed()) { // Если процесс завершен
                        processedCount++; // добавить в счетчик
                    }
                }
                if (processedCount == threads.size()) { // Если все процессы завершены
                    System.out.println(ProcessThread.getResult());
                    break; // выйти из цикла вайл
                }
            }
        }
        // Остановим все потоки
        for (ProcessThread thread : threads) {
            thread.stop();
        }
    }

}

interface NumberProcessor { // Интерфейс
    void process(int number); // Метод интерфейса
}

class ProcessThread extends Thread {
    private static String result = "";
    public synchronized static void setResult(String r) {result = r;}
    public static String getResult() {return result;}

    private int number; // Приватное поле (номер)
    private AtomicBoolean processed = new AtomicBoolean(true); // Начальное значение тру (завершен)
    private NumberProcessor processor; // Приватное поле интерфейсного типа

    public ProcessThread(NumberProcessor processor) { // Конструктор, принимает интерфейс тип
        this.processor = processor; // Присваивает приватному полю значение интерфейса
    }

    public void process(int number) { // Публичный метод, принимающий номер
        this.number = number; // Полю номер присвоить
        processed.set(false); // Задать значение флага, через метод класса AtomicBoolean (не завершен)
        setResult("");
    }

    public boolean isProcessed() { // Метод узнающий значение поля processed
        return processed.get(); // Взять значение
    }

    @Override // Метка переопределения для. Thread
    public void run() {
        while(true) { // Выполнять пока тру
            if (processed.get()) { // Если флаг тру
                continue; // Перейти в начало цикла
            }

            processor.process(number); // В интерфейс отправить номер

            processed.set(true); // В флаг записать тру (завершен)
        }
    }
}