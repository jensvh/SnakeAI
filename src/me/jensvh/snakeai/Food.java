package me.jensvh.snakeai;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PVector;

public class Food {
	
	private PVector position;
	
	/**
	 * Creates a food somewhere random on the screen.
	 */
	public Food() {
		// Generate a random position, WIDTH/TILESIZE to get the amount of tiles on the screen
		//position = new PVector(-400, -400);
		position = new PVector((int) Math.floor(new Random().nextInt((int) Settings.HORIZONTAL_TILES)),(int) Math.floor(new Random().nextInt((int) Settings.VERTICAL_TILES)));
	}
	
	public void draw(PApplet applet) {
		applet.fill(255, 0, 0);
		applet.rect(position.x * Settings.TILE_SIZE, position.y * Settings.TILE_SIZE, Settings.TILE_SIZE, Settings.TILE_SIZE);
	}

	public PVector getPosition() {
		return position;
	}
	
}
