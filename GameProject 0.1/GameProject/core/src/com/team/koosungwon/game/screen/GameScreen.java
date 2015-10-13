package com.team.koosungwon.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.team.koosungwon.game.base.screen.BaseScreen;
import com.team.koosungwon.game.camera.CameraUtil;
import com.team.koosungwon.game.controller.UnitController;
import com.team.koosungwon.game.definition.UnitState;
import com.team.koosungwon.game.utils.PlatformerResourceManager;
import com.team.koosungwon.game.view.FightUIView;
import com.team.koosungwon.game.view.MapView;
import com.team.koosungwon.game.view.SpellTimerView;
import com.team.koosungwon.game.view.SpellView;
import com.uwsoft.editor.renderer.SceneLoader;

public class GameScreen extends BaseScreen implements InputProcessor {

	//fps 로그 확인
	private boolean isStageEnd;
	private FPSLogger fpsLogger;

	private SpriteBatch batch;
	private InputMultiplexer inputMultiplexer;

	private PlatformerResourceManager resourceManager;
	private SceneLoader loader;

	//타이머
	private Timer timer;
	private int passTime;

	//Ui view
	private CameraUtil cameraUtil;
	private MapView mapView;
	private SpellView spellView;
	private FightUIView fightUiView;
	private SpellTimerView spellTimerView;

	//controller
	private UnitController unitController;

	public GameScreen(Game game) {
		super(game);

		isStageEnd = false;
		fpsLogger = new FPSLogger();

		batch = new SpriteBatch();

		//UI 해상도
		resourceManager = new PlatformerResourceManager();
		resourceManager.initPlatformerResources();

		//Overlap2d loader
		loader = new SceneLoader(resourceManager);
		loader.setResolution(resourceManager.currentResolution.name);

		//타이머
		timer = new Timer();

		//UI view
		cameraUtil = new CameraUtil();
		fightUiView = new FightUIView(loader, resourceManager);
		mapView = new MapView("stage_1");
		spellView = new SpellView();
		spellTimerView = new SpellTimerView();

		//controller
		unitController = new UnitController("stage_1");

		//Input 처리
		inputMultiplexer = new InputMultiplexer();
	}

	@Override
	public void show () {

		//인풋
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(fightUiView);
		Gdx.input.setInputProcessor(inputMultiplexer);

		fightUiView.setPlayerUnit(unitController.getPlayerUnit());
		fightUiView.setSupporterUnit(unitController.getSupporterUnit());
		fightUiView.setMonsterUnitArray(unitController.getMonsterArray());

		//미니맵
		fightUiView.getMiniMapView().setMapWidth(mapView.getMapSize());

		//카메라
		cameraUtil.setPlayerDistance(unitController.getPlayerPos().x);
		cameraUtil.setLimitBound(mapView.getMapSize());

		//스펠타이머
		spellTimerView.setSpellTime(unitController.getPlayerUnit().getCastingTime());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		cameraUtil.update();
		batch.setProjectionMatrix(cameraUtil.getCameraCombined());

		mapView.render(batch);
		unitController.render(batch, delta);

		if(!isStageEnd) {
			unitController.act();
			cameraUtil.chasePlayer(unitController.getPlayerPos().x);
		}

		//UI
		batch.setProjectionMatrix(cameraUtil.getCameraUiCombined());

		fightUiView.act();
		fightUiView.setWaveIndex(unitController.waveIndex);
		fightUiView.setPlayerInfo();
		fightUiView.setSupporterInfo();
		fightUiView.draw();

		spellView.render(batch);
		spellTimerView.render(batch);

		if(!isStageEnd) {
			checkCasting();
			checkGameState();
		} else { //게임 결과 화면을 보여준다.

		}

		//output current FPS
		fpsLogger.log();
	}

	//게임 승리 패배를 확인한다.
	private void checkGameState() {	
		if(unitController.getPlayerUnit().getHp() <= 0) { //패배
			Gdx.app.log("Error", "player defeat");
			isStageEnd = true;
		} else if(unitController.getWaveinfoArraySize() == 0 && unitController.getMonsterUnitArraySize() == 0) { //승
			Gdx.app.log("Error", "player win");
			isStageEnd = true;
		}
	}

	//casting 중 hit을 확인한다.
	private void checkCasting() {
		if(spellTimerView.getIsRender()) {
			if(unitController.getPlayerUnit().getUnitState() != UnitState.STATE_CASTING) {
				castingFail();
			}
		}
	}

	//casting time을 확인한다.
	private void spellTimerThread() {
		passTime = 0;

		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				spellTimerView.setPassTime(passTime);
				passTime += 1;

				if(spellTimerView.getSpellTime() + 1 == passTime) {
					if(unitController.getPlayerUnit().getUnitState() == UnitState.STATE_CASTING) {
						castingFail();
					}
				}
			}
		}, 0, 1, spellTimerView.getSpellTime());
	}

	//casting 실패시
	private void castingFail() {
		spellView.touchUp(0, 0, 0, 0);
		unitController.getPlayerUnit().castingPenalty();
		unitController.getSupporterUnit().isPlayerCasting = false;
		spellTimerView.isRender(false);
		timer.clear();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(unitController.getPlayerUnit().getUnitState() != UnitState.STATE_WALK && 
				unitController.getPlayerUnit().getUnitState() != UnitState.STATE_MEET_ENEMY) return false;

		if(spellView.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button)) {
			unitController.getPlayerUnit().isCasting();
			unitController.getSupporterUnit().isPlayerCasting = true;
			spellTimerThread();
			spellTimerView.setCenter(new Vector2(screenX, Gdx.graphics.getHeight() - screenY + (70 * Gdx.graphics.getDensity())));
			spellTimerView.isRender(true);
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(spellView.isFirstSpellTouch) {
			String spell = spellView.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
			unitController.getPlayerUnit().casting(spell);
			unitController.getSupporterUnit().isPlayerCasting = false;
			spellTimerView.isRender(false);
			timer.clear();
		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(spellView.isFirstSpellTouch) {
			spellView.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
			spellTimerView.setCenter(new Vector2(screenX, Gdx.graphics.getHeight() - screenY + (70 * Gdx.graphics.getDensity())));
		}

		return true;
	}

	@Override
	public boolean keyDown(int keycode) {return false;}

	@Override
	public boolean keyUp(int keycode) {return false;}

	@Override
	public boolean keyTyped(char character) {return false;}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {return false;}

	@Override
	public boolean scrolled(int amount) {return false;}

}
