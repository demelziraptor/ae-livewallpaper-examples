package com.circularvale.wallpaperegs;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Toast;

//public class BallShakeActivity extends SimpleBaseGameActivity {
public class BallShakeActivity extends BaseLiveWallpaperService {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	private static final float BALL_STARTING_VELOCITY = 200.0f;
	private static final float BALL_ACCELERATION = -10f;

	// ===========================================================
	// Fields
	// ===========================================================

	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mFaceTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	//@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, BallShakeActivity.CAMERA_WIDTH, BallShakeActivity.CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(BallShakeActivity.CAMERA_WIDTH, BallShakeActivity.CAMERA_HEIGHT), camera);
	}

	//@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensorListener = new ShakeEventListener();
	    mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

	      public void onShake() {
	        Toast.makeText(BallShakeActivity.this, "Shake!", Toast.LENGTH_SHORT).show();
	      }
	    });
	    
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 0, 2, 1);
		this.mBitmapTextureAtlas.load();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	//@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		final float centerX = (BallShakeActivity.CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final float centerY = (BallShakeActivity.CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		final Ball ball = new Ball(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager());

		scene.attachChild(ball);
		
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);

		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}
	
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
		
	}

	/*
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}
*/
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class Ball extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;

		public Ball(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			this.mPhysicsHandler.setVelocity(BallShakeActivity.BALL_STARTING_VELOCITY, BallShakeActivity.BALL_STARTING_VELOCITY);
			this.mPhysicsHandler.setAcceleration(BallShakeActivity.BALL_ACCELERATION, BallShakeActivity.BALL_ACCELERATION);
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			final float currentVelocityX = this.mPhysicsHandler.getVelocityX();
			final float currentVelocityY = this.mPhysicsHandler.getVelocityY();
			
			if(this.mX < 0) {
				this.mPhysicsHandler.setVelocityX(BallShakeActivity.BALL_STARTING_VELOCITY);
//				this.mPhysicsHandler.setAccelerationX(BallShakeActivity.BALL_ACCELERATION);
			} else if(this.mX + this.getWidth() > BallShakeActivity.CAMERA_WIDTH) {
				this.mPhysicsHandler.setVelocityX(-currentVelocityX);
//				this.mPhysicsHandler.setAccelerationX(BallShakeActivity.BALL_ACCELERATION);
			}

			if(this.mY < 0) {
				this.mPhysicsHandler.setVelocityY(BallShakeActivity.BALL_STARTING_VELOCITY);
//				this.mPhysicsHandler.setAccelerationY(BallShakeActivity.BALL_ACCELERATION);
			} else if(this.mY + this.getHeight() > BallShakeActivity.CAMERA_HEIGHT) {
				this.mPhysicsHandler.setVelocityY(-currentVelocityY);
//				this.mPhysicsHandler.setAccelerationY(BallShakeActivity.BALL_ACCELERATION);
			}

			super.onManagedUpdate(pSecondsElapsed);
		}
	}

}
