package com.team.koosungwon.game.controller;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.team.koosungwon.game.model.Skill;
import com.team.koosungwon.game.model.unit.MonsterUnit;
import com.team.koosungwon.game.model.unit.PlayerUnit;
import com.team.koosungwon.game.model.unit.SupporterUnit;
import com.team.koosungwon.game.utils.ParticleEffectManager;

public class UnitController {

	//상단의 HP바 크기
	private final float WAVE_BOUND_WIDTH = 800 * Gdx.graphics.getDensity();

	public static final float MEET_BOUND_WIDTH = 70 * Gdx.graphics.getDensity();
	private final float FRONT_BACK_RANGE = 50 * Gdx.graphics.getDensity();

	//전위 후위 거리
	private float front, back;

	public int waveIndex;
	private ArrayList<WaveInfo> waveinfoArray; //wave 정보를 담고 있는 list

	private SkeletonRenderer renderer;

	private PlayerUnit playerUnit;
	private SupporterUnit supporterUnit;
	private ArrayList<MonsterUnit> monsterUnitArray;
	private MonsterUnit loadMonsterUnit;

	private long beforeloadDelay;
	private final long loadDelay  = 500; 

	private ArrayList<ActiveSkill> activeSkillArray;

	private ParticleEffectManager particleEffectManager;

	private UnitInfoController unitInfoController;

	public UnitController() {super();}

	//loadasset 필요
	public UnitController(String stageId) {
		renderer = new SkeletonRenderer();
		renderer.setPremultipliedAlpha(true); // PMA results in correct blending without outlines.

		waveIndex = 0;

		waveinfoArray = new ArrayList<UnitController.WaveInfo>();
		monsterUnitArray = new ArrayList<MonsterUnit>();

		activeSkillArray = new ArrayList<ActiveSkill>();
		particleEffectManager = new ParticleEffectManager();

		loadAsset(stageId);

		unitInfoController = new UnitInfoController();
		unitInfoController.setPlayerUnit(playerUnit);
		unitInfoController.setSupporterUnit(supporterUnit);
		unitInfoController.setMonsterUnitArray(monsterUnitArray);
	}

	private void loadAsset(String stageId) {
		FileHandle file = Gdx.files.internal("user/user_info.json");

		JsonReader jsonReader = new JsonReader();
		JsonValue jsonValue = jsonReader.parse(file);

		playerUnit = new PlayerUnit(jsonValue.getString("player"));

		//서포터는 일단 전위로 고정
		supporterUnit = new SupporterUnit(jsonValue.getString("supporter"), playerUnit.getPos().x + FRONT_BACK_RANGE, playerUnit.getPos().y + 100, 1);
		supporterUnit.setPlayerSpeed(playerUnit.getSpeed());

		//stage monster wave 파싱
		file = Gdx.files.internal("map/info/" + stageId + ".json");
		jsonValue = jsonReader.parse(file);

		JsonValue waveList = jsonValue.get("wave");
		JsonValue waveMonsterList;
		WaveInfo waveInfo;
		MonsterWaveInfo monsterWaveInfo;

		for(int i = 0; i < waveList.size; i++) {
			waveInfo = new WaveInfo();
			waveInfo.distance = waveList.get(i).getInt("waveDistance");
			waveInfo.check = false;

			waveMonsterList = waveList.get(i).get("waveMonster");

			for(int j = 0; j < waveMonsterList.size; j++) {
				monsterWaveInfo = new MonsterWaveInfo();
				monsterWaveInfo.id = waveMonsterList.get(j).getString("monsterId");
				monsterWaveInfo.yindex = waveMonsterList.get(j).getInt("yIndex");

				waveInfo.monsterWaveInfoArray.add(monsterWaveInfo);
			}

			waveinfoArray.add(waveInfo);
		}
	}

	//몬스터를 wave거리에 맞게 로딩한다.
	//Thread를 돌린다.
	private void checkWaveDistance() {
		for(int i = 0; i < waveinfoArray.size(); i++) {
			if(waveinfoArray.get(i).distance - playerUnit.getPos().x < WAVE_BOUND_WIDTH &&
					!waveinfoArray.get(i).check) {
				waveinfoArray.get(i).check = true;
				waveIndex++;
				break;
			}
		}

		//몬스터를 하나씩 순차적으로 로딩한다.
		createMonsterUnit();
	}

	//몬스터 생성
	private void createMonsterUnit() {
		WaveInfo waveInfo = null;
		for(int i = 0; i < waveinfoArray.size(); i++) {
			if(waveinfoArray.get(i).check) {
				waveInfo = waveinfoArray.get(i);
			}
		}

		if(waveInfo == null || loadMonsterUnit != null || System.currentTimeMillis() - beforeloadDelay < loadDelay) return;

		if(waveInfo.monsterWaveInfoArray.size() == 0) {
			waveinfoArray.remove(waveInfo);
			return;
		}

		MonsterWaveInfo monsterWaveInfo;
		monsterWaveInfo = waveInfo.monsterWaveInfoArray.get(0);

		switch(monsterWaveInfo.yindex) {
		case 1:
			loadMonsterUnit = new MonsterUnit(monsterWaveInfo.id, waveInfo.distance, playerUnit.getPos().y + 100);
			break;

		case 2:
			loadMonsterUnit = new MonsterUnit(monsterWaveInfo.id, waveInfo.distance, playerUnit.getPos().y + 50);
			break;

		case 3:
			loadMonsterUnit = new MonsterUnit(monsterWaveInfo.id, waveInfo.distance, playerUnit.getPos().y);
			break;

		case 4:
			loadMonsterUnit = new MonsterUnit(monsterWaveInfo.id, waveInfo.distance + 300, playerUnit.getPos().y + 100);
			break;

		case 5:
			loadMonsterUnit = new MonsterUnit(monsterWaveInfo.id, waveInfo.distance + 300, playerUnit.getPos().y);
			break;
		}

		waveInfo.monsterWaveInfoArray.remove(0);
	}

	private void updateLoadingMonster() {
		if(loadMonsterUnit == null) return;

		if(!loadMonsterUnit.isLoading) {
			loadMonsterUnit.updateAsset();
		} else {
			monsterUnitArray.add(loadMonsterUnit);
			loadMonsterUnit = null;
			beforeloadDelay = System.currentTimeMillis();
		}
	}

	public PlayerUnit getPlayerUnit() {
		return playerUnit;
	}

	public SupporterUnit getSupporterUnit() {
		return supporterUnit;
	}

	public ArrayList<MonsterUnit> getMonsterArray() {
		return monsterUnitArray;
	}

	public Vector2 getPlayerPos() {
		return playerUnit.getPos();
	}

	public int getWaveinfoArraySize() {
		return waveinfoArray.size();
	}

	public int getMonsterUnitArraySize() {
		return monsterUnitArray.size();
	}

	private void checkIsAlive() {
		MonsterUnit monsterUnit;
		for(int i = 0; i < monsterUnitArray.size(); i++) {
			monsterUnit = monsterUnitArray.get(i);

			if(monsterUnit.getHp() <= 0) {
				monsterUnitArray.remove(i);
				i--;
			}
		}
	}

	private void checkIsMeet() {
		//몬스터와 서포터는 AI에서 판단
		playerUnit.isMeetEnemy(false);
		for(MonsterUnit monsterUnit : monsterUnitArray) {
			if(monsterUnit.getPos().x - playerUnit.getPos().x < MEET_BOUND_WIDTH) {
				playerUnit.isMeetEnemy(true);
				return;
			}
		}
	}

	//공격 스킬이 있는지 확인
	private void checkIsActiveSkill() {
		if(particleEffectManager.isUserParticleFinish()) {
			if(playerUnit.isAttack()) {
				activeSkillArray.add(new ActiveSkill(playerUnit.getAttack(), playerUnit.getActivateSkill(), playerUnit.getPos(), 0));
				playerUnit.isAttackMotion = false;
				playerUnit.setIsAttack(false);
			}

			if(supporterUnit.isAttack()) {
				activeSkillArray.add(new ActiveSkill(supporterUnit.getAttack(), supporterUnit.getActivateSkill(), supporterUnit.getPos(), 1));
				supporterUnit.setIsAttack(false);
				supporterUnit.setDelayTime(supporterUnit.getActivateSkill().delay);
			}

			for(MonsterUnit monsterUnit : monsterUnitArray) {
				if(monsterUnit.isAttack()) {
					activeSkillArray.add(new ActiveSkill(monsterUnit.getAttack(), monsterUnit.getActivateSkill(), monsterUnit.getPos(), 2));
					monsterUnit.setIsAttack(false);
					monsterUnit.setDelayTime(monsterUnit.getActivateSkill().delay);
				}
			}
		}
	}

	private void checkIsAttack() {

		float minimumRange = 0;
		float maximumRange = 0;
		Vector2 startPos;
		Vector2 endPos;
		ActiveSkill activeSkill;

		for(int i = 0; i < activeSkillArray.size(); i++) {

			activeSkill = activeSkillArray.get(i);

			//이펙트가 null이면 벗어난다.
			if(activeSkill.skill.effect != null) {
				if(activeSkill.type != 2) {
					startPos = new Vector2(activeSkill.unitPos.x + activeSkill.skill.minimumRange, activeSkill.unitPos.y);
					endPos = new Vector2(activeSkill.unitPos.x + activeSkill.skill.maximumRange, activeSkill.unitPos.y);
				} else {
					startPos = new Vector2(activeSkill.unitPos.x - activeSkill.skill.minimumRange, activeSkill.unitPos.y);
					endPos = new Vector2(activeSkill.unitPos.x - activeSkill.skill.maximumRange, activeSkill.unitPos.y);
				}

				particleEffectManager.loadParticle(activeSkill.skill.effect, activeSkill.type, startPos, endPos);
			}

			//몬스터
			if(activeSkill.skill.target == 0) {

				minimumRange = activeSkill.unitPos.x + activeSkill.skill.minimumRange;
				maximumRange = activeSkill.unitPos.x + activeSkill.skill.maximumRange;

				for(MonsterUnit monsterUnit : monsterUnitArray) {
					if(monsterUnit.getPos().x < maximumRange && monsterUnit.getPos().x > minimumRange) {
						switch(activeSkill.skill.isBuff) {
						case 0:
							monsterUnit.isHit(activeSkill.attack, activeSkill.skill);
							break;
						case 1:
							monsterUnit.isBuff(activeSkill.attack, activeSkill.skill);
							break;
						case 2:
							monsterUnit.isDebuff(activeSkill.attack, activeSkill.skill);
							break;
						}
					}
				}
			} else { //플레이어 팀
				maximumRange = activeSkill.unitPos.x - activeSkill.skill.minimumRange;
				minimumRange = activeSkill.unitPos.x - activeSkill.skill.maximumRange;

				if(playerUnit.getPos().x < maximumRange && playerUnit.getPos().x > minimumRange) {
					switch(activeSkill.skill.isBuff) {
					case 0:
						playerUnit.isHit(activeSkill.attack, activeSkill.skill);
						break;
					case 1:
						playerUnit.isBuff(activeSkill.attack, activeSkill.skill);
						break;
					case 2:
						playerUnit.isDebuff(activeSkill.attack, activeSkill.skill);
						break;
					}
				}

				if(supporterUnit.getPos().x < maximumRange && supporterUnit.getPos().x > minimumRange) {
					switch(activeSkill.skill.isBuff) {
					case 0:
						supporterUnit.isHit(activeSkill.attack, activeSkill.skill);
						break;
					case 1:
						supporterUnit.isBuff(activeSkill.attack, activeSkill.skill);
						break;
					case 2:
						supporterUnit.isDebuff(activeSkill.attack, activeSkill.skill);
						break;
					}
				}
			}

			//사용한 스킬은 제거한다.
			activeSkillArray.remove(i);
			i--;
		}
	}

	public void act() {
		checkWaveDistance();
		updateLoadingMonster();

		checkIsAlive();
		checkIsMeet();
		checkIsActiveSkill();
		checkIsAttack();

		particleEffectManager.act();

		if(!particleEffectManager.isUserParticleFinish()) return;
		playerUnit.act();

		if(playerUnit.isAttackMotion) return;

		//서포터
		front =  playerUnit.getPos().x + FRONT_BACK_RANGE;
		back =  playerUnit.getPos().x - FRONT_BACK_RANGE;
		if(monsterUnitArray.size() > 0) {
			supporterUnit.act(front, back, monsterUnitArray.get(0).getPos());
		} else {
			supporterUnit.act(front, back, null);
		}

		//몬스터
		for(MonsterUnit monsterUnit : monsterUnitArray) {
			monsterUnit.act(playerUnit.getPos(), supporterUnit.getPos());
		}
	}

	public void render(SpriteBatch batch, float deltaTime) {
		supporterUnit.render(renderer, batch, deltaTime);
		playerUnit.render(renderer, batch, deltaTime);
		for(MonsterUnit monsterUnit : monsterUnitArray) {
			monsterUnit.render(renderer, batch, deltaTime);
		}
		//각 캐릭터의 hpbar를 그린다.
		unitInfoController.renderHpBar(batch);
		unitInfoController.renderDamageCounter(batch);
		particleEffectManager.render(batch, deltaTime);
	}

	//active 스킬
	public class ActiveSkill {

		public float attack;
		public Skill skill;
		public Vector2 unitPos;
		public int type; //0 : 주인공 1 : 서포터 2 : 몬스터

		public ActiveSkill(){};

		public ActiveSkill(float attack, Skill skill, Vector2 unitPos, int type) {
			this.attack = attack;
			this.skill = new Skill(skill);
			this.unitPos = new Vector2(unitPos);
			this.type = type;
		}
	}

	//웨이브 관련 클래스
	protected class WaveInfo {
		public float distance;
		public boolean check; //주인공이 wave를 지나갔는지 확인
		public ArrayList<MonsterWaveInfo> monsterWaveInfoArray;

		public WaveInfo() {
			monsterWaveInfoArray = new ArrayList<UnitController.MonsterWaveInfo>();
		}
	}

	//웨이브 몬스터 정보 클래스
	protected class MonsterWaveInfo {
		public String id;
		public int yindex;
	}
}
