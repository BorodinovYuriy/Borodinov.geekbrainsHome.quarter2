package lesson_2_Exception;
/**
 * Метод checkArrAndSum проверяет корректность массива на размерность и ввод
 *В классе main создал экземпляр класса ExepArrMain
 * и выполнил всё в блоке try-catch(что бы не падало в ошибку)
 */

public class ExcepArrMain {
    public static void main(String[] args) {
        String[][] arr1 = {
                {"1", "2", "3", "4"},
                {"5", "6", "7", "8"},
                {"9", "0", "1", "2"},
                {"3", "4", "5", "6"},
        };

        ExcepArrMain m = new ExcepArrMain();

        try{
        m.checkArrAndSum(arr1);
        }catch (MyArraySizeException | MyArrayDataException e){
            System.out.println("ups!");
            System.out.println(e.getMessage());

        }
    }

    public void checkArrAndSum(String[][] arr) throws MyArraySizeException,MyArrayDataException{
        if(arr.length != 4) throw new MyArraySizeException("Неверная длинна массива");
        for (String[] strings : arr) {
            if (strings.length != 4) throw new MyArraySizeException("Неверная длинна строк массива");

        }
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                try{
                    sum += Integer.parseInt(arr[i][j]);
                }catch (NumberFormatException e){
                    throw new MyArrayDataException(String.format(
                            "Ошибка формата!  в %d-й строке, %d-м столбце", i + 1, j + 1));
                }
            }
        }
        System.out.println(sum);
    }

    static class MyArraySizeException extends Exception {
    public MyArraySizeException(String message) {
            super(message);
        }
    }
    static class MyArrayDataException extends Exception {
        public MyArrayDataException(String message) {
            super(message);
        }
    }


}
