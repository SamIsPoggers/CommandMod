package org.sam.commandmod.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigClass {
    private static final Path CONFIG_PATH = Paths.get("config", "usernames.txt");
    private static final Path COORDINATES_PATH = Paths.get("config", "coordinates.txt");

    static {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
                Files.createFile(CONFIG_PATH);
            }
            if (Files.notExists(COORDINATES_PATH)) {
                Files.createFile(COORDINATES_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readUsernames() {
        try {
            return Files.readAllLines(CONFIG_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static void addUsername(String username) {
        List<String> usernames = readUsernames();
        if (!usernames.contains(username)) {
            usernames.add(username);
            writeUsernames(usernames);
        }
    }

    public static void removeUsername(String username) {
        List<String> usernames = readUsernames();
        if (usernames.remove(username)) {
            writeUsernames(usernames);
        }
    }

    public static boolean isAuthorizedUser(String username) {
        return readUsernames().contains(username);
    }

    private static void writeUsernames(List<String> usernames) {
        try {
            Files.write(CONFIG_PATH, usernames);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Updated method to read from the file directly
    public static List<String> getAuthorizedUsers() {
        return readUsernames(); // Read usernames from the file
    }

    public static Optional<double[]> readCoordinates() {
        try {
            List<String> lines = Files.readAllLines(COORDINATES_PATH);
            double[] coordinates = new double[3];
            for (String line : lines) {
                if (line.startsWith("X:")) {
                    coordinates[0] = Double.parseDouble(line.split(" ")[1]);
                } else if (line.startsWith("Y:")) {
                    coordinates[1] = Double.parseDouble(line.split(" ")[1]);
                } else if (line.startsWith("Z:")) {
                    coordinates[2] = Double.parseDouble(line.split(" ")[1]);
                }
            }
            return Optional.of(coordinates);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
