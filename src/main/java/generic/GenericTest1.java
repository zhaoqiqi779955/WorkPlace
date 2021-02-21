package generic;
import java.util.*;

public class GenericTest1 {
    public static <E extends Integer> boolean isEquivalent(E a,E b){
        return  a.equals(b);
    }

    public static void main(String[] args) {
        System.out.println(GenericTest1.isEquivalent(100,10));
        List<Geometric> list=new ArrayList<Geometric>();
        list.add(new Geometric(10));
        list.add(new Geometric(5));
        list.add(new Geometric(36));
        for (Geometric geometric : list) {
            System.out.println(geometric);
        }
        Geometric.sort(list);
        for (Geometric geometric : list) {
            System.out.println(geometric);
        }
    }
}
class Geometric implements Comparable<Geometric>{

    double area;
    public int compareTo(Geometric o) {
        if(this.area>o.getArea()) return 1;
        else if (this.getArea()==o.getArea()) return 0;
        else return -1;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public Geometric(double area) {
        this.area = area;
    }

    //使用泛型方法限制
    public static <E extends Geometric> void sort(List<E> list)
    {
        Collections.sort(list);
    }
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Geometric{");
        sb.append("area=").append(area);
        sb.append('}');
        return sb.toString();
    }
}
