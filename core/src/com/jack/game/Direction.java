package com.jack.game;

/**
 * Created by Forer on 4/28/2017.
 */
public enum Direction {
	Right {
		@Override
		public Direction prev() {
			return DownRight;
		}
	},
	UpRight,
	UpLeft,
	Left,
	DownLeft,
	DownRight {
		@Override
		public Direction next() {
			return Right;
		}
	};

	public Direction next() {
		return values()[ordinal() + 1];
	}

	public Direction prev() {
		return values()[ordinal() - 1];
	}
}