package co.profiland.co.utils;

import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.LogException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReadWriteLock;
import org.springframework.stereotype.Component;
import java.util.logging.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class Utilities implements Serializable {

    private static final Logger logger = Logger.getLogger(Utilities.class.getName());
    private static final long serialVersionUID = 1L;
    private final ThreadPoolManager threadPool;
    private static FileHandler fileHandler;
    private static Utilities instance;
       private static final String DEFAULT_BACKUP_PATH = "C:/td/persistence/backup/";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("dd-MM-yyyy_HH_mm_ss");

    private Utilities() {
        this.threadPool = ThreadPoolManager.getInstance();
    }
    
    public static Utilities getInstance() {
        if (instance == null) {
            synchronized (Utilities.class) {
                if (instance == null) {
                    instance = new Utilities();
                }
            }
        }
        return instance;
    }

    public static boolean setupLogger(String logPath) {
        try {
            fileHandler = new FileHandler(logPath, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to set up logger file handler: " + e.getMessage());
            return false;
        } catch (SecurityException e) {
            System.err.println("Security violation while setting up logger: " + e.getMessage());
            return false;
        }
    }

    public boolean writeIntoLogger(String msg, Level level) {
        try {
            if (fileHandler == null) {
                throw new LogException("Logger has not been properly initialized");
            }
            logger.log(level, msg);
            return true;
        } catch (LogException e) {
            System.err.println("Logger error: " + e.getMessage());
            return false;
        } catch (SecurityException e) {
            System.err.println("Security violation while writing to log: " + e.getMessage());
            return false;
        }
    }
    public void initializeFile(String path, Object obj) {
        if (path == null || obj == null) {
            throw new IllegalArgumentException("Path and object cannot be null");
        }

        File file = new File(path);
        try {
            if (file.exists()) {
                backupFile(file);
            } else {
                createNewFile(file, obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("File initialization failed", e);
        }
    }

    private void createNewFile(File file, Object obj) throws IOException {
        Files.createDirectories(file.getParentFile().toPath());
        serializeObject(file.getPath(), obj);
    }


    private void backupFile(File file) throws IOException {
        String fileName = file.getName();
        String fileExtension = "";
        String nameWithoutExt = fileName;

        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            fileExtension = fileName.substring(lastDot);
            nameWithoutExt = fileName.substring(0, lastDot);
        }

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String backupFileName = String.format("%s_%s%s", nameWithoutExt, timestamp, fileExtension);
        
        Path backupPath = Paths.get(DEFAULT_BACKUP_PATH, backupFileName);
        try {
            Files.createDirectories(backupPath.getParent());
            Files.copy(file.toPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
            // log.info("Created backup at: {}", backupPath);
        } catch (IOException e) {
            // log.error("Failed to create backup at: " + backupPath, e);
            throw e;
        }
    }

 
    public void serializeObject(String filePath, Object object) {
        ReadWriteLock lock = threadPool.getLockForFile(filePath);
        lock.writeLock().lock();
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(filePath)))) {
                oos.writeObject(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    public Object deserializeObject(String filePath) {
        ReadWriteLock lock = threadPool.getLockForFile(filePath);
        boolean lockAcquired = false;
        
        try {
            try {
                lock.readLock().lock();
                lockAcquired = true;
                
                try (ObjectInputStream entrance = new ObjectInputStream(new FileInputStream(filePath))) {
                    return entrance.readObject();
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, "File not found for deserialization: " + e.getMessage());
                    return null;
                } catch (ClassNotFoundException e) {
                    logger.log(Level.SEVERE, "Class not found during deserialization: " + e.getMessage());
                    return null;
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IO error during deserialization: " + e.getMessage());
                return null;
            }
        } finally {
            if (lockAcquired) {
                try {
                    lock.readLock().unlock();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error releasing read lock: " + e.getMessage());
                }
            }
        }
    }
    // Convert any object to JSON string
    public String convertToJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}