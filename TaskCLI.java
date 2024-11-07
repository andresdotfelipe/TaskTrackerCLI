public class TaskCLI {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify a command (add, update, delete, mark-in-progress, mark-done, list)");
            return;
        }

        String command = args[0];

        switch (command) {
            case "add":
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
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }
}
