package co.profiland.co.components;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ThreadPoolManager {
    private final ExecutorService executorService;
    private final Map<String, ReadWriteLock> fileLocks;
    
    private static ThreadPoolManager instance;
    
    private ThreadPoolManager() {
        // Poolcito Sizes 
        this.executorService = Executors.newFixedThreadPool(10);
        this.fileLocks = new ConcurrentHashMap<>();
    }
    
    public static ThreadPoolManager getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolManager.class) {
                if (instance == null) {
                    instance = new ThreadPoolManager();
                }
            }
        }
        return instance;
    }
    
    public ReadWriteLock getLockForFile(String filePath) {
        return fileLocks.computeIfAbsent(filePath, k -> new ReentrantReadWriteLock());
    }
    
    public <T> CompletableFuture<T> submitTask(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }
    
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
