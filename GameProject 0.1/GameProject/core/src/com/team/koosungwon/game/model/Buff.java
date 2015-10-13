package com.team.koosungwon.game.model;

public class Buff {
	
	public int buffType;
	public int buffTime;
	public int isBuff;
	public float buffFigure;
	
	public Buff() {super();}
	
	public Buff(int buffType, int buffTime, int isBuff, float buffFigure) {
		this.buffType = buffType;
		this.buffTime = buffTime;
		this.isBuff = isBuff;
		this.buffFigure = buffFigure;
	}
}
