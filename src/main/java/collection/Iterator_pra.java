package collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class Iterator_pra {
    public static void main(String[] args) {
        List<String> list=new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        Iterator<String> iterator=list.iterator();
//        while (iterator.hasNext()){
//            String h=(String) iterator.next();
//            System.out.println(h);
//            if(h.equals("2"))
//                iterator.remove();
//        }
//        for(String s:list){
//            System.out.println(s);
//        }
        iterator.forEachRemaining(new Consumer<String>() {
            @Override
            public void accept(String s) {
                print(s);
            }
        });
        iterator=list.iterator();
        iterator.forEachRemaining(e->{
            print(e);
        });
    }
    public static void print(String s)
    {
        System.out.println(s);
    }
}
