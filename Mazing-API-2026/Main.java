import controller.GameController;
import model.CrusaderModel;
import view.GameView;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // initial position and orientation of the crusader
            CrusaderModel model = new CrusaderModel(5.0, 5.0, 0.0);
            GameView view = new GameView(model);
            new GameController(model, view);
        });
    }
}
