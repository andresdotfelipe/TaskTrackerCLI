import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TaskManager {
    private static final String FILE_NAME = "tasks.json";
    private static final Logger logger = Logger.getLogger(TaskManager.class.getName());
    private final List<Map<String, Object>> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
        loadTasks(); // Load tasks from file at startup
    }

    public void addTask(Task task) {
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("id", task.getId());
        taskMap.put("description", task.getDescription());
        taskMap.put("status", task.getStatus());
        taskMap.put("createdAt", task.getCreatedAt());
        taskMap.put("updatedAt", task.getUpdatedAt());
        tasks.add(taskMap);
        saveTasks();
    }

    private void saveTasks() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            writer.write("[\n");
            for (int i = 0; i < tasks.size(); i++) {
                Map<String, Object> task = tasks.get(i);
                writer.write("  {\n");
                writer.write("    \"id\": " + task.get("id") + ",\n");
                writer.write("    \"description\": \"" + task.get("description") + "\",\n");
                writer.write("    \"status\": \"" + task.get("status") + "\",\n");
                writer.write("    \"createdAt\": \"" + task.get("createdAt") + "\",\n");
                writer.write("    \"updatedAt\": \"" + task.get("updatedAt") + "\"\n");
                writer.write("  }" + (i < tasks.size() - 1 ? "," : "") + "\n");
            }
            writer.write("]");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while saving tasks.", e);
        }
    }

    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        tasks.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            Map<String, Object> currentTask = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{")) {
                    currentTask = new HashMap<>();
                } else if (line.startsWith("}")) {
                    tasks.add(currentTask);
                } else if (currentTask != null && !line.startsWith("]")) {
                    String[] parts = line.replace("\"", "").split(": ");
                    String key = parts[0].trim();
                    String value = parts[1].trim().replace(",", "");
                    if (key.equals("id")) {
                        currentTask.put(key, Integer.parseInt(value));
                    } else {
                        currentTask.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while loading tasks.", e);
        }
    }

    public boolean updateTask(int id, String newDescription) {
        Map<String, Object> taskToFind = findTaskById(id);
        if (taskToFind == null) {
            return Boolean.FALSE;
        }
        taskToFind.put("description", newDescription);
        taskToFind.put("updatedAt", LocalDateTime.now());
        saveTasks();
        return Boolean.TRUE;
    }

    public int generateTaskId() {
        if (tasks.isEmpty()) {
            return 1;
        }
        return (int) tasks.getLast().get("id") + 1;
    }

    public List<Map<String, Object>> getTasks() {
        return new ArrayList<>(tasks);
    }

    private Map<String, Object> findTaskById(int id) {
        return tasks
                .stream()
                .filter(task -> id == (int) task.get("id"))
                .findFirst()
                .orElse(null);
    }

    public String formatTask(Map<String, Object> task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedCreatedAtDate = LocalDateTime.parse(task.get("createdAt").toString()).format(formatter);
        String formattedUpdatedAtDate = LocalDateTime.parse(task.get("updatedAt").toString()).format(formatter);
        return String.format("ID: %s, Description: %s, Status: %s, Created At: %s, Updated At: %s",
                task.get("id"), task.get("description"), task.get("status"), formattedCreatedAtDate, formattedUpdatedAtDate);
    }

    public boolean isDescriptionEmpty(String description) {
        return description == null || description.trim().isEmpty();
    }
}
