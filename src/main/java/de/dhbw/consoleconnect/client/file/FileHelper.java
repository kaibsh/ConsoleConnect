package de.dhbw.consoleconnect.client.file;

import java.io.*;

public class FileHelper {

    public static String read(final String fileName) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final File file = new File(fileName);
            if (file.exists()) {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void write(final String fileName, final String content) {
        try {
            final File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }
}
