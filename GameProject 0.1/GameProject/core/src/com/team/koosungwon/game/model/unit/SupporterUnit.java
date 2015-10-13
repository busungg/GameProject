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
import com.team.koosungwon.game.controller.UnitController;
import com.team.koosungwon.game.definition.UnitState;
import com.team.koosungwon.game.model.Skill;

public class SupporterUnit extends BaseUnitModel {
	
	private final int POS_FRONT = 1;
	private final int POS_BACK = 2;
	
	public boolean isPlayerCasting;
	public boolean isFrontBack; //전위 후위 이동중인지 확인
	private int frontBack; //전위 후위 이동 1 : 전위 2: 후위
	
	private float playerSpeed;
	
	private int activateSkillIndex;
	private ArrayList<Skill> skillArray;

	public SupporterUnit() {super();}

	public SupporterUnit(String unitId, float unitPosX, float unitPosY, int frontBack) {
		super();

		isPlayerCasting = false;
		isFrontBack = false;
		this.frontBack = frontBack;
		
		activateSkillIndex = -1;
		skillArray = new ArrayList<Skill>();

		unitPos = new Vector2(unitPosX, unitPosY);
		
		loadAsset(unitId);
		initialize();
	}

	@Override
	protected void loadAsset(String unitId) {
		if(Gdx.files.internal("unit/supporter/json/" + unitId + ".json").exists()) {
			FileHandle file = Gdx.files.internal("unit/supporter/json/" + unitId + ".json");

			JsonReader jsonReader = new JsonReader();
			JsonValue jsonValue = jsonReader.parse(file);

			hp = jsonValue.getInt("hp");
			attack = jsonValue.getFloat("attack");
			speed = jsonValue.getFloat("speed");
			protect = jsonValue.getFloat("protect");
			nuckbackResist = jsonValue.getFloat("nuckbackResist");

			JsonValue jsonValueArray = jsonValue.get("skills");
			Skill skill;
			for(JsonValue skillJsonValue : jsonValueArray) {
				skill = new Skill();
				
				skill.animationState = skillJsonValue.getString("animationState");
				skill.effect = skillJsonValue.getString("effect");
				if(skill.effect.length() == 0) { //effect가 없을때
					skill.effect = null;
				}
				skill.magnification = skillJsonValue.getFloat("magnification");
				skill.minimumRange = skillJsonValue.getFloat("minimumRange");
				skill.maximumRange = skillJsonValue.getFloat("maximumRange");
				skill.nuckback = skillJsonValue.getFloat("nuckback");
				skill.nuckbackSpeed = skillJsonValue.getFloat("nuckbackSpeed");
				skill.buffType = skillJsonValue.getInt("buffType");
				skill.buffTime = skillJsonValue.getInt("buffTime");
				skill.isBuff = skillJsonValue.getInt("isBuff");
				skill.target = skillJsonValue.getInt("target");
				skill.unlock = skillJsonValue.getInt("unlock");
				skill.delay = skillJsonValue.getInt("delay");
				
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

		state.addListener(new AnimationStateListener(){

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
	
	public void setPlayerSpeed(float playerSpeed) {
		this.playerSpeed = playerSpeed;
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

		case UnitState.STATE_MEET_ENEMY:
			state.addAnimation(UnitState.STATE_MEET_ENEMY, "idle", true, 0);
			break;

		case UnitState.STATE_HIT:
			state.addAnimation(UnitState.STATE_HIT, "hit", false, 0);
			break;

		case UnitState.STATE_ATTACK:
			state.addAnimation(UnitState.STATE_ATTACK, getActivateSkill().animationState, false, 0);
			break;
			
		case UnitState.STATE_FRONT_BACK:
			if(frontBack == POS_FRONT) {
				skeleton.setFlipX(false);
			} else {
				skeleton.setFlipX(true);
			}
			
			isFrontBack = true;
			state.addAnimation(UnitState.STATE_FRONT_BACK, "run", true, 0);
			break;
		}
	}
	
	//스킬 버튼을 클릭시
	public void clickSkill() {
		if(isFrontBack) return;
		
		for(int i = 0; i < skillArray.size(); i ++) {
			if(skillArray.get(i).unlock == 0) {
				activateSkillIndex = i;
				setUnitState(UnitState.STATE_ATTACK);
				return;
			}
		}
	}
	
	//Front Back 버튼을 클릭시
	public void clickFrontBack(int frontBack) {
		if(isFrontBack || isMeetEnemy) return;
		
		this.frontBack = frontBack;
		setUnitState(UnitState.STATE_FRONT_BACK);
	}
	
	
	//스킬을 정한다.
	private void checkDistance(Vector2 monsterUnitPos) {
		for(int i = 0; i < skillArray.size(); i ++) {
			if(skillArray.get(i).unlock == 1) {
				if(monsterUnitPos.x < skillArray.get(i).maximumRange + unitPos.x &&
						monsterUnitPos.x > skillArray.get(i).minimumRange + unitPos.x) {
					activateSkillIndex = i;
					setUnitState(UnitState.STATE_ATTACK);
					return;
				}
			}
		}
	}
	
	//전위 후위 이동애니메이션
	private void moveFrontBack(float front, float back) {
		if(isMeetEnemy) {
			setUnitState(UnitState.STATE_MEET_ENEMY);
		}
		
		switch(frontBack) {
		case POS_FRONT:
			if(posDistance(front) > 10) {
				unitPos.x += speed;
			} else {
				isFrontBack = false;
				setUnitState(UnitState.STATE_WALK);
			}
			break;
			
		case POS_BACK:
			if(posDistance(back) > 10) {
				unitPos.x += (-speed);
			} else {
				isFrontBack = false;
				setUnitState(UnitState.STATE_WALK);
			}
			break;
		}
		
		skeleton.setX(unitPos.x);
	}
	
	private float posDistance(float pos) {
		return Math.abs(pos - unitPos.x);
	}

	@Override
	public void act(float front, float back, Vector2 monsterUnitPos) {
		checkBuff();
		isMove = checkDebuff();
		
		switch(unitState) {
		case UnitState.STATE_WALK:
			if(isMeetEnemy) {
				setUnitState(UnitState.STATE_MEET_ENEMY);
			} else if(frontBack == POS_FRONT) { //수정이 필요하다
				if(front - unitPos.x < 1) {
					isFrontBack = false;
					skeleton.setFlipX(false);
					unitPos.x += playerSpeed;
				} else if(front >= unitPos.x) {
					isFrontBack = true;
					skeleton.setFlipX(false);
					unitPos.x += speed;
				} else { //귀소본능
					isFrontBack = true;
					skeleton.setFlipX(true);
					unitPos.x -= speed;
				}
				skeleton.setX(unitPos.x);
			} else if(frontBack == POS_BACK) {
				if(back - unitPos.x < 1) {
					isFrontBack = false;
					skeleton.setFlipX(false);
					unitPos.x += playerSpeed;
				} else if(back >= unitPos.x) {
					isFrontBack = true;
					skeleton.setFlipX(false);
					unitPos.x += speed;
				} else { //귀소본능 캐릭터의 속도에 맞춘다.
					isFrontBack = true;
					skeleton.setFlipX(true);
					unitPos.x -= speed;
				}
				skeleton.setX(unitPos.x);
			}
			break;

		case UnitState.STATE_HIT:
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
			
		case UnitState.STATE_ATTACK:
			return;
		
		case UnitState.STATE_FRONT_BACK:
			moveFrontBack(front, back);
			return;
		}
		
		if(monsterUnitPos != null) {
			//적과의 거리를 확인한다.
			if(UnitController.MEET_BOUND_WIDTH > monsterUnitPos.x - unitPos.x) {
				isMeetEnemy = true;
			} else {
				isMeetEnemy = false;
			}
		} else {
			isMeetEnemy = false;
		}
		
		if(!isMeetEnemy && isPlayerCasting) {
			isMeetEnemy = true;
		}
		
		//딜레이 타임을 적용
		if(System.currentTimeMillis() - beforeDelayTime > delayTime) {
			beforeDelayTime = System.currentTimeMillis();	
			if(monsterUnitPos != null) {
				checkDistance(monsterUnitPos);
			}
		}
	}
}
