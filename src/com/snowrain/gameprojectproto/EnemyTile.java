package com.snowrain.gameprojectproto;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class EnemyTile extends Sprite {

	private Player player;
	private float locX, locY;
	private int tileNmber;
	private boolean isPlayerHere;
	private boolean playerTurn;

	private Context mContext;

	public EnemyTile(float pX, float pY, float pWidth, float pHeight,
			ITextureRegion pTextureRegion, VertexBufferObjectManager vbom,
			Player player, int tileNumber, Context context) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, vbom);
		// TODO Auto-generated constructor stub
		this.player = player;
		this.locX = pX;
		this.locY = pY;
		this.tileNmber = tileNumber;
		this.mContext = context;
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
		if (pSceneTouchEvent.isActionDown()) {
			// if (!playerTurn) {
			//***Player Attack Turn Movement (When red square is clicked)
			//Log.e("Tile", "touched" + tileNmber);
			//player.startRunningToPoint(locX, locY);
			//player.setLocation(tileNmber);

			Intent i = new Intent("myTurnAction");
			i.putExtra("TileLoc", tileNmber);
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(i);
			return true;
			// }else{
			// getTileNumber();
			// }
		}
		return false;
	}

	public int getTileNumber() {
		return tileNmber;
	}

	public void setPlayerTurn(boolean playerturn) {
		this.playerTurn = playerturn;
	}

}
