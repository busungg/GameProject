package com.team.koosungwon.game.model;

import com.badlogic.gdx.math.Vector2;

public class DamageCounter {

	private final int velocityY = 2;
	private final int animationTime = 500;
	
	public Vector2 startPos;
	public float damage;
	public int type; //1 : 버퍼, 2 : 데미지
	public long startAnimationTime;
	public boolean isEnd;
	
	public DamageCounter() {super();}
	
	public DamageCounter(Vector2 startPos, float damage, int type, long startAnimationTime) {
		this.startPos = startPos;
		this.damage = damage;
		this.type = type;
		this.startAnimationTime = startAnimationTime;
		
		isEnd = false;
	}
	
	public void act() {
		if(System.currentTimeMillis() - startAnimationTime > animationTime) {
			isEnd = true;
		} else {
			startPos.y += velocityY;
		}
	}
}
