package lectureNotes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.*;

public class InterfaceExecutor {}

/*
    Интерфейс Executor#
    Интерфейс Executor определяет один метод для запуска на выполнение задачи интерфейса Runnable.

    Executor может выполнять асинхронные задачи и управлять пулом потоков, поэтому нет необходимости в явном
    создании потоков.

    ExecutorService#
    Интерфейс ExecutorService расширяет интерфейс Executor и предоставляет методы, возвращающие экземпляр класса
    интерфейса Future, который может контролировать процесс выполнения одной или более асинхронных задач.

    Добавление задач#
    Для исполнения задач в интерфейсе ExecutorService есть следующие методы:
        execute(Runnable runnable) - метод ничего не возвращает и не дает возможности узнать результат выполнения
        задачи. Принимает объект типа Runnable;

        submit(Callable callable) - принимает объект типа Callable, и возвращает объект типа Future;

        invokeAny(Collection<? extends Callable<T>> tasks) - принимает коллекцию Callable, и возвращает результат
        исполнения первой успешно выполнившейся задачи (если такая задача будет);

        invokeAll(Collection<? extends Callable<T>> tasks) - принимает коллекцию Callable, и возвращает коллекцию Future.

    Прерывание задач#
    Выполнение задач экземпляра ExecutorService может быть прервано одним из двух методов:
        shutdown() - он позволит закончить выполение ранее стартовавших задач;
        shutdownNow() - предотвращает запуск ожидающих выполнения задач и пытается остановить уже выполняющиеся задачи.

    ScheduledExecutorService#
    Интерфейс ScheduledExecutorService расширяет интерфейс ExecutorService, предоставляя методы для выполнения задач
    с указанным интервалом и с нужной периодичностью.

    Доступны следующие методы:
        <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) - выполняет задачу с
        указанной задержкой, возвращая результат типа ScheduledFuture.

        ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) - аналогично предыщущему методу
        выполняет задачу с указанной задержкой, но поскольку передаем Runnable, то не получим никакого посчитанного
        значения в ответ.

        ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
        - запускает указанную задачу с задержкой initialDelay и периодичностью period указанных временных единиц
        TimeUnit. Задача будет периодически исполняться каждые period единиц времени, независимо от длительности
        исполнения самой задачи.

        ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
        - запускает указанную задачу с задержкой initialDelay и периодичностью period указанных временных единиц
        TimeUnit. Задача будет периодически исполняться каждые period единиц времени после завершения работы задачи.
        То есть, когда задача завершилась, отсчитывается period единиц времени, и только тогда задача снова запускается.

    Вот пример, который на протяжении часа (3600 секунд) выводит в консоль текст beep:
*/
    class BeeperControl {
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        public void beepForAnHour() {
            final Runnable beeper = new Runnable() {
                public void run() {
                    System.out.println("beep");
                }
            };

            final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);

            scheduler.schedule(new Runnable() {
                public void run() {
                    beeperHandle.cancel(true);
                }
            }, 60 * 60, SECONDS);
        }

        public static void main(String[] args) {
            BeeperControl test = new BeeperControl();
            test.beepForAnHour();
        }
    }
/*
    В примере выше с периодичностью в 10 секунд запускается регулярная задача beeper. Дальше, для этого же экземпляра
    scheduler, запускается отложенная на час (3600 секунд - 60*60) задача, которая отменяет предыдущую задачу.

    ThreadPoolExecutor#
    Класс ThreadPoolExecutor представляет собой реализацию пула потоков. Это удобно, если у нас есть много задач,
    которые мы хотим исполнять одновременно, но не хотим заботиться о ручной работе с потоками.

    Мы можем в таком случае использовать ThreadPoolExecutor в таком случае, и для нас это будет выглядеть как некий
    черный ящик, куда мы посылаем на исполнение задачи.

    Обычно для создания объектов типа ThreadPoolExecutor рекомендуется использовать фабричные методы класса Executors.
    Вот некоторые из них:
        ExecutorService newCachedThreadPool() - метод предоставляет неограниченный пул потоков с атоматическим
        переиспользованием потоков;

        ExecutorService newFixedThreadPool(int nThreads) - метод предоставляет пул потоков фиксированного размера;

        newSingleThreadExecutor() - метод предоставляет исполнитель с одним фоновым потоком.

    Поскольку методы, описанные выше, возвращают объекты типа ExecutorService, дальнейшая работа с ними аналогична тому,
    что мы рассматривали выше - конструируем объекты типа Runnable и Callable и передаем их на исполнение.
 */