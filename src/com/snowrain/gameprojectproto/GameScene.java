package com.snowrain.gameprojectproto;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.IAnimationData;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.badlogic.gdx.math.Vector2;
import com.snowrain.gameprojectproto.SceneManager.SceneType;

public class GameScene extends BaseScene {

	private HUD gameHUD;
	private Text skillText1, skillText2, actionText;
	// private Text shotText, moveText, skillText, restText, itemText;
	private int score = 0;
	private Rectangle blue_square1, blue_square2, blue_square3, blue_square4,
			green_square1, green_square2, green_square3, green_square4;
	private org.andengine.entity.Entity playerTileGroup, enemyTileGroup;
	private Sprite game_background, red_circle;
	private Sprite shotActionButton, moveActionButton, skillActionButton,
			itemActionButton;
	private Rectangle flash_effect;

	private PhysicsWorld physicsWorld;
	private Player player, enemyPlayer;

	private AnimatedSprite battleStartAnimation;

	public static int B1 = 1, B2 = 2, B3 = 3, B4 = 4, R1 = 5, R2 = 6, R3 = 7,
			R4 = 8;
	private boolean attackTurn;

	private BroadcastReceiver myTurnReceiver;

	private LoopEntityModifier blinkModifier1, blinkModifier2,blinkModifier3,blinkModifier4 ;

	@Override
	public void createScene() {

		// *********Set who attack first (true = player, false = AI)
		attackTurn = true;

		myTurnReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Random r = new Random();
				int catchsuccess = r.nextInt(100);

				if (attackTurn) { // Player attack (click on red tiles)
					hideEnemyBlinkingTile();

					int attackLoc = intent.getIntExtra("TileLoc", 1);
					int AIMovement = r.nextInt(9 - 5) + 5; // gives random
															// number of
															// 5 to 8
					// Log.e("GameScene", "AI moved" + AIMovement);
					int enemyPrevLoc = enemyPlayer.getLocation();
					enemyPlayer.enemyMovement(AIMovement);
					enemyPlayer.setLocation(AIMovement);
					showDemoBallFromPlayer(attackLoc);
					// actionText.setText("You attacked " + clickedLoc);
					if (attackLoc == AIMovement) {
						if (catchsuccess < 20
								&& enemyPrevLoc == enemyPlayer.getLocation()) { // 10
																				// percent
																				// of
																				// catching
																				// the
																				// ball
																				// and
																				// didn't
																				// move

							setActionTextBlinking("Catch Success!");
						} else {
							enemyPlayer.onDie(); // ideally hp --;
						}
					} else { // End Turn: enemy turn to attack
						endAttackTurn();
					}
				} else { // Player Defense turn (clicks on blue tile)
					hidePlayerBlinkingTile();

					// Random r = new Random();
					int defenseLoc = intent.getIntExtra("TileLoc", 1);
					int AIMovement = r.nextInt(5 - 1) + 1; // 1 to 4
					// Log.e("GameScene", "AI attacked" + AIMovement);
					int playerPrevLoc = player.getLocation();
					player.setLocation(defenseLoc);
					showDemoBallFromEnemy(AIMovement);
					if (defenseLoc == AIMovement) {
						if (catchsuccess < 20
								&& playerPrevLoc == player.getLocation()) { // 10
																			// percent
																			// of
																			// catching
																			// the
																			// ball
																			// and
																			// didn't
																			// move

							setActionTextBlinking("Catch Success!");
						} else {
							player.onDie(); // ideally hp --;
						}
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

		createActionOptions();
		createStartBattleAnimation();

		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
		lbm.registerReceiver(myTurnReceiver, new IntentFilter("myTurnAction"));

		blinkModifier1 = new LoopEntityModifier(new SequenceEntityModifier(
				new FadeOutModifier(0.25f), new FadeInModifier(0.25f)));
		blinkModifier2 = new LoopEntityModifier(new SequenceEntityModifier(
				new FadeOutModifier(0.25f), new FadeInModifier(0.25f)));
		blinkModifier3 = new LoopEntityModifier(new SequenceEntityModifier(
				new FadeOutModifier(0.25f), new FadeInModifier(0.25f)));
		blinkModifier4 = new LoopEntityModifier(new SequenceEntityModifier(
				new FadeOutModifier(0.25f), new FadeInModifier(0.25f)));
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

	private void createStartBattleAnimation() {
		// flash_effect = new Rectangle(0, 0, 1280, 720, vbom);
		// flash_effect.setColor(Color.WHITE);
		battleStartAnimation = new AnimatedSprite(320, 180,
				resourcesManager.battleStartRegion, vbom);
		battleStartAnimation.setScale(2.0f);
		// attachChild(flash_effect);
		attachChild(battleStartAnimation);
		// flash_effect.setVisible(false);
		battleStartAnimation.animate(new long[] { 800, 250 }, false,
				new IAnimationListener() {

					@Override
					public void onAnimationLoopFinished(
							AnimatedSprite pAnimatedSprite,
							int pRemainingLoopCount, int pInitialLoopCount) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationFrameChanged(
							AnimatedSprite pAnimatedSprite, int pOldFrameIndex,
							int pNewFrameIndex) {
						// TODO Auto-generated method stub
						// flash_effect.setVisible(false);
					}

					@Override
					public void onAnimationFinished(
							AnimatedSprite pAnimatedSprite) {
						// TODO Auto-generated method stub
						SceneManager.getInstance().getCurrentScene()
								.detachChild(battleStartAnimation);
						// SceneManager.getInstance().getCurrentScene().detachChild(flash_effect);
					}

					@Override
					public void onAnimationStarted(
							AnimatedSprite pAnimatedSprite,
							int pInitialLoopCount) {
						// TODO Auto-generated method stub

						// flash_effect.setVisible(true);
					}
				});
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine, "GameScene");
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
		ResourcesManager.getInstance().unloadGameTextures();
	}

	private void createBackground() {
		setBackground(new Background(Color.BLUE));
		blue_square1 = new PlayerTile(45, 170, 250, 250, vbom, player, B1,
				activity);
		blue_square2 = new PlayerTile(355, 170, 250, 250, vbom, player, B2,
				activity);
		blue_square3 = new PlayerTile(45, 420, 250, 250, vbom, player, B3,
				activity);
		blue_square4 = new PlayerTile(355, 420, 250, 250, vbom, player, B4,
				activity);
		green_square1 = new EnemyTile(680, 170, 250, 250, vbom, enemyPlayer,
				R1, activity);
		green_square2 = new EnemyTile(990, 170, 250, 250, vbom, enemyPlayer,
				R2, activity);
		green_square3 = new EnemyTile(680, 420, 250, 250, vbom, enemyPlayer,
				R3, activity);
		green_square4 = new EnemyTile(990, 420, 250, 250, vbom, enemyPlayer,
				R4, activity);
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
		// registerTouchArea(green_square1);
		// registerTouchArea(green_square2);
		// registerTouchArea(green_square3);
		// registerTouchArea(green_square4);

	}

	private void attachAssets() {
		// Make tile Layer
		playerTileGroup = new org.andengine.entity.Entity();
		enemyTileGroup = new org.andengine.entity.Entity();
		playerTileGroup.attachChild(blue_square1);
		playerTileGroup.attachChild(blue_square2);
		playerTileGroup.attachChild(blue_square3);
		playerTileGroup.attachChild(blue_square4);
		enemyTileGroup.attachChild(green_square1);
		enemyTileGroup.attachChild(green_square2);
		enemyTileGroup.attachChild(green_square3);
		enemyTileGroup.attachChild(green_square4);
		// attachChild(enemyTileGroup);
		// attachChild(playerTileGroup);
		attachChild(game_background);

		enemyTileGroup.setVisible(false);
		playerTileGroup.setVisible(false);
		attachChild(enemyTileGroup);
		attachChild(playerTileGroup);

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
		player.setLocation(B1);

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
		enemyPlayer.setLocation(R2);

	}

	private void createHUD() {
		gameHUD = new HUD();

		// CREATE SCORE TEXT
		// IMPORTANT to put all 0123456789 in the text field to initiallize or
		// it will lag
		// skillText1 = new Text(20, 650, resourcesManager.font, "Skill 1",
		// new TextOptions(HorizontalAlign.LEFT), vbom);
		// // scoreText.setAnchorCenter(0, 0);
		// skillText1.setText("Skill 1");
		// gameHUD.attachChild(skillText1);
		//
		// skillText2 = new Text(1100, 650, resourcesManager.font, "Skill 2",
		// new TextOptions(HorizontalAlign.LEFT), vbom);
		// // scoreText.setAnchorCenter(0, 0);
		// skillText2.setText("Skill 2");
		// gameHUD.attachChild(skillText2);

		actionText = new Text(550, 550, resourcesManager.font,
				"your turn win lose!", new TextOptions(HorizontalAlign.CENTER),
				vbom);
		actionText.setText("Your Turn!");
		gameHUD.attachChild(actionText);
		camera.setHUD(gameHUD);
	}

	private void createActionOptions() {
		// Buttons on the left Layout
		/*
		 * demoActionButton1 = new Sprite(0, 160,
		 * resourcesManager.demo_action_button, vbom){
		 * 
		 * @Override public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
		 * float pTouchAreaLocalX, float pTouchAreaLocalY) {
		 * registerTouchArea(green_square1); registerTouchArea(green_square2);
		 * registerTouchArea(green_square3); registerTouchArea(green_square4);
		 * SceneManager
		 * .getInstance().getCurrentScene().detachChild(demoActionButton1);
		 * SceneManager
		 * .getInstance().getCurrentScene().detachChild(demoActionButton2);
		 * SceneManager
		 * .getInstance().getCurrentScene().detachChild(demoActionButton3);
		 * SceneManager
		 * .getInstance().getCurrentScene().detachChild(demoActionButton4);
		 * unregisterTouchArea(demoActionButton1); return true; } };
		 * demoActionButton2 = new Sprite(0, 290,
		 * resourcesManager.demo_action_button, vbom); demoActionButton3 = new
		 * Sprite(0, 420, resourcesManager.demo_action_button, vbom);
		 * demoActionButton4 = new Sprite(0, 550,
		 * resourcesManager.demo_action_button, vbom);
		 */
		shotActionButton = new Sprite(300, 600, 225, 120,
				resourcesManager.shotActionButton, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				enableAttack();
				hideActionButtons();

				return true;
			}
		};
		moveActionButton = new Sprite(300, 600, 225, 120,
				resourcesManager.moveActionButton, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				enableDefence(); // movement in playertile
				hideActionButtons();
				return true;
			}
		};
		skillActionButton = new Sprite(550, 600, 225, 120,
				resourcesManager.skillActionButton, vbom);

		itemActionButton = new Sprite(800, 600, 225, 120,
				resourcesManager.itemActionButton, vbom);
		// showAttackActionButtons();

		// initialize buttons when game starts
		attachChild(shotActionButton);
		attachChild(moveActionButton);
		attachChild(skillActionButton);
		attachChild(itemActionButton);

		moveActionButton.setVisible(false);

		registerTouchArea(shotActionButton);
		// registerTouchArea(demoActionButton2);
		registerTouchArea(skillActionButton);
		registerTouchArea(itemActionButton);

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
		setActionTextBlinking("Your Turn!");
		showAttackActionButtons();
		unregisterAllTiles();
		// unregisterTouchArea(blue_square1);
		// unregisterTouchArea(blue_square2);
		// unregisterTouchArea(blue_square3);
		// unregisterTouchArea(blue_square4);
		// registerGreenTiles(); //can click enemy tile
		attackTurn = true;
	}

	private void endAttackTurn() {
		setActionTextBlinking("Enemy Turn!");
		showDefenceActionButtons();
		unregisterAllTiles();
		// registerBlueTiles(); //can click player tile
		// unregisterTouchArea(green_square1);
		// unregisterTouchArea(green_square2);
		// unregisterTouchArea(green_square3);
		// unregisterTouchArea(green_square4);
		attackTurn = false;
	}

	private void setActionTextBlinking(CharSequence text) {
		LoopEntityModifier blinkTwiceModifier = new LoopEntityModifier(
				new SequenceEntityModifier(new FadeOutModifier(0.25f),
						new FadeInModifier(0.25f)), 2);
		actionText.setText(text);
		actionText.registerEntityModifier(blinkTwiceModifier);
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
			red_circle = new DemoBallAttack(enemyPlayer.getX(),
					enemyPlayer.getY(), resourcesManager.red_circle, vbom, 45,
					170);
			attachChild(red_circle);
			break;
		case 2:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(),
					enemyPlayer.getY(), resourcesManager.red_circle, vbom, 355,
					170);
			attachChild(red_circle);
			break;
		case 3:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(),
					enemyPlayer.getY(), resourcesManager.red_circle, vbom, 45,
					420);
			attachChild(red_circle);
			break;
		case 4:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(),
					enemyPlayer.getY(), resourcesManager.red_circle, vbom, 355,
					420);
			attachChild(red_circle);
			break;
		case 5:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(),
					enemyPlayer.getY(), resourcesManager.red_circle, vbom, 680,
					170);
			attachChild(red_circle);
			break;
		case 6:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(),
					enemyPlayer.getY(), resourcesManager.red_circle, vbom, 990,
					170);
			attachChild(red_circle);
			break;
		case 7:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(),
					enemyPlayer.getY(), resourcesManager.red_circle, vbom, 680,
					420);
			attachChild(red_circle);
			break;
		case 8:
			detachChild(red_circle);
			red_circle = new DemoBallAttack(enemyPlayer.getX(),
					enemyPlayer.getY(), resourcesManager.red_circle, vbom, 990,
					420);
			attachChild(red_circle);
			break;
		default:
			break;
		}
	}

	private void enableAttack() {
		registerTouchArea(green_square1);
		registerTouchArea(green_square2);
		registerTouchArea(green_square3);
		registerTouchArea(green_square4);
		showEnemyBlinkingTile();
		// LoopEntityModifier blinkModifier = new LoopEntityModifier(
		// new SequenceEntityModifier(new FadeOutModifier(0.25f),
		// new FadeInModifier(0.25f)), 2);
		// enemyTileGroup.registerEntityModifier(blinkModifier);
	}

	private void showEnemyBlinkingTile() {
		enemyTileGroup.setVisible(true);
		green_square1.registerEntityModifier(blinkModifier1);
		green_square2.registerEntityModifier(blinkModifier2);
		green_square3.registerEntityModifier(blinkModifier3);
		green_square4.registerEntityModifier(blinkModifier4);
	}

	private void enableDefence() {
		registerTouchArea(blue_square1);
		registerTouchArea(blue_square2);
		registerTouchArea(blue_square3);
		registerTouchArea(blue_square4);

		// playerTileGroup.registerEntityModifier(blinkModifier);
		showPlayerBlinkingTile();
	}

	private void showPlayerBlinkingTile() {
		playerTileGroup.setVisible(true);
		blue_square1.registerEntityModifier(blinkModifier1);
		blue_square2.registerEntityModifier(blinkModifier2);
		blue_square3.registerEntityModifier(blinkModifier3);
		blue_square4.registerEntityModifier(blinkModifier4);
	}

	private void hideActionButtons() {
		// SceneManager.getInstance().getCurrentScene()
		// .detachChild(demoActionButton1);
		// SceneManager.getInstance().getCurrentScene()
		// .detachChild(demoActionButton2);
		// SceneManager.getInstance().getCurrentScene()
		// .detachChild(demoActionButton3);
		// SceneManager.getInstance().getCurrentScene()
		// .detachChild(demoActionButton4);

		shotActionButton.setVisible(false);
		moveActionButton.setVisible(false);
		skillActionButton.setVisible(false);
		itemActionButton.setVisible(false);

		unregisterTouchArea(shotActionButton);
		unregisterTouchArea(moveActionButton);
		unregisterTouchArea(skillActionButton);
		unregisterTouchArea(itemActionButton);
	}

	private void showAttackActionButtons() {
		// attachChild(demoActionButton1);
		// //attachChild(demoActionButton2);
		// attachChild(demoActionButton3);
		// attachChild(demoActionButton4);

		shotActionButton.setVisible(true);
		skillActionButton.setVisible(true);
		itemActionButton.setVisible(true);

		registerTouchArea(shotActionButton);
		// registerTouchArea(demoActionButton2);
		registerTouchArea(skillActionButton);
		registerTouchArea(itemActionButton);
	}

	private void showDefenceActionButtons() {
		// //attachChild(demoActionButton1);
		// attachChild(demoActionButton2);
		// attachChild(demoActionButton3);
		// attachChild(demoActionButton4);

		moveActionButton.setVisible(true);
		skillActionButton.setVisible(true);
		itemActionButton.setVisible(true);

		// registerTouchArea(demoActionButton1);
		registerTouchArea(moveActionButton);
		registerTouchArea(skillActionButton);
		registerTouchArea(itemActionButton);
	}

	private void unregisterAllTiles() {
		unregisterTouchArea(blue_square1);
		unregisterTouchArea(blue_square2);
		unregisterTouchArea(blue_square3);
		unregisterTouchArea(blue_square4);

		unregisterTouchArea(green_square1);
		unregisterTouchArea(green_square2);
		unregisterTouchArea(green_square3);
		unregisterTouchArea(green_square4);
	}

	private void hidePlayerBlinkingTile() {
		playerTileGroup.setVisible(false); // hide boxes
		blue_square1.unregisterEntityModifier(blinkModifier1); // unregister
																// blinking
																// or
																// they
																// will
																// stack
		blue_square2.unregisterEntityModifier(blinkModifier2);
		blue_square3.unregisterEntityModifier(blinkModifier3);
		blue_square4.unregisterEntityModifier(blinkModifier4);
	}

	private void hideEnemyBlinkingTile() {
		enemyTileGroup.setVisible(false); // hide the boxes
		green_square1.unregisterEntityModifier(blinkModifier1);
		green_square2.unregisterEntityModifier(blinkModifier2);
		green_square3.unregisterEntityModifier(blinkModifier3);
		green_square4.unregisterEntityModifier(blinkModifier4);
	}

}
