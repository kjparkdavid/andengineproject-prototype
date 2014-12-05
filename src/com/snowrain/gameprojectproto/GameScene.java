package com.snowrain.gameprojectproto;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.util.GLState;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.badlogic.gdx.math.Vector2;
import com.snowrain.gameprojectproto.SceneManager.SceneType;

public class GameScene extends BaseScene {

	private HUD gameHUD;
	private Text skillText1, skillText2, actionText;
	private int score = 0;
	private Sprite blue_square1, blue_square2, blue_square3, blue_square4,
			green_square1, green_square2, green_square3, green_square4,
			game_background, red_circle;

	private PhysicsWorld physicsWorld;
	private Player player, enemyPlayer;

	public static int B1 = 1, B2 = 2, B3 = 3, B4 = 4, R1 = 5, R2 = 6, R3 = 7,
			R4 = 8;
	private boolean attackTurn;

	private BroadcastReceiver myTurnReceiver;

	@Override
	public void createScene() {

		// *********Set who attack first (true = player, false = AI)
		attackTurn = true;

		myTurnReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (attackTurn) { // Player attack
					Random r = new Random();
					int attackLoc = intent.getIntExtra("TileLoc", 1);
					int AIMovement = r.nextInt(9 - 5) + 5; // gives random
															// number of
															// 5 to 8
					// Log.e("GameScene", "AI moved" + AIMovement);
					enemyPlayer.enemyMovement(AIMovement);
					showDemoBallFromPlayer(attackLoc);
					// actionText.setText("You attacked " + clickedLoc);
					if (attackLoc == AIMovement) {
						enemyPlayer.onDie(); // ideally hp --;
					} else { // End Turn: enemy turn to attack
						endAttackTurn();
					}
				} else { // Player Defense turn
					Random r = new Random();
					int defenseLoc = intent.getIntExtra("TileLoc", 1);
					int AIMovement = r.nextInt(5 - 1) + 1; // 1 to 4
					// Log.e("GameScene", "AI attacked" + AIMovement);
					showDemoBallFromEnemy(AIMovement);
					if (defenseLoc == AIMovement) {
						player.onDie(); // ideally hp --;
					} else { // End Turn: your turn to attack
						endDefenseTurn();
					}
				}
			}
		};

		createPlayer(); // order matters must create after background in order
						// to put it in front
		createEnemyPlayer();
		createBackground();
		createHUD();
		createPhysics();
		attachAssets();

		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
		lbm.registerReceiver(myTurnReceiver, new IntentFilter("myTurnAction"));
		// ********If game loop is required
		// this.registerUpdateHandler(new IUpdateHandler() {
		// public void reset() {
		// }
		//
		// public void onUpdate(float pSecondsElapsed) {
		// // HERE IS THE GAME LOOP
		//
		// }
		// });
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

		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
		lbm.unregisterReceiver(myTurnReceiver);
		// TODO code responsible for disposing scene
		// removing all game scene objects.
	}

	private void createBackground() {
		setBackground(new Background(Color.BLUE));
		blue_square1 = new PlayerTile(45, 170, 250, 250,
				resourcesManager.blue_square, vbom, player, B1, activity);
		blue_square2 = new PlayerTile(355, 170, 250, 250,
				resourcesManager.blue_square, vbom, player, B2, activity);
		blue_square3 = new PlayerTile(45, 420, 250, 250,
				resourcesManager.blue_square, vbom, player, B3, activity);
		blue_square4 = new PlayerTile(355, 420, 250, 250,
				resourcesManager.blue_square, vbom, player, B4, activity);
		green_square1 = new EnemyTile(680, 170, 250, 250,
				resourcesManager.blue_square, vbom, enemyPlayer, R1, activity);
		green_square2 = new EnemyTile(990, 170, 250, 250,
				resourcesManager.blue_square, vbom, enemyPlayer, R2, activity);
		green_square3 = new EnemyTile(680, 420, 250, 250,
				resourcesManager.blue_square, vbom, enemyPlayer, R3, activity);
		green_square4 = new EnemyTile(990, 420, 250, 250,
				resourcesManager.blue_square, vbom, enemyPlayer, R4, activity);
		game_background = new Sprite(0, 0,
				resourcesManager.game_background_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		// registerTouchArea(blue_square1);
		// registerTouchArea(blue_square2);
		// registerTouchArea(blue_square3);
		// registerTouchArea(blue_square4);
		registerTouchArea(green_square1);
		registerTouchArea(green_square2);
		registerTouchArea(green_square3);
		registerTouchArea(green_square4);

	}

	private void attachAssets() {

		attachChild(blue_square1);
		attachChild(blue_square2);
		attachChild(blue_square3);
		attachChild(blue_square4);
		attachChild(green_square1);
		attachChild(green_square2);
		attachChild(green_square3);
		attachChild(green_square4);
		attachChild(game_background);
		attachChild(player);
		attachChild(enemyPlayer);
	}

	private void createPlayer() {
		player = new Player(45, 170, vbom, camera, physicsWorld, B1) {

			@Override
			public void onDie() {
				// TODO Auto-generated method stub
				actionText.setText("You LOSE!!");
			}
		};

	}

	private void createEnemyPlayer() {
		enemyPlayer = new Player(990, 170, vbom, camera, physicsWorld, R2) {

			@Override
			public void onDie() {
				// TODO Auto-generated method stub
				actionText.setText("You WIN!!");
			}
		};
		enemyPlayer.setFlippedHorizontal(true);
		enemyPlayer.setColor(Color.RED);

	}

	private void createHUD() {
		gameHUD = new HUD();

		// CREATE SCORE TEXT
		// IMPORTANT to put all 0123456789 in the text field to initiallize or
		// it will lag
		skillText1 = new Text(20, 650, resourcesManager.font, "Skill 1",
				new TextOptions(HorizontalAlign.LEFT), vbom);
		// scoreText.setAnchorCenter(0, 0);
		skillText1.setText("Skill 1");
		gameHUD.attachChild(skillText1);

		skillText2 = new Text(1100, 650, resourcesManager.font, "Skill 2",
				new TextOptions(HorizontalAlign.LEFT), vbom);
		// scoreText.setAnchorCenter(0, 0);
		skillText2.setText("Skill 2");
		gameHUD.attachChild(skillText2);

		actionText = new Text(550, 550, resourcesManager.font, "your turn win lose!",
				new TextOptions(HorizontalAlign.CENTER), vbom);
		actionText.setText("Your Turn!");
		gameHUD.attachChild(actionText);
		camera.setHUD(gameHUD);
	}

	// private void addToScore(int i) {
	// score += i;
	// scoreText.setText("Score: " + score);
	// }

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false);
		registerUpdateHandler(physicsWorld);

	}

	public boolean getPlayerTurn() {
		return attackTurn;
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.attackTurn = playerTurn;
	}

	private void endDefenseTurn() {
		LoopEntityModifier blinkModifier = new LoopEntityModifier(
			    new SequenceEntityModifier(new FadeOutModifier(0.25f), new FadeInModifier(0.25f)),2);
		// detachChild(red_circle);
		actionText.setText("Your Turn!");
		actionText.registerEntityModifier(blinkModifier);
		unregisterTouchArea(blue_square1);
		unregisterTouchArea(blue_square2);
		unregisterTouchArea(blue_square3);
		unregisterTouchArea(blue_square4);
		registerTouchArea(green_square1);
		registerTouchArea(green_square2);
		registerTouchArea(green_square3);
		registerTouchArea(green_square4);
		attackTurn = true;
	}

	private void endAttackTurn() {
		LoopEntityModifier blinkModifier = new LoopEntityModifier(
			    new SequenceEntityModifier(new FadeOutModifier(0.25f), new FadeInModifier(0.25f)),2);
		// detachChild(red_circle);
		actionText.setText("Enemy Turn!");
		actionText.registerEntityModifier(blinkModifier);
		registerTouchArea(blue_square1);
		registerTouchArea(blue_square2);
		registerTouchArea(blue_square3);
		registerTouchArea(blue_square4);
		unregisterTouchArea(green_square1);
		unregisterTouchArea(green_square2);
		unregisterTouchArea(green_square3);
		unregisterTouchArea(green_square4);
		attackTurn = false;
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
	private void showDemoBallFromPlayer(int loc) {
		switch (loc) {
		case 1:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(player.getX(), player.getY(),
					resourcesManager.red_circle, vbom, 45, 170);
			attachChild(red_circle);
			break;
		case 2:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(player.getX(), player.getY(),
					resourcesManager.red_circle, vbom, 355, 170);
			attachChild(red_circle);
			break;
		case 3:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(player.getX(), player.getY(),
					resourcesManager.red_circle, vbom, 45, 420);
			attachChild(red_circle);
			break;
		case 4:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(player.getX(), player.getY(),
					resourcesManager.red_circle, vbom, 355, 420);
			attachChild(red_circle);
			break;
		case 5:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(player.getX(), player.getY(),
					resourcesManager.red_circle, vbom, 680, 170);
			attachChild(red_circle);
			break;
		case 6:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(player.getX(), player.getY(),
					resourcesManager.red_circle, vbom, 990, 170);
			attachChild(red_circle);
			break;
		case 7:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(player.getX(), player.getY(),
					resourcesManager.red_circle, vbom, 680, 420);
			attachChild(red_circle);
			break;
		case 8:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(player.getX(), player.getY(),
					resourcesManager.red_circle, vbom, 990, 420);
			attachChild(red_circle);
			break;
		default:
			break;
		}
	}

	private void showDemoBallFromEnemy(int loc) {
		switch (loc) {
		case 1:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(), enemyPlayer.getY(),
					resourcesManager.red_circle, vbom, 45, 170);
			attachChild(red_circle);
			break;
		case 2:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(), enemyPlayer.getY(),
					resourcesManager.red_circle, vbom, 355, 170);
			attachChild(red_circle);
			break;
		case 3:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(), enemyPlayer.getY(),
					resourcesManager.red_circle, vbom, 45, 420);
			attachChild(red_circle);
			break;
		case 4:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(), enemyPlayer.getY(),
					resourcesManager.red_circle, vbom, 355, 420);
			attachChild(red_circle);
			break;
		case 5:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(), enemyPlayer.getY(),
					resourcesManager.red_circle, vbom, 680, 170);
			attachChild(red_circle);
			break;
		case 6:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(), enemyPlayer.getY(),
					resourcesManager.red_circle, vbom, 990, 170);
			attachChild(red_circle);
			break;
		case 7:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(), enemyPlayer.getY(),
					resourcesManager.red_circle, vbom, 680, 420);
			attachChild(red_circle);
			break;
		case 8:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(), enemyPlayer.getY(),
					resourcesManager.red_circle, vbom, 990, 420);
			attachChild(red_circle);
			break;
		default:
			break;
		}
	}
	
	

}
