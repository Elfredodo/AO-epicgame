package controller;


import model.CrusaderModel;
import model.SpiderModel;
import java.util.List;
import javax.swing.Timer;



import fr.ubordeaux.ao.mazing.view.Orientation;
import view.GameView;

import javax.swing.JPanel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.SpiderModel;

public class SpiderController {

    private final List<SpiderModel> spiders;
    private final CrusaderModel model;

    public SpiderController(List<SpiderModel> spiders, CrusaderModel model) {

        this.spiders = spiders;
        this.model = model;

        // Moving every 300s
        Timer timer = new Timer(300, e -> moveSpiders());
        timer.start();
    }

    private void moveSpiders() {

        for (SpiderModel spider : spiders) {

            if (spider.isDead()) {
                continue;
            }

            double dx = model.getX() - spider.getX();
            double dy = model.getY() - spider.getY();

            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < 3.0) { // poursuite quand a 3 cases du crusader

                spider.moveTowards(model);
            }
        }
    }   
}