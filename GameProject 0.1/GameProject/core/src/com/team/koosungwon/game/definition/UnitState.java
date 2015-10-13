package com.team.koosungwon.game.definition;

public class UnitState {
	
	//공통
	public static final int STATE_WALK = 0;
	public static final int STATE_HIT = 1;
	public static final int STATE_ATTACK = 2;
	public static final int STATE_MEET_ENEMY = 3;
	
	//플레이어
	public static final int STATE_CASTING = 4;
	public static final int STATE_CASTING_PENALTY = 5;
	
	//서포터
	public static final int STATE_FRONT_BACK = 6;
}
