package me.jensvh.snakeai;

import me.jensvh.snakeai.ga.GeneticAlgorithm;
import processing.core.PApplet;

public class SnakeAI extends PApplet {
	
	int FPS = Settings.FPS;
	
	@Override
	public void settings() {
		size(Settings.WIDTH, Settings.HEIGHT);
	}
	
	/*
	 * TODO: implementing GA
	 * TODO: visualization
	 */
	
	GeneticAlgorithm ga;
	
	@Override
	public void setup() {
		frameRate(Settings.FPS);
		ga = new GeneticAlgorithm();
	}
	
	@Override
	public void draw() {
		// Draw background
		background(51);
		
		ga.update(this);
	}
	
	@Override
	public void keyPressed() {
		// Speed up the process
		if (keyCode == UP) {
			frameRate(FPS + 20);
			FPS += 20;
			System.out.println("New fps: " + FPS);
		} else if (keyCode == DOWN) {
			frameRate(FPS - 20);
			FPS -= 20;
			System.out.println("New fps: " + FPS);
		} else if (keyCode == LEFT) {
			frameRate(5);
			FPS = 5;
		} else if (keyCode == RIGHT) {
			frameRate(2000000);
			FPS = 2000000;
		}
	}
	
	// Bootstrap
	public static void main(String[] args) {
		PApplet.main("me.jensvh.snakeai.SnakeAI");
	}

}
