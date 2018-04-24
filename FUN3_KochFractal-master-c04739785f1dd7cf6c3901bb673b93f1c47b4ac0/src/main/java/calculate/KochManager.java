/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import fun3kochfractalfx.FUN3KochFractalFX;
import jdk.nashorn.internal.runtime.Debug;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public abstract class KochManager implements Callable<Edge> {

    private KochFractal koch;

    private KochFractal kochLeft;
    private KochFractal kochRight;
    private KochFractal kochBottom;

    private ArrayList<Edge> edges;

    private ArrayList<Edge> edgesLeft;
    private ArrayList<Edge> edgesRight;
    private ArrayList<Edge> edgesBottom;

    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();

        this.edgesLeft = new ArrayList<Edge>();
        this.edgesRight = new ArrayList<Edge>();
        this.edgesBottom = new ArrayList<Edge>();

        //this.koch = new KochFractal(this);

        kochLeft = new KochFractal();
        kochRight = new KochFractal();
        kochBottom = new KochFractal();

        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }

    public void changeLevel(int nxt) {
        edges.clear();
        koch.setLevel(nxt);
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    kochLeft.generateLeftEdge();
                }
                catch (Exception e)
                {
                    Thread.interrupted();
                    System.out.println(e);
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    kochRight.generateRightEdge();
                }
                catch (Exception e)
                {
                    Thread.interrupted();
                    System.out.println(e);
                }
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    kochBottom.generateBottomEdge();
                }
                catch (Exception e)
                {
                    Thread.interrupted();
                    System.out.println(e);
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

        try
        {
            thread1.join();
            thread2.join();
            thread3.join();
        }
        catch (InterruptedException ex)
        {
            System.out.println(ex.toString());
        }
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
        tsCalc.setEnd("End calculating");
        application.setTextNrEdges("" + koch.getNrOfEdges());
        application.setTextCalc(tsCalc.toString());
        drawEdges();
    }

    public void drawEdges() {
        tsDraw.init();
        tsDraw.setBegin("Begin drawing");
        application.clearKochPanel();
        for (Edge e : edges) {
            application.drawEdge(e);
        }
        tsDraw.setEnd("End drawing");
        application.setTextDraw(tsDraw.toString());
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }
}