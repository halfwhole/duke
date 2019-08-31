package duke;

import duke.command.Command;
import javafx.scene.layout.VBox;

public class Duke {

    public static final String DATA_FILE_TASKS = "./data/duke.txt";

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Creates an instance of Duke, setting up the UI, storage, and task list.
     */
    public Duke() {
        ui = new SystemUi();
        storage = new Storage(DATA_FILE_TASKS);
        try {
            tasks = new TaskList(storage.load());
        } catch (DukeException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    public void setupGuiUi(VBox dialogContainer) {
        ui = new GuiUi(dialogContainer);
    }

    /**
     * Runs the instance of Duke with the main loop.
     */
    public void run() {
        ui.showWelcome();
        ui.showLine();
        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            isExit = respond(fullCommand);
        }
        try {
            storage.save(tasks.dump());
        } catch (DukeException e) {
            ui.showSavingError();
        }
    }

    /**
     * This is the main method and entry point for the Duke program.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        new Duke().run();
    }

    /**
     * Returns whether the program should exit
     * @param input Input string
     * @return Boolean indicating of program should exit
     */
    public boolean respond(String input) {
        boolean isExit = false;
        try {
            ui.showLine();
            Command cmd = Parser.parse(input);
            cmd.execute(tasks, ui);
            isExit = cmd.isExit();
        } catch (DukeException e) {
            ui.showError(e);
        } finally {
            ui.showLine();
        }
        return isExit;
    }
}
