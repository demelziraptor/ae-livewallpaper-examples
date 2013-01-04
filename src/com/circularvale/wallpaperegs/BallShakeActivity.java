package com.circularvale.wallpaperegs;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSCounter;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Toast;

//public class BallShakeActivity extends SimpleBaseGameActivity {
public class BallShakeActivity extends BaseLiveWallpaperService {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;

	private static final float BALL_STARTING_VELOCITY = 200.0f;
	private static final float BALL_ACCELERATION = -20f;

	// ===========================================================
	// Fields
	// ===========================================================

	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mFaceTextureRegion;
	
	//private Text centerText;
	private Font mFont;
	
	private final Scene scene = new Scene();

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

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(BallShakeActivity.CAMERA_WIDTH, BallShakeActivity.CAMERA_HEIGHT), camera);
	}

	//@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		
	    
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 0, 2, 1);
		this.mBitmapTextureAtlas.load();
		
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 20);
		this.mFont.load();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	//@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final FPSCounter fpsCounter = new FPSCounter();
		this.mEngine.registerUpdateHandler(fpsCounter);

		this.scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		
		final float centerX = (BallShakeActivity.CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final float centerY = (BallShakeActivity.CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		final Ball ball = new Ball(centerX, centerY, this.mFaceTextureRegion, vertexBufferObjectManager);
		
		//this.centerText = new Text(100, 40, mFont, "Initial text", new TextOptions(HorizontalAlign.CENTER), vertexBufferObjectManager);
		
		final Text elapsedText = new Text(100, 160, this.mFont, "Seconds elapsed:", "Seconds elapsed: XXXXX".length(), vertexBufferObjectManager);

		this.scene.attachChild(ball);
		this.scene.attachChild(elapsedText);
		
		scene.registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
			//@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				elapsedText.setText("Seconds elapsed: " + BallShakeActivity.this.mEngine.getSecondsElapsedTotal());
			}
		}));
		
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensorListener = new ShakeEventListener();
	    mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

	      public void onShake() {
	        Toast.makeText(BallShakeActivity.this, "Shake!", Toast.LENGTH_SHORT).show();
	        ball.mPhysicsHandler.setAcceleration(BallShakeActivity.BALL_STARTING_VELOCITY, BallShakeActivity.BALL_STARTING_VELOCITY);
	      }
	    });
		
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

	private class Ball extends AnimatedSprite {
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
			
			//BallShakeActivity.this.setText(this, BallShakeActivity.this.centerText);
			
			final float currentVelocityX = this.mPhysicsHandler.getVelocityX();
			final float currentVelocityY = this.mPhysicsHandler.getVelocityY();
			final float currentAccelerationX = this.mPhysicsHandler.getAccelerationX();
			final float currentAccelerationY = this.mPhysicsHandler.getAccelerationY();
			
			// has been travelling to the left
			if(this.mX < 0) {
				this.mPhysicsHandler.setVelocityX(-currentVelocityX);
				this.mPhysicsHandler.setAccelerationX(makeNegative(currentAccelerationX));
			// has been travelling to the right
			} else if(this.mX + this.getWidth() > BallShakeActivity.CAMERA_WIDTH) {
				this.mPhysicsHandler.setVelocityX(-currentVelocityX);
				this.mPhysicsHandler.setAccelerationX(makePositive(currentAccelerationX));
			}
			// has been travelling to the bottom
			if(this.mY < 0) {
				this.mPhysicsHandler.setVelocityY(-currentVelocityY);
				this.mPhysicsHandler.setAccelerationY(makeNegative(currentAccelerationY));
			// has been travelling to the top
			} else if(this.mY + this.getHeight() > BallShakeActivity.CAMERA_HEIGHT) {
				this.mPhysicsHandler.setVelocityY(-currentVelocityY);
				this.mPhysicsHandler.setAccelerationY(makePositive(currentAccelerationY));
			}
			
			// stop if velocity reaches 0
			if(currentVelocityX == 0.0f){
				this.mPhysicsHandler.setAccelerationX(0.0f);
				this.mPhysicsHandler.setAccelerationY(0.0f);
				this.mPhysicsHandler.setVelocityX(0.0f);
				this.mPhysicsHandler.setVelocityY(0.0f);
			}
			if(currentVelocityY == 0.0f){
				this.mPhysicsHandler.setAccelerationX(0.0f);
				this.mPhysicsHandler.setAccelerationY(0.0f);
				this.mPhysicsHandler.setVelocityX(0.0f);
				this.mPhysicsHandler.setVelocityY(0.0f);
			}

			super.onManagedUpdate(pSecondsElapsed);
		}
		
		private float makePositive(final float velo){
			return Math.abs(velo);
		}
		
		private float makeNegative(final float velo){
			return -(Math.abs(velo));
		}
	}

}
