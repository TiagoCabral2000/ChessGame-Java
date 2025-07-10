package pt.isec.pa.chess.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing game event logs.
 * Stores timestamped messages and provides log management functionality.
 *
 * @author Group 06
 * @version 1.0.0
 * @since 1.0.0
 */
public class ModelLog implements Serializable {
    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 3L;

    private static ModelLog instance;
    private final ArrayList<String> logs;
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Private constructor to prevent instantiation for singleton pattern.
     */
    private ModelLog() {
        logs = new ArrayList<>();
    }

    /**
     * Gets the singleton instance.
     * @return The single ModelLog instance
     */
    public static ModelLog getInstance() {
        if (instance == null) {
            instance = new ModelLog();
        }
        return instance;
    }

    /**
     * Ensures singleton integrity during deserialization.
     * Replaces any existing instance with the deserialized one.
     *
     * @return The singleton instance
     *
     * @see Serializable
     */
    protected Object readResolve() {
        instance = this;
        return instance;
    }

    /**
     * Adds a new log entry with timestamp.
     * @param message The message to log (ignored if null or blank)
     */
    public void addLog(String message) {
        if (message == null || message.isBlank()) return;

        List<String> oldLogs = new ArrayList<>(logs);
        String timestamp = LocalTime.now().format(TIME_FORMATTER);
        String logEntry = "[" + timestamp + "] " + message;

        logs.add(0, logEntry); // Add at beginning to show newest first
    }

    /**
     * Removes a log entry by index.
     * @param index The index of log to remove
     * @return true if removal succeeded, false if index invalid
     */
    public boolean removeLog(int index) {
        if (index < 0 || index >= logs.size()) {
            return false;
        }

        List<String> oldLogs = new ArrayList<>(logs);
        logs.remove(index);
        return true;
    }

    /**
     * Gets a copy of all log entries.
     * @return List of log messages (newest first)
     */
    public List<String> getList() {
        return new ArrayList<>(logs);
    }

    /**
     * Clears all log entries.
     */
    public void clear() {
        List<String> oldLogs = new ArrayList<>(logs);
        logs.clear();
    }
}