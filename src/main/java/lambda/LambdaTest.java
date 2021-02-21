package lambda;

public class LambdaTest {
    public static void main(String[] args) {

        MathOperation add=(int a,int b)->{
            return  a+b;
        };
        System.out.println(add.operate(100,10));
        LambdaTest lambdaTesta=new LambdaTest();
        //传递函数
        lambdaTesta.operate(100,20,add);
    }

    private int operate(int a,int b,MathOperation mathOperation)
    {
        return  mathOperation.operate(a,b);
    }
}
interface MathOperation{
    public int operate(int a,int b);
}
