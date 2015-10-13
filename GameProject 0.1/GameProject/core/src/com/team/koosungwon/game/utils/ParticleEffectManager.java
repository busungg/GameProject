package com.team.koosungwon.game.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.team.koosungwon.game.model.ParticleEffectModel;

public class ParticleEffectManager {

	private ArrayList<ParticleEffectModel> userParticleEffectArray;
	private ArrayList<ParticleEffectModel> supporterParticleEffectArray;
	private ArrayList<ParticleEffectModel> monsterParticleEffectArray;

	public ParticleEffectManager() {
		super();

		userParticleEffectArray = new ArrayList<ParticleEffectModel>();
		supporterParticleEffectArray = new ArrayList<ParticleEffectModel>();
		monsterParticleEffectArray = new ArrayList<ParticleEffectModel>();
	}

	//type
	//0 : 주인공
	//1 : 서포터
	//2 : 몬스터

	//anitype
	//0 : 고정형
	//1 : 
	//2 : 날아가는 형
	public void loadParticle(String effectName, int type, Vector2 startPos, Vector2 endPos) {
		FileHandle file = Gdx.files.internal("effects/json/" + effectName + ".json");

		JsonReader jsonReader = new JsonReader();
		JsonValue jsonValue = jsonReader.parse(file);

		int effectAniType = jsonValue.getInt("type");
		if(type == 0) {
			if(effectAniType == 2) {
				userParticleEffectArray.add(new ParticleEffectModel(effectName, startPos, endPos));
			}
		} else if(type == 1) {
			if(effectAniType == 2) {
				supporterParticleEffectArray.add(new ParticleEffectModel(effectName, startPos, endPos));
			}
		}
	}

	public boolean isUserParticleFinish() {
		if(userParticleEffectArray.size() == 0) {
			return true;
		}

		return false;
	}

	public void act() {
		for(int i = 0; i < userParticleEffectArray.size(); i++) {
			if(userParticleEffectArray.get(i).isComplete()) {
				userParticleEffectArray.remove(i);
				i--;
			}
		}

		for(int i = 0; i < supporterParticleEffectArray.size(); i++) {
			if(supporterParticleEffectArray.get(i).isComplete()) {
				supporterParticleEffectArray.remove(i);
				i--;
			}
		}

		for(ParticleEffectModel effect : userParticleEffectArray) {
			effect.act();
		}

		for(ParticleEffectModel effect : supporterParticleEffectArray) {
			effect.act();
		}
	}

	public void render(SpriteBatch batch, float deltaTime) {
		batch.begin();
		for(ParticleEffectModel effect : userParticleEffectArray) {
			effect.render(batch, deltaTime);
		}

		for(ParticleEffectModel effect : supporterParticleEffectArray) {
			effect.render(batch, deltaTime);
		}
		batch.end();
	}

}
