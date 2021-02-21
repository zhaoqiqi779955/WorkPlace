package Web.UDP;

public class Test {
    public static void main(String[] args) {
        UDPCommunicator user=new UDPCommunicator(8000);
        user.communicate("127.0.0.1",10000);
    }

}
