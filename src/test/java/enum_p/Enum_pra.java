package enum_p;

import org.junit.Test;


public class Enum_pra {

    @Test
    public void test1()
    {
        Enum_pra pra=new Enum_pra();
        pra.showSize(Size.MIDDLE);
    }
    @Test
    public void test2()
    {
      Size [] sizes=Size.values();
        for (Size size : sizes) {
            System.out.println(size);
        }
    }
    
    void showSize(Size size)
    {
//        switch (size)
//        {
//            case BIG:
//                System.out.println("大号");
//                break;
//            case SMALL:
//                System.out.println("小号");
//                break;
//            case MIDDLE:
//                System.out.println("中号");
//                break;
//        }
        System.out.println(size.getName());

    }

}
enum Size
{
    BIG("大号"),MIDDLE("中号"),SMALL("小号");
    String name;
    Size(String name)
    {
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}