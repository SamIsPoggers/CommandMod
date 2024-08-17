package org.sam.commandmod.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SpawnCoordSave {
    private static final String FILE_NAME = "config/coordinates.txt";

    public static void saveCoordinates(int x, int y, int z) {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("X: " + x + "\n");
            writer.write("Y: " + y + "\n");
            writer.write("Z: " + z + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
