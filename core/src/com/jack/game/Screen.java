package com.jack.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Forer on 4/27/2017.
 */
public abstract class Screen {
	public abstract void update(float dt);
	public abstract void render(SpriteBatch b);
}
