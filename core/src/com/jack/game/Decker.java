package com.jack.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jack.aistuff.NeatAI;

/**
 * Created by Forer on 6/25/2017.
 */
public class Decker {

    boolean isEgg = true;
    float eggCountdown = 4f;

    static GameMap map;
    public Texture pic;
    public static Texture eggPic;
    public static Texture bugPic;
    public float posX;
    public float posY;
    float offsetX = 3;
    float offsetY = 3;
    public int gridX;
    public int gridY;
    public int rot = 0;

    int rotSpeed = 20;
    public boolean rotating = false;
    int rotatingAmount = 0;
    Direction facingDirection = Direction.Right;

    float dummyCount = 0f;
    boolean dummy = false;

    float eggCount = 0f;
    boolean layingEgg = false;

    float eatCount = 0f;
    boolean eating = false;

    float movSpeed = 10f;
    public boolean moving = false;
    float movingAmount = 0;
    public int movingFromGridX;
    public int movingFromGridY;
    Vector2 pos;
    Vector2 oldPos;

    public NeatAI ai;

    float maxFood = 60f;
    public float currentFood = 12f;

    //AI Stuff
    public boolean bugInFront = false;
    public boolean bugLeft = false;
    public boolean bugRight = false;
    public boolean bugBehind = false;

    public boolean foodInFront = false;
    public boolean foodLeft = false;
    public boolean foodRight = false;
    public boolean foodBehind = false;

    public boolean wallInFront = false;
    public boolean wallLeft = false;
    public boolean wallRight = false;
    public boolean wallBehind = false;

    public float foodPercent = 0f;

    public boolean currentlyDoingSomething = false;

    public Decker (int x, int y) {
        if (map == null) {
            map = GameMap.getI();
        }

        if (bugPic == null) {
            bugPic = new Texture("bug.png");
            eggPic = new Texture("egg.png");
        }

        pic = eggPic;
        isEgg = true;

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

        if (currentFood < 0) {
            map.removeDecker(this);
        } else if (!isEgg) {
            currentFood -= dt * (ai.hiddenNodeCount() * .5f);
        }


        if (isEgg) {
            eggTimer(dt);
        } else {
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
            } else if (ai != null) {
                setSensors();
                ai.update();
            } else if (layingEgg) {
                if (eggCount > 0) {
                    eggCount -= dt;
                } else {
                    layingEgg = false;
                }
            } else if (eating) {
                if (eatCount > 0) {
                    eatCount -= dt;
                } else {
                    eating = false;
                }
            }
        }
    }

    void eggTimer(float dt) {
        if (eggCountdown > 0f) {
            eggCountdown-=dt;
        } else {
            //DING
            isEgg = false;
            pic = bugPic;
            offsetX = 0;
            offsetY = 0;
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

            if (map.getDeckerAt(toGridX, toGridY) != null) {
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
        bugBehind = isFoodAtPosition(180);

        foodInFront = isFoodAtPosition(0);
        foodLeft = isFoodAtPosition(-1);
        foodRight = isFoodAtPosition(1);
        foodBehind = isFoodAtPosition(180);

        wallInFront = !isWallAtPosition(0);
        wallLeft = !isWallAtPosition(-1);
        wallRight = !isWallAtPosition(1);
        wallBehind = !isWallAtPosition(180);

        foodPercent = currentFood / maxFood;
    }

    public void eat() {
        if (isBugAtPosition(0)) {
            Vector2 front = tileInFront(0);
            Decker eaten = map.getDeckerAt((int) front.x, (int) front.y);
            currentFood += eaten.currentFood;
            map.removeDecker(eaten);
        }

        if (isFoodAtPosition(0)) {
            Vector2 front = tileInFront(0);
            Food eaten = map.getFoodAt((int) front.x, (int) front.y);
            currentFood += 2f;
            map.removeFood(eaten);
        }
    }

    boolean isFoodAtPosition (int turn) {
        Vector2 tileFront = tileInFront(0);

        if (GameMap.getI().getFoodAt((int) tileFront.x, (int) tileFront.y) == null) {
            return false;
        } else {
            return true;
        }
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

    public void draw(SpriteBatch sb) {
        if (!isEgg) {
            sb.draw(pic, posX, posY, 6,
                    6, pic.getWidth(), pic.getHeight(), 1.0f, 1.0f, rot,
                    0, 0, 12, 12, false, false);
        } else {
            sb.draw(pic, posX + offsetX, posY + offsetY);
        }
    }

    public void layEgg() {
        Vector2 toLay = tileInFront(180);
        if (map.doesTileExistAtLocation((int) toLay.x, (int) toLay.y)) {
            map.addDecker(toLay.x, toLay.y, ai.nodeListToString());
            currentFood -= 2f;
        }
            eggCount = 2f;
            layingEgg = true;
    }

    public boolean doingSomething() {
        if (moving) return true;
        if (rotating) return true;
        if (dummy) return true;
        if (layingEgg) return true;
        return false;
    }
}
