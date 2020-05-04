package Work_URL;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
interface  Getsize
{
    long getReadBytes();

}


public class DownloadRs extends JFrame implements Getsize {
    URL url = null;
    File file = null;
    URLConnection uc = null;
    JProgressBar pb;//进度条
    long contentLength;
    long readBytes;
    JLabel pr;
    HashMap<URL, ArrayList> undoneFile = new HashMap<>();
    ProgressBar progressBar;
    public DownloadRs() //创建下载器
    {
        super("下载窗口");
        setSize(400, 200);
        JPanel panel = new JPanel();
        JLabel prompt = new JLabel("请输入下载地址");

        prompt.setForeground(Color.blue);
        prompt.setFont(new Font("宋体", Font.BOLD, 20));//字体
        JPanel panel1;
        LineBorder line = new LineBorder(Color.black);
        panel1 = new JPanel();
        panel1.setPreferredSize(new Dimension(400, 60));
        panel1.setLayout(null);
        prompt.setBounds(getX() + getWidth() / 2 - 100, 10, 200, 40);
        //prompt.setBorder(line);
        panel1.add(prompt);

        JTextField input = new JTextField(30);
        input.setPreferredSize(new Dimension(200, 40));
        JButton down = new JButton("下载");
        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println(input.getText());
                    url = new URL(input.getText());

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
                try { //创建连接对象
                    uc = url.openConnection();
                    contentLength = uc.getContentLength();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (undoneFile.containsKey(url)) {
                    String type = uc.getContentType();
                    ArrayList value = undoneFile.get(url);
                    Integer st = (Integer) value.get(1);
                    long start = st.longValue();
                    file = (File) value.get(0);
                    if (type.startsWith("text"))
                        downloadAgain(url, start, false);
                    else downloadAgain(url, start, true);

                } else {
                    int loc = url.getFile().lastIndexOf("/");
                    String fileName = url.getPath().substring(loc + 1);//获取文件名

                    JFileChooser jFileChooser = new JFileChooser("c:/");//设置默认磁盘路径
                    jFileChooser.setSelectedFile(new File(fileName));//设置默认文件名
                    int n = jFileChooser.showSaveDialog(null);
                    if (n != JFileChooser.CANCEL_OPTION) {

                        file = jFileChooser.getSelectedFile();

                        try {

                            String type = uc.getContentType();
                            InputStream in = uc.getInputStream();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar=new ProgressBar(contentLength,DownloadRs.this::getReadBytes);
                                    Thread progres=new Thread(progressBar);
                                    progres.start();
                                }
                            }).start();




                            if (type.startsWith("text")) saveAsText(in, false);//如果为文本表示
                            else saveAsBinary(in, false);

                        } catch (IOException ex) {

                            ex.printStackTrace();
                        }
                    }


                }
            }
        });

        panel.add(Box.createHorizontalStrut(10));
        panel.add(input);
        // panel.add(Box.createHorizontalStrut(10));
        panel.add(down);
        add(panel, BorderLayout.CENTER);
        add(panel1, BorderLayout.NORTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    void saveAsBinary(Object o, boolean append)//存为二进制文件
    {
        //由于并不是所有头部信息都有contentlength故采用缓冲数组加速
        byte bufferedArray[] = new byte[4096];//创建一个缓冲数组用于加速读取内容
        try (BufferedInputStream in = new BufferedInputStream((InputStream) o);//使用try with resource关闭资源
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, append))) {


            System.out.println("下载开始");
            if (append) System.out.println("继续之前下载");
            while (true) {
                int x = in.read(bufferedArray);
                if (x == -1) break;
                readBytes += x;
                out.write(bufferedArray, 0, x);
                out.flush();
                System.out.println("已经下载: " + readBytes + "Bytes ");

            }
            progressBar.setEnd(true);
            System.out.println("finished");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            setUndoneFile(readBytes);


        }


    }

    void setUndoneFile(long start) {
        if (!undoneFile.containsKey(url)) {
            ArrayList tem = new ArrayList();
            tem.add(file);
            tem.add(new Integer(String.valueOf(start)));
        }
    }

    void saveAsText(Object o, boolean append) {
        BufferedReader in = new BufferedReader(new InputStreamReader((InputStream) o));
        int count = 0;
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file, append));
        } catch (IOException e) {
            e.printStackTrace();
        }

        char[] buffereddArray = new char[4096];
        try {
            System.out.println("下载开始");
            if (append) System.out.println("继续之前下载");
            while (true) {
                int x = in.read(buffereddArray);
                if (x == -1) break;
                count += x;
                System.out.println("已经下载: " + count + "Bytes ");
                progressBar.setReadBytes(count);
                out.write(buffereddArray, 0, x);
                out.flush();


            }

            progressBar.setEnd(true);
            System.out.println("finished");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            setUndoneFile(count);
        } finally {
            try {   //关闭流
                if (in != null)
                    in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void yieldHttpResquest(OutputStream out, String path, long start) {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        try {
            writer.write("GET " + path + "HTTP/1.1\r\n");
            writer.write("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\r\n");
            writer.write("User-Agent:NetFox\r\n");
            writer.write("Range: bytes=" + String.valueOf(start) + "-\r\n");
            writer.write("Connection: close\r\n\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void downloadAgain(URL url, long start, boolean isSaveAsBin) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(url.getHost(), 20000));//建立连接并设置最长响应时间
            OutputStream out = socket.getOutputStream();
            String path = url.getPath();
            yieldHttpResquest(out, path, start);
            InputStream in = socket.getInputStream();
            if (isSaveAsBin) saveAsBinary(in, true);
            else saveAsText(in, true);
            undoneFile.remove(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {
        DownloadRs downloader = new DownloadRs();
    }

    @Override
    public long getReadBytes() {
        return readBytes;
    }
}

class ProgressBar extends JFrame implements Runnable {
    long readBytes=0;
    Getsize down;
    JLabel progress;
    boolean end;
    long contentLength;
    JLabel pr;
    JButton pause;
    @Override
    public void run() {
        int x=1;
        while (!end)
        {

            readBytes=down.getReadBytes();
            System.out.println("已下载 "+readBytes);
           setProgress();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    void setEnd(boolean end)
    {
        this.end=end;
    }
    void setProgress() {
        if (contentLength != -1) {
            int values = (int) (1.0*readBytes / contentLength*100) ;
            pr.setText("已下载: "+values+"%");
        } else pr.setText("已下载: " + readBytes + " Bytes");

    }
    void setReadBytes(long bytes)
    {
        readBytes=bytes;
    }
    public  ProgressBar(long Length,Getsize down)
    {
        super("下载进度");
        this.down=down;
        System.out.println("hello");
        end=false;
        readBytes=0;
        this.contentLength=Length;
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

         pr = new JLabel();
         add(pr, BorderLayout.CENTER);
         pause = new JButton("暂停");
         pause.setFont(new Font("仿宋", Font.BOLD, 20));
         pause.setPreferredSize(new Dimension(50, 50));
         pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        add(pause, BorderLayout.SOUTH);
        setVisible(true);
        validate();

    }

}
