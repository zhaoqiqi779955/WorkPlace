package Web.task4;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class HttpServer extends JFrame implements Runnable {
    ServerSocket serverSocket = null;
    FileManager fileManager = null;//管理文件

    public HttpServer() {
        try {
            serverSocket = new ServerSocket(8888);
            fileManager = new FileManager("Practice/resource");
        } catch (Exception e) {
            System.out.println(e);
        }


    }


    public static void main(String[] args) {

        HttpServer server = new HttpServer();
        Thread thread = new Thread(server);
        thread.start();
        System.out.println("服务器已启动");

    }

    public void run() {
        //创建线程池
        ExecutorService pool = Executors.newFixedThreadPool(100);//创建一个线程池

        while (true) {
            try {
                Socket socketAtServer = serverSocket.accept();
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                String now = formatter.format(date);
                //为每一个客户端创建一个handler来处理
                Handler handler = new Handler(socketAtServer, fileManager);//提交入线程池
                pool.submit(handler);
                Thread.sleep(100);

            } catch (Exception e) {
                System.out.println(e);
            }


        }


    }
}

class Handler implements Runnable {//用于处理每一个客户端请求
    ArrayList<String> responseContent = new ArrayList<>(); //响应信息
    Socket socket = null;
    FileManager fileManager = null;
    HashMap<String, String> requestMap = new HashMap();
    String protocol;//传输协议
    String requestType;//请求类型
    String filePath;//请求资源路径
    OutputStream out = null;
    InputStream in = null;

    public Handler(Socket soc, FileManager manager) {

        socket = soc;
        fileManager = manager;
        System.out.println("已连接主机: " + socket.getInetAddress().getCanonicalHostName()+ "端口: " + socket.getPort());
    }

    void analyseRequest(InputStream in) throws IOException {//分析请求信息

        BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
        System.out.println("请求信息:");
        boolean isFirst = true;
        String str = null;
        while ((str = reader.readLine()) != null) {
            if (str.equals("")) break;
            System.out.println(str);
            if (isFirst) {
                analyseFirstLine(str);
                isFirst = false;
            } else addRequestToMap(str);

        }

        System.out.println("接受请求完毕");
    }

    //设置Content-Length
    void setContentLength(long length) {
        responseContent.add("Content-Length: " + length);
    }

    void setCode(String code, String message) {
        responseContent.add(protocol + " " + code + " " + message);
    }

    void setContentType(String type, String charset) {
        responseContent.add("Content-Type: " + type + "; charset=" + charset);
    }

    void setContentType(String type)//默认utf-8
    {
        setContentType(type, "utf-8");
    }

    void setLastModified(String date) {
        responseContent.add("Last-Modified: " + date);
    }
    void addCookie(Cookie cookie)
    {
       StringBuffer con=new StringBuffer("Set-Cookie: ");
       con.append(cookie.getName()+"="+cookie.getValue());
       if(cookie.getMaxAge()!=null)
       {
           con.append("; Max-Age="+cookie.getMaxAge());

       }
       if(cookie.getPath()!=null)
       {
           con.append("; path="+cookie.getPath());
       }
       if(cookie.getDomain()!=null)
       {
           con.append("; domain="+cookie.getDomain());
       }
       //con.append("; httponly\r\n");//防止第三方攻击
       responseContent.add(con.toString());
    }
    void addRequestToMap(String str)//将请求信息存入map中
    {
        String s[] = str.split("\\:");
        requestMap.put(s[0].trim(), s[1].trim());

    }

    void analyseFirstLine(String str)//分析第一行，获取请求类型，和资源
    {

        String[] s = str.split("\\s");
        requestType = s[0];
        //System.out.println("类型:" + s[0]);
        filePath = s[1];
        if(filePath.equals("/")) filePath="/p3.html";
       // System.out.println("路径为:" + filePath);
        protocol = s[2];
    }
//处理GET
    long getRange()
    {
       if( requestMap.containsKey("Range"))
       {
           String len=requestMap.get("Range");
           int start=len.indexOf('=');
           int end=len.indexOf('-');
           String length=len.substring(start,end);
           System.out.println(length);
           return Long.parseLong(length);

       }
       else return 0;
    }
    void handleGET(OutputStream out) {


        long size = fileManager.getFileSize(filePath);
        if (size >= 0) { //表示文件存在

            setCode("202", "OK");
            int loc = filePath.lastIndexOf('.');
            //设置文件类型
            String contentType = analyseFileType(filePath);
            setContentType(contentType);
            setContentLength(size);
            // 创建cookie
            Cookie cookie=new Cookie("username","zhangsan");
            //设置cookie最大有效时间
            cookie.setMaxAge(3*24*60*60);
            addCookie(cookie);
            setLastModified(fileManager.getLastModified(filePath));

        } else setCode("404", "not found");

        respondToClient(out, responseContent);
        if (size > 0) {
            long start=getRange();
            System.out.println("开始位置："+start);
            fileManager.transferResource(filePath, out,start);
        }
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    String analyseFileType(String filename)//分析文件类型
    {
        String fileType;
        String regText=".*\\.(txt|html)$";//常见文本
        String regImage=".*\\.(jpeg|GPEG|jpg|JPG|png|PNG)$";//常见图像
        String regAudio=".*\\.(mp3|MP3|MPEG|mpeg|wma|WMA)$";//常见音频
        int loc=filename.lastIndexOf('.');
        if(loc==-1) return "application/octet-stream";

        if(filename.matches(regText)) {

            String suffix=filename.substring(loc+1);
            fileType="text/"+suffix;
        }
        else if(filename.matches(regImage))
        {
            String suffix=filename.substring(loc+1);
            fileType="image/"+suffix;
        }
        else if(filename.matches(regAudio))
        {
            String suffix=filename.substring(loc+1);
            fileType="audio/"+suffix;
        }
        else {	String suffix=filename.substring(loc+1);
            fileType="application/"+suffix;
        }
        return fileType;
    }
   //处理PUT
    void handlePUT(InputStream in) {
        responseContent.clear();
        setCode("200", "OK");
        respondToClient(out, responseContent);
        fileManager.saveResource(filePath, in, false);
    }

    void handleHEAD(OutputStream out) {
        long size = fileManager.getFileSize(filePath);
        if (size >= 0) { //表示文件存在

            setCode("202", "OK");
            int loc = filePath.lastIndexOf('.');
            String contentType =analyseFileType(filePath);
            setContentType(contentType);
            setContentLength(size);
            setLastModified(fileManager.getLastModified(filePath));
        } else setCode("404", "not found");

        respondToClient(out, responseContent);


    }
    void handlePOST(OutputStream out,InputStream in)
    {

        setCode("202", "OK");
        setContentType("html/text");
        respondToClient(out,responseContent);
        String news="已经收到你的表单";
        try {
            out.write(news.getBytes("utf-8"));
            out.flush();
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    void respondToClient(OutputStream out, ArrayList<String> content) {//响应头信息

        System.out.println("返回信息:");
        for (int i = 0; i < content.size(); i++) {
            System.out.println(content.get(i));
        }
        int i = 0;
        while (i < content.size()) {
            try {
                out.write(content.get(i).getBytes());
                out.write("\r\n".getBytes());
                out.flush();
            } catch (IOException e) {

                e.printStackTrace();
                break;
            }
            i++;
        }
        try {

            out.write("\r\n".getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {

        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
            try {
                analyseRequest(in);
                if (requestType.equalsIgnoreCase("GET")) handleGET(out);
                else if (requestType.equalsIgnoreCase("PUT")) {
                    System.out.println("上传");
                    handlePUT(in);

                } else if (requestType.equalsIgnoreCase("HEAD")) handleHEAD(out);
                else if(requestType.equalsIgnoreCase("POST"))
                    handlePOST(out,in);
            } catch (IOException e) {
                e.printStackTrace();
            }

        try {
            if(socket!=null)
              socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

class Cookie {
    String name=null;
    String value=null;
    String path=null;//作用路径
    String domain=null;//作用域
    String MaxAge=null;//最大有效时间

    public Cookie(String k, String v) {
        name = k;
        value = v;
    }

    void setMaxAge(int time)
    {
        MaxAge=""+time;
    }
    void setPath(String path1) {
        path = path1;
    }

    void setDomain(String dm) {
        domain = dm;
    }

    String getName()
    {
        return name;
    }
    String getValue()
    {
        return value;
    }
    String getMaxAge() {
        return MaxAge;
    }
    String getDomain()
    {
        return domain;
    }
    String getPath()
    {
        return  path;
    }
}
