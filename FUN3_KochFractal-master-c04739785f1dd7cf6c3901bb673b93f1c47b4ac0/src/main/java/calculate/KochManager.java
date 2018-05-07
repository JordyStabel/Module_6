/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;

import javafx.application.Platform;
import fun3kochfractalfx.FUN3KochFractalFX;
import javafx.scene.control.ProgressBar;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager implements _ChangeListener, Runnable {

    private Thread leftThread;
    private Thread rightThread;
    private Thread bottomThread;

    private ArrayList<Edge> allEdges;

    private KochFractal kochFractalLeft;
    private KochFractal kochFractalRight;
    private KochFractal kochFractalBottom;

    private int count;

    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;

    private ProgressBar pbBottom;
    private ProgressBar pbRight;
    private ProgressBar pbLeft;

    public KochManager(FUN3KochFractalFX application) {
        this.application = application;

        this.allEdges = new ArrayList<Edge>();

        kochFractalLeft = new KochFractal(TypeEdge.left);
        kochFractalRight = new KochFractal(TypeEdge.right);
        kochFractalBottom = new KochFractal(TypeEdge.bottom);

        kochFractalBottom.subscribe(this::changeEvent);
        kochFractalLeft.subscribe(this::changeEvent);
        kochFractalRight.subscribe(this::changeEvent);

        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }

    public synchronized void changeLevel_1(int nxt) {
        tsCalc = new TimeStamp();
        count = 0;
        allEdges.clear();
        kochFractalLeft.setLevel(nxt);
        kochFractalRight.setLevel(nxt);
        kochFractalBottom.setLevel(nxt);
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        bottomThread = new Thread(kochFractalBottom);
        rightThread = new Thread(kochFractalRight);
        leftThread = new Thread(kochFractalLeft);

        bottomThread.start();
        rightThread.start();
        leftThread.start();
    }

    private void threadJoiner(Thread left, Thread right, Thread bottom){
        try{
            left.join();
            right.join();
            bottom.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    
    public void changeLevel_2(){
        allEdges.addAll(kochFractalBottom.getAllEdges());
        allEdges.addAll(kochFractalRight.getAllEdges());
        allEdges.addAll(kochFractalLeft.getAllEdges());
        tsCalc.setEnd("End");
        Platform.runLater(() -> {
            application.setTextNrEdges("" + (kochFractalLeft.getNrOfEdges() + kochFractalRight.getNrOfEdges() + kochFractalBottom.getNrOfEdges()));
            application.setTextCalc(tsCalc.toString());
        });
        drawEdges();
    }

    public void updateProgress(ProgressBar left, ProgressBar right, ProgressBar bottom){
        pbLeft = left;
        pbBottom = bottom;
        pbRight = right;
    }

    public synchronized void drawEdges() {
        Platform.runLater(() -> {
            tsDraw.init();
            tsDraw.setBegin("Begin drawing");
            application.clearKochPanel();
            for (Edge e : allEdges) {
                application.drawEdge(e);
            }
            tsDraw.setEnd("End drawing");
            application.setTextDraw(tsDraw.toString());
        });
    }

    @Override
    public void run(){
        // Doesn't need to do anything
    }

    @Override
    public synchronized void changeEvent(_ChangeEvent changeEvent){
        count++;
        if (count == 3){
            changeLevel_2();
        }
    }
}