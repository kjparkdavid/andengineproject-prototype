package com.snowrain.gameprojectproto;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.snowrain.gameprojectproto.SceneManager.SceneType;

public class GameScene extends BaseScene {

	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	private Sprite blue_square1, blue_square2, blue_square3, blue_square4,
			green_square1, green_square2, green_square3, green_square4, game_background;

	private PhysicsWorld physicsWorld;

	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private Player player;

	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";

	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";

	@Override
	public void createScene() {
		
		createBackground();
		createPlayer(); // order matters must create after background in order to put it in front
		createHUD();
		createPhysics();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(640, 360);

		// TODO code responsible for disposing scene
		// removing all game scene objects.
	}

	private void createBackground() {
		setBackground(new Background(Color.BLUE));
		blue_square1 = new Sprite(140, 110, resourcesManager.blue_square, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				player.startRunningToPoint(160, 110);
				return true;
			}

		};
		blue_square2 = new Sprite(390, 110, resourcesManager.blue_square, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				player.startRunningToPoint(410, 110);
				return true;
			}
		};
		blue_square3 = new Sprite(140, 360, resourcesManager.blue_square, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				player.startRunningToPoint(160, 360 );
				return true;
			}
		};
		blue_square4 = new Sprite(390, 360, resourcesManager.blue_square, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				player.startRunningToPoint(410, 360);
				return true;
			}
		};
		green_square1 = new Sprite(640, 110, resourcesManager.green_square,
				vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				player.startRunningToPoint(640 + 125, 235);
				return true;
			}
		};
		green_square2 = new Sprite(890, 110, resourcesManager.green_square,
				vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				player.startRunningToPoint(890 + 125, 235);
				return true;
			}
		};
		green_square3 = new Sprite(640, 360, resourcesManager.green_square,
				vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				player.startRunningToPoint(640 +125, 360 +125);
				return true;
			}
		};
		green_square4 = new Sprite(890, 360, resourcesManager.green_square,
				vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				player.startRunningToPoint(890 +125, 360 +125);
				return true;
			}
		};
		game_background =  new Sprite(0, 0, resourcesManager.game_background_region,
				vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		registerTouchArea(blue_square1);
		registerTouchArea(blue_square2);
		registerTouchArea(blue_square3);
		registerTouchArea(blue_square4);
//		registerTouchArea(green_square1);
//		registerTouchArea(green_square2);
//		registerTouchArea(green_square3);
//		registerTouchArea(green_square4);

		attachChild(blue_square1);
		attachChild(blue_square2);
		attachChild(blue_square3);
		attachChild(blue_square4);
		attachChild(green_square1);
		attachChild(green_square2);
		attachChild(green_square3);
		attachChild(green_square4);
		attachChild(game_background);
		

	}
	
	private void createPlayer(){
		player = new Player(160, 110, vbom, camera, physicsWorld) {

			@Override
			public void onDie() {
				// TODO Auto-generated method stub

			}
		};
		attachChild(player);
	}

	private void createHUD() {
		gameHUD = new HUD();

		// CREATE SCORE TEXT
		// IMPORTANT to put all 0123456789 in the text field to initiallize or
		// it will lag
		scoreText = new Text(20, 42, resourcesManager.font, "HP: 0123456789",
				new TextOptions(HorizontalAlign.LEFT), vbom);
		// scoreText.setAnchorCenter(0, 0);
		scoreText.setText("Score: 0");
		gameHUD.attachChild(scoreText);

		camera.setHUD(gameHUD);
	}

	private void addToScore(int i) {
		score += i;
		scoreText.setText("Score: " + score);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false);
		registerUpdateHandler(physicsWorld);
	}

	// private void loadLevel(int levelID)
	// {
	// final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
	//
	// final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f,
	// 0.5f);
	//
	// levelLoader.registerEntityLoader(new
	// EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
	// {
	// public IEntity onLoadEntity(final String pEntityName, final IEntity
	// pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData
	// pSimpleLevelEntityLoaderData) throws IOException
	// {
	// final int width = SAXUtils.getIntAttributeOrThrow(pAttributes,
	// LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
	// final int height = SAXUtils.getIntAttributeOrThrow(pAttributes,
	// LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
	//
	// // TODO later we will specify camera BOUNDS and create invisible walls
	// // on the beginning and on the end of the level.
	//
	// return GameScene.this;
	// }
	// });
	//
	// levelLoader.registerEntityLoader(new
	// EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
	// {
	// public IEntity onLoadEntity(final String pEntityName, final IEntity
	// pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData
	// pSimpleLevelEntityLoaderData) throws IOException
	// {
	// final int x = SAXUtils.getIntAttributeOrThrow(pAttributes,
	// TAG_ENTITY_ATTRIBUTE_X);
	// final int y = SAXUtils.getIntAttributeOrThrow(pAttributes,
	// TAG_ENTITY_ATTRIBUTE_Y);
	// final String type = SAXUtils.getAttributeOrThrow(pAttributes,
	// TAG_ENTITY_ATTRIBUTE_TYPE);
	//
	// final Sprite levelObject;
	//
	// if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1))
	// {
	// levelObject = new Sprite(x, y, resourcesManager.platform1_region, vbom);
	// PhysicsFactory.createBoxBody(physicsWorld, levelObject,
	// BodyType.StaticBody, FIXTURE_DEF).setUserData("platform1");
	// }
	// else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2))
	// {
	// levelObject = new Sprite(x, y, resourcesManager.platform2_region, vbom);
	// final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject,
	// BodyType.StaticBody, FIXTURE_DEF);
	// body.setUserData("platform2");
	// physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject,
	// body, true, false));
	// }
	// else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3))
	// {
	// levelObject = new Sprite(x, y, resourcesManager.platform3_region, vbom);
	// final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject,
	// BodyType.StaticBody, FIXTURE_DEF);
	// body.setUserData("platform3");
	// physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject,
	// body, true, false));
	// }
	// else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN))
	// {
	// levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom)
	// {
	// @Override
	// protected void onManagedUpdate(float pSecondsElapsed)
	// {
	// super.onManagedUpdate(pSecondsElapsed);
	//
	// /**
	// * TODO
	// * we will later check if player collide with this (coin)
	// * and if it does, we will increase score and hide coin
	// * it will be completed in next articles (after creating player code)
	// */
	// }
	// };
	// levelObject.registerEntityModifier(new LoopEntityModifier(new
	// ScaleModifier(1, 1, 1.3f)));
	// }
	// else
	// {
	// throw new IllegalArgumentException();
	// }
	//
	// levelObject.setCullingEnabled(true);
	//
	// return levelObject;
	// }
	// });
	//
	// levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID +
	// ".lvl");
	// }

}
