package lesson_3_Collection;

import java.util.ArrayList;

public class PhoneDir {
    private final ArrayList<String> name = new ArrayList<>();
    private final ArrayList<String> phone = new ArrayList<>();

    public void setCaller (String _name,String _phone){
        //Блок try сдесь просто - как напоминание...
        try{
           Long num = Long.parseLong(_phone);
        }catch (NumberFormatException e){
            System.out.println("В номере телефона должны быть цифры!");
        }
        name.add(_name);
        phone.add(_phone);
    }
    public void getCallerByName(String _name){
        for (int i = 0; i < name.size(); i++) {
            if(name.get(i).equals(_name)){
                System.out.println(name.get(i)+" "+phone.get(i));
            }
        }

    }



}
