package com.team.koosungwon.game.view;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.team.koosungwon.game.model.unit.MonsterUnit;
import com.team.koosungwon.game.model.unit.PlayerUnit;
import com.team.koosungwon.game.model.unit.SupporterUnit;
import com.team.koosungwon.game.utils.PlatformerResourceManager;
import com.team.koosungwon.game.view.actor.Card;
import com.team.koosungwon.game.view.actor.InfoBar;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.ImageItem;
import com.uwsoft.editor.renderer.script.SimpleButtonScript;

public class FightUIView extends Stage {

	//shapeRenderer
	ShapeRenderer shapeRenderer;

	//font
	LabelStyle infoLabelStyle;

	private PlayerUnit playerUnit;
	private SupporterUnit supporterUnit;
	private ArrayList<MonsterUnit> monsterUnitArray;

	//player
	private Card playerCard;
	private InfoBar playerHpBar;
	private InfoBar playerMpBar;

	//supporter
	private Card supporterCard;
	private InfoBar supporterHpBar;

	private SimpleButtonScript supporterSkillScript;
	private SimpleButtonScript supporterFrontScript;
	private SimpleButtonScript supporterBackScript;

	//wave
	private Label waveLabel;

	//minimap
	private MiniMapView minimapView;

	public FightUIView(){super();}

	public FightUIView(SceneLoader loader, PlatformerResourceManager resourceManager) {
		super(new StretchViewport(resourceManager.currentResolution.width, resourceManager.currentResolution.height));

		loader.loadScene("FightUiScene");

		//shapeRenderer
		shapeRenderer = new ShapeRenderer();

		//font
		infoLabelStyle = new LabelStyle(new BitmapFont(Gdx.files.internal("fonts/font.fnt")), Color.WHITE);

		//전체 UI
		addActor(loader.getRoot());

		//player ui
		initialPlayerInfo(loader);

		//supporter ui
		initialSupporterInfo(loader);
		setSupporterButtonScript(loader);

		//wave label
		initialWaveText(loader);

		//minimap ui
		initialMinimapView(loader);
		
		
	}

	public void setPlayerUnit(PlayerUnit playerUnit) {
		this.playerUnit = playerUnit;
		minimapView.setPlayerUnit(playerUnit);
	}

	//서포터 캐릭터 스킬, 전위 후위 이동을 위해 받아온다.
	public void setSupporterUnit(SupporterUnit supporterUnit) {
		this.supporterUnit = supporterUnit;
	}

	public void setMonsterUnitArray(ArrayList<MonsterUnit> monsterUnitArray) {
		this.monsterUnitArray = monsterUnitArray;
		minimapView.setMonsterUnitArray(monsterUnitArray);
	}

	private void initialPlayerInfo(SceneLoader loader) {
		CompositeItem playerInfo = loader.getCompositeElementById("playerInfo");

		ImageItem unitTexture = playerInfo.getImageById("unitTexture");
		ImageItem hpbackground = playerInfo.getImageById("hpBackground");
		ImageItem mpbackground = playerInfo.getImageById("mpBackground");

		//기준점은 부모
		Rectangle playerUnitTextureBound = new Rectangle(playerInfo.getX() + unitTexture.getX(), playerInfo.getY() + unitTexture.getY(), 
				unitTexture.getWidth() * unitTexture.getScaleX(), unitTexture.getHeight() * unitTexture.getScaleY());

		Rectangle hpBarBackgroundBound = new Rectangle(playerInfo.getX() + hpbackground.getX(), playerInfo.getY() + hpbackground.getY(), 
				hpbackground.getWidth() * hpbackground.getScaleX(), hpbackground.getHeight() * hpbackground.getScaleY());

		Rectangle mpBarBackgroundBound = new Rectangle(playerInfo.getX() + mpbackground.getX(), playerInfo.getY() + mpbackground.getY(), 
				mpbackground.getWidth() * mpbackground.getScaleX(), mpbackground.getHeight() * mpbackground.getScaleY());

		playerCard = new Card();
		playerCard.setBound(playerUnitTextureBound);
		playerCard.setTexture("player.png");

		playerHpBar = new InfoBar();
		playerHpBar.setBarBound(hpBarBackgroundBound);
		playerHpBar.setBarTexture("hp.png");
		playerHpBar.setBarBackgroundBound(hpBarBackgroundBound);
		playerHpBar.setBarBacgroundTexture("hpbackground.png");
		playerHpBar.initialLabel(hpBarBackgroundBound, infoLabelStyle);
		playerHpBar.setTextScale(0.5f);

		playerMpBar = new InfoBar();
		playerMpBar.setBarBound(mpBarBackgroundBound);
		playerMpBar.setBarTexture("mp.png");
		playerMpBar.setBarBackgroundBound(mpBarBackgroundBound);
		playerMpBar.setBarBacgroundTexture("hpbackground.png");
		playerMpBar.initialLabel(mpBarBackgroundBound, infoLabelStyle);
		playerMpBar.setTextScale(0.5f);

		addActor(playerCard);
		addActor(playerHpBar);
		addActor(playerMpBar);
	}

	//user의 hp와 mp 상태를 나타낸다.
	public void setPlayerInfo() {
		playerHpBar.setScaleX((playerUnit.getHp() / (float)playerUnit.initialHp));
		playerMpBar.setScaleX((playerUnit.getMp() / (float)playerUnit.initialMp));

		playerHpBar.setText(playerUnit.getHp() + "");
		playerMpBar.setText(playerUnit.getMp() + "");
	}

	//서포터의 정보를 나타낸다.
	private void initialSupporterInfo(SceneLoader loader) {
		CompositeItem supporterInfo = loader.getCompositeElementById("supporterInfo");

		ImageItem unitTexture = supporterInfo.getImageById("unitTexture");
		ImageItem hpbackground = supporterInfo.getImageById("hpBackground");

		//기준점은 부모
		Rectangle supporterUnitTextureBound = new Rectangle(supporterInfo.getX() + unitTexture.getX(), supporterInfo.getY() + unitTexture.getY(), 
				unitTexture.getWidth() * unitTexture.getScaleX(), unitTexture.getHeight() * unitTexture.getScaleY());

		Rectangle hpBarBackgroundBound = new Rectangle(supporterInfo.getX() + hpbackground.getX(), supporterInfo.getY() + hpbackground.getY(), 
				hpbackground.getWidth() * hpbackground.getScaleX(), hpbackground.getHeight() * hpbackground.getScaleY());

		supporterCard = new Card();
		supporterCard.setBound(supporterUnitTextureBound);
		supporterCard.setTexture("supporter.png");

		supporterHpBar = new InfoBar();
		supporterHpBar.setBarBound(hpBarBackgroundBound);
		supporterHpBar.setBarTexture("hp.png");
		supporterHpBar.setBarBackgroundBound(hpBarBackgroundBound);
		supporterHpBar.setBarBacgroundTexture("hpbackground.png");
		supporterHpBar.initialLabel(hpBarBackgroundBound, infoLabelStyle);
		supporterHpBar.setTextScale(0.5f);

		addActor(supporterCard);
		addActor(supporterHpBar);
	}

	//supporter의 hp와 mp 상태를 나타낸다.
	public void setSupporterInfo() {
		supporterHpBar.setScaleX((supporterUnit.getHp() / (float)supporterUnit.initialHp));
		supporterHpBar.setText(supporterUnit.getHp() + "");
	}

	private void setSupporterButtonScript(SceneLoader loader) {
		supporterSkillScript = SimpleButtonScript.selfInit(loader.getRoot().getCompositeById("supporterSkill"));
		supporterSkillScript.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				supporterUnit.clickSkill();
			}
		});

		supporterFrontScript = SimpleButtonScript.selfInit(loader.getRoot().getCompositeById("supporterFront"));
		supporterFrontScript.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				supporterUnit.clickFrontBack(1);
			}
		});

		supporterBackScript = SimpleButtonScript.selfInit(loader.getRoot().getCompositeById("supporterBack"));
		supporterBackScript.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				supporterUnit.clickFrontBack(2);
			}
		});
	}

	//Wave
	public void setWaveIndex(int waveIndex) {
		if(waveIndex > 0) {
			waveLabel.setText("Wave " + waveIndex);
		}
	}

	private void initialWaveText(SceneLoader loader) {
		CompositeItem waveBackground = loader.getRoot().getCompositeById("waveInfo");

		LabelStyle waveLabelStyle = new LabelStyle(new BitmapFont(Gdx.files.internal("fonts/font.fnt")), Color.WHITE);

		waveLabel = new Label("Wave start", waveLabelStyle);
		waveLabel.setBounds(waveBackground.getX(), waveBackground.getY(), waveBackground.getWidth(), waveBackground.getHeight());
		waveLabel.setAlignment(Align.center);

		addActor(waveLabel);
	}

	//미니맵 백그라운드 크기를 확인한다.
	public MiniMapView getMiniMapView() {
		return minimapView;
	}

	private void initialMinimapView(SceneLoader loader) {
		CompositeItem minimapBackground = loader.getRoot().getCompositeById("minimapBackground");

		minimapView = new MiniMapView(new Rectangle(minimapBackground.getX(), minimapBackground.getY(), 
				minimapBackground.getWidth(), minimapBackground.getHeight()), shapeRenderer);

		addActor(minimapView);
	}
}
