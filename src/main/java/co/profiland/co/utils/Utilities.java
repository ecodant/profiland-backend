package co.profiland.co.utils;

import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.LogException;
import co.profiland.co.exception.PersistenceException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReadWriteLock;
import org.springframework.stereotype.Component;
import java.util.logging.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
public class Utilities implements Serializable {

    private static final Logger logger = Logger.getLogger(Utilities.class.getName());
    private static final long serialVersionUID = 1L;
    private final ThreadPoolManager threadPool;
    private static FileHandler fileHandler;
    private static Utilities instance;
    
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

    public boolean initializeFile(String pathcito, Object obj) throws BackupException, PersistenceException {
        File file = new File(pathcito);
        if (file.exists()) {
            return backupFile(file) && serializeObject(pathcito, obj);
        } else {
            try {
                if (!file.getParentFile().mkdirs() && !file.getParentFile().exists()) {
                    logger.log(Level.SEVERE, "Failed to create directory structure");
                    return false;
                }
                return serializeObject(pathcito, obj);
            } catch (SecurityException e) {
                logger.log(Level.SEVERE, "Security violation during file initialization: " + e.getMessage());
                return false;
            }
        }
    }

    private boolean backupFile(File file) {
        String customPathForTheBackUp = "C:/td/persistence/backup/";
        String originalFileName = file.getName();
        String fileExtension = "";
        String fileNameWithoutExtension = originalFileName;

        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = originalFileName.substring(lastDotIndex);
            fileNameWithoutExtension = originalFileName.substring(0, lastDotIndex);
        }

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH_mm_ss"));
        
        String backupFileName = String.format("%s_%s%s", fileNameWithoutExtension, timestamp, fileExtension);
        String backupPath = customPathForTheBackUp != null ? customPathForTheBackUp : file.getParent();
        String fullBackupPath = backupPath + File.separator + backupFileName;
        
        File backupFile = new File(fullBackupPath);
        try {
            Files.createDirectories(backupFile.getParentFile().toPath());
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.log(Level.INFO, "Backup created: " + fullBackupPath);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create backup file: " + e.getMessage());
            return false;
        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "Security violation while creating backup: " + e.getMessage());
            return false;
        }
    }

    public boolean serializeObject(String filePath, Object object) {
        ReadWriteLock lock = threadPool.getLockForFile(filePath);
        boolean lockAcquired = false;
        
        try {
            try {
                lock.writeLock().lock();
                lockAcquired = true;
                
                try (ObjectOutputStream exit = new ObjectOutputStream(new FileOutputStream(filePath))) {
                    exit.writeObject(object);
                    return true;
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, "File not found for serialization: " + e.getMessage());
                    return false;
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IO error during serialization: " + e.getMessage());
                return false;
            }
        } finally {
            if (lockAcquired) {
                try {
                    lock.writeLock().unlock();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error releasing write lock: " + e.getMessage());
                }
            }
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