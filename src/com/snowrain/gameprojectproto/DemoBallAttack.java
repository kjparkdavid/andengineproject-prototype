package com.snowrain.gameprojectproto;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class DemoBallAttack extends Sprite {

	public DemoBallAttack(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager vbom, int toX, int toY) {
		super(pX, pY, pTextureRegion, vbom);
		// TODO Auto-generated constructor stub
		
		MoveModifier mod1 = new MoveModifier(1, pX, toX,
				pY, toY) {
			@Override
			protected void onModifierStarted(IEntity pItem) {
				super.onModifierStarted(pItem);
				// Your action after starting modifier
				//setRunning();
			}

			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				// Your action after finishing modifier
				// STOP ANIMATION HERE!!!
				//stopRunning();
				//detachChild(this);
			}
		};
		this.registerEntityModifier(mod1);
	}

}
