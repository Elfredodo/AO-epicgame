package model;

import java.util.ArrayList;
import java.util.List;
import fr.ubordeaux.ao.mazing.view.Orientation;

public class CrusaderModel implements Subject{
    
    private double x;
    private double y;
    private double z;
    private Orientation orientation;
    private boolean moving;
    private final List<Observer> observers;

    public CrusaderModel(double startX, double startY, double startZ) {
        this.x = startX;
        this.y = startY;
        this.z = startZ;
        this.orientation = Orientation.SOUTH;
        this.moving = false;
        this.observers = new ArrayList<>();
    }

    public void move(double dx, double dy, Orientation newOrientation) {
        this.x += dx;
        this.y += dy;
        this.orientation = newOrientation;
        this.moving = true;
        notifyObservers();
    }

    public void stop() {
        if (this.moving) {
            this.moving = false;
            notifyObservers();
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public Orientation getOrientation() { return orientation; }
    public boolean isMoving() { return moving; }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}
