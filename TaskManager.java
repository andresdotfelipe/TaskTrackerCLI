import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskManager {
    private static final String FILE_NAME = "tasks.json";
    private static final Logger logger = Logger.getLogger(TaskManager.class.getName());

    public TaskManager() {
        // Check if file exists; if not, create it
        File file = new File(FILE_NAME);
        try {
            if (!file.exists()) {
                boolean isFileCreated = file.createNewFile();
                if (isFileCreated) {
                    FileWriter writer = new FileWriter(file);
                    writer.write("[]");
                    writer.close();
                    logger.info("Created new file:" + FILE_NAME);
                }
            } else {
                logger.warning("Failed to create file:" + FILE_NAME);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while creating the JSON file.", e);
        }
    }
}
