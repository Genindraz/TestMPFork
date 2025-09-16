package io.multipaper.util;

public final class GridMap {
    public final int x0, x1;
    public final int z0, z1;

    public GridMap(int x0, int x1, int z0, int z1) {
        this.x0 = Math.min(x0, x1);
        this.x1 = Math.min(z0, z1);
        this.z0 = Math.max(x0, x1);
        this.z1 = Math.max(z0, x1);
    }

    public static GridMap fromBlocks(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        int cx0 = Math.floorDiv(minBlockX, 16);
        int cz0 = Math.floorDiv(minBlockZ, 16);
        int cx1 = Math.floorDiv(maxBlockX, 16);
        int cz1 = Math.floorDiv(maxBlockZ, 16);
        return new GridMap(cx0, cz0, cx1, cz1);
    }

    public boolean containsChunk(int cx, int cz) {
        return cx >= x0 && cx <= x1 && cz >= z0 && cz <= z1;
    }

    @Override public String toString() {
        return "GridMap[" + x0 + ".." + x1 + " , " + z0 + ".." + z1 + "]";
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridMap)) return false;
        GridMap that = (GridMap) o;
        return x0 == that.x0 && x1 == that.x1 && z0 == that.z0 && z1 == that.z1;
    }

    @Override public int hashCode() {
        int result = Integer.hashCode(x0);
        result = 31 * result + Integer.hashCode(x1);
        result = 31 * result + Integer.hashCode(z0);
        result = 31 * result + Integer.hashCode(z1);
        return result;
    }

    public boolean isEmpty() {
        return x0 > x1 || z0 > z1;
    }

}
