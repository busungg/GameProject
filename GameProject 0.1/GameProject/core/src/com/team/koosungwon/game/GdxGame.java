package com.team.koosungwon.game;

import com.badlogic.gdx.Game;
import com.team.koosungwon.game.screen.GameScreen;

public class GdxGame extends Game {
	
	private GameScreen gameScreen;
	
	@Override
	public void create () {
		
		gameScreen = new GameScreen(this);
		setScreen(gameScreen);
	}
}
