package co.profiland.co.utils;

import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.ExportException;
import co.profiland.co.exception.LogException;
import co.profiland.co.exception.PersistenceException;

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
    private static final String LOG_PATH ="C:/td/persistence/log/Profiland_Log.log";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("dd-MM-yyyy_HH_mm_ss");

    private Utilities() {
        this.threadPool = ThreadPoolManager.getInstance();
        setupLogger();
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

    public static boolean setupLogger() {
        try {
            Path path = Paths.get(LOG_PATH);
            Path parentDir = path.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir); // Create directories if they do not exist
            }
            if (!Files.exists(path)) {
                Files.createFile(path); // Create the log file if it does not exist
            }
            fileHandler = new FileHandler(LOG_PATH, true);
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
            System.err.println(e.getMessage());
            return false;
        }
    }
    public void initializeFile(String path, Object obj) throws PersistenceException, BackupException {
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
        }catch (IOException e) {
            throw new PersistenceException("File initialization failed", e);
        } 
    }

    private void createNewFile(File file, Object obj) throws IOException, PersistenceException {
        Files.createDirectories(file.getParentFile().toPath());
        serializeObject(file.getPath(), obj);
    }


    private void backupFile(File file) throws IOException, BackupException {
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
        } catch (IOException e) {
            throw new BackupException("Failed to create backup", e);
        }
    }

 
    public void serializeObject(String filePath, Object object) throws PersistenceException {
        ReadWriteLock lock = threadPool.getLockForFile(filePath);
        lock.writeLock().lock();
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(filePath)))) {
                oos.writeObject(object);
            } catch (IOException e) {
                throw new PersistenceException("Failed to serialize object", e);
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
    //This method converts an object to JSON
    public String convertToJson(Object object) throws ExportException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new ExportException("Failed to convert object to JSON", e);
        }
        
    }
}