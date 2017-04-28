package com.jack.game;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Forer on 4/28/2017.
 */
public class RandomAI extends AI {

	float aiTimer = 1f;
	float aiCount = 0;
	float aiSpeed = 2f;

	public void update(float dt) {
		if ((!parent.moving) && (!parent.rotating)) {
			if (aiCount >= aiTimer) {
				aiCount=0;
				action();
			} else {
				aiCount+=aiSpeed * dt;
			}
		}
	}

	public void doNothing() {

	}

	public void action() {
		int x = MathUtils.random(3);
		switch(x) {
			case 0:
				parent.move();
				break;
			case 1:
				parent.rotateLeft();
				break;
			case 2:
				parent.rotateRight();
				break;
			case 3:
				doNothing();
				break;
		}
	}
}
