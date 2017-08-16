package com.jack.aistuff;

import com.jack.game.Decker;

/**
 * Created by Forer on 7/2/2017.
 */
public class DummyAI extends AI {

    public DummyAI(Decker p) {
        parent = p;
    }

    //DUMMY AI DOES NOTHING

    public void update(){}

    @Override
    public void printOutput() {

    }

    @Override
    public void printInput() {

    }
}
