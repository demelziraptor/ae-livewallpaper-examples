About
------
Some of the AndEngine examples (and other misc tests) converted to live wallpapers.

- AccelerometerSpriteActivity = sprite position altered by accelerometer (tilting the device)
- BallActivity = ball infinitely bouncing off camera bounds
- WallpaperActivity = few parallax entities in a scene
- ShakeActivity = message on screen when shake the device

Notes
------
- use GPU emulation on device > 4.0.3
- thanks to Morlicando (https://github.com/morlicando/Wallpaper-example-Andengine-GLES2) for the original working code (used in WallpaperActivity)
- thanks to peceps (http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it#answer-5117254) for shake code

Adding the AndEngine libraries
-------------------------------
Import the GLES2 branch of the following into eclipse via git
- https://github.com/nicolasgramlich/AndEngine.git
- https://github.com/nicolasgramlich/AndEngineLiveWallpaperExtension.git
- https://github.com/nicolasgramlich/AndEnginePhysicsBox2DExtension.git

Then make sure they're added as a library to the project.  (Right-click the andengine projects > properties > android > is library should be checked already, and for the extensions, you may need to remove and re-add the AndEngine project, then right-click this project > properties > android > remove the current libraries and add the andengine projects you just imported)
