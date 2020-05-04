package Work_URL;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DNS_Interpreter {

    public static void main(String[] args) {
        if(args.length!=0)
        {
            try {
                InetAddress adr=InetAddress.getByName(args[0]);
                System.out.println(adr.getHostAddress());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
}
