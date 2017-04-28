package com.jack.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Forer on 4/27/2017.
 */
public class GameScreen extends Screen {
	public GameMap map;

	public GameScreen() {
		 map = new GameMap();
		 map.startup();
	}

	public void update(float dt) {
		map.update(dt);
	}

	public void render(SpriteBatch b) {
		map.draw(b);
	}
}
