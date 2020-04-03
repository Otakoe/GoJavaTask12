import org.w3c.dom.ls.LSOutput;

import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
        //оптимальное число потоков после которого для этой задачи прирост в производительности незначителен, или наоборот хуже и ошибки в вещественном числе растут.
        int numOfThreads = 16;
        int arr[] = createArray(80000000);
        int N;
        Scanner scan = new Scanner(System.in);
        try {
            N = scan.nextInt();
            ThreadMode(arr, N, numOfThreads);
            //ThreadPoolMode(arr,N,numOfThreads);
        }catch (Exception e){
            System.out.println("Wrong input");
        }
    }

    static void ThreadPoolMode (int arr[],int N,int numOfThreads){
        ExecutorService serv1 = Executors.newFixedThreadPool(N);
        FutureTask<Double> future = new FutureTask<Double>(()->{
            double result=0.0;

           return result;
        });




        for (int i =0;i<N;i++){
            serv1.submit(future);
        }
    }


    static void ThreadMode(int arr[], int N,int numOfThreads) throws InterruptedException {
        long timeOnStart = System.currentTimeMillis();
        Runnable run = () -> {
            AtomicDouble sum = new AtomicDouble();
            int normalThreadCount = Thread.activeCount(); //нужно для костыля
            System.out.println(Thread.activeCount());
            //создаём фиксированное количество потоков
            for (int t = 0; t < numOfThreads; t++) {
                int tt = t;
                Thread threadInside = new Thread(() -> {
                    //каждому потоку пропорционально свой кусок массива
                    double sumInThread = 0;
                            //System.out.println(arr.length/numOfThreads*tt+" do "+arr.length/numOfThreads*(tt+1)); //для дебага, показывает какой отрезок массива какой поток получил
                    for (int i = arr.length / numOfThreads * tt; i < arr.length / numOfThreads * (tt + 1); i++) {
                        int it = i;
                        sumInThread += calc(it);
                    }
                            //System.out.print(Thread.currentThread().getName()+" "+sum.get()+ " " +sumInThread+" "); //для дебага, показывает сколько было в атомарном дубле и сколько насчитал поток.
                    sum.set(sum.get() + sumInThread);
                });
                threadInside.start();
            }
            //костыль, чтобы вся программа подождала завершение потоков в форе
            while (Thread.activeCount() > normalThreadCount) {
            }
            System.out.println("сумма результатов = " + sum.get());
        };

        for (int i=0;i<N;i++) {
            Thread threadN = new Thread(run);
            System.out.println("Итерация № "+i);
            threadN.start();
            threadN.join();
        }
        System.out.println("Программа в режиме thread затратила = " + (System.currentTimeMillis() - timeOnStart)/1000.0+" сукунд");
    }
//операция одного вычисление (на 1 000 000 итераций 78мс)
    static double calc ( double x){
            return Math.sin(x) + Math.cos(x);
        }
//создание большого массива (172мс)
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

