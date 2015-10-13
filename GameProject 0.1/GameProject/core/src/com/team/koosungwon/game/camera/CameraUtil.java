package com.team.koosungwon.game.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class CameraUtil {
	
	private OrthographicCamera camera; //전투용 카메라
	private OrthographicCamera cameraUi; //UI용 카메라
	private Rectangle cameraBound; //카메라 바운드
	private Rectangle limitBound; //전체 제한 바운드
	
	private float distance;
	
	public CameraUtil() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		cameraUi = new OrthographicCamera();
		cameraUi.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		cameraBound = new Rectangle(0, 0, camera.viewportWidth, camera.viewportHeight);
	}
	
	public void setLimitBound(float mapSize) {
		limitBound = new Rectangle(0, 0, mapSize, Gdx.graphics.getHeight());
	}
	
	public Matrix4 getCameraCombined() {
		return camera.combined;
	}
	
	public Matrix4 getCameraUiCombined() {
		return cameraUi.combined;
	}
	
	public void update() {
		camera.update();
	}
	
	//플레이어와 거리를 적용
	public void setPlayerDistance(float x) {
		distance = camera.position.x - x;
	}
	
	//플레이어를 chasing한다.
	public void chasePlayer(float posX) {
		
		float cameraPosX = distance + posX;
		
		if(limitCameraMove(cameraPosX)) {
			camera.translate(cameraPosX - camera.position.x, 0);
		}
	}
	
	private boolean limitCameraMove(float cameraPosX) {
		
		if(cameraPosX + cameraBound.width > limitBound.width || cameraPosX < (Gdx.graphics.getWidth() / 2)) {
			return false;
		}
		
		cameraBound.setX(cameraPosX);
		return true;
	}
}
