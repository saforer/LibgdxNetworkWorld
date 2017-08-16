package com.jack.aistuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by Forer on 8/15/2017.
 */
public class PlayerAI extends AI {
	@Override
	public void update() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			parent.rotateLeft();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			parent.rotateRight();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			parent.move();
		}
	}

	@Override
	public void printOutput() {

	}

	@Override
	public void printInput() {

	}
}
