package view;

import fr.ubordeaux.ao.mazing.Scene;
import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.OccludingEntity;
import fr.ubordeaux.ao.mazing.view.entity.Entity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
//import fr.ubordeaux.ao.mazing.view.sound.FootstepSound;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import model.CrusaderModel;
import model.Observer;
import model.SpiderModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JPanel;

public class GameView implements Observer {
    private final Scene scene;
    private final AnimatedEntity crusaderEntity;
    //private final FootstepSound footstepSound;
    private boolean currentlyWalking = false;
    private int attackFramesLeft = 0;
    private static final int ATTACK_FRAMES = 15; // number of frames to play during attack 
    private Runnable onAttackFinished;

    private final Map<SpiderModel, AnimatedEntity> spiderEntities = new HashMap<>(); //spider models and their corresponding animated entities


    public GameView() {
        // Create the scene (window width, window height, grid width, grid height)
        this.scene = new Scene(1024, 768, 12, 12);
        //this.footstepSound = new FootstepSound();

        // Create a basic stone floor of 10x10 to see where the crusader walks
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                scene.addRenderable(new FloorTile(IsometricSprites.STONE_TILE, Orientation.NORTH, x, y, 0));
            }
        }

        // Murs

        Random r = new Random(); 

         for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                scene.addRenderable(new OccludingEntity(IsometricSprites.STONE_WALL, Orientation.WEST, 0, y, 0));
                scene.addRenderable(new OccludingEntity(IsometricSprites.STONE_WALL, Orientation.NORTH, x, 9, 0));
            }

            // caisse

            int max_x=9,min_x=5; 
            int max_y=5, min_y=1; 

            Entity caisse = new Entity(IsometricSprites.WOODEN_CRATE, Orientation.NORTH,r.nextInt(r.nextInt(max_x - min_x + 1) + min_x), r.nextInt(r.nextInt(max_y - min_y + 1) + min_y), 0);scene.addRenderable(caisse);
            Entity caiss_2 = new Entity(IsometricSprites.WOODEN_CRATE, Orientation.NORTH,r.nextInt(r.nextInt(max_x - min_x + 1) + min_x), r.nextInt(r.nextInt(max_y - min_y + 1) + min_y), 0);scene.addRenderable(caisse);

        }

    


        /* 
        // Instantiate the crusader entity with the initial position and orientation from the model
        this.crusaderEntity = new AnimatedEntity(
            AnimatedSprites.CRUSADER_IDLE,
            model.getOrientation(),
            (int) model.getX(),
            (int) model.getY(),
            (int) model.getZ()
        );

        for (SpiderModel spider : spiderModels) {
            AnimatedEntity spiderEntity = new AnimatedEntity(
                AnimatedSprites.SPIDER_WALK,
                spider.getOrientation(),
                (int) spider.getX(),
                (int) spider.getY(),
                (int) spider.getZ()
            );
            // position the spider entity based on the model's coordinates
            spiderEntity.setPosition(spider.getX(), spider.getY(), spider.getZ());
            
            // pause the spider  animation if it is not moving
            spiderEntity.setAnimationPaused(!spider.isMoving());

            spiderEntities.put(spider, spiderEntity);
            scene.addRenderable(spiderEntity);
        }
        */

        //scene.addRenderable(crusaderEntity);
        scene.show();
        scene.centerMap();
        scene.startAnimation(60); // Start the animation loop at 60 FPS
    }

    public void setOnAttackFinished(Runnable onAttackFinished) {
        this.onAttackFinished = onAttackFinished;
    }

    @Override
    public void onCrusaderCreated(double x, double y, double z, Orientation orientation) {
        this.crusaderEntity = new AnimatedEntity(
            AnimatedSprites.CRUSADER_IDLE,
            orientation,
            (int) x,
            (int) y,
            (int) z
        );
        this.crusaderEntity.setPosition(x, y, z);

        // Se registra el listener una única vez al crear la entidad
        this.crusaderEntity.setAnimationFrameListener(frame -> {
            if (attackFramesLeft > 0) {
                attackFramesLeft--;
                if (attackFramesLeft == 0) {
                    crusaderEntity.setSprite(AnimatedSprites.CRUSADER_IDLE);
                    if (onAttackFinished != null) {
                        onAttackFinished.run();
                    }
                }
            }
        });

        scene.addRenderable(crusaderEntity);
        scene.repaint();
    }

    @Override
    public void onCrusaderMoved(double x, double y, double z, Orientation orientation) {
        if (crusaderEntity == null) return;
        crusaderEntity.setOrientation(orientation);
        crusaderEntity.setPosition(x, y, z);

        if (!currentlyWalking && attackFramesLeft == 0) {
            crusaderEntity.setSprite(AnimatedSprites.CRUSADER_WALK);
            currentlyWalking = true;
        }
        scene.repaint();
    }

    @Override
    public void onCrusaderAttackStarted() {
        if (crusaderEntity == null || attackFramesLeft > 0) return;
        currentlyWalking = false;
        crusaderEntity.setSprite(AnimatedSprites.CRUSADER_ATTACK);
        attackFramesLeft = ATTACK_FRAMES;
        scene.repaint();
    }

    @Override
    public void onSpiderCreated(int id, double x, double y, double z, Orientation orientation) {
        AnimatedEntity spiderEntity = new AnimatedEntity(
            AnimatedSprites.SPIDER_WALK,
            orientation,
            (int) x,
            (int) y,
            (int) z
        );
        spiderEntity.setPosition(x, y, z);
        spiderEntity.setAnimationPaused(true);

        spiderEntities.put(id, spiderEntity);
        scene.addRenderable(spiderEntity);
        scene.repaint();
    }

    @Override
    public void onSpiderMoved(int id, double x, double y, double z, Orientation orientation) {
        AnimatedEntity entity = spiderEntities.get(id);
        if (entity == null) return;

        entity.setOrientation(orientation);
        entity.setPosition(x, y, z);
        if (entity.getSprite() != AnimatedSprites.SPIDER_WALK) {
            entity.setSprite(AnimatedSprites.SPIDER_WALK);
        }
        entity.setAnimationPaused(false);
        scene.repaint();
    }

    @Override
    public void onSpiderAttacked(int id, Orientation orientation) {
        AnimatedEntity entity = spiderEntities.get(id);
        if (entity == null) return;

        entity.setOrientation(orientation);
        if (entity.getSprite() != AnimatedSprites.SPIDER_ATTACK) {
            entity.setSprite(AnimatedSprites.SPIDER_ATTACK);
        }
        entity.setAnimationPaused(false);
        scene.repaint();
    }

    @Override
    public void onSpiderDied(int id) {
        AnimatedEntity entity = spiderEntities.get(id);
        if (entity == null) return;

        if (entity.getSprite() != AnimatedSprites.SPIDER_DEATH) {
            entity.setSprite(AnimatedSprites.SPIDER_DEATH);
            entity.setAnimationPaused(false);
            int lastFrame = AnimatedSprites.SPIDER_DEATH.animationSize() - 1;
            entity.setAnimationFrameListener(frame -> {
                if (frame >= lastFrame) {
                    entity.setAnimationPaused(true);
                }
            });
        }
        scene.repaint();
    }

    /* 
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

        } else if (subject instanceof SpiderModel spider) {
            AnimatedEntity entity = spiderEntities.get(spider);
                if (entity != null) {
                    entity.setOrientation(spider.getOrientation());
                    entity.setPosition(spider.getX(), spider.getY(), spider.getZ());

                    if (spider.isDead()) {
                        if (entity.getSprite() != AnimatedSprites.SPIDER_DEATH) {
                            entity.setSprite(AnimatedSprites.SPIDER_DEATH);
                            entity.setAnimationPaused(false);

                            int lastFrame = AnimatedSprites.SPIDER_DEATH.animationSize() - 1;
                            entity.setAnimationFrameListener(frame -> {
                                if (frame >= lastFrame) {
                                    entity.setAnimationPaused(true);
                                    // Si quieres que desaparezca del mapa al morir, descomenta la siguiente línea:
                                    // scene.removeRenderable(entity);
                                }
                            });
                        }
                    } else if (spider.isAttacking()) {
                        entity.setSprite(AnimatedSprites.SPIDER_ATTACK);
                        entity.setAnimationPaused(false);
                    } else {
                        entity.setSprite(AnimatedSprites.SPIDER_WALK);
                        entity.setAnimationPaused(!spider.isMoving());
                    }
                    scene.repaint();
                }
        }
    }
        */

    public JPanel getPanel() {
        return scene.getPanel();
    }

}
