package com.team.koosungwon.game.view;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.team.koosungwon.game.model.MapBackground;

public class MapView {
	
	private final float MAP_BOUND_WIDTH = 1000 * Gdx.graphics.getDensity();
	
	private ArrayList<MapBackground> backgroundArray;
	private float mapSize;
	
	public MapView() {super();}
	
	public MapView(String stageId) {
		backgroundArray = new ArrayList<MapBackground>();
		loadAsset(stageId);
	}
	
	private void loadAsset(String stageId) {
		FileHandle file = Gdx.files.internal("map/info/"+ stageId +".json");
		
		JsonReader jsonReader = new JsonReader();
		JsonValue jsonValue = jsonReader.parse(file);
		
		mapSize = MAP_BOUND_WIDTH * jsonValue.getInt("count");
		JsonValue jsonValueArray = jsonValue.get("background");
		
		MapBackground mapBackground;
		for(int i = 0; i < jsonValueArray.size; i++) {
			mapBackground = new MapBackground(MAP_BOUND_WIDTH * i, MAP_BOUND_WIDTH,jsonValueArray.get(i).getString("texture"));
			backgroundArray.add(mapBackground);
		}
	}

	public void render(SpriteBatch batch) {
		for(MapBackground mapBackground : backgroundArray) {
			mapBackground.render(batch);
		}
	}
	
	public float getMapSize() {
		return mapSize;
	}
}
