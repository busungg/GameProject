package com.team.koosungwon.game.utils;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class LuaScriptManager {
	
	protected Globals globals;
	protected boolean isExist;
	
	public LuaScriptManager() {
		globals = JsePlatform.standardGlobals();
		isExist = false;
	}
	
	protected void loadLua(String fileName) {
		if (Gdx.files.internal(fileName).exists()) {
			try {
				FileHandle handle = Gdx.files.internal(fileName);
				globals.load(handle.readString()).call();
				isExist = true;
			} catch(Exception e) {
				Gdx.app.log("Error", "loadLua Error");
			}
		}
	}
	
	protected void disposeLua() {
		if(globals != null) {
		}
	}
}
