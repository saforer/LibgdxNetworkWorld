package com.jack.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.jack.aistuff.AI;
import com.jack.aistuff.NeatAI;

/**
 * Created by Forer on 6/25/2017.
 */
public class Decker {

    static GameMap map;
    public static Texture pic;
    public float posX;
    public float posY;
    public int gridX;
    public int gridY;
    public int rot = 0;

    int rotSpeed = 20;
    public boolean rotating = false;
    int rotatingAmount = 0;
    Direction facingDirection = Direction.Right;

    float dummyCount = 0f;
    boolean dummy = false;

    float movSpeed = 10f;
    public boolean moving = false;
    float movingAmount = 0;
    public int movingFromGridX;
    public int movingFromGridY;
    Vector2 pos;
    Vector2 oldPos;

    AI ai;

    /*AI Stuff needed
        Is there a bug infront?
        is there bug to the left?
        is there bug to the right?

        Is there food infront?
        is there food to the left?
        is there food to the right?


        is there a wall infront?
        is there a wall to the left?
        is there a wall to the right?
     */
    public boolean bugInFront = false;
    public boolean bugLeft = false;
    public boolean bugRight = false;

    public boolean wallInFront = false;
    public boolean wallLeft = false;
    public boolean wallRight = false;

    public boolean currentlyDoingSomething = false;

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

        if (ai == null) {
            ai = new NeatAI(this);
        }
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
        if (doingSomething()) {
            currentlyDoingSomething = true;
        } else {
            currentlyDoingSomething = false;
        }

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
                posX = pos.x;
                posY = pos.y;
                posX += 3;
                posY += 4;
            }
        } else if (dummy) {
            if (dummyCount > 0) {
                dummyCount -= dt;
            } else {
                dummy = false;
            }
        }
        if (ai != null) {
            setSensors();
            ai.update();
        }
    }

    public void rotateLeft() {
        if (!doingSomething()) {
            rotating = true;
            rotatingAmount = 60;
            facingDirection = facingDirection.next();
        }
    }

    public void rotateRight() {
        if (!doingSomething()) {
            rotating = true;
            rotatingAmount = -60;
            facingDirection = facingDirection.prev();
        }
    }

    public void dummyCountDown() {
        if (!doingSomething()) {
            //Tried to do something couldn't SOOOOOOOO Dummy move
            ai.printInput();
            ai.printOutput();
            dummy = true;
            dummyCount = 5f;
        }
    }

    public void move() {
        if (!doingSomething()) {
            int toGridX;
            int toGridY;

            Vector2 tile = tileInFront(0);

            toGridX = (int) tile.x;
            toGridY = (int) tile.y;

            if (!map.doesTileExistAtLocation(toGridX, toGridY)) {
                dummyCountDown();
                return;
            }

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

    void setSensors() {
        bugInFront = isBugAtPosition(0);
        bugLeft = isBugAtPosition(-1);
        bugRight = isBugAtPosition(1);

        wallInFront = !isWallAtPosition(0);
        wallLeft = !isWallAtPosition(-1);
        wallRight = !isWallAtPosition(1);
    }

    boolean isBugAtPosition (int turn) {
        Vector2 tileFront = tileInFront(turn);

        if (GameMap.getI().getDeckerAt((int) tileFront.x, (int) tileFront.y) == null) {
            return false;
        } else {
            return true;
        }
    }

    boolean isWallAtPosition (int turn) {
        Vector2 tileFront = tileInFront(turn);

        if (GameMap.getI().doesTileExistAtLocation((int) tileFront.x, (int) tileFront.y)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean doingSomething() {
        if (moving) return true;
        if (rotating) return true;
        if (dummy) return true;
        return false;
    }
}
