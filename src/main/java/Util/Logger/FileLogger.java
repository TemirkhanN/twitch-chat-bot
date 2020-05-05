package Util.Logger;

import java.io.*;
import java.nio.charset.Charset;

public class FileLogger implements Logger {
    private Writer logWriter;

    public FileLogger(String path) throws RuntimeException {
        try {
            logWriter = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path),
                            Charset.forName("utf-8")
                    )
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
    }

    @Override
    public void log(String message) {
        try {
            logWriter.write(message);
            logWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            logWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
