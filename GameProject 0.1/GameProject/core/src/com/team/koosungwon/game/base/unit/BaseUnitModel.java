package com.team.koosungwon.game.base.unit;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.team.koosungwon.game.definition.BuffState;
import com.team.koosungwon.game.definition.DebuffState;
import com.team.koosungwon.game.definition.UnitState;
import com.team.koosungwon.game.model.Buff;
import com.team.koosungwon.game.model.DamageCounter;
import com.team.koosungwon.game.model.Skill;
import com.team.koosungwon.game.utils.LuaScriptManager;

abstract public class BaseUnitModel extends LuaScriptManager {

	abstract protected void loadAsset(String unitId);
	abstract protected void loadSpine(JsonValue jsonValue);
	abstract protected void setUnitState(int unitState);
	abstract public Skill getActivateSkill();

	protected int unitState; //스파인 애니메이션

	protected int hp;
	protected int mp;
	protected float attack; //공격력
	protected float speed; //이동속도
	protected float protect; //방어력
	protected float nuckbackResist; //넉백 저항

	protected boolean isAlive; //생존유무
	protected boolean isAttack; //공격
	protected boolean isMeetEnemy; //적과 조우
	protected boolean isMove; //움직임 가능(Debuff)
	protected long beforeDelayTime;
	protected int delayTime; //서포터 및 몬스터 딜레이타임
	
	protected float nuckback; //넉백 거리
	protected float nuckbackSpeed; //넉백 스피드

	protected Vector2 unitPos; //유닛 위치
	protected Vector2 unitHeadPos; //유닛 머리 위치
	
	protected ArrayList<Buff> buffArray; //버프 및 디버프 처리
	private Random random;
	private ArrayList<DamageCounter> damageCounterArray; //데미지 및 버프 표현처리

	public int initialHp;
	public int initialMp;
	
	//Spine 처리를 위한 변수
	protected TextureAtlas atlas;
	protected Skeleton skeleton;
	protected AnimationState state;
	protected Bone headBone;
	
	//hpbar 여유공간
	private final float headBoneMargin = 35 * Gdx.graphics.getDensity();

	protected BaseUnitModel() {
		super();
	}
	
	protected void initialize() {
		isAlive = true;
		isAttack = false;
		isMeetEnemy = false;
		
		initialHp = hp;
		initialMp = mp;
		
		buffArray = new ArrayList<Buff>();
		
		unitState = -1;
		setUnitState(UnitState.STATE_WALK);
		
		//unit info가 나올 위치를 정한다.
		skeleton.updateWorldTransform();
		headBone = skeleton.findBone("head");
		
		unitHeadPos = new Vector2();
		unitHeadPos.y = unitPos.y + headBone.getWorldY() + headBoneMargin;
		
		random = new Random();
		damageCounterArray = new ArrayList<DamageCounter>();
	}
	
	//유닛 헤드
	public void setUnitHeadPos() {
		unitHeadPos.x = unitPos.x;
	}
	
	public Vector2 getUnitHeadPos() {
		return unitHeadPos;
	}
	
	public ArrayList<DamageCounter> getDamageCounterArray() {
		return damageCounterArray;
	}
	
	public int getHp() {
		return hp;
	}
	
	public int getMp() {
		return mp;
	}
	
	public float getAttack() {
		return attack;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public int getUnitState() {
		return unitState;
	}

	public Vector2 getPos() {
		return unitPos;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public boolean isAttack() {
		return isAttack;
	}
	
	public void setIsAttack(boolean isAttack) {
		this.isAttack = isAttack;
	}

	public void isMeetEnemy(boolean isMeetEnemy) {
		this.isMeetEnemy = isMeetEnemy;
	}
	
	public boolean getIsMeetEnemy() {
		return isMeetEnemy;
	}

	//일반공격
	public void isHit(float damage, Skill skill) {
		this.nuckback = skill.nuckback - (skill.nuckback * nuckbackResist);
		this.nuckbackSpeed = skill.nuckbackSpeed;
		
		float realDamage = ((damage * skill.magnification) - ((damage * skill.magnification) * protect));
		damageCounterArray.add(new DamageCounter(new Vector2(unitPos.x + random.nextInt(50), unitHeadPos.y), realDamage, 2, System.currentTimeMillis()));
		
		hp = (int) (hp - realDamage) ;
		setUnitState(UnitState.STATE_HIT);
	}
	
	//기존의 버프를 확인한다.
	private boolean isExsistBuff(Skill skill) {
		
		Buff buff;
		for(int i = 0; i < buffArray.size(); i++) {
			buff = buffArray.get(i);
			
			if(buff.isBuff == skill.isBuff && buff.buffType == skill.buffType) {
				if(skill.buffTime > 0) {
					buff.buffTime = skill.buffTime;
				}
				return true;
			}
		}
		
		return false;
	}
	
	//버프일때 처리
	public void isBuff(float damage, Skill skill) {
		if(isExsistBuff(skill)) return;
		
		float buffFigure = (damage * skill.magnification);
		damageCounterArray.add(new DamageCounter(new Vector2(unitPos.x - random.nextInt(50), unitHeadPos.y), buffFigure, 1, System.currentTimeMillis()));
		
		switch(skill.buffType) {
		case BuffState.BUFF_HEAL:
			hp += buffFigure;
			if(hp > initialHp) {
				hp = initialHp;
			}
			
			break;
			
		case BuffState.BUFF_PROTECTION:
			protect += buffFigure;
			buffArray.add(new Buff(skill.buffType, skill.buffTime, skill.isBuff, buffFigure));
			break;
		}
	}
	
	//디버프일때 처리
	public void isDebuff(float damage, Skill skill) {
		this.nuckback = skill.nuckback - (skill.nuckback * nuckbackResist);
		this.nuckbackSpeed = skill.nuckbackSpeed;
		float buffFigure = (damage * skill.magnification);
		damageCounterArray.add(new DamageCounter(new Vector2(unitPos.x + random.nextInt(25), unitHeadPos.y), buffFigure, 2, System.currentTimeMillis()));
		hp -= buffFigure;
		setUnitState(UnitState.STATE_HIT);
		
		if(isExsistBuff(skill)) return;
		buffArray.add(new Buff(skill.buffType, skill.buffTime, skill.isBuff, buffFigure));
	}
	
	//버프 효과 처리
	public void checkBuff() {
		Buff buff;
		for(int i = 0; i < buffArray.size(); i++) {
			buff = buffArray.get(i);
			
			if(buff.isBuff != 1) continue;
			
			if(buff.buffTime > 0) {
				buff.buffTime -= 1;
			} else {
				if(buff.buffType == BuffState.BUFF_PROTECTION) {
					protect -= buff.buffFigure;
				}
				
				buffArray.remove(i);
				i--;
			}
		}
	}
	
	//디버프 처리
	public boolean checkDebuff() {
		boolean isPauseAnimation = false;
		Buff buff;
		
		for(int i = 0; i < buffArray.size(); i++) {
			buff = buffArray.get(i);
			
			if(buff.isBuff != 2) continue;
			
			if(buff.buffTime > 0) {
				buff.buffTime -= 1;
				
				if(buff.buffType >= DebuffState.DEBUFF_FROZEN &&
						buff.buffType <= DebuffState.DEBUFF_PARALYZE) {
					isPauseAnimation = true;
					
				} else if(buff.buffType >= DebuffState.DEBUFF_BURN && 
						buff.buffType <= DebuffState.DEBUFF_POISON) {
					
					if((buff.buffTime % 10) == 0) {
						hp -= buff.buffFigure;
						damageCounterArray.add(new DamageCounter(new Vector2(unitPos.x + random.nextInt(25), unitHeadPos.y), buff.buffFigure, 2, System.currentTimeMillis()));
					}
				}
			}
			
			if(buff.buffTime < 0) {
				buffArray.remove(i);
				i--;
			}
		}
		
		return isPauseAnimation;
	}
	
	//서포터 및 몬스터의 AI에 딜레이를 적용한다.
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	//player용 act
	public void act() {}

	//monster용 act
	public void act(Vector2 playerUnitPos, Vector2 supporterUnitPos) {}

	//supporter용 act
	public void act(float front, float back, Vector2 monsterUnitPos) {}
	
	public void render(SkeletonRenderer renderer, SpriteBatch batch, float deltaTime) {
		state.update(deltaTime);
		state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
		skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
		
		setUnitHeadPos();

		batch.begin();
		renderer.draw(batch, skeleton);
		batch.end();
	}
}
