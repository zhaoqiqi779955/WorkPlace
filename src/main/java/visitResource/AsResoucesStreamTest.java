package visitResource;

import java.io.*;
import java.util.Properties;

public class AsResoucesStreamTest {


    public static   void writeResource()
    {
        File file=new File("src/config.properties");
        System.out.println(file.getPath());
        FileOutputStream out=null;

        try {
            out=new FileOutputStream(file);
            Properties properties=new Properties();
            properties.setProperty("name","zhangSan");
            properties.setProperty("pw","12345678");
            properties.store(out,"made by zhaoqiqi");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public   static  void  readResouce() throws IOException {
       InputStream in= AsResoucesStreamTest.class.getClass().getResourceAsStream("/resource/config.properties");
       Properties properties=new Properties();
       properties.load(in);
       System.out.println(properties.getProperty("name"));

    }
//    获取classPath以外的资源
    public  static void readExternalFile() throws IOException {
        String pt=AsResoucesStreamTest.class.getResource("/").getPath();
        File file=new File(pt,"../resource/config.properties").getCanonicalFile();
        InputStream in=new FileInputStream(file);
        Properties properties=new Properties();
        properties.load(in);
        System.out.println(properties.getProperty("name"));
        if(in!=null)
        {
            in.close();
        }
    }

    public static void main(String[] args) {
        try {
            readExternalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
