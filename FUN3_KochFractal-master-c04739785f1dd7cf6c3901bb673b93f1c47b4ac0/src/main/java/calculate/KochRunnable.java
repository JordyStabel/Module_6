package calculate;

import java.util.Observable;
import java.util.Observer;

public class KochRunnable implements Runnable, Observer{

    KochFractal koch = new KochFractal();
    KochManager kochManager;
    TypeEdge typeEdge;

    public KochRunnable(KochManager kochManager, TypeEdge typeEdge, int level){
        this.koch.setLevel(level);
        this.typeEdge = typeEdge;
        this.koch.addObserver(this);
    }

    @Override
    public void run() {

        switch (typeEdge){
            case left:{
                koch.generateLeftEdge();
                break;
            }
            case right:{
                koch.generateRightEdge();
                break;
            }
            case bottom:{
                koch.generateBottomEdge();
                break;
            }
        }
        kochManager.finishedAdding();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        kochManager.addEdge((Edge) o);
    }
}
