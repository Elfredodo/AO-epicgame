package controller;

import fr.ubordeaux.ao.mazing.view.Orientation;
import model.CrusaderModel;
import view.GameView;

import javax.swing.JPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.SpiderModel;


public class GameController {
    private final CrusaderModel model;
    private final CommandManager commandManager;
    private static final double STEP = 0.2; // Speed of the crusader's movement per command
    

    public GameController(CrusaderModel model, GameView view) {
        this.model = model;
        this.commandManager = new CommandManager();

        // the controller observes the model and updates the view accordingly
        this.model.addObserver(view);

        // captures keyboard input from the view and translates it into commands for the model
        JPanel panel = view.getPanel();
        panel.setFocusable(true);
        panel.requestFocusInWindow();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (Character.toLowerCase(e.getKeyChar())) {
                    case 'w' -> commandManager.addCommand(() -> 
                        model.move(0, STEP, Orientation.NORTH));
                    case 's' -> commandManager.addCommand(() -> 
                        model.move(0, -STEP, Orientation.SOUTH));
                    case 'd' -> commandManager.addCommand(() -> 
                        model.move(STEP, 0, Orientation.EAST));
                    case 'a' -> commandManager.addCommand(() -> 
                        model.move(-STEP, 0, Orientation.WEST));
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char c = Character.toLowerCase(e.getKeyChar());
                if (c == 'w' || c == 'a' || c == 's' || c == 'd') {
                    commandManager.addCommand(model::stop);
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    commandManager.addCommand(() -> model.attack(true));
                }
            }

             @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    commandManager.addCommand(() -> model.attack(false));
                }
            }
        });
    }
}
