package club.tulane.design.singleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 多线程安全单例
 * 饿汉
 */
public class LoggerForHungary {

    private FileWriter writer;
    private static final LoggerForHungary instance = new LoggerForHungary();

    private LoggerForHungary() {
        File file = new File("/Users/Tulane/tmp/single-log.txt");
        try {
            writer = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LoggerForHungary getInstance(){
        return instance;
    }

    public void log(String message){
        try {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
