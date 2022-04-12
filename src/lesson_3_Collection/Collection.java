package lesson_3_Collection;

import java.util.*;

public class Collection {
    public static void main(String[] args) {
//1
        String[] arrStrColor ={
                "Ультрамарин", "Айвори", "Бирюза",
                "Фуксия", "Капучино", "Пудра",
                "Мята", "Аделиада", "Блакитный",
                "Вощаный ", "Гаити", "Кубовый",
                "Лани", "Ультрамарин", "Ультрамарин",
                "Ультрамарин", "Ультрамарин", "Айвори",
                "Айвори", "Айвори", "Айвори",
        };
        uniquePrintStringArr(arrStrColor);

        System.out.println();
//2
        PhoneDir myDir = new PhoneDir();

        myDir.setCaller("Васечкин","3223322");
        myDir.setCaller("Петечкин","3223322");
        myDir.setCaller("Вовочкин","88002000600");
        myDir.setCaller("Васечкин","6666666");

        myDir.getCallerByName("Васечкин");
    }
    //Метод uniquePrintStringArr получился немного читерским,
    //так как нормального элегантного способа не придумал...

    public static void uniquePrintStringArr(String[] arr){
        ArrayList<String> printList = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            String string = arr[i];
            int count = 0;
            for (int j = 1; j < arr.length; j++) {
                if (arr[i].equals(arr[j])) count++;
            }
            printList.add(arr[i] +" - встречается, раз: "+ count);
        }
        Set<String> unique = new HashSet<>(printList);
        Iterator<String> iterator = unique.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
