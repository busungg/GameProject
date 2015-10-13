package com.team.koosungwon.game.definition;

/**
 * 디버프 종류
 * 0 : 빙결 - 일정시간동안 speed = 0;
 * 1 : 절단 - 일정시간동안 speed = 0;
 * 2 : 마비 - 일정시간동안 speed = 0;
 * 3 : 화상 - 일정시간동안 hp 깍임;
 * 4 : 출혈 - 일정시간동안 hp 깍임;
 * 5 : 중독 - 일정시간동안 hp 깍임;
 */


public class DebuffState {
	public static final int DEBUFF_FROZEN = 0;
	public static final int DEBUFF_CUT = 1;
	public static final int DEBUFF_PARALYZE = 2;
	public static final int DEBUFF_BURN = 3;
	public static final int DEBUFF_BLEEDING = 4;
	public static final int DEBUFF_POISON = 5;
}
