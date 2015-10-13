package com.team.koosungwon.game.view.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Card extends Actor {

	private Rectangle bound;
	private Texture texture;
	
	public Card() {
		super();
	}
	
	public void setBound(Rectangle bound) {
		this.bound = bound;
	}
	
	public void setTexture(String fileName) {
		texture = new Texture(fileName);
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		batch.draw(texture, bound.x, bound.y, bound.width, bound.height);
	}
}
