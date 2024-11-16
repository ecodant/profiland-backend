package co.profiland.co.exception;

public class BackupException extends Exception{
    public BackupException(String message){
        super(message);
    }
    public BackupException(String message, Throwable cause){
        super(message, cause);
    }
}
