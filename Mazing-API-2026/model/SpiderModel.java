package model;

import fr.ubordeaux.ao.mazing.view.Orientation;
import java.util.ArrayList;
import java.util.List;

import model.CrusaderModel;

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

    public void moveTowards(CrusaderModel model) {
        double distance_x = model.getX() - this.x; 
        double distance_y = model.getY()-this.y; 

        double distance = Math.sqrt(distance_x * distance_x + distance_y * distance_y);

        if (distance > 0.5) {
            double speed = 0.1;

            this.x += (distance_x / distance) * speed;
            this.y += (distance_y / distance) * speed;

        // To implement ; attack when close 


            // Déterminer la direction
            if (Math.abs(distance_x) > Math.abs(distance_y)) {
                if (distance_x > 0) {
                    setOrientation(Orientation.EAST);
                } else {
                    setOrientation(Orientation.WEST);
                }
            } else {
                if (distance_y > 0) {
                    setOrientation(Orientation.NORTH);
                } else {
                    setOrientation(Orientation.SOUTH);
                }
            }

            this.moving = true ; 
            this.attacking = false ; 

            notifyObservers();
        }
        
            else {

                this.moving = false ; 
                this.attacking = true ; 

                notifyObservers(); 
            }
    
    }

    public void die() {
        if (!this.dead) {
            this.dead = true;
            this.moving = false;
            this.attacking = false;
            notifyObservers();
        }
    }


    public void setOrientation (Orientation orientation) {
        this.orientation = orientation ; 

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
