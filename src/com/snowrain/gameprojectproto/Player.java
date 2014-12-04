package com.snowrain.gameprojectproto;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class Player extends AnimatedSprite {
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	private Body body;

	private boolean canRun = true;

	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------

	public Player(float pX, float pY, VertexBufferObjectManager vbo,
			Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		// createPhysics(camera, physicsWorld);
		// camera.setChaseEntity(this);
		// startRunningToPoint();
		// stopRunning();
	}

	public abstract void onDie();

	// private void createPhysics(final Camera camera, PhysicsWorld
	// physicsWorld)
	// {
	// body = PhysicsFactory.createBoxBody(physicsWorld, this,
	// BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
	//
	// body.setUserData("player");
	// body.setFixedRotation(true);
	//
	// physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body,
	// true, false)
	// {
	// @Override
	// public void onUpdate(float pSecondsElapsed)
	// {
	// super.onUpdate(pSecondsElapsed);
	// camera.onUpdate(0.1f);
	//
	// if (getY() <= 0)
	// {
	// onDie();
	// }
	//
	// if (canRun)
	// {
	// body.setLinearVelocity(new Vector2(5, body.getLinearVelocity().y));
	// }
	// }
	// });
	// }

	public void setRunning() {
		canRun = false;

		final long[] PLAYER_ANIMATE = new long[] { 100, 100 }; //each frame

		animate(PLAYER_ANIMATE, 1, 2, true);
	}

	public void startRunningToPoint(float x, float y) {
		if (canRun) {
			MoveModifier mod1 = new MoveModifier(1, this.getX(), x,
					this.getY(), y) {
				@Override
				protected void onModifierStarted(IEntity pItem) {
					super.onModifierStarted(pItem);
					// Your action after starting modifier

					setRunning();
				}

				@Override
				protected void onModifierFinished(IEntity pItem) {
					super.onModifierFinished(pItem);
					// Your action after finishing modifier
					// STOP ANIMATION HERE!!!
					stopRunning();
				}
			};
			this.registerEntityModifier(mod1);
		}
	}

	public void stopRunning() {
		canRun = true;
		stopAnimation(0);
	}
}
