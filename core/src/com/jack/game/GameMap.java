package com.jack.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Forer on 4/27/2017.
 */
public class GameMap {
	private static GameMap i;
	int row = 35;
	int column = 31;
	int deckerMin = 0;
	public static float actionTimer = 2.0f;
	static Tile[][] tiles;
	static List<Decker> deckerList = new ArrayList<Decker>();
	static List<Food> foodList = new ArrayList<Food>();
	static List<Object> destroy = new ArrayList<Object>();
	static List<Object> addingObject = new ArrayList<Object>();

	protected GameMap() {}

	public void startup() {
		tiles = new Tile[row][column];
		for (int x = 0; x< row; x++) {
			for (int y = 0; y< column; y++) {
				Vector2 pos = gridToWorld(x,y);
				tiles[x][y] = new Tile(x, y, (int) pos.x, (int) pos.y);
			}
		}

		for (int i = 0; i < 1; i++) {
			newRandomDecker();
		}
	}

	public static GameMap getI() {
		if (i==null) {
			i = new GameMap();
		}
		return i;
	}

	public void draw(SpriteBatch batch) {
		for (int x = 0; x < row; x++) {
			for (int y = 0; y < column; y++) {
				if (tiles[x][y] != null) {
					batch.draw(tiles[x][y].pic, tiles[x][y].posX, tiles[x][y].posY);
				}
			}
		}

		for (Food food : foodList) {
			batch.draw(food.pic, food.posX, food.posY);
		}

		for (Decker decker : deckerList) {
			batch.draw(decker.pic, decker.posX, decker.posY,6,
			6, decker.pic.getWidth(), decker.pic.getHeight(),1.0f,1.0f, decker.rot,
					0, 0,12, 12, false, false);
		}
	}

	public void update(float dt) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
			while (deckerList.size() < 100) {
				newRandomDecker();
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
			killAllDeckers();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
			while (deckerList.size() < 1) {
				newRandomDecker();
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
			int x = MathUtils.random(row-1);
			int y = MathUtils.random(column-1);

			if (!doesDeckerExistAtLocation(x,y)) {
				if (!foodOnTile(x,y)) {
					foodList.add(new Food(x, y));
				}
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			if (actionTimer>.5f) actionTimer-=.5f;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			actionTimer+=.5f;
		}

		while (deckerList.size() < deckerMin) {
			newRandomDecker();
		}

		for (Tile[] tA : tiles) {
			for (Tile t : tA) {
				t.update(dt);
			}
		}

		for (Food f : foodList) {
			f.update(dt);
		}

		for (Decker decker : deckerList) {
			if (decker != null) {
				decker.update(dt);
			}
		}

		for (Object o : addingObject) {
			if (o.getClass() == Decker.class) {
				deckerList.add((Decker)o);
			}
		}

		for (Object o : destroy) {
			if (o.getClass() == Decker.class) {
				deckerList.remove(o);
			} else if (o.getClass() == Food.class) {
				foodList.remove(o);
			}
		}

		destroy.clear();
	}

	//Helper functions
	public Vector2 gridToWorld (int x, int y) {
		int gridX = x;
		int gridY = y;
		int posX = 0;
		int posY = 0;
		posX += x * 18;
		posY += y * 15;

		if (y%2==0) {
			posX += 9;
		}
		return new Vector2(posX,posY);
	}

	public boolean doesTileExistAtLocation (int x, int y) {
		if (x > row -1 || x < 0) return false;
		if (y > column - 1 || y < 0) return false;
		return true;
	}

	public boolean doesDeckerExistAtLocation (int x, int y) {
		for (Decker decker : deckerList) {
			if ((decker.gridX == x) && (decker.gridY == y)) return true;
			if (decker.moving == true) {
				if ((decker.movingFromGridX == x) && (decker.movingFromGridY == y)) return true;
			}
		}
		return false;
	}

	public void makeNewFood(int x, int y) {
		if (!foodOnTile(x, y)) {
			foodList.add(new Food(x, y));
		}
	}

	public boolean foodOnTile(int x, int y) {
		for (Food f : foodList) {
			if ((x == f.gridX) && (y == f.gridY)) return true;
		}
		return false;
	}

	public void eatFood(int x, int y) {
		for (Food f : foodList) {
			if ((x == f.gridX) && (y == f.gridY)) {
				destroy.add(f);
			}
		}
	}

	public void newRandomDecker() {
		int x = MathUtils.random(row-1);
		int y = MathUtils.random(column-1);

		if (!doesDeckerExistAtLocation(x,y)) {
			int r = MathUtils.random(Direction.values().length-1);
			Decker d = new Decker(x, y, Direction.values()[r]);
			deckerList.add(d);
			d.ai.parent = d;
		}
	}

	public void addDecker (Decker d) {
		addingObject.add(d);
	}

	public void killAllDeckers () {
		for (Decker decker : deckerList) {
			destroyDecker(decker);
		}
	}

	public void destroyDecker (Decker d) {
		for (Decker decker : deckerList) {
			if (decker == d) destroy.add(decker);
		}
	}

}
