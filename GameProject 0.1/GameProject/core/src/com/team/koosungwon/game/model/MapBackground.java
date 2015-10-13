package com.team.koosungwon.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MapBackground {
	
	private Rectangle bound;
	private Texture texture;
	
	public MapBackground() {super();}
	
	public MapBackground(float x, float width, String textureName) {
		bound = new Rectangle(x, 0, width, Gdx.graphics.getHeight());
		texture = new Texture("map/texture/" + textureName);
	}
	
	public void render(SpriteBatch batch) {
		batch.begin();
		batch.draw(texture, bound.x, bound.y, bound.width, bound.height);
		batch.end();
	}
	
}
