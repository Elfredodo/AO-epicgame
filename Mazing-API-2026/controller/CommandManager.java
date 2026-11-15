package controller;

import javax.swing.SwingUtilities;
import java.util.LinkedList;
import java.util.Queue;

public class CommandManager {
    private final Queue<Command> bagOfCommands;
    private boolean isRunning;

    public CommandManager() {
        this.bagOfCommands = new LinkedList<>();
        this.isRunning = false;
        startProcessingCommands();
    }

    public synchronized void addCommand(Command command) {
        bagOfCommands.add(command);
    }

    public void startProcessingCommands() {
        if (isRunning) return;
        isRunning = true;

        new Thread(() -> {
            while (true) {
                Command command = null;
                synchronized (this) {
                    if (!bagOfCommands.isEmpty()) {
                        command = bagOfCommands.poll();
                    }
                }
                if (command != null) {
                    final Command finalCommand = command;
                    // execute the command on the Event Dispatch Thread to ensure thread safety with Swing components
                    SwingUtilities.invokeLater(finalCommand::execute);
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
}
