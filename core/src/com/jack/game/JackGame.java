package com.jack.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class JackGame extends ApplicationAdapter {
	SpriteBatch batch;
	public Screen s;

	@Override
	public void create () {
		s = new GameScreen();
		batch = new SpriteBatch();

	}

	@Override
	public void render () {
		s.update(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0, 0, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		s.render(batch);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
