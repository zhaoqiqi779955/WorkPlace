package Web.testForInet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class Test {
    private URL url=null;
    public Test()
    {

        try {
            url=new URL("https://dldir1.qq.com/invc/tt/QQBrowser_Setup_QB10_10026022.exe");

    } catch (MalformedURLException e) {
        e.printStackTrace();
    }
    Socket socket=new Socket();
        try {
        socket.connect(new InetSocketAddress(url.getHost(),80),20000);
        System.out.println("successful");
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
    public static void main(String[] args) {


         new Test();
    }
}
