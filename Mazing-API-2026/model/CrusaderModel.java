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
    private boolean attacking; 

    //For the collision detection, we define the boundaries of the grid where the crusader can move
    private static final double MIN_X = -0.5;
    private static final double MAX_X = 9.5;
    private static final double MIN_Y = -0.5;
    private static final double MAX_Y = 9.5;

    public CrusaderModel(double startX, double startY, double startZ) {
        this.x = startX;
        this.y = startY;
        this.z = startZ;
        this.orientation = Orientation.SOUTH;
        this.moving = false;
        this.observers = new ArrayList<>();
    }

    public void move(double dx, double dy, Orientation newOrientation) {
        double nextX = this.x + dx;
        double nextY = this.y + dy;

        // Update the orientation 
        this.orientation = newOrientation;

        // Check if the next position is within the defined boundaries before updating the position
        if (nextX >= MIN_X && nextX <= MAX_X && nextY >= MIN_Y && nextY <= MAX_Y) {
            this.x = nextX;
            this.y = nextY;
        }

        this.moving = true;
        notifyObservers();
    }

    public void stop() {
        if (this.moving) {
            this.moving = false;
            notifyObservers();
        }
    }

    public void attack(boolean hit){
        this.attacking = hit; 
        notifyObservers(); 
    }

    public void stopAttack() {
        if (this.attacking) {
            this.attacking = false; 
            notifyObservers();
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public Orientation getOrientation() { return orientation; }
    public boolean isMoving() { return moving; }
    public boolean isAttacking() { return attacking;} 

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
