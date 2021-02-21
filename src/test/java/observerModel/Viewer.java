package observerModel;

import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

public class Viewer implements Observer {
//    编号
    int no;
    @Override
    public void update(Observable o, Object arg) {

        if(arg==null){
            System.out.println(no+":热烈欢迎");
        }
       else System.out.println(no+":"+arg);
    }

    public Viewer(int no) {
        this.no = no;
    }
}
class Actor extends  Observable
{
    public void showOn()
    {
        setChanged();
        notifyObservers();
    }
    public void exit(Result result)
    {
        setChanged();
        notifyObservers(result);
    }
}
enum Result
{
    GOOD("好"),BAD("差"),MORMAL("一般");
    String value;

    Result(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
