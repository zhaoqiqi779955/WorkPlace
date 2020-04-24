package Web;

import jdk.jfr.consumer.RecordedClass;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class Server extends JFrame implements Runnable {
    ServerSocket serverSocket;
    JTabbedPane tabbedPane = null;
    ServerLog serverLog=null;
    public Server() {
        //初始化图形界面
        super.setTitle("服务器");
        setSize(600, 600);
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            serverSocket = new ServerSocket(8888);
        } catch (Exception e) {
            System.out.println(e);
        }

        serverLog=new ServerLog("Practice/src/Web/log.txt");

    }


    public static void main(String[] args) {

        Server server = new Server();
        new Thread(server).start();
    }

    public void run() {

        ExecutorService pool= Executors.newFixedThreadPool(100);//创建一个线程池

        while (true) {
            try {
                Socket socketAtServer = serverSocket.accept();

                Date date=new Date(System.currentTimeMillis());
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                String now=formatter.format(date);
                now="地址: "+socketAtServer.getInetAddress()+" 端口号：" + socketAtServer.getPort()+ " "+now+ " 连接";
                System.out.println("已与" + socketAtServer.getInetAddress() + " 端口号：" + socketAtServer.getPort() + "连接！");

                Handler handler=new Handler(socketAtServer,now);//提交入线程池
                serverLog.getLog(handler);
                pool.submit(handler);
                tabbedPane.add("客户：" + socketAtServer.getInetAddress() + " 端口号：" + socketAtServer.getPort(),
                       handler.getPane());
                setVisible(true);
            } catch (Exception e) {
                System.out.println(e);
            }


        }


    }
}
interface  WriteLog{ //接口用于实现回调
    void appendLog(String str);
}

class ServerLog implements WriteLog { //服务器日志，采用异步回调
    Handler handler=null;
    BufferedWriter writer=null;
    ServerLog(String str)
    {
        try {
            writer=new BufferedWriter(new FileWriter(str));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getLog(Object hand)
    {
        handler=(Handler) hand;
          new Runnable(){

              @Override
              public void run() {

                  handler.record(ServerLog.this);
              }
          }.run();


    }
    synchronized  public void appendLog(String str) {//写入日志

        try {
            System.out.println("写入文件"+str);
            writer.write(str);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
class Handler implements Runnable{//用于处理每一个客户端请求
    WriteLog writeLog=null;
    DisplayPane displayPane = null;
    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    String digest=null;
    public Handler(Socket soc,String connettime) {
        socket = soc;

        digest=connettime;
        displayPane = new DisplayPane(this);
        InetAddress inetAddress = soc.getInetAddress();

        displayPane.setClinet("与客户: " + inetAddress.getHostAddress() + " 端口号: " + socket.getPort());
        displayPane.setState(true);
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            displayPane.setState(false);
            Date date=new Date(System.currentTimeMillis());
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            String now=formatter.format(date);
            digest+='\n'+now+ "断开连接\n";
            writeLog.appendLog(digest);
            System.out.println("写");
//            e.printStackTrace();
        }


    }

    public void record(WriteLog wl)
    {
        writeLog=wl;
    }
    public void send(String str) {//发送消息
        try {
            writer.write(str);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JPanel getPane() {
        return displayPane.getPane();
    }

    @Override
    public void run() {
        try { //接听消息
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String str = reader.readLine();
                if (str != null)
                    displayPane.ShowMessage("客户端：" + str);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block

            displayPane.setState(false);
            Date date=new Date(System.currentTimeMillis());
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            String now=formatter.format(date);
            digest+='\n'+now+ "断开连接\n";
            writeLog.appendLog(digest);
            System.out.println("->append");

        }
    }
}
