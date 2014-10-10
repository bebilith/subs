package com.warrensofthought.subs.physics;

import java.util.Random;

/**
 * Created by till on 09.10.14.
 */
public class WorldBuilder {

    Random rand;
    int max = 10;
    int height = 10;
    int width = 10;
    int[][] heightmap;

    public WorldBuilder() {
        rand = new Random();
        heightmap = new int[height][width];
        heightmap = create();
    }

    public int[][] create() {
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                heightmap[h][w] = rand.nextInt(max);
            }
        }
        return heightmap;
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth(int h, int w) {
        return heightmap[h][w];
    }
}
