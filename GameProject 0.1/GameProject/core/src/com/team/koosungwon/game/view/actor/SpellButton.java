package com.team.koosungwon.game.view.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SpellButton {

	public int index; //스펠버튼 index
	public Vector2 center;
	private Rectangle bound;
	private Texture texture;
	
	private boolean activate;

	private final int SPELL_BUTTON_SIZE = 40; //dpi기준

	public SpellButton() {super();}

	public SpellButton(Vector2 center, int index) {
		activate = false;
		
		this.index = index;
		this.center = center;

		float size = SPELL_BUTTON_SIZE * Gdx.graphics.getDensity();

		texture = new Texture("spellbutton.png");
		bound = new Rectangle();
		bound.setSize(size);
		bound.setCenter(center);
	}
	
	public boolean isActivate() {
		return activate;
	}
	
	public void setActivate(boolean activate) {
		this.activate = activate;
	}
	
	public boolean isHit(float x, float y) {
		return bound.contains(x, y);
	}

	public void render(SpriteBatch batch) {
		batch.begin();
		batch.draw(texture, bound.x, bound.y, bound.width, bound.height);
		batch.end();
	}
}
