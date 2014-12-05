package com.snowrain.gameprojectproto;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

public class ResourcesManager {
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	private static final ResourcesManager INSTANCE = new ResourcesManager();

	public Engine engine;
	public InGameActivity activity;
	public Camera camera;
	public VertexBufferObjectManager vbom;

	// ---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	// ---------------------------------------------

	// 1.Splash asset
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	private ITexture splashTexture;

	// 2. Main menu asset
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion options_region, shop_region;
	private BuildableBitmapTextureAtlas menuTextureAtlas;

	// 3. Loading Scene
	public Font font;

	// 4. Game Scene
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	public ITextureRegion blue_square, blue_square2, blue_square3,
			blue_square4, green_square, green_square2, green_square3,
			green_square4, game_background_region, red_circle;
	// 5. Player asset
	public ITiledTextureRegion player_region;

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}

	public void loadGameResources() {
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(), 1280, 1280,
				TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(menuTextureAtlas, activity,
						"menu_background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "play.png");
		shop_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "shop.png");
		options_region = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(menuTextureAtlas, activity, "options.png");

		try {
			this.menuTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadMenuAudio() {

	}

	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(
				activity.getTextureManager(), 256, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		font = FontFactory.createStrokeFromAsset(activity.getFontManager(),
				mainFontTexture, activity.getAssets(), "font.ttf", 36f, true,
				Color.WHITE.getARGBPackedInt(), 2,
				Color.BLACK.getARGBPackedInt());
		font.load();
	}

	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}

	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(), 1280, 1280,
				TextureOptions.BILINEAR);

		game_background_region = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(gameTextureAtlas, activity,
						"game_background.png");
		// blue squares
		blue_square = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameTextureAtlas, activity, "blue-square.png");
		// blue_square2 =
		// BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
		// activity, "blue-square.png");
		// blue_square3 =
		// BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
		// activity, "blue-square.png");
		// blue_square4 =
		// BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
		// activity, "blue-square.png");
		// green squares
		green_square = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameTextureAtlas, activity, "green-square.png");
		
		red_circle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameTextureAtlas, activity, "red_circle.png");
		// green_square2 =
		// BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
		// activity, "green-square.png");
		// green_square3 =
		// BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
		// activity, "green-square.png");
		// green_square4 =
		// BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
		// activity, "green-square.png");
		player_region = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(gameTextureAtlas, activity, "player.png",
						3, 1); // 3 columns, and 1 row

		try {
			this.gameTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			this.gameTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadGameFonts() {

	}

	private void loadGameAudio() {

	}

	public void unloadGameTextures() {
		// TODO (Since we did not create any textures for game scene yet)
	}

	public void loadSplashScreen() {
		// BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// splashTextureAtlas = new
		// BitmapTextureAtlas(activity.getTextureManager(), 256, 256,
		// TextureOptions.BILINEAR);
		// splash_region =
		// BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas,
		// activity, "splash.png", 0, 0);
		// splashTextureAtlas.load();
		try {
			splashTexture = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/splash.png");
						}
					});
			splashTexture.load();
			splash_region = TextureRegionFactory
					.extractFromTexture(splashTexture);
		} catch (IOException e) {
			Debug.e(e);
		}
	}

	public void unloadSplashScreen() {
		splashTexture.unload();
		splash_region = null;
	}

	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br>
	 * <br>
	 *            We use this method at beginning of game loading, to prepare
	 *            Resources Manager properly, setting all needed parameters, so
	 *            we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, InGameActivity activity,
			Camera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}

	// ---------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------

	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
}
