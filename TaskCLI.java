import config.LoggerConfigurator;
import utils.constants.TaskConstants;

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
                handleDelete(taskManager, args);
                break;
            case "mark-in-progress":
                handleMarkInProgress(taskManager, args);
                break;
            case "mark-done":
                handleMarkDone(taskManager, args);
                break;
            case "list":
                handleList(taskManager, args);
                break;
            default:
                logger.log(Level.INFO,
                        "Unknown command: {0}\nAvailable commands: add, list, update, delete, mark-in-progress, mark-done",
                        command);
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
        if (taskManager.isDescriptionEmpty(description)) {
            logger.log(Level.SEVERE, "Task description cannot be empty or consist only of whitespace.");
            return;
        }
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
            if (!status.equals(TaskConstants.STATUS_DONE) && !status.equals(TaskConstants.STATUS_TODO) && !status.equals(TaskConstants.STATUS_IN_PROGRESS)) {
                logger.log(Level.INFO, "Invalid task status: {0}\nUsage: java TaskCLI list [done|todo|in-progress]",
                        status);
                return;
            }
            List<Map<String, Object>> tasksByStatus = taskManager.getTasks()
                    .stream()
                    .filter(task -> status.equals(task.get(TaskConstants.KEY_STATUS)))
                    .toList();
            if (tasksByStatus.isEmpty()) {
                logger.log(Level.INFO, "No tasks with {0} status.", status);
            } else {
                logger.log(Level.INFO, "List of tasks with {0} status:", status);
                tasksByStatus.forEach(task -> System.out.println(taskManager.formatTask(task)));
            }
        } else {
            if (taskManager.getTasks().isEmpty()) {
                logger.log(Level.INFO, "No tasks added.");
            } else {
                logger.log(Level.INFO, "List of all tasks:");
                taskManager.getTasks().forEach(task -> System.out.println(taskManager.formatTask(task)));
            }
        }
    }

    private static void handleUpdate(TaskManager taskManager, String[] args) {
        if (args.length < 3) {
            logger.log(Level.INFO, "Usage: java TaskCLI update <id> <new_description>");
            return;
        }

        Long id = taskManager.validateTaskId(args[1]);
        if (id == null) {
            return;
        }

        String newDescription = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (taskManager.isDescriptionEmpty(newDescription)) {
            logger.log(Level.SEVERE, "Task description cannot be empty or consist only of whitespace.");
            return;
        }

        boolean success = taskManager.updateTask(id, newDescription);
        if (success) {
            logger.log(Level.INFO, "Task with ID \"{0}\" updated successfully.", id);
        } else {
            logger.log(Level.WARNING, "Task with ID \"{0}\" not found. Please ensure the task ID is correct.", id);
        }
    }

    private static void handleDelete(TaskManager taskManager, String[] args) {
        if (args.length != 2) {
            logger.log(Level.INFO, "Usage: java TaskCLI delete <id>");
            return;
        }

        Long id = taskManager.validateTaskId(args[1]);
        if (id == null) {
            return;
        }

        boolean success = taskManager.deleteTask(id);
        if (success) {
            logger.log(Level.INFO, "Task with ID \"{0}\" deleted successfully.", id);
        } else {
            logger.log(Level.INFO, "Task with ID \"{0}\" not found. Please ensure the task ID is correct.", id);
        }
    }

    private static void handleMarkInProgress(TaskManager taskManager, String[] args) {
        if (args.length != 2) {
            logger.log(Level.INFO, "Usage: java TaskCLI mark-in-progress <id>");
            return;
        }

        Long id = taskManager.validateTaskId(args[1]);
        if (id == null) {
            return;
        }

        boolean success = taskManager.markTaskAsInProgress(id);
        if (success) {
            logger.log(Level.INFO, "Task with ID \"{0}\" marked as in-progress successfully.", id);
        } else {
            logger.log(Level.WARNING, "Task with ID \"{0}\" not found. Please ensure the task ID is correct.", id);
        }
    }

    private static void handleMarkDone(TaskManager taskManager, String[] args) {
        if (args.length != 2) {
            logger.log(Level.INFO, "Usage: java TaskCLI mark-done <id>");
            return;
        }

        Long id = taskManager.validateTaskId(args[1]);
        if (id == null) {
            return;
        }

        boolean success = taskManager.markTaskAsDone(id);
        if (success) {
            logger.log(Level.INFO, "Task with ID \"{0}\" marked as done successfully.", id);
        } else {
            logger.log(Level.WARNING, "Task with ID \"{0}\" not found. Please ensure the task ID is correct.", id);
        }
    }
}
