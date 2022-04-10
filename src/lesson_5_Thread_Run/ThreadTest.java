package lesson_5_Thread_Run;

import java.util.Arrays;

public class ThreadTest {
    static final int SIZE = 10_000_000;
    static final int HALF = SIZE/2;

    public void firstMethod(){
        float[] arr = new float[SIZE];
        Arrays.fill(arr, 1.0f);
        long a = System.currentTimeMillis();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) *
                    Math.cos(0.4f + i / 2));
        }
        System.out.println("time method 1: "+(System.currentTimeMillis() - a));
    }
    public void secondMethod() throws InterruptedException {
        float[] arr = new float[SIZE];
        Arrays.fill(arr, 1.0f);
        long b = System.currentTimeMillis();
        float[] arr1 = new float[HALF];
        float[] arr2 = new float[HALF];
        System.arraycopy(arr, 0, arr1,0, HALF);
        System.arraycopy(arr, HALF, arr2,0, HALF);

        //создал 2 отдельных потока, можно было бы объединить методом, оставил так для наглядности
//вычисление первого потока
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < arr1.length; i++) {
                arr1[i] = (float)(arr1[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) *
                        Math.cos(0.4f + i / 2));
            }
        });
//вычисление второго потока
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < arr2.length; i++) {
                arr2[i] = (float)(arr2[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) *
                        Math.cos(0.4f + i / 2));
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        float[] mergedArr = new float[SIZE];

        System.arraycopy(arr1, 0, mergedArr,0, HALF);
        System.arraycopy(arr2, 0, mergedArr, HALF, HALF);

        System.out.println("time method 2: "+(System.currentTimeMillis() - b));
    }


    public static void main(String[] args) throws InterruptedException {
        ThreadTest t = new ThreadTest();
        t.firstMethod();
        t.secondMethod();

    }

}
