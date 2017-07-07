package com.jack.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    int deckerStartCount = 60;

    public List<Decker> deckerList = new ArrayList<Decker>();

    protected GameMap() {}

    public void startup() {
        tiles = new Tile[row][column];
        for (int x = 0; x< row; x++) {
            for (int y = 0; y< column; y++) {
                Vector2 pos = gridToWorld(x,y);
                tiles[x][y] = new Tile(x, y, (int) pos.x, (int) pos.y);
            }
        }

        int randX;
        int randY;
        Direction randD;

        while (deckerList.size() < deckerStartCount) {
            randX = MathUtils.random(row-1);
            randY = MathUtils.random(column-1);
            randD = Direction.values()[MathUtils.random(Direction.values().length -1)];

            if (!doesDeckerExistAtLocation(randX, randY)) {
                Decker d = new Decker(randX, randY, randD);
                deckerList.add(d);
            }
        }

        randX = MathUtils.random(row-1);
        randY = MathUtils.random(column-1);
        randD = Direction.values()[MathUtils.random(Direction.values().length -1)];

        Decker player = new Decker(randX, randY, randD);
        player.ai = null;
        deckerList.add(player);
    }

    public static GameMap getI() {
        if (i==null) {
            i = new GameMap();
        }
        return i;
    }


    public void update (float dt) {
        for (Decker d : deckerList) {
            d.update(dt);
        }
    }

    public void draw (SpriteBatch sb) {
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < column; y++) {
                if (tiles[x][y] != null) {
                    sb.draw(tiles[x][y].pic, tiles[x][y].posX, tiles[x][y].posY);
                }
            }
        }

        for (Decker d : deckerList) {
            sb.draw(d.pic, d.posX, d.posY,6,
                    6, d.pic.getWidth(), d.pic.getHeight(),1.0f,1.0f, d.rot,
                    0, 0,12, 12, false, false);
        }
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

    public boolean doesDeckerExistAtLocation (int x, int y) {
        System.out.print("1");

        for (Decker decker : deckerList) {
            System.out.print("2");
            if ((decker.gridX == x) && (decker.gridY == y)) return true;
            if (decker.moving) {
                if ((decker.movingFromGridX == x) && (decker.movingFromGridY == y)) return true;
            }
        }
        System.out.print("3");
        return false;
    }
}
