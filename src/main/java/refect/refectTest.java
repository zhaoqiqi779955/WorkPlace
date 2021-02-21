package refect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class refectTest {
    public static void main(String[] args) {

        Student student;
        Class<?> clazz=Student.class;
        {
            try {
                System.out.println(clazz.getName());
                //获取class对象的无参构造函数
                Constructor<?> constructor= clazz.getConstructor(null);
                //创建对象
                Object obj= constructor.newInstance();
//                获取show方法
                Method m=clazz.getMethod("show", String.class, int.class);
//                获取字段（变量）name
                Field field=clazz.getDeclaredField("name");
                field.set(obj,"王五");
                m.invoke(obj,"hello",20);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }
}

