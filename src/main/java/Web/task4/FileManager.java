package Web.task4;

import java.io.*;
import java.text.SimpleDateFormat;

public class FileManager {

    String pathBase;

    public FileManager(String Base) {
        pathBase = Base;
    }

    public long getFileSize(String relativePath) {//返回文件大小，若不存在文件则返回-1
        String path = pathBase + relativePath;
        System.out.println(path);
        File file = new File(path);
        if(file.exists()){
            return  file.length();
        }
        return -1;
    }
   String getLastModified(String relativePath)
   {
       String path = pathBase + relativePath;
       File file = new File(path);
       SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
       return format.format(file.lastModified());
   }
    //存储资源
    void saveResource(String relativePath, InputStream in, boolean append) {

        String path = pathBase + relativePath;
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte bufferedArray[] = new byte[4096];//创建一个缓冲数组用于加速读取内容
        long readBytes = 0;
        try (BufferedInputStream input = new BufferedInputStream(in);//使用try with resource关闭资源
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, append))) {
//
//            System.out.println("下载开始");
//            if (append) System.out.println("继续之前下载");
            while (true) {
                int x = input.read(bufferedArray);
                if (x == -1) break;
                readBytes += x;
                out.write(bufferedArray, 0, x);
                out.flush();


            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();


        }
    }

    void transferResource(String relativePath, OutputStream out,long begin) {

        String path = pathBase + relativePath;
        byte bufferedArray[] = new byte[4096];//创建一个缓冲数组用于加速读取内容
        try {
            RandomAccessFile rf = new RandomAccessFile(path, "r");
            rf.seek(begin+1);
            System.out.println("开始");
            BufferedOutputStream output = new BufferedOutputStream(out);
            try {
                int t;
                while ((t = rf.read(bufferedArray)) >0) {

                    output.write(bufferedArray, 0, t);
                    output.flush();

                }
                System.out.println("传输完成");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("传输中断");
            }


        }catch (IOException e) {
            e.printStackTrace();
        }


    }
}
