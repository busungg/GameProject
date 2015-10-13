package com.team.koosungwon.game.view;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.team.koosungwon.game.model.unit.MonsterUnit;
import com.team.koosungwon.game.model.unit.PlayerUnit;

public class MiniMapView extends Actor {
	private float miniMapWidth;
	private float mapWidth;

	private Vector2 startPos;
	private Vector2 endPos;
	private Vector2 viewRangeEndPos;

	private PlayerUnit player;
	private ArrayList<MonsterUnit> monsterUnitArray;

	private ShapeRenderer shapeRenderer;

	private ArrayList<MinimapUnit> minimapUnitArray;

	public MiniMapView() {super();}

	public MiniMapView(Rectangle minimapBackground, ShapeRenderer shapeRenderer) {

		miniMapWidth = minimapBackground.width;

		startPos = new Vector2(minimapBackground.x, minimapBackground.y + (minimapBackground.height / 2));
		endPos = new Vector2(startPos.x + miniMapWidth, startPos.y);
		viewRangeEndPos = new Vector2();
		viewRangeEndPos.y = startPos.y;

		this.shapeRenderer = shapeRenderer;

		minimapUnitArray = new ArrayList<MiniMapView.MinimapUnit>();
	}

	public void setPlayerUnit(PlayerUnit player) {
		this.player = player;
	}

	public void setMonsterUnitArray(ArrayList<MonsterUnit> monsterUnitArray) {
		this.monsterUnitArray = monsterUnitArray;
	}

	public void setMapWidth(float mapWidth) {
		this.mapWidth = mapWidth;
	}

	public void act() {
		MinimapUnit minimapUnit = new MinimapUnit();

		minimapUnit.unitPos.x = (miniMapWidth * player.getPos().x) / mapWidth + startPos.x;
		minimapUnit.unitPos.y = startPos.y;
		minimapUnit.target = 1;

		minimapUnitArray.add(minimapUnit);

		float mapViewRange = Gdx.graphics.getWidth() + player.getPos().x;
		viewRangeEndPos.x = (miniMapWidth * mapViewRange) / mapWidth + startPos.x;

		for(MonsterUnit monster : monsterUnitArray) {
			if(monster.getPos().x < mapViewRange) {
				minimapUnit = new MinimapUnit();
				minimapUnit.unitPos.x = (miniMapWidth * monster.getPos().x) / mapWidth + startPos.x;
				minimapUnit.unitPos.y = startPos.y;
				minimapUnit.target = 0;
				minimapUnitArray.add(minimapUnit);
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		act();
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.line(startPos, viewRangeEndPos);
		shapeRenderer.setColor(Color.LIGHT_GRAY);
		shapeRenderer.line(viewRangeEndPos, endPos);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeType.Line);
		for(MinimapUnit unit : minimapUnitArray) {
			if(unit.target == 0) {
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.circle(unit.unitPos.x, unit.unitPos.y, 15);
			} else {
				shapeRenderer.setColor(Color.GREEN);
				shapeRenderer.circle(unit.unitPos.x, unit.unitPos.y, 15);
			}
		}
		shapeRenderer.end();

		minimapUnitArray.clear();
		batch.begin();
	}

	protected class MinimapUnit {
		public Vector2 unitPos;
		public int target; //0: 몬스터, 1: 플레이어 팀

		public MinimapUnit() {
			unitPos = new Vector2();
		}
	}
}
