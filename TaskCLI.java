import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskCLI {
    private static final Logger logger = Logger.getLogger(TaskCLI.class.getName());

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        if (args.length == 0) {
            System.out.println("Usage: java TaskCLI <command> [options]");
            return;
        }

        String command = args[0];

        switch (command) {
            case "add":
                handleAdd(taskManager, args);
                break;
            case "update":
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
                System.out.println("Unknown command: " + command);
                System.out.println("Available commands: add, list");
                break;
        }
    }

    private static void handleAdd(TaskManager taskManager, String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TaskCLI add <description>");
            return;
        }

        // Combine all arguments after "add" into a single description
        String description = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        Task task = new Task(taskManager.generateTaskId(), description);
        taskManager.addTask(task);
        logger.log(Level.INFO, "Task added successfully (ID: {0})", task.getId());
    }

    private static void handleList(TaskManager taskManager, String[] args) {
        if (args.length > 2) {
            System.out.println("Usage: \njava TaskCLI list\njava TaskCLI list [done|todo|in-progress]");
            return;
        }

        if (args.length == 2) {
            String status = args[1];
            if (!status.equals("done") && !status.equals("todo") && !status.equals("in-progress")) {
                System.out.println("Invalid status. Usage: java TaskCLI list [done|todo|in-progress]");
                return;
            }
            taskManager.getTasks().stream()
                    .filter(task -> status.equals(task.get("status")))
                    .forEach(System.out::println);
        } else {
            taskManager.getTasks().forEach(System.out::println);
        }
    }
}
