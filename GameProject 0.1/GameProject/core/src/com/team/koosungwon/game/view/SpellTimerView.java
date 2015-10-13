package com.team.koosungwon.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class SpellTimerView {
	private final float BOUND_WIDTH = 40 * Gdx.graphics.getDensity();
	private final float BOUND_HEIGHT = 10 * Gdx.graphics.getDensity();
	
	private int spellTime;
	private float passTime;
	
	private Rectangle bound;
	private NinePatchDrawable ninePatchDrawble;
	
	private boolean isRender;
	
	public SpellTimerView() {
		bound = new Rectangle(0, 0, BOUND_WIDTH, BOUND_HEIGHT);
		ninePatchDrawble = new NinePatchDrawable(new NinePatch(new Texture("hpbackground.png")));
		isRender = false;
	}
	
	public void setPassTime(float passTime) {
		this.passTime = passTime;
	}
	
	public int getSpellTime() {
		return spellTime;
	}
	
	public void setSpellTime(int spellTime) {
		this.spellTime = spellTime;
	}
	
	public void setCenter(Vector2 center) {
		bound.setCenter(center);
	}
	
	public void isRender(boolean isRender) {
		this.isRender = isRender;
	}
	
	public boolean getIsRender() {
		return isRender;
	}
	
	public void render(SpriteBatch batch) {
		if(!isRender) return;
		
		batch.begin();
		ninePatchDrawble.draw(batch, bound.x, bound.y, bound.width * ((spellTime - passTime) / spellTime), bound.height);
		batch.end();
	}
}
