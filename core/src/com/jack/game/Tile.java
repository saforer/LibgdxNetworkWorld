package com.jack.game;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Forer on 4/27/2017.
 */
public class Tile {
	static GameMap map;
	public static Texture pic;
	public int posX;
	public int posY;
	public int gridX;
	public int gridY;
	public float energy = 1000;
	public float energyPerSec = 100;
	public Tile (int gridX, int gridY, int posX, int posY) {
		if (map == null) {
			map = GameMap.getI();
		}
		if (pic == null) {
			pic = new Texture("Hex.png");
		}
		this.gridX = gridX;
		this.gridY = gridY;
		this.posX = posX;
		this.posY = posY;
	}

	public void update(float dt) {
		if (!map.doesDeckerExistAtLocation(gridX, gridY)) {
			if (!map.foodOnTile(gridX, gridY)) {
				energy += dt * energyPerSec;
				if (energy >= 1000) {
					map.makeNewFood(gridX, gridY);
					energy = 0;
				}
			}
		}
	}
}
