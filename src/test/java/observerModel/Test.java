package observerModel;

public class Test {
    public static void main(String[] args) {
        Actor actor=new Actor();
        for (int i = 0; i < 10; i++) {
            Viewer viewer=new Viewer(i);
            actor.addObserver(viewer);
        }
        actor.showOn();
        actor.exit(Result.GOOD);
    }
}
