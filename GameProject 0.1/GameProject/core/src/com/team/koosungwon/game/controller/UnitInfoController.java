package com.team.koosungwon.game.controller;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.team.koosungwon.game.model.DamageCounter;
import com.team.koosungwon.game.model.unit.MonsterUnit;
import com.team.koosungwon.game.model.unit.SupporterUnit;
import com.team.koosungwon.game.model.unit.PlayerUnit;

public class UnitInfoController {

	private final float HP_BAR_BOUND_WIDTH = 40 * Gdx.graphics.getDensity();
	private final float HP_BAR_BOUND_HEIGHT = 10 * Gdx.graphics.getDensity();

	//유닛 hp 
	private Texture hpBar;
	private Texture hpBarBackground;
	
	//유닛 데미지
	private BitmapFont buffFont;
	private BitmapFont damageFont;

	private PlayerUnit playerUnit;
	private SupporterUnit supporterUnit;
	private ArrayList<MonsterUnit> monsterUnitArray;

	public UnitInfoController() {
		hpBar = new Texture("hp.png");
		hpBarBackground = new Texture("hpbackground.png");
		
		FileHandle fontFile = Gdx.files.internal("fonts/font.fnt");
		
		buffFont = new BitmapFont(fontFile);
		buffFont.setColor(Color.GREEN);
		damageFont = new BitmapFont(fontFile);
		damageFont.setColor(Color.RED);
	}

	public void setPlayerUnit(PlayerUnit playerUnit) {
		this.playerUnit = playerUnit;
	}

	public void setSupporterUnit(SupporterUnit supporterUnit) {
		this.supporterUnit = supporterUnit;
	}

	public void setMonsterUnitArray(ArrayList<MonsterUnit> monsterUnitArray) {
		this.monsterUnitArray = monsterUnitArray;
	}

	public void renderHpBar(SpriteBatch batch) {
		batch.begin();

		if(playerUnit.getHp() > 0) {
			batch.draw(hpBarBackground, playerUnit.getUnitHeadPos().x - (HP_BAR_BOUND_WIDTH / 2), playerUnit.getUnitHeadPos().y, HP_BAR_BOUND_WIDTH, HP_BAR_BOUND_HEIGHT);
			batch.draw(hpBar,  playerUnit.getUnitHeadPos().x - (HP_BAR_BOUND_WIDTH / 2), playerUnit.getUnitHeadPos().y, HP_BAR_BOUND_WIDTH * (playerUnit.getHp() / (float)playerUnit.initialHp), HP_BAR_BOUND_HEIGHT);
		}
		
		if(supporterUnit.getHp() > 0) {
			batch.draw(hpBarBackground, supporterUnit.getUnitHeadPos().x - (HP_BAR_BOUND_WIDTH / 2), supporterUnit.getUnitHeadPos().y, HP_BAR_BOUND_WIDTH, HP_BAR_BOUND_HEIGHT);
			batch.draw(hpBar,  supporterUnit.getUnitHeadPos().x - (HP_BAR_BOUND_WIDTH / 2), supporterUnit.getUnitHeadPos().y, HP_BAR_BOUND_WIDTH * (supporterUnit.getHp() / (float)supporterUnit.initialHp), HP_BAR_BOUND_HEIGHT);
		}
		
		MonsterUnit monster;
		for(int i = 0; i < monsterUnitArray.size(); i++) {
			monster = monsterUnitArray.get(i);
			batch.draw(hpBarBackground, monster.getUnitHeadPos().x - (HP_BAR_BOUND_WIDTH / 2), 
					monster.getUnitHeadPos().y, HP_BAR_BOUND_WIDTH, HP_BAR_BOUND_HEIGHT);
			batch.draw(hpBar,  monster.getUnitHeadPos().x - (HP_BAR_BOUND_WIDTH / 2), monster.getUnitHeadPos().y,
					HP_BAR_BOUND_WIDTH * (monster.getHp() / (float)monster.initialHp), HP_BAR_BOUND_HEIGHT);
		}

		batch.end();
	}
	
	public void renderDamageCounter(SpriteBatch batch) {
		ArrayList<DamageCounter> damageCounterArray;
		DamageCounter damageCounter;
		
		batch.begin();
		
		//플레이어 먼저
		damageCounterArray = playerUnit.getDamageCounterArray();
		for(int i = 0; i < damageCounterArray.size(); i++) {
			damageCounter = damageCounterArray.get(i);
			
			if(damageCounter.isEnd) {
				damageCounterArray.remove(i);
				i--;
			} else {
				damageCounter.act();
				if(damageCounter.type == 1) {
					buffFont.draw(batch, (int)damageCounter.damage + "", damageCounter.startPos.x, damageCounter.startPos.y);
				} else {
					damageFont.draw(batch, (int)damageCounter.damage + "", damageCounter.startPos.x, damageCounter.startPos.y);
				}
			}
		}
		
		//서포터
		damageCounterArray = supporterUnit.getDamageCounterArray();
		for(int i = 0; i < damageCounterArray.size(); i++) {
			damageCounter = damageCounterArray.get(i);
			
			if(damageCounter.isEnd) {
				damageCounterArray.remove(i);
				i--;
			} else {
				damageCounter.act();
				if(damageCounter.type == 1) {
					buffFont.draw(batch, (int)damageCounter.damage + "", damageCounter.startPos.x, damageCounter.startPos.y);
				} else {
					damageFont.draw(batch, (int)damageCounter.damage + "", damageCounter.startPos.x, damageCounter.startPos.y);
				}
			}
		}
		
		//몬스터
		MonsterUnit monster;
		for(int j = 0; j < monsterUnitArray.size(); j++) {
			monster = monsterUnitArray.get(j);
			
			damageCounterArray = monster.getDamageCounterArray();
			for(int i = 0; i < damageCounterArray.size(); i++) {
				damageCounter = damageCounterArray.get(i);
				
				if(damageCounter.isEnd) {
					damageCounterArray.remove(i);
					i--;
				} else {
					damageCounter.act();
					if(damageCounter.type == 1) {
						buffFont.draw(batch, (int)damageCounter.damage + "", damageCounter.startPos.x, damageCounter.startPos.y);
					} else {
						damageFont.draw(batch, (int)damageCounter.damage + "", damageCounter.startPos.x, damageCounter.startPos.y);
					}
				}
			}
		}
		
		batch.end();
	}
}
