package com.jack.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jack.aistuff.NeatAI;
import com.jack.aistuff.PlayerAI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Forer on 6/25/2017.
 */
public class GameMap {
    private static GameMap i;
    int row = 35;
    int column = 31;
    Tile[][] tiles;

    public List<Decker> deckerList = new ArrayList<Decker>();
    public List<Food> foodList = new ArrayList<Food>();
    List<Object> toRemove = new ArrayList<Object>();
    List<Object> toAdd = new ArrayList<Object>();

    private GameMap() {}

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

        int deckerCount = 60;
        for (int i = 0; i < deckerCount; i++) {
            randX = MathUtils.random(row - 1);
            randY = MathUtils.random(column - 1);
            randD = Direction.values()[MathUtils.random(Direction.values().length - 1)];
            Decker d = new Decker(randX, randY, randD);
            deckerList.add(d);
        }

        int foodCount = 500;
        for (int i = 0; i < foodCount; i++) {
            randX = MathUtils.random(row - 1);
            randY = MathUtils.random(column - 1);
            if (getFoodAt(randX, randY) == null) {
                foodList.add(new Food(randX, randY));
            }
        }

    }

    public static GameMap getI() {
        if (i==null) {
            i = new GameMap();
        }
        return i;
    }


    public void update (float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            //Add Food
            int randX = MathUtils.random(row - 1);
            int randY = MathUtils.random(column - 1);
            if (getFoodAt(randX, randY) == null) {
                toAdd.add(new Food(randX, randY));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            //Show bug brain
            System.out.println("------------------------------------------------------");
            for (Decker d : deckerList) {
                System.out.println(d.ai.nodeListToString());
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            randomAddDecker();
        }

        for (Decker d : deckerList) {
            d.update(dt);
        }

        if (deckerList.size() <= 30) {
            int toAddCount = 30 - deckerList.size();
            for (int i = 0; i < toAddCount; i++) {
                randomAddDecker();
            }
        }

        for (Object a : toAdd) {
            if (a.getClass() == Decker.class) {
                deckerList.add((Decker) a);
            } else {
                foodList.add((Food) a);
            }
        }

        for (Object r : toRemove) {
            if (r.getClass() == Decker.class) {
                deckerList.remove(r);
            } else {
                foodList.remove(r);
            }
        }

        toAdd.clear();
        toRemove.clear();
    }

    public void draw (SpriteBatch sb) {
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < column; y++) {
                if (tiles[x][y] != null) {
                    sb.draw(tiles[x][y].pic, tiles[x][y].posX, tiles[x][y].posY);
                }
            }
        }

        for (Food f : foodList) {
            f.draw(sb);
        }

        for (Decker d : deckerList) {
            d.draw(sb);
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

    public Decker getDeckerAt(int x, int y) {
        for (Decker decker : deckerList) {

            if ((decker.gridX == x) && (decker.gridY == y)) return decker;

            if (decker.moving) {
                if ((decker.movingFromGridX == x) && (decker.movingFromGridY == y)) return decker;
            }
        }

        for (Object o : toAdd) {
            if (o.getClass() == Decker.class) {
                Decker decker = (Decker) o;
                if ((decker.gridX == x) && (decker.gridY == y)) return decker;

                if (decker.moving) {
                    if ((decker.movingFromGridX == x) && (decker.movingFromGridY == y)) return decker;
                }
            }
        }

        return null;
    }

    public Food getFoodAt(int x, int y) {
        for (Food food : foodList) {
            if ((food.gridX == x) && (food.gridY == y)) return food;
        }
        return null;
    }


    public void removeDecker(Decker d) {
        toRemove.add(d);
    }

    public void removeFood(Food f) {toRemove.add(f);}

    public void addDecker(float x, float y, String aiStart) {
        int tileX = (int) x;
        int tileY = (int) y;

        if (getDeckerAt(tileX, tileY) == null) {
            Direction randD = Direction.values()[MathUtils.random(Direction.values().length - 1)];
            Decker d = new Decker(tileX, tileY, randD);
            d.ai = new NeatAI(d, aiStart);
            d.ai.Mutate();
            toAdd.add(d);
        }
    }

    public void randomAddDecker() {
        int randX = MathUtils.random(row - 1);
        int randY = MathUtils.random(column - 1);
        Direction randD = Direction.values()[MathUtils.random(Direction.values().length - 1)];
        if (getDeckerAt(randX, randY) == null) {
            Decker d = new Decker(randX, randY, randD);
            toAdd.add(d);
        }
    }
}
