/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import fun3kochfractalfx.FUN3KochFractalFX;
import jdk.nashorn.internal.runtime.Debug;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager {

    private KochFractal leftKoch;
    private KochFractal rightKoch;
    private KochFractal bottomKoch;

    private ArrayList<Edge> edges;

    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();

        this.leftKoch = new KochFractal(this, EdgeType.Left);
        this.rightKoch = new KochFractal(this, EdgeType.Right);
        this.bottomKoch = new KochFractal(this, EdgeType.Bottom);
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }

    public void changeLevel(int nxt) {
        edges.clear();
        leftKoch.run();
        leftKoch.setLevel(nxt);
        rightKoch.setLevel(nxt);
        bottomKoch.setLevel(nxt);
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                run();
//                try
//                {
//                    leftKoch.generateLeftEdge();
//                }
//                catch (Exception e)
//                {
//                    Thread.interrupted();
//                    System.out.println(e);
//                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                run();
//                try
//                {
//                    rightKoch.generateRightEdge();
//                }
//                catch (Exception e)
//                {
//                    Thread.interrupted();
//                    System.out.println(e);
//                }
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                run();
//                try
//                {
//                    bottomKoch.generateBottomEdge();
//                }
//                catch (Exception e)
//                {
//                    Thread.interrupted();
//                    System.out.println(e);
//                }
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
        tsCalc.setEnd("End calculating");
        application.setTextNrEdges("" + (leftKoch.getNrOfEdges() + rightKoch.getNrOfEdges() + bottomKoch.getNrOfEdges()));
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