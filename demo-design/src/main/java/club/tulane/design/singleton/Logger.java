package club.tulane.design.singleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private FileWriter writer;

    public Logger() throws IOException {
        File file = new File("/Users/Tulane/tmp/single-log.txt");
        writer = new FileWriter(file, true);
    }

    public void log(String message) throws IOException {
        writer.write(message);
    }
}
