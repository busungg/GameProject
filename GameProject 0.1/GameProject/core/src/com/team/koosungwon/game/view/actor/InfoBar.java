package com.team.koosungwon.game.view.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

public class InfoBar extends Actor {

	private Label label;
	private LabelStyle labelStyle;
	
	private Rectangle barBound;
	private Rectangle barBackgroundBound;
	private Texture bar;
	private NinePatchDrawable barBackground;
	
	private float scaleX;
	private float scaleY;
	
	public InfoBar() {
		super();
		
		scaleX = 1;
		scaleY = 1;
	}
	
	public void setBarTexture(String fileName) {
		bar = new Texture(fileName);
	}
	
	public void setBarBacgroundTexture(String fileName) {
		barBackground = new NinePatchDrawable(new NinePatch(new Texture(fileName)));
	}
	
	public void initialLabel(Rectangle bound, LabelStyle labelStyle) {
		label = new Label("", labelStyle);
		label.setBounds(bound.getX(), bound.getY(), bound.getWidth(), bound.getHeight());
		label.setAlignment(Align.center);
	}
	
	public void setTextScale(float scaleXY) {
		label.setFontScale(scaleXY);
	}
	
	public void setText(String text) {
		label.setText(text);
	}
	
	public void setBarBound(Rectangle bound) {
		this.barBound = bound;
	}
	
	public void setBarBackgroundBound(Rectangle bound) {
		this.barBackgroundBound = bound;
	}
	
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}
	
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		barBackground.draw(batch, barBackgroundBound.x, barBackgroundBound.y, barBackgroundBound.width, barBackgroundBound.height);
		batch.draw(bar, barBound.x, barBound.y, barBound.width * scaleX, barBound.height * scaleY);
		
		if(label != null) {
			label.draw(batch, parentAlpha);
		}
	}
}
