package calculate;

import fun3kochfractalfx.FUN3KochFractalFX;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class KochTask extends Task implements Observer {

    KochFractal koch;
    TypeEdge typeEdge;
    List<Edge> edgeList;
    int total;
    AtomicInteger done = new AtomicInteger(0);
    FUN3KochFractalFX application;
    @Override
    protected Object call() throws Exception {
        if(!isCancelled()){
            switch (typeEdge){
                case left:{
                    typeEdge = typeEdge;
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
        }
        return edgeList;
    }

    public KochTask(int level, TypeEdge typeEdge, FUN3KochFractalFX application){
        this.koch = new KochFractal();
        this.koch.setLevel(level);
        koch.addObserver(this);
        this.typeEdge = typeEdge;
        this.edgeList = new ArrayList<>();
        this.total = koch.getNrOfEdges() /3 ;
        this.application = application;
    }

    @Override
    public void update(Observable o, Object arg) {
        edgeList.add((Edge) arg);
        try {
            Thread.currentThread().sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.cancel();
        }
        updateProgress(done.incrementAndGet(), total);
        updateMessage(String.valueOf(done.get()));
        Platform.runLater(()->{
            this.application.drawEdge((Edge) arg);
        });
    }
    @Override
    public void cancelled(){
        application.clearKochPanel();
        super.cancelled();
        koch.cancel();
    }
}