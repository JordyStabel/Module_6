/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import javafx.concurrent.Task;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Peter Boots
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochFractal extends Task<ArrayList<Edge>> {

    private int level = 1;      // The current level of the fractal
    private int nrOfEdges = 3;  // The number of edges in the current level of the fractal
    private Hue hue;          // Hue value of color for next edge
    private boolean cancelled;  // Flag to indicate that calculation has been cancelled
    private TypeEdge typeEdge;
    private ArrayList<Edge> allEdges;
    private final CopyOnWriteArrayList<_ChangeListener> listeners;

    public synchronized ArrayList<Edge> getAllEdges(){
        return allEdges;
    }

    public KochFractal(TypeEdge _typeEdge){
        typeEdge = _typeEdge;
        allEdges = new ArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
    }

    private void drawKochEdge(double ax, double ay, double bx, double by, int n, Hue hue) {
        if (!cancelled) {
            if (n == 1) {
                hue.setHue(hue.getHue() + 1.0f / nrOfEdges);
                allEdges.add(new Edge(ax, ay, bx, by, Color.hsb(hue.getHue() * 360, 1.0, 1.0)));
                updateProgress(allEdges.size(), nrOfEdges / 3);
            } else {
                double angle = Math.PI / 3.0 + Math.atan2(by - ay, bx - ax);
                double distabdiv3 = Math.sqrt((bx - ax) * (bx - ax) + (by - ay) * (by - ay)) / 3;
                double cx = Math.cos(angle) * distabdiv3 + (bx - ax) / 3 + ax;
                double cy = Math.sin(angle) * distabdiv3 + (by - ay) / 3 + ay;
                final double midabx = (bx - ax) / 3 + ax;
                final double midaby = (by - ay) / 3 + ay;
                drawKochEdge(ax, ay, midabx, midaby, n - 1, hue);
                drawKochEdge(midabx, midaby, cx, cy, n - 1, hue);
                drawKochEdge(cx, cy, (midabx + bx) / 2, (midaby + by) / 2, n - 1, hue);
                drawKochEdge((midabx + bx) / 2, (midaby + by) / 2, bx, by, n - 1, hue);
            }
        }
    }

    private void generateLeftEdge() {
        hue = new Hue(0f);
        cancelled = false;
        drawKochEdge(0.5, 0.0, (1 - Math.sqrt(3.0) / 2.0) / 2, 0.75, level, hue);
    }

    private void generateBottomEdge() {
        hue = new Hue(1f / 3f);
        cancelled = false;
        drawKochEdge((1 - Math.sqrt(3.0) / 2.0) / 2, 0.75, (1 + Math.sqrt(3.0) / 2.0) / 2, 0.75, level, hue);
    }

    private void generateRightEdge() {
        hue = new Hue(2f / 3f);
        cancelled = false;
        drawKochEdge((1 + Math.sqrt(3.0) / 2.0) / 2, 0.75, 0.5, 0.0, level, hue);
    }

    public void setLevel(int lvl) {
        level = lvl;
        nrOfEdges = (int) (3 * Math.pow(4, level - 1));
    }

    public int getNrOfEdges() {
        return nrOfEdges;
    }

    private ArrayList<Edge> edgeSwitch(){
        allEdges.clear();
        switch (typeEdge){
            case left:
                generateLeftEdge();
                break;
            case right:
                generateRightEdge();
                break;
            case bottom:
                generateBottomEdge();
                break;
            default:
                return null;
        }
        changeEvent();
        return allEdges;
    }

    public void subscribe(_ChangeListener changeListener){
        listeners.add(changeListener);
    }

    public void unSubscribe(_ChangeListener changeListener){
        listeners.remove(changeListener);
    }

    private synchronized void changeEvent(){
        _ChangeEvent changeEvent = new _ChangeEvent(this);

        for (_ChangeListener subscribers : listeners){
            subscribers.changeEvent(changeEvent);
        }
    }

    @Override
    protected ArrayList<Edge> call(){
        return edgeSwitch();
    }

    @Override
    public void run(){
        edgeSwitch();
    }
}