package model;

import fr.ubordeaux.ao.mazing.view.Orientation;
import java.util.ArrayList;
import java.util.List;

public class SpiderModel implements Subject {
    private double x;
    private double y;
    private double z;
    private Orientation orientation;
    private boolean moving;
    private boolean attacking;
    private boolean dead;
    private final List<Observer> observers;

    public SpiderModel(double startX, double startY, double startZ, Orientation orientation) {
        this.x = startX;
        this.y = startY;
        this.z = startZ;
        this.orientation = orientation;

        this.moving = false;
        this.attacking = false;
        this.dead = false;

        this.observers = new ArrayList<>();
    }

    public double getX() { 
        return x; 
    }

    public double getY() { 
        return y; 
    }

    public double getZ() { 
        return z; 
    }

    public Orientation getOrientation() { 
        return orientation; 
    }
    public boolean isMoving() { 
        return moving; 
    }
    public boolean isAttacking() { 
        return attacking; 
    
    }
    public boolean isDead() { 
        return dead; 
    }

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
