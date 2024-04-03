package de.dhbw.consoleconnect.client.file;

import java.io.*;
import java.util.Properties;

public class PropertiesFile {

    private static final String FILE_NAME = "./build/configuration.properties";
    private final Properties properties;

    public PropertiesFile() {
        this.properties = new Properties();
        this.load();
    }

    private void load() {
        final File file = new File(FILE_NAME);
        if (file.exists()) {
            try (final InputStream inputStream = new FileInputStream(file)) {
                this.properties.load(inputStream);
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void save() {
        try {
            final File file = new File(FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }

            final OutputStream outputStream = new FileOutputStream(file);
            this.properties.store(outputStream, null);
            outputStream.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public Properties getProperties() {
        return this.properties;
    }
}
