import utils.constants.DateTimeConstants;
import utils.constants.TaskConstants;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskManager {
    private static final String FILE_NAME = "tasks.json";
    private static final Logger logger = Logger.getLogger(TaskManager.class.getName());
    private final List<Map<String, Object>> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
        loadTasks(); // Load tasks from file at startup
    }

    public boolean addTask(Task task) {
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put(TaskConstants.KEY_ID, task.getId());
        taskMap.put(TaskConstants.KEY_DESCRIPTION, task.getDescription());
        taskMap.put(TaskConstants.KEY_STATUS, task.getStatus());
        taskMap.put(TaskConstants.KEY_CREATED_AT, task.getCreatedAt());
        taskMap.put(TaskConstants.KEY_UPDATED_AT, task.getUpdatedAt());
        tasks.add(taskMap);
        if (!saveTasks()) {
            logger.log(Level.SEVERE, "Failed to add task to file.");
            return false;
        }
        return true;
    }

    private boolean saveTasks() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            writer.write("[\n");
            for (int i = 0; i < tasks.size(); i++) {
                Map<String, Object> task = tasks.get(i);
                writer.write("  {\n");
                writer.write("    \"" + TaskConstants.KEY_ID + "\": " + task.get("id") + ",\n");
                writer.write("    \"" + TaskConstants.KEY_DESCRIPTION + "\": \"" + task.get("description") + "\",\n");
                writer.write("    \"" + TaskConstants.KEY_STATUS + "\": \"" + task.get("status") + "\",\n");
                writer.write("    \"" + TaskConstants.KEY_CREATED_AT + "\": \"" + task.get("createdAt") + "\",\n");
                writer.write("    \"" + TaskConstants.KEY_UPDATED_AT + "\": \"" + task.get("updatedAt") + "\"\n");
                writer.write("  }" + (i < tasks.size() - 1 ? "," : "") + "\n");
            }
            writer.write("]");
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while saving tasks to file.", e);
            return false;
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
                    if (key.equals(TaskConstants.KEY_ID)) {
                        currentTask.put(key, Long.parseLong(value));
                    } else {
                        currentTask.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while loading tasks.", e);
        }
    }

    public boolean updateTask(Long id, String newDescription) {
        Map<String, Object> taskToFind = findTaskById(id);
        if (taskToFind == null) {
            logger.log(Level.WARNING, "Task with ID \"{0}\" not found. Please ensure the task ID is correct.", id);
            return false;
        }
        taskToFind.put(TaskConstants.KEY_DESCRIPTION, newDescription);
        taskToFind.put(TaskConstants.KEY_UPDATED_AT, LocalDateTime.now());
        if (!saveTasks()) {
            logger.log(Level.SEVERE, "Failed to update task description in file.");
            return false;
        }
        return true;
    }

    public boolean deleteTask(Long id) {
        Map<String, Object> taskToFind = findTaskById(id);
        if (taskToFind == null) {
            logger.log(Level.WARNING, "Task with ID \"{0}\" not found. Please ensure the task ID is correct.", id);
            return false;
        }
        tasks.remove(taskToFind);
        if (!saveTasks()) {
            logger.log(Level.SEVERE, "Failed to delete task in file.");
            return false;
        }
        return true;
    }

    public boolean markTaskAsInProgress(Long id) {
        Map<String, Object> taskToFind = findTaskById(id);
        if (taskToFind == null) {
            logger.log(Level.WARNING, "Task with ID \"{0}\" not found. Please ensure the task ID is correct.", id);
            return false;
        }
        taskToFind.put(TaskConstants.KEY_STATUS, TaskConstants.STATUS_IN_PROGRESS);
        taskToFind.put(TaskConstants.KEY_UPDATED_AT, LocalDateTime.now());
        if (!saveTasks()) {
            logger.log(Level.SEVERE, "Failed to mark task as in-progress in file.");
            return false;
        }
        return true;
    }

    public boolean markTaskAsDone(Long id) {
        Map<String, Object> taskToFind = findTaskById(id);
        if (taskToFind == null) {
            logger.log(Level.WARNING, "Task with ID \"{0}\" not found. Please ensure the task ID is correct.", id);
            return false;
        }
        taskToFind.put(TaskConstants.KEY_STATUS, TaskConstants.STATUS_DONE);
        taskToFind.put(TaskConstants.KEY_UPDATED_AT, LocalDateTime.now());
        if (!saveTasks()) {
            logger.log(Level.SEVERE, "Failed to mark task as done in file.");
            return false;
        }
        return true;
    }

    public Long generateTaskId() {
        if (tasks.isEmpty()) {
            return 1L;
        }
        return (Long) tasks.getLast().get(TaskConstants.KEY_ID) + 1;
    }

    public Long validateTaskId(String taskId) {
        Long id;
        try {
            id = Long.parseLong(taskId);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid task ID: {0}. The task ID must be a valid integer.", taskId);
            return null;
        }
        return id;
    }

    public List<Map<String, Object>> getTasks() {
        return new ArrayList<>(tasks);
    }

    private Map<String, Object> findTaskById(Long id) {
        return tasks
                .stream()
                .filter(task -> id.equals(task.get(TaskConstants.KEY_ID)))
                .findFirst()
                .orElse(null);
    }

    public String formatTask(Map<String, Object> task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeConstants.DATE_TIME_PATTERN);
        String formattedCreatedAtDate = LocalDateTime.parse(task.get(TaskConstants.KEY_CREATED_AT).toString()).format(formatter);
        String formattedUpdatedAtDate = LocalDateTime.parse(task.get(TaskConstants.KEY_UPDATED_AT).toString()).format(formatter);
        return String.format(TaskConstants.TASK_DETAILS_FORMAT, task.get(TaskConstants.KEY_ID), task.get(TaskConstants.KEY_DESCRIPTION), task.get(TaskConstants.KEY_STATUS), formattedCreatedAtDate, formattedUpdatedAtDate);
    }

    public boolean isDescriptionEmpty(String description) {
        return description == null || description.trim().isEmpty();
    }
}
