package com.snowrain.gameprojectproto;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;

public class InGameActivity extends BaseGameActivity {

	private static int CAMERA_WIDTH = 1280;
	private static int CAMERA_HEIGHT = 720;

	private Camera mCamera;
	private ResourcesManager resourcesManager;

	private ITextureRegion mBlueSquareRegion, mGreenSquareRegion,
			mRedCircleRegion, mPlayerRegion;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		ResourcesManager.prepareManager(mEngine, this, mCamera,
				getVertexBufferObjectManager());
		resourcesManager = ResourcesManager.getInstance();
		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);

	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
//		 mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
//		    {
//		            public void onTimePassed(final TimerHandler pTimerHandler) 
//		            {
//		                mEngine.unregisterUpdateHandler(pTimerHandler);
//		                // load menu resources, create menu scene
//		                // set menu scene using scene manager
//		                // disposeSplashScene();
//		                // READ NEXT ARTICLE FOR THIS PART.
//		                SceneManager.getInstance().createMenuScene();
//		            }
//		    }));
	//	pScene.setOnSceneTouchListener(pOnSceneTouchListener)
		SceneManager.getInstance().getCurrentScene().setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					SceneManager.getInstance().createMenuScene();
			        return true;
			    }
			    return false;
			}
		});
		    pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}

}
