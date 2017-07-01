package com.jack.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Forer on 6/25/2017.
 */
public class Decker {

    /*
       TODO: Bugs eat bugs that get walked on their tile
     */
    static GameMap map;
    public static Texture pic;
    public int posX;
    public int posY;
    public int gridX;
    public int gridY;
    public int rot = 0;

    int rotSpeed = 10;
    public boolean rotating = false;
    int rotatingAmount = 0;
    Direction facingDirection = Direction.Right;

    float movSpeed = 5f;
    public boolean moving = false;
    float movingAmount = 0;
    public int movingFromGridX;
    public int movingFromGridY;
    Vector2 pos;
    Vector2 oldPos;

    public Decker (int x, int y) {
        if (map == null) {
            map = GameMap.getI();
        }

        if (pic == null) {
            pic = new Texture("bug.png");
        }

        gridX = x;
        gridY = y;

        Vector2 currentPos = map.gridToWorld(x, y);
        posX = (int) currentPos.x;
        posY = (int) currentPos.y;
        posX += 3;
        posY += 4;
    }

    public Decker (int x, int y, Direction d) {
        this(x, y);
        facingDirection = d;
        rot = 0;
        switch (facingDirection) {
            case DownRight:
                rot += 60;
            case DownLeft:
                rot += 60;
            case Left:
                rot += 60;
            case UpLeft:
                rot += 60;
            case UpRight:
                rot += 60;
            case Right:
                break;
        }
    }


    public void update(float dt) {
        if (rotating) {
            if (rotatingAmount > 0) {
                rot += rotSpeed;
                rotatingAmount -= rotSpeed;
            } else if (rotatingAmount < 0) {
                rot -= rotSpeed;
                rotatingAmount += rotSpeed;
            } else {
                rotating = false;
            }
        } else if (moving) {
            if (movingAmount < 1f) {
                movingAmount += movSpeed * dt;
                Vector2 currentPos = oldPos.cpy();
                currentPos.lerp(pos, movingAmount);
                posX = (int) currentPos.x + 3;
                posY = (int) currentPos.y + 4;
            } else {
                moving = false;
                movingAmount = 0f;
                movingFromGridX = 0;
                movingFromGridY = 0;
                pos = null;
                oldPos = null;

                Vector2 pos = map.gridToWorld(gridX, gridY);
                posX = (int) pos.x;
                posY = (int) pos.y;
                posX += 3;
                posY += 4;
            }
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && !rotating && !moving) {
                rotateLeft();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && !rotating && !moving) {
                rotateRight();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !rotating && !moving) {
                move();
            }
        }
    }

    void rotateLeft() {
        rotating = true;
        rotatingAmount = 60;
        facingDirection = facingDirection.next();
    }

    void rotateRight() {
        rotating = true;
        rotatingAmount = -60;
        facingDirection = facingDirection.prev();
    }

    void move() {
        int toGridX;
        int toGridY;

        Vector2 tile = tileInFront(0);

        toGridX = (int) tile.x;
        toGridY = (int) tile.y;

        if (map.doesTileExistAtLocation(toGridX, toGridY)) {
            moving = true;
            movingFromGridX = gridX;
            movingFromGridY = gridY;
            gridX = toGridX;
            gridY = toGridY;
            pos = map.gridToWorld(gridX, gridY);
            oldPos = map.gridToWorld(movingFromGridX, movingFromGridY);
        }
    }



    public Vector2 tileInFront(int turn) {
        int toGridX = gridX;
        int toGridY = gridY;
        Direction dir = facingDirection;

        switch (turn) {
            case -1:
                //Turn left
                dir = dir.next();
                break;
            case 0:
                //No turn
                break;
            case 1:
                //Turn right
                dir = dir.prev();
                break;
            case 180:
                dir = dir.prev();
                dir = dir.prev();
                dir = dir.prev();
        }

        switch (dir) {
            case Right:
                toGridX++;
                break;
            case UpRight:
                toGridY++;
                if (gridY%2==0) {
                    toGridX++;
                }
                break;
            case UpLeft:
                toGridY++;
                if (gridY%2!=0) {
                    toGridX--;
                }
                break;
            case Left:
                toGridX--;
                break;
            case DownLeft:
                toGridY--;
                if (gridY%2!=0) {
                    toGridX--;
                }
                break;
            case DownRight:
                toGridY--;
                if (gridY%2==0) {
                    toGridX++;
                }
                break;
        }

        return new Vector2 (toGridX, toGridY);

    }

}
