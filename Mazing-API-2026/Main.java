import controller.GameController;
import model.CrusaderModel;
import model.SpiderModel;
import view.GameView;
import fr.ubordeaux.ao.mazing.view.Orientation;
import java.util.List;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // initial position and orientation of the crusader
            CrusaderModel model = new CrusaderModel(5.0, 5.0, 0.0);
            
            //pair of spiders with their initial positions and orientations
            SpiderModel sp1 = new SpiderModel(2.0, 2.0, 0.0, Orientation.NORTH_EAST);
            SpiderModel sp2 = new SpiderModel(8.0, 3.0, 0.0, Orientation.SOUTH_WEST);
            List<SpiderModel> spiders = List.of(sp1, sp2);
            
            GameView view = new GameView(model, spiders);

            //add the view as an observer to each spider model
            for (SpiderModel spider : spiders) {
                spider.addObserver(view);
            }

            new GameController(model, view);
        });
    }
}
