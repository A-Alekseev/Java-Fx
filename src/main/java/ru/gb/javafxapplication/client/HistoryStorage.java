package ru.gb.javafxapplication.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryStorage {

    private static final int MAX_MESSAGES_COUNT = 100;

    public static Path getPath(String login){
        String fileName = login + ".history" + ".txt";
        return Path.of("Histories", fileName);
    }

    public static void saveHistory(String login, String text) throws IOException {
        if (text == null || text.length() < 1){
            return;
        }

        //selecting last messages
        text = text.replace("\r\n", "\n");
        String[] allLines = text.split("\n");

        int firstIndex = Math.max(0, allLines.length - MAX_MESSAGES_COUNT);
        int lastIndex = allLines.length;
        String[] lastLines = Arrays.copyOfRange(allLines, firstIndex, lastIndex);
        String lastLinesText = String.join("\r\n", lastLines);

        Path path = getPath(login);

        //deleting old history file
        if (Files.exists(path)){
            Files.delete(path);
        }

        if(!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        Files.createFile(path);

        //saving
        Files.writeString(path, lastLinesText);
    }

    public static String loadHistory(String login){
        Path path = getPath(login);
        if (!Files.exists(path)){
            return "";
        }

        try {
            return Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
