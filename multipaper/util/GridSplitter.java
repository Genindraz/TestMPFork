package io.multipaper.util;

import java.util.ArrayList;
import java.util.List;

public final class GridSplitter {
    private GridSplitter() {}

    public static List<GridMap> split(int minCX, int minCZ, int maxCX, int maxCZ, int workers) {
        int width  = Math.max(1, (maxCX - minCX + 1));
        int height = Math.max(1, (maxCZ - minCZ + 1));
        workers = Math.max(1, Math.min(workers, width * height));

        // rows*cols ≈ workers, near-square grid
        int rows = (int) Math.floor(Math.sqrt(workers));
        int cols = (int) Math.ceil((double) workers / rows);

        int tileW = (int) Math.ceil((double) width  / cols);
        int tileH = (int) Math.ceil((double) height / rows);

        List<GridMap> out = new ArrayList<>(rows * cols);
        for (int r = 0; r < rows; r++) {
            int z0 = minCZ + r * tileH;
            int z1 = Math.min(maxCZ, z0 + tileH - 1);
            for (int c = 0; c < cols; c++) {
                int x0 = minCX + c * tileW;
                int x1 = Math.min(maxCX, x0 + tileW - 1);
                out.add(new GridMap(x0, z0, x1, z1));
                if (out.size() == workers) return out; // exactly Z tiles
            }
        }
        return out;
    }
}
