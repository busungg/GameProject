package com.team.koosungwon.game.view;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.team.koosungwon.game.view.actor.SpellButton;

public class SpellView {
	
	private final int SPELL_VIEW_SIZE = 240;
	private final int SPELL_VIEW_MARGIN = 10;
	private Rectangle bound;
	
	private ShapeRenderer shapeRenderer;
	private Vector2 touchDragPos;
	
	private ArrayList<SpellButton> spellButtonArray;
	private ArrayList<Vector2> linePosArray;
	
	public boolean isFirstSpellTouch;
	private String spell;
	
	public SpellView() {
		
		float gameScreenWidth = Gdx.graphics.getWidth();
		
		bound = new Rectangle();
		bound.setSize(SPELL_VIEW_SIZE * Gdx.graphics.getDensity());
		
		bound.x = gameScreenWidth - (SPELL_VIEW_SIZE * Gdx.graphics.getDensity() + SPELL_VIEW_MARGIN * Gdx.graphics.getDensity());
		bound.y = 0;
		
		shapeRenderer = new ShapeRenderer();
		
		//라인 굵기
		Gdx.gl.glLineWidth(10);
		
		touchDragPos = new Vector2();
		
		spellButtonArray = new ArrayList<SpellButton>();
		linePosArray = new ArrayList<Vector2>();
		spell = "";
		
		initialSpellButton();
	}
	
	private void initialSpellButton() {
		
		float centerDistance = (SPELL_VIEW_SIZE / 3) * Gdx.graphics.getDensity();
		float firstCenterX = bound.x + (centerDistance / 2f);
		float firstCenterY = bound.y + (centerDistance / 2f);
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				spellButtonArray.add(new SpellButton(new Vector2(firstCenterX + centerDistance * i, firstCenterY + centerDistance * j),
						(i * 3) + (j + 1)));
			}
		}
	}
	
	public void render(SpriteBatch batch) {	
		for(SpellButton button : spellButtonArray) {
			button.render(batch);
		}
		
		if(isFirstSpellTouch) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.valueOf("99FFFF"));
			
			for(int i = 0; i < linePosArray.size(); i++) {
				if(i + 1 < linePosArray.size()) {
					shapeRenderer.line(linePosArray.get(i), linePosArray.get(i+1));
				} else {
					shapeRenderer.line(linePosArray.get(i), touchDragPos);
				}
			}
			shapeRenderer.end();
		}
		
	}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touchDragPos.x = screenX;
		touchDragPos.y = screenY;
		
		for(SpellButton spellButton : spellButtonArray) {
			if(spellButton.isHit(screenX, screenY)) {
				isFirstSpellTouch = true;
				linePosArray.add(spellButton.center);
				spellButton.setActivate(true);
				spell += spellButton.index;
				return true;
			}
		}
		
		return false;
	}

	public String touchUp(int screenX, int screenY, int pointer, int button) {
		isFirstSpellTouch = false;
		
		linePosArray.clear();
		for(SpellButton spellButton : spellButtonArray) {
			spellButton.setActivate(false);
		}
		
		String tempSpell = spell;
		spell = "";
		
		return tempSpell;
	}

	public void touchDragged(int screenX, int screenY, int pointer) {
		touchDragPos.x = screenX;
		touchDragPos.y = screenY;
		
		for(SpellButton spellButton : spellButtonArray) {
			if(!spellButton.isActivate()) {
				if(spellButton.isHit(screenX, screenY)) {
					spell += spellButton.index;
					spellButton.setActivate(true);
					
					linePosArray.add(spellButton.center);
					return;
				}
			}
		}
	}
}
