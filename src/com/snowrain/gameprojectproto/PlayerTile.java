package com.snowrain.gameprojectproto;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class PlayerTile extends Rectangle {

	private Player player;
	private float locX, locY;
	private int tileNmber;
	private boolean isPlayerHere;
	private boolean playerTurn;

	private Context mContext;

	public PlayerTile(float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager vbom, Player player, int tileNumber,
			Context context) {
		super(pX, pY, pWidth, pHeight, vbom);
		// TODO Auto-generated constructor stub
		this.player = player;
		this.locX = pX;
		this.locY = pY;
		this.tileNmber = tileNumber;
		this.mContext = context;
		isPlayerHere = false;
		
		//this.setAlpha(0.75f);
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
			// Log.e("Tile", "touched" + tileNmber);
			// ****Player defense turn movement (when blue square is clicked)

			player.startRunningToPoint(locX, locY);
			// player.setLocation(tileNmber);

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
