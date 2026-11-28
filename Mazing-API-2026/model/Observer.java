package model;

public interface Observer {
    // Crusader evnets
    default void onCrusaderCreated(double x, double y, double z, Orientation orientation) {

    }
    default void onCrusaderMoved(double x, double y, double z, Orientation orientation) {

    }
    default void onCrusaderStopped() {

    }
    default void onCrusaderAttackStarted() {

    }

    //Spiders events
    default void onSpiderCreated(int id, double x, double y, double z, Orientation orientation) {

    }
    default void onSpiderMoved(int id, double x, double y, double z, Orientation orientation) {

    }
    default void onSpiderAttacked(int id, Orientation orientation) {

    }
    default void onSpiderDied(int id) 
    {}
}
