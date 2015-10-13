package com.team.koosungwon.game.model;

public class Skill {
	//공통
	public String animationState; //애니메이션
	public String effect; //effect
	public float magnification; //배수
	public float minimumRange; //스킬최소거리
	public float maximumRange; //스킬최대거리
	public float nuckback; //뒤로 날라가는 거리
	public float nuckbackSpeed; //뒤로 날라가는 속도
	public int buffType; //버프 종류
	public int buffTime; //버프 지속시간
	public int isBuff; //0: 일반공격, 1: 버프 2: 디버프
	public int target; // 0:몬스터 1: 플레이어 팀

	//몬스터 및 서포터
	public int delay; //몬스터 및 서포터의 스킬 이용후 delay

	//플레이어
	public float mpWastage; //mp 소모량
	public int unlock; //플레이어 스킬 언락 서포터는 버프형 스킬(유저가 사용해야하는 스킬을 확인하는 용도)
	public String spell; //플레이어 스펠식

	public Skill() {super();}

	public Skill(Skill skill) {
		//공통
		this.animationState = skill.animationState;
		this.effect = skill.effect;
		this.magnification = skill.magnification;
		this.minimumRange = skill.minimumRange;
		this.maximumRange = skill.maximumRange;
		this.nuckback = skill.nuckback;
		this.nuckbackSpeed = skill.nuckbackSpeed;
		this.buffType = skill.buffType;
		this.buffTime = skill.buffTime;
		this.isBuff = skill.isBuff;
		this.target = skill.target;

		//서포터 및 몬스터
		this.delay = skill.delay;

		//플레이어
		this.mpWastage = skill.mpWastage;
		this.unlock = skill.unlock;
		this.spell = skill.spell;
	}
}
