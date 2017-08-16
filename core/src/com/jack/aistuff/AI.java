package com.jack.aistuff;

import com.jack.game.Decker;

/**
 * Created by Forer on 7/2/2017.
 */
public abstract class AI {
    public Decker parent;
    public abstract void update();
    public abstract void printOutput();
    public abstract void printInput();
}
