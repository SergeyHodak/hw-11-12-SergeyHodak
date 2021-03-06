package lectureNotes;

public class SynchronizationBasics {}

/*
    Основы синхронизации#
    Правильная реализация многопоточности - одна из наиболее сложных задач в программировании.
    Основная сложность заключается именно в синхронизации данных. Разные потоки могут одновременно читать и изменять
    одни и те же данные. Это приводит к непредсказуемым эффектам. Чтобы избежать этих проблем, в Java есть специальные
    средства синхронизации. Основная идея в том, что на какое-то время мы делаем данные доступными для изменения лишь
    одному потоку. Правильная синхронизация - это сложно. В этом разделе мы затронем лишь основы синхронизации.

    Ключевое слово synchronized#
    Если у класса есть состояние (например, есть поля) и есть методы, изменяющие это состояние, то этот класс может
    являться не потокобезопасным.
    Например, рассмотрим такой класс:
*/
    class BankAccount {
        private int card1Balance;
        private int card2Balance;

        public void change(int amount) {
            card1Balance -= amount;
            card2Balance += amount;
        }
    }
/*
    В этом классе есть две переменные card1Balance и card2Balance. Метод change() у переменной card1Balance отнимает
    значение amount, а переменной card2Balance добавляет это же значение.

    Проблема здесь в том, что если два разных потока одновременно начнут работать с одним и тем же объектом класса
    BankAccount, то переменные card1Balance и card2Balance начнут изменяться разными потоками. Это приведет к
    несогласованному изменению этих переменных.

    Наиболее простой способ решить эту проблему - сделать метод синхронизированным. В таком случае в один момент
    времени лишь один поток сможет работать с таким методом:
*/
    class BankAccount1 {
        private int card1Balance;
        private int card2Balance;

        public synchronized void change(int amount) {
            card1Balance -= amount;
            card2Balance += amount;
        }
    }
/*
    Работает это так. Каждый экземпляр класса обладает встроенным монитором (lock). Первому перешедшему к
    синхронизированному коду потоку передается этот монитор. После завершения выполнения этого кода монитор
    освобождается, и другой поток может захватить "контроль".

    Если нам нужно синхронизировать не весь метод, а лишь критичный блок, можно применить синхронизацию к нужному блоку:
*/
    class BankAccount2 {
        private int card1Balance;
        private int card2Balance;

        public void change(int amount) {
            synchronized(this) {
                card1Balance -= amount;
                card2Balance += amount;
            }
        }
    }
/*
    В таком случае в блоке synchronized в круглых скобках нам нужно указать объект синхронизации.

    Статические методы также могут быть синхронизированы
*/
    class ClassName {
        public static void synchronizedMethodName() {
            synchronized(ClassName.class) {
                //code...
            }
        }
    }
/*
    Если два потока одновременно исполняют несинхронизированный метод, то каждый поток будет иметь в этом методе свой
    набор локальных переменных.

    ВНИМАНИЕ
    Потокобезопасным (thread-safe) называется код, в котором доступ к статическим полям осуществляется из
    синхронизированных статических методов, а доступ к нестатическим полям - из нестатических синхронизированных методов.

    Взаимная блокировка#
    Deadlock (взаимная блокировка) - это ситуация, при которой каждый из потоков находится в состоянии бесконечного
    ожидания освобождения ресурсов, которые захватил другой ресурс. Другими словами, потоки никогда не получат
    ожидаемого освобождения блокировок. Обычно это ошибка проектирования архитектуры. Здесь эта ошибка приводится
    как пример подводных камней, которые могут вас ожидать.
                     ------------------------------------------------------------------------
                                 Figure-1                                   Figure-2

                                 Thread X                                   Thread X
                                 |      |                                   |      |
                                 |      v                                   |      v
                                 v                                        ( v )    :  Waiting
                                 :                                Locked (     )
                      Resource   o      o   Resource                      ( o )  ( o )
                         A              :      B                   Waiting  :   (     ) Locked
                                        ^                                   :    ( ^ )
                                 ^      |                                   ^      |
                                 |      |                                   |      |
                                 Thread X                                   Thread X
                     ------------------------------------------------------------------------
                                                Взаимная блокировка

    Ключевое слово volatile#
    Все данные приложения размещаются в основном хранилище данных (оперативная память). При запуске нового потока
    создается копия данных, используемых этим потоком, которая размещается в буфере (процессорная память). Сделанные
    потоком изменения вносятся в копию данных (в буфер) и не имеют немедленного отображения в основном хранилище.

    То есть, когда один поток изменил какую-то переменную, то другой поток не сразу увидит изменение этой переменной.
    Это сделано для оптимизации производительности.

               |-------------------------------------------------------------------------------------|
               |                                      Main Memory                                    |
               |                                                                                     |
               |        __________       __________    __________    __________       __________     |
               |       | Variable | ... | Variable |  | Variable |  | Variable | ... | Variable |    |
               |        ----------       ----------    ----------    ----------       ----------     |
               |_____________________________________________________________________________________|
                               ^                           ^                           ^
                               |                           |                           |
                           Load/Save                   Load/Save                   Load/Save
                               |                           |                           |
                               v                           v                           v
                       |----------------|          |----------------|          |----------------|
                       |    V ... V     |          |    V ... V     |          |    V ... V     |
                       | Working Memory |          | Working Memory |          | Working Memory |
                       |                |          |                |          |                |
                   |---|----------------|----------|----------------|----------|----------------|---|
                   |   |     Thread     |   ...    |     Thread     |    ...   |     Thread     |   |
                   |   |________________|          |________________|          |________________|   |
                   |                                                                                |
                   |                                 Thread Engine                                  |
                   |________________________________________________________________________________|


    Если же нам нужно, чтобы все изменения переменных сразу были видны всем потокам, то для этого используется
    ключевое слово volatile. Значение переменной, помеченной этим словом, будет видно всем потокам сразу после изменения
    этой переменной.

    ВНИМАНИЕ
    volatile обеспечивает синхронизацию данных, но применим только к атомарным операциям. volatile не может сделать
    код полностью потокобезопасным.
 */