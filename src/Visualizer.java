// FILE: Visualizer.java
// DATE: May 11, 2025
// AUTHOR: Timothy Prisk; tprisk@gmail.com

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Visualizer
{

    //
    // Public methods.
    //

    public void makeSnapshot(UUID id, int[][] state)
    {
        BufferedImage snapshot = new BufferedImage(state.length, state.length, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < state.length; i++)
        {
            for (int j = 0; j < state.length; j++)
            {
                int color = state[i][j] == -1 ? 0x000000 : 0xFFFFFF;
                snapshot.setRGB(i, j, color);
            }
        }

        try
        {
            String filename = "Ising-snapshot-" + id.toString();
            File snapshotImage = new File(filename);
            ImageIO.write(snapshot, "png", snapshotImage);
            System.out.println("Snapshot saved.");
        }
        catch (IOException e)
        {
            System.err.println("Error saving snapshot: " + e.getMessage());
        }
    }
}
