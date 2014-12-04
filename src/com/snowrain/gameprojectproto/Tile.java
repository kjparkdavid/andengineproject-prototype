package com.snowrain.gameprojectproto;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Tile extends Sprite {

	private Player player;
	private float locX, locY;
	private int tileNmber;
	private boolean isPlayerHere;
	private boolean playerTurn;

	public Tile(float pX, float pY, float pWidth, float pHeight,
			ITextureRegion pTextureRegion, VertexBufferObjectManager vbom,
			Player player, int tileNumber) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, vbom);
		// TODO Auto-generated constructor stub
		this.player = player;
		this.locX = pX;
		this.locY = pY;
		this.tileNmber = tileNumber;
		isPlayerHere = false;
	}

	@Override
	protected void preDraw(GLState pGLState, Camera pCamera) {
		super.preDraw(pGLState, pCamera);
		pGLState.enableDither();
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {

		//if (!playerTurn) {
			player.startRunningToPoint(locX, locY);
			player.setLocation(tileNmber);
			return true;
		//}else{
			//getTileNumber();
		//}
		
		//return false;
	}

	public int getTileNumber() {
		return tileNmber;
	}
	
	public void setPlayerTurn(boolean playerturn){
		this.playerTurn = playerturn;
	}

}
