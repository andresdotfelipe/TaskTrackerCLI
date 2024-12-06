import config.LoggerConfigurator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

public class TaskCLI {
    private static final Logger logger = Logger.getLogger(TaskCLI.class.getName());

    public static void main(String[] args) {
        LoggerConfigurator.configure();
        TaskManager taskManager = new TaskManager();

        if (args.length == 0) {
            logger.log(Level.INFO, "Usage: java TaskCLI <command> [options]");
            return;
        }

        String command = args[0];

        switch (command) {
            case "add":
                handleAdd(taskManager, args);
                break;
            case "update":
                handleUpdate(taskManager, args);
                break;
            case "delete":
                break;
            case "mark-in-progress":
                break;
            case "mark-done":
                break;
            case "list":
                handleList(taskManager, args);
                break;
            default:
                logger.log(Level.INFO, "Unknown command: {0}\nAvailable commands: add, list, update", command);
                break;
        }
    }

    private static void handleAdd(TaskManager taskManager, String[] args) {
        if (args.length < 2) {
            logger.log(Level.INFO, "Usage: java TaskCLI add <description>");
            return;
        }

        // Combine all arguments after "add" into a single description
        String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Task task = new Task(taskManager.generateTaskId(), description);
        taskManager.addTask(task);
        logger.log(Level.INFO, "Task added successfully (ID: {0})", task.getId());
    }

    private static void handleList(TaskManager taskManager, String[] args) {
        if (args.length > 2) {
            logger.log(Level.INFO, "Usage: \njava TaskCLI list\njava TaskCLI list [done|todo|in-progress]");
            return;
        }

        if (args.length == 2) {
            String status = args[1];
            if (!status.equals("done") && !status.equals("todo") && !status.equals("in-progress")) {
                logger.log(Level.INFO, "Invalid task status: {0}\nUsage: java TaskCLI list [done|todo|in-progress]", status);
                return;
            }
            List<Map<String, Object>> tasksByStatus = taskManager.getTasks()
                    .stream()
                    .filter(task -> status.equals(task.get("status")))
                    .toList();
            if (tasksByStatus.isEmpty()) {
                logger.log(Level.INFO, "No tasks with {0} status.", status);
            } else {
                logger.log(Level.INFO, "List of tasks with {0} status:", status);
                tasksByStatus.forEach(System.out::println);
            }
        } else {
            if (taskManager.getTasks().isEmpty()) {
                logger.log(Level.INFO, "No tasks added.");
            } else {
                logger.log(Level.INFO, "List of all tasks:");
                taskManager.getTasks().forEach(System.out::println);
            }

        }
    }

    private static void handleUpdate(TaskManager taskManager, String[] args) {
        if (args.length < 3) {
            logger.log(Level.INFO, "Usage: java TaskCLI update <id> <new_description>");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid task ID: {0}. The task ID must be a valid integer.", args[1]);
            return;
        }
        String newDescription = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        boolean success = taskManager.updateTask(id, newDescription);
        if (success) {
            logger.log(Level.INFO, "Task with ID \"{0}\" updated.", id);
        } else {
            logger.log(Level.INFO, "Task with ID \"{0}\" not found.", id);
        }

    }
}
