import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Exercise2 {
    /*Написать функцию которая создает массив интов размером size. Числа в массиве идут по возрастанию от 1 до size.

С помощью этой функции создайте массив с size равным 80 000 000.

Подсчет
для каждого элемента массива подсчитать result = sin(x) + cos(x), где x - итый элемент массива.
Вывести в консоль сумму всех result для всего массива. Распараллельте эту логику на потоки для ускорения вычислений.

Пользователю надо ввести N в консоль. N это кол-во раз, сколько надо повторить Подсчет. Одновременно в программе может быть запущено только вычисление одного Подсчета. Но при этом саму итерацию подсчета нужно параллелить.

Программа должна во время одного запуска отработать в двух режимах. И подсчитывать время которое она затратила на каждый из режимов работы:
Режим Thread. Программа для работы создает Thread-ы.
Режим Thread Pool. Программа использует один thread pool  единожды созданный.
Режим Thread и Thread Pool не могут работать одновременно друг с другом. Только по очереди.
*/
    public static void main(String[] args) throws InterruptedException {
        //оптимальное число потоков для режима Сред =16, после прирост в производительности незначителен, или наоборот хуже и ошибки в вещественном числе растут.
        int numOfThreads = 8;
        int divArray =16;
        int arr[] = createArray(80000000);
        int N=0;
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите число сколько раз подряд провести подсчёт");
        try {
            N = scan.nextInt();
            ThreadMode(arr, N, numOfThreads);
            ThreadPoolMode(arr,N,numOfThreads,divArray);
        }catch (Exception e){
            System.out.println("Wrong input");
        }
    }

    static void ThreadPoolMode (int arr[],int N,int numOfThreads,int divArray) throws ExecutionException, InterruptedException {
        ExecutorService service1 = Executors.newFixedThreadPool(numOfThreads+1);
        Callable<Double> callableSum = () -> {
            Double sum = 0.0;
            ArrayList<Future<Double>> futures = new ArrayList<>();
        //цикл определяет и разбивает выполнение функции по потокам, каждому свой интервал массива
        //проходимся по этому циклу создавая список будующего субмитом калабла в виде лямбды
                for (int i = 0; i < arr.length; i += arr.length / divArray) {
                    int fi = i;
                    futures.add(service1.submit(()->{
                        return calcArray(arr, fi, fi + arr.length / divArray);
                    }));
                }
        //собираем значения будующего по мере его готовности и суммируем
                for (Future<Double> future:futures)
                {
                    sum+=future.get();
                }
                return sum;
        };
        //поочерёдный вызов потоков
        long timeOnStart = System.currentTimeMillis();
        for (int i =0;i<N;i++){
            System.out.println("Итерация № "+(i+1));
            System.out.println("сумма результатов = " + service1.submit(callableSum).get());
        }
        System.out.println("Программа в режиме threadPool затратила = " + (System.currentTimeMillis() - timeOnStart)/1000.0+" сукунд");
        service1.shutdown();
    }
    //функция работает с массивом в указанном интервале
    static double calcArray(int arr[], int iBegin, int iEnd) {
        double result= 0.0;
        for (int i = iBegin;i<iEnd;i++){
            result+=calc(arr[i]);
        }
        return result;
    }

    static void ThreadMode(int arr[], int N,int numOfThreads) throws InterruptedException {
        long timeOnStart = System.currentTimeMillis();
        Runnable runnable = () -> {
            AtomicDouble sum = new AtomicDouble(0.0);
            int normalThreadCount = Thread.activeCount(); //нужно для костыля
            //создаём фиксированное количество потоков
            //каждому потоку пропорционально свой кусок массива
            for (int i = 0; i < arr.length; i+=arr.length/numOfThreads) {
                int fi = i;
                Thread threadInside = new Thread(() -> {
                    double sumInThread = calcArray(arr,fi,fi+arr.length/numOfThreads);
                    //System.out.println(fi+" do "+(fi +arr.length/numOfThreads)); //для дебага, показывает какой отрезок массива какой поток получил
                    sum.add(sumInThread);
                });

                threadInside.start();
            }
            //костыль, чтобы вся программа подождала завершение потоков в форе
            while (Thread.activeCount() > normalThreadCount) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("сумма результатов = " + sum.get());
        };

        for (int i=0;i<N;i++) {
            Thread threadN = new Thread(runnable);
            System.out.println("Итерация № "+(i+1));
            threadN.start();
            threadN.join();
        }
        System.out.println("Программа в режиме thread затратила = " + (System.currentTimeMillis() - timeOnStart)/1000.0+" сукунд");
    }

//операция одного вычисление (на 1 000 000 итераций ~78мс)
    static double calc ( double x){
            return Math.sin(x) + Math.cos(x);
        }
//создание большого массива (~172мс)
    static int[] createArray ( int size){
            int arr[] = new int[size];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = i + 1;
            }
            return arr;
        }

    static class AtomicDouble extends Number {
            private AtomicLong bits;

            public AtomicDouble() {
                this(0);
            }

            public AtomicDouble(double d) {
                bits = new AtomicLong(Double.doubleToLongBits(d));
            }

            public final double get() {
                return Double.longBitsToDouble(bits.get());
            }

            public final void set(double newValue) {
                bits.set(Double.doubleToLongBits(newValue));
            }

            public final double getAndSet(double newValue) {
                return Double.longBitsToDouble(bits.getAndSet(Double.doubleToLongBits(newValue)));
            }

            public final void add(double value){
                set(get()+ value);
            }
            @Override
            public int intValue() {
                return (int) get();
            }

            @Override
            public long longValue() {
                return (long) get();
            }

            @Override
            public float floatValue() {
                return (float) get();
            }

            @Override
            public double doubleValue() {
                return get();
            }
        }
}

