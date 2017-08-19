package com.jack.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jack.aistuff.NeatAI;

import static com.jack.game.Decker.map;

/**
 * Created by Forer on 8/19/2017.
 */
public class Food {
	static Texture pic;
	public float posX;
	public float posY;
	public int gridX;
	public int gridY;
	public GameMap map;

	public Food (int x, int y) {
		if (map == null) {
			map = GameMap.getI();
		}

		pic = new Texture("food.png");

		gridX = x;
		gridY = y;

		Vector2 currentPos = map.gridToWorld(x, y);
		posX = (int) currentPos.x;
		posY = (int) currentPos.y;
		posX += 3;
		posY += 4;
	}

	public void draw(SpriteBatch sb) {
		sb.draw(pic, posX, posY);
	}
}
