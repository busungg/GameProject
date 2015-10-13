package com.team.koosungwon.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * particle type
 * 0 : 고정형
 * 1 : 
 * 2 : 날라가는형
 * 3 : 운석형
 * @author kimbusung
 */

public class ParticleEffectModel {
	
	private Vector2 startPos;
	private Vector2 endPos;
	
	private ParticleEffect effect;
	
	public ParticleEffectModel() {}
	
	public ParticleEffectModel(String effectName, Vector2 startPos) {
		this.startPos = new Vector2(startPos);
		this.endPos = null;
		
		loadEffect(effectName);
	}
	
	public ParticleEffectModel(String effectName, Vector2 startPos, Vector2 endPos) {
		this.startPos = new Vector2(startPos);
		this.endPos = new Vector2(endPos);
		
		loadEffect(effectName);
	}
	
	public void loadEffect(String effectName) {
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("effects/p/" + effectName + ".p"), Gdx.files.internal("effects/image"));
		effect.setPosition(startPos.x, startPos.y);
		effect.start();
	}
	
	public void setStartPos(Vector2 startPos) {
		this.startPos.x = startPos.x;
		this.startPos.y = startPos.y;
	}
	
	public void setEndPos(Vector2 endPos) {
		this.endPos.x = endPos.x;
		this.endPos.y = endPos.y;
	}
	
	public ParticleEffect getEffect() {
		return effect;
	}
	
	//effect null�Ͻ� ����Ʈ�� ��
	public boolean isComplete() {
		if(effect == null) return true;
		
		return false;
	}
	
	public float centerDistance(Vector2 startPos, Vector2 endPos) {
		return (float)Math.sqrt(Math.pow((startPos.x - endPos.x), 2) + Math.pow((startPos.y - endPos.y), 2));
	}
	
	public void act() {
		if(effect == null) return;
		
		if(endPos != null) {
			if(centerDistance(startPos, endPos) > 2) {
				float deltaX = (endPos.x - startPos.x) * 0.3f;
				float deltaY = (endPos.y - startPos.y) * 0.3f;
				
				startPos.x += deltaX;
				startPos.y += deltaY;
				
				effect.setPosition(startPos.x, startPos.y);
				
				if(effect.isComplete()) {
					effect.reset();
				}
			} else {
				effect.dispose();
				effect = null;
			}
		} else {
			if(effect.isComplete()) {
				effect.dispose();
				effect = null;
			}
		}
	}
	
	public void render(SpriteBatch batch, float deltaTime) {
		if(effect == null) return;
		
		effect.draw(batch, deltaTime);
	}
}
