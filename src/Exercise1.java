import org.w3c.dom.ls.LSOutput;

import java.util.Scanner;
import java.util.concurrent.*;

public class Exercise1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //Основные переменные для инпута
        int a,b;
        String operator;
        Scanner scan = new Scanner(System.in);

        ExecutorService serv = Executors.newFixedThreadPool(1);

//////////////////////////////////////////////////////////////
        while(true){
            try{
                a=scan.nextInt();
                b=scan.nextInt();
                break;
            }catch(Exception e){
                if(scan.hasNext()){
                    scan.nextLine();
                }
                System.out.println("Неверный ввод");
            }
        }
        exitWhile:
        while(true){
            operator=scan.next();
            switch (operator){
                case "+":
                    whatThread();
                    System.out.println("Сумма двух чисел равна "+plus(serv,a,b));
                    break;
                case "-":
                    whatThread();
                    System.out.println("Разность двух чисел равна "+minus(serv,a,b));
                    break;
                case "*":
                    whatThread();
                    System.out.println("Произведение двух чисел равно "+mult(serv,a,b));
                    break;
                case "/":
                    whatThread();
                    System.out.println("Частное двух чисел равно "+div(serv,a,b));
                    break;
                case "%":
                    whatThread();
                    System.out.println("Остаток от деления А на Б равен "+divLeft(serv,a,b));
                    break;
                case "==":
                    whatThread();
                    if(isEqual(serv,a,b))
                        System.out.println("Числа равны");
                    else
                        System.out.println("Числа не равны");
                    break;
                case "<":
                    whatThread();
                    if(less(serv,a,b))
                        System.out.println("Да, А меньше Б");
                    else
                        System.out.println("Нет, А не меньше Б");
                    break;
                case ">":
                    whatThread();
                    if(more(serv,a,b))
                        System.out.println("Да, А больше Б");
                    else
                        System.out.println("Нет, А не больше Б");
                    break;
                case "0":
                    whatThread();
                    System.out.println("До свидания");
                    break exitWhile;
                default:
                    System.out.println("Неверный ввод, для выхода нажмите 0");
            }
            if(scan.hasNext()){
                scan.nextLine();
            }
        }
    }
    //функции вычисляющие каждая в своём потоке
    static int plus(ExecutorService serv,int a, int b) throws ExecutionException, InterruptedException {
        Future<Integer> future= serv.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return a + b;
            }
        });
        return Integer.valueOf(future.get());
    }
    static int minus(ExecutorService serv,int a, int b) throws ExecutionException, InterruptedException {
        Future<Integer> future= serv.submit(() -> {
            return a - b;
        });
        return Integer.valueOf(future.get());
    }
    static int mult(ExecutorService serv,int a, int b) throws ExecutionException, InterruptedException {
        Future<Integer> future= serv.submit(() -> a*b);
        return Integer.valueOf(future.get());
    }
    static int div(ExecutorService serv,int a, int b) throws ExecutionException, InterruptedException {
        Future<Integer> future= serv.submit(() -> a/b);
        return Integer.valueOf(future.get());
    }
    static int divLeft(ExecutorService serv,int a, int b) throws ExecutionException, InterruptedException {
        Future<Integer> future= serv.submit(() -> a%b);
        return Integer.valueOf(future.get());
    }
    static boolean isEqual(ExecutorService serv,int a, int b) throws ExecutionException, InterruptedException {
        Future<Boolean> future= serv.submit(() -> a==b);
        return future.get();
    }
    static boolean more(ExecutorService serv,int a, int b) throws ExecutionException, InterruptedException {
        Future<Boolean> future= serv.submit(() -> a>b);
        return future.get();
    }
    static boolean less(ExecutorService serv,int a, int b) throws ExecutionException, InterruptedException {
        Future<Boolean> future= serv.submit(() -> a<b);
        return future.get();
    }

    static void whatThread(){
        System.out.print(Thread.currentThread().getName()+" поток говорит: ");
    }
}
