package com.jack.aistuff;

import com.badlogic.gdx.math.MathUtils;
import com.jack.game.Decker;


/**
 * Created by Forer on 7/2/2017.
 */
public class RandomAI extends AI{
    public RandomAI(Decker p) {
        parent = p;
    }


    @Override
    public void update() {
        if (!parent.doingSomething()) {
            thinkOfSomethingToDo();
        }
    }

    @Override
    public void printOutput() {

    }

    @Override
    public void printInput() {

    }


    void thinkOfSomethingToDo() {
        int rand = MathUtils.random(2);
        switch(rand) {
            case 0:
                parent.rotateLeft();
                break;
            case 1:
                parent.rotateRight();
                break;
            case 2:
                parent.move();
                break;
        }
    }
}
