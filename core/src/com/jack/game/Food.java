package com.jack.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Forer on 4/28/2017.
 */
public class Food {
	static GameMap map;
	public static Texture pic;
	public int posX;
	public int posY;
	public int gridX;
	public int gridY;

	public Food(int x, int y) {
		if (pic == null) {
			pic = new Texture("food.png");
		}
		if (map == null) {
			map = GameMap.getI();
		}

		gridX = x;
		gridY = y;
		Vector2 pos = map.gridToWorld(x, y);
		posX = (int) pos.x + 4;
		posY = (int) pos.y + 4;
	}

	public void update(float dt) {

	}
}
