package com.jack.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Forer on 6/25/2017.
 */
public class GameMap {
    private static GameMap i;
    int row = 35;
    int column = 31;
    final float actionTimer = 2.0f;
    float actionCount = 0.0f;
    static Tile[][] tiles;

    Decker d;

    protected GameMap() {}

    public void startup() {
        tiles = new Tile[row][column];
        for (int x = 0; x< row; x++) {
            for (int y = 0; y< column; y++) {
                Vector2 pos = gridToWorld(x,y);
                tiles[x][y] = new Tile(x, y, (int) pos.x, (int) pos.y);
            }
        }

        d = new Decker(10,10);
    }

    public static GameMap getI() {
        if (i==null) {
            i = new GameMap();
        }
        return i;
    }


    public void update (float dt) {
        d.update(dt);
    }

    public void draw (SpriteBatch sb) {
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < column; y++) {
                if (tiles[x][y] != null) {
                    sb.draw(tiles[x][y].pic, tiles[x][y].posX, tiles[x][y].posY);
                }
            }
        }

        sb.draw(d.pic, d.posX, d.posY,6,
                6, d.pic.getWidth(), d.pic.getHeight(),1.0f,1.0f, d.rot,
                0, 0,12, 12, false, false);
    }

    //Helper functions
    public Vector2 gridToWorld (int x, int y) {
        int gridX = x;
        int gridY = y;
        int posX = 0;
        int posY = 0;
        posX += x * 18;
        posY += y * 15;

        if (y%2==0) {
            posX += 9;
        }
        return new Vector2(posX,posY);
    }

    public boolean doesTileExistAtLocation (int x, int y) {
        if (x > row -1 || x < 0) return false;
        if (y > column - 1 || y < 0) return false;
        return true;
    }
}
