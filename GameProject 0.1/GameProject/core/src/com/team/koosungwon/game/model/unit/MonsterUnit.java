package com.team.koosungwon.game.model.unit;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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

public class MonsterUnit extends BaseUnitModel {

	//assetManager
	public boolean isLoading;
	private AssetManager assetManager;
	
	private String spineAtlas;
	private String spineSkeleton;
	
	//스킬
	private Skill skill;

	//AI lua
	private LuaValue checkDistance;
	private LuaValue returnValue;

	public MonsterUnit() {super();}

	public MonsterUnit(String unitId, float unitPosX, float unitPosY) {
		super();

		isLoading = false;
		assetManager = new AssetManager();
		
		unitPos = new Vector2(unitPosX, unitPosY);

		loadLua("unit/monster/lua/" + unitId + ".lua");
		checkDistance = globals.get("checkDistance");

		skill = new Skill();

		loadAsset(unitId);
	}

	@Override
	protected void loadAsset(String unitId) {
		if(Gdx.files.internal("unit/monster/json/" + unitId + ".json").exists()) {
			FileHandle file = Gdx.files.internal("unit/monster/json/" + unitId + ".json");

			JsonReader jsonReader = new JsonReader();
			JsonValue jsonValue = jsonReader.parse(file);

			hp = jsonValue.getInt("hp");
			attack = jsonValue.getFloat("attack");
			speed = jsonValue.getFloat("speed") * -1;
			protect = jsonValue.getFloat("protect");
			nuckbackResist = jsonValue.getFloat("nuckbackResist");
			spineAtlas = jsonValue.getString("spineAtlas");
			spineSkeleton = jsonValue.getString("spineSkeleton");
			
			assetManager.load(spineAtlas, TextureAtlas.class);
		}
	}
	
	public void updateAsset() {
		if(assetManager.update()) {
			loadSpine(null);
		}
	}

	@Override
	protected void loadSpine(JsonValue jsonValue) {
		atlas = assetManager.get(spineAtlas, TextureAtlas.class);
		SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
		json.setScale(0.35f); // Load the skeleton at 60% the size it was in Spine.
		
		SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(spineSkeleton));
		
		skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
		skeleton.setPosition(unitPos.x, unitPos.y);
		skeleton.setFlipX(true);

		AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

		state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).

		//공격애니메이션 적용
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
		
		initialize();
		isLoading = true;
	}

	@Override
	public Skill getActivateSkill() {
		return skill;
	}

	@Override
	protected void setUnitState(int unitState) {
		if(this.unitState == unitState) return;

		//walk 일때 meet을 확인
		if(unitState == UnitState.STATE_WALK) {
			if(isMeetEnemy) {
				unitState = UnitState.STATE_MEET_ENEMY;
			}
		}

		this.unitState = unitState;
		state.clearTracks();

		//애니메이션을 세팅한다.
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
		}
	}

	@Override
	public void act(Vector2 playerUnitPos, Vector2 supporterUnitPos) {
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
			if(nuckback > 0) {
				if(nuckback < nuckbackSpeed){
					nuckback = 0;
					break;
				}

				unitPos.x += nuckbackSpeed;
				nuckback -= nuckbackSpeed;
				skeleton.setX(unitPos.x);
			} else {
				if(isMove) return;
				setUnitState(UnitState.STATE_WALK);
			}
			return;
			
		case UnitState.STATE_ATTACK:
			return;
		}
		
		//IsMeet 판단
		Vector2 enemyPos;

		float playerDistance = unitPos.x - playerUnitPos.x;
		float supporterDistance = unitPos.x - supporterUnitPos.x;	
		enemyPos = playerDistance < supporterDistance ? playerUnitPos : supporterUnitPos;
		
		//적과의 거리를 확인한다.
		if(UnitController.MEET_BOUND_WIDTH > unitPos.x - enemyPos.x) {
			isMeetEnemy = true;
		} else {
			isMeetEnemy = false;
		}

		//딜레이 타임을 적용
		if(System.currentTimeMillis() - beforeDelayTime > delayTime) {
			beforeDelayTime = System.currentTimeMillis();	
		} else {
			return;
		}

		//루아로 스테이트를 적용한다.
		if( !checkDistance.isnil() ) {
			returnValue = checkDistance.call(CoerceJavaToLua.coerce(enemyPos), CoerceJavaToLua.coerce(unitPos), 
					CoerceJavaToLua.coerce(skill));

			//스테이트를 넘겨준다.
			setUnitState(returnValue.toint());
		}
	}
}
