package view;

import fr.ubordeaux.ao.mazing.Scene;
import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
//import fr.ubordeaux.ao.mazing.view.sound.FootstepSound;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import model.CrusaderModel;
import model.Observer;
import model.Subject;

import javax.swing.JPanel;

public class GameView implements Observer {
    private final Scene scene;
    private final AnimatedEntity crusaderEntity;
    //private final FootstepSound footstepSound;
    private boolean currentlyWalking = false;
    private boolean currentlyAttacking = false;
    private int attackFramesLeft = 0;
    private static final int ATTACK_FRAMES = 15; // number of frames to play during attack 


    public GameView(CrusaderModel model) {
        // Create the scene (window width, window height, grid width, grid height)
        this.scene = new Scene(1024, 768, 12, 12);
        //this.footstepSound = new FootstepSound();

        // Create a basic stone floor of 10x10 to see where the crusader walks
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                scene.addRenderable(new FloorTile(IsometricSprites.STONE_TILE, Orientation.NORTH, x, y, 0));
            }
        }

        // Instantiate the crusader entity with the initial position and orientation from the model
        this.crusaderEntity = new AnimatedEntity(
            AnimatedSprites.CRUSADER_IDLE,
            model.getOrientation(),
            (int) model.getX(),
            (int) model.getY(),
            (int) model.getZ()
        );

        scene.addRenderable(crusaderEntity);
        scene.show();
        scene.centerMap();
        scene.startAnimation(60); // Start the animation loop at 60 FPS
    }

    @Override
    public void update(Subject subject) {
        if (subject instanceof CrusaderModel model) {
            // Update orientation and exact position in the API
            crusaderEntity.setOrientation(model.getOrientation());
            crusaderEntity.setPosition(model.getX(), model.getY(), model.getZ());

            // Switch between walking (WALK) and idle (IDLE) sprite
            if (model.isMoving() && !currentlyWalking) {
                crusaderEntity.setSprite(AnimatedSprites.CRUSADER_WALK);
                currentlyWalking = true;
                //footstepSound.play();
            } else if (!model.isMoving() && currentlyWalking) {
                crusaderEntity.setSprite(AnimatedSprites.CRUSADER_IDLE);
                currentlyWalking = false;
            }
            

            // Attack 
            crusaderEntity.setAnimationFrameListener(frame -> {
                if (attackFramesLeft > 0) {
                    attackFramesLeft--; //decrementation to avoid infinite loop
                    if (attackFramesLeft == 0) {
                        crusaderEntity.setSprite(AnimatedSprites.CRUSADER_IDLE);
                        model.stopAttack();   // put isAttack at false when no frames left
                    }
                }
            });

            // Starting an attack 
            if (model.isAttacking() && attackFramesLeft == 0) {
                crusaderEntity.setSprite(AnimatedSprites.CRUSADER_ATTACK);
                attackFramesLeft = ATTACK_FRAMES; //number of frames to play at each attack
}

            scene.repaint();
        
            }
        }


    public JPanel getPanel() {
        return scene.getPanel();
    }
}
