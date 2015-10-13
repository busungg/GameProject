package com.team.koosungwon.game.model.unit;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateListener;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.team.koosungwon.game.base.unit.BaseUnitModel;
import com.team.koosungwon.game.definition.UnitState;
import com.team.koosungwon.game.model.Skill;

public class PlayerUnit extends BaseUnitModel {
	
	public boolean isAttackMotion;
	
	private int castingTime;
	private int activateSkillIndex;
	private ArrayList<Skill> skillArray;

	public PlayerUnit() {super();}

	public PlayerUnit(String unitId) {
		super();

		isAttackMotion = false;
		activateSkillIndex = -1;
		skillArray = new ArrayList<Skill>();

		unitPos = new Vector2(400, Gdx.graphics.getHeight() / 3 + 100);
		
		//loadLua(unitId);
		loadAsset(unitId);
		initialize();
	}

	@Override
	protected void loadAsset(String unitId) {
		if(Gdx.files.internal("unit/player/json/" + unitId + ".json").exists()) {
			FileHandle file = Gdx.files.internal("unit/player/json/" + unitId + ".json");

			JsonReader jsonReader = new JsonReader();
			JsonValue jsonValue = jsonReader.parse(file);

			hp = jsonValue.getInt("hp");
			mp = jsonValue.getInt("mp");
			attack = jsonValue.getFloat("attack");
			castingTime = jsonValue.getInt("castingTime");
			speed = jsonValue.getFloat("speed");
			protect = jsonValue.getFloat("protect");
			nuckbackResist = jsonValue.getFloat("nuckbackResist");

			JsonValue jsonValueArray = jsonValue.get("skills");
			Skill skill;
			for(JsonValue skillJsonValue : jsonValueArray) {
				
				skill = new Skill();
				skill.animationState = skillJsonValue.getString("animationState");
				skill.effect = skillJsonValue.getString("effect");
				skill.magnification = skillJsonValue.getFloat("magnification");
				skill.minimumRange = skillJsonValue.getFloat("minimumRange");
				skill.maximumRange = skillJsonValue.getFloat("maximumRange");
				skill.nuckback = skillJsonValue.getFloat("nuckback");
				skill.nuckbackSpeed = skillJsonValue.getFloat("nuckbackSpeed");
				skill.buffType = skillJsonValue.getInt("buffType");
				skill.buffTime = skillJsonValue.getInt("buffTime");
				skill.isBuff = skillJsonValue.getInt("isBuff");
				skill.target = skillJsonValue.getInt("target");
				skill.mpWastage = skillJsonValue.getFloat("mpWastage");
				skill.unlock = skillJsonValue.getInt("unlock");
				skill.spell = skillJsonValue.getString("spell");
				
				skillArray.add(skill);
			}

			loadSpine(jsonValue);
		}
	}

	@Override
	protected void loadSpine(JsonValue jsonValue) {
		atlas = new TextureAtlas(Gdx.files.internal(jsonValue.getString("spineAtlas")));
		SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
		json.setScale(0.35f); // Load the skeleton at 60% the size it was in Spine.
		SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(jsonValue.getString("spineSkeleton")));
		
		skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
		skeleton.setPosition(unitPos.x, unitPos.y);

		AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

		state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).

		state.addListener(new AnimationStateListener() {

			@Override
			public void event(int trackIndex, Event event) {}

			@Override
			public void complete(int trackIndex, int loopCount) {
				if(trackIndex == UnitState.STATE_ATTACK) {
					setIsAttack(true);
					setUnitState(UnitState.STATE_WALK);
				}
			}

			@Override
			public void start(int trackIndex) {}

			@Override
			public void end(int trackIndex) {}
		});
	}
	
	public ArrayList<Skill> getSkill() {
		return skillArray;
	}
	
	public int getCastingTime() {
		return castingTime;
	}
	
	@Override
	public Skill getActivateSkill() {
		return skillArray.get(activateSkillIndex);
	}

	@Override
	protected void setUnitState(int unitState) {
		if(this.unitState == unitState) return;
		
		if(unitState == UnitState.STATE_WALK) {
			if(isMeetEnemy) {
				unitState = UnitState.STATE_MEET_ENEMY;
			}
		}
		
		this.unitState = unitState;
		state.clearTracks();

		switch(unitState) {
		case UnitState.STATE_WALK:
			state.addAnimation(UnitState.STATE_WALK, "walk", true, 0);
			break;

		case UnitState.STATE_CASTING:
		case UnitState.STATE_MEET_ENEMY:
			state.addAnimation(UnitState.STATE_MEET_ENEMY, "idle", true, 0);
			break;

		case UnitState.STATE_HIT:
		case UnitState.STATE_CASTING_PENALTY:
			isAttackMotion = false;
			state.addAnimation(UnitState.STATE_HIT, "hit", false, 0);
			break;

		case UnitState.STATE_ATTACK:
			state.addAnimation(UnitState.STATE_ATTACK, getActivateSkill().animationState, false, 0);
			break;
		}
	}

	public void isCasting() {
		activateSkillIndex = -1;
		setUnitState(UnitState.STATE_CASTING);
	}

	public void casting(String spell) {
		for(int i = 0; i < skillArray.size(); i ++) {
			if(skillArray.get(i).unlock == 1 && skillArray.get(i).spell.equals(spell)) {
				if(mp - skillArray.get(i).mpWastage > 0) {
					mp -= skillArray.get(i).mpWastage;
					activateSkillIndex = i;
					isAttackMotion = true;
					setUnitState(UnitState.STATE_ATTACK);
					return;
				} else {
					break;
				}
			}
		}

		castingPenalty();
	}

	public void castingPenalty() {
		activateSkillIndex = -1;

		//임시
		setUnitState(UnitState.STATE_CASTING_PENALTY);
		
		//루아 로딩
	}

	@Override
	public void act() {
		checkBuff();
		isMove = checkDebuff();
		
		switch(unitState) {
		case UnitState.STATE_WALK:
			if(isMeetEnemy) {
				setUnitState(UnitState.STATE_MEET_ENEMY);
			} else {
				unitPos.x += speed;
				skeleton.setX(unitPos.x);
			}
			break;

		case UnitState.STATE_HIT:
		case UnitState.STATE_CASTING_PENALTY:
			if(nuckback > 0) {
				unitPos.x -= nuckbackSpeed;
				nuckback -= nuckbackSpeed;
				skeleton.setX(unitPos.x);
			} else {
				if(isMove) return;
				setUnitState(UnitState.STATE_WALK);
			}
			break;
		
		case UnitState.STATE_MEET_ENEMY:
			if(!isMeetEnemy) {
				setUnitState(UnitState.STATE_WALK);
			}
			break;
		}
	}
}
