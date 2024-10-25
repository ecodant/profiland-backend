package co.profiland.co.utils;

import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.profiland.co.components.ThreadPoolManager;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReadWriteLock;

import org.springframework.stereotype.Component;
import java.util.logging.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Slf4j
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

    public static void setupLogger(String logPath) {
        try {
            fileHandler = new FileHandler(logPath, true); 
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); 
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to set up logger", e);
        }
    } 

    public void writeIntoLogger(String msg, Level level){
        logger.log(level, msg);
    }


    public void initializeFile(String pathcito, Object obj) {
        File file = new File(pathcito);
        if (file.exists()) {
            backupFile(file);
        } else {
            try {
                file.getParentFile().mkdirs();
                serializeObject(pathcito, obj);
            } catch (IOException e) {
                log.error("Failed to initialize XML file", e);
            }
        }
    }

    private void backupFile(File file) {
        String customPathForTheBackUp = "C:/td/persistence/backup/";  String originalFileName = file.getName();
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
            log.info("Backup created: " + fullBackupPath);
        } catch (IOException e) {
            log.error("Failed to create backup file", e);
        }
    }

    public void serializeObject(String filePath, Object object) throws IOException {
        ReadWriteLock lock = threadPool.getLockForFile(filePath);
        lock.writeLock().lock();
        try {
            try (ObjectOutputStream exit = new ObjectOutputStream(new FileOutputStream(filePath))) {
                exit.writeObject(object);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public Object deserializeObject(String filePath) throws IOException, ClassNotFoundException {
        ReadWriteLock lock = threadPool.getLockForFile(filePath);
        lock.readLock().lock();
        try {
            try (ObjectInputStream entrance = new ObjectInputStream(new FileInputStream(filePath))) {
                return entrance.readObject();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    // Convert any object to JSON string
    public String convertToJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}