package com.gemserk.commons.gdx.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.animation4j.FrameAnimationImpl;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.gdx.resources.dataloaders.SoundDataLoader;
import com.gemserk.commons.gdx.resources.dataloaders.TextureDataLoader;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.dataloaders.DataLoader;
import com.gemserk.resources.resourceloaders.CachedResourceLoader;
import com.gemserk.resources.resourceloaders.ResourceLoaderImpl;

public class LibgdxResourceBuilder {

	ResourceManager<String> resourceManager;

	private boolean cacheWhenLoad = false;

	public void setCacheWhenLoad(boolean cacheWhenLoad) {
		this.cacheWhenLoad = cacheWhenLoad;
	}

	public LibgdxResourceBuilder(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public FileHandle internal(String file) {
		return Gdx.files.internal(file);
	}

	public FileHandle absolute(String file) {
		return Gdx.files.absolute(file);
	}

	public void texture(String id, String file) {
		texture(id, internal(file), true);
	}

	public void texture(String id, String file, boolean linearFilter) {
		texture(id, internal(file), linearFilter);
	}

	public void texture(String id, FileHandle fileHandle, boolean linearFilter) {
		resourceManager.add(id, new CachedResourceLoader<Texture>(new ResourceLoaderImpl<Texture>(new TextureDataLoader(fileHandle, linearFilter))));
		if (cacheWhenLoad)
			resourceManager.get(id).get();
	}

	public void sound(String id, String file) {
		sound(id, internal(file));
	}

	public void sound(String id, FileHandle fileHandle) {
		resourceManager.add(id, new CachedResourceLoader<Sound>(new ResourceLoaderImpl<Sound>(new SoundDataLoader(fileHandle))));
		if (cacheWhenLoad)
			resourceManager.get(id).get();
	}

	/**
	 * registers a new sprite resource builder returning a new sprite each time it is called.
	 */
	public void sprite(String id, String textureId) {
		final Resource<Texture> texture = resourceManager.get(textureId);
		resourceManager.add(id, new ResourceLoaderImpl<Sprite>(new DataLoader<Sprite>() {
			@Override
			public Sprite load() {
				return new Sprite(texture.get());
			}
		}));
	}

	/**
	 * registers a new sprite resource builder returning a new sprite each time it is called.
	 */
	public void sprite(String id, String textureId, final int x, final int y, final int width, final int height) {
		final Resource<Texture> texture = resourceManager.get(textureId);
		resourceManager.add(id, new ResourceLoaderImpl<Sprite>(new DataLoader<Sprite>() {
			@Override
			public Sprite load() {
				return new Sprite(texture.get(), x, y, width, height);
			}
		}));
	}

	public void animation(String id, String spriteSheetId, final int x, final int y, final int w, final int h, final int framesCount, //
			final boolean loop, final int time, final int... times) {
		final Texture spriteSheet = resourceManager.getResourceValue(spriteSheetId);
		resourceManager.add(id, new ResourceLoaderImpl<Animation>(new DataLoader<Animation>() {

			@Override
			public Animation load() {
				Sprite[] frames = new Sprite[framesCount];
				for (int i = 0; i < frames.length; i++) {
					frames[i] = new Sprite(spriteSheet, x + i * w, y, w, h);
				}

				int[] newTimes = new int[framesCount - 1];
				int lastTime = time;

				for (int i = 0; i < framesCount - 1; i++) {
					if (i < times.length) {
						newTimes[i] = times[i];
						lastTime = times[i];
					} else
						newTimes[i] = lastTime;
				}

				FrameAnimationImpl frameAnimation = new FrameAnimationImpl(time, newTimes);
				frameAnimation.setLoop(loop);

				return new Animation(frames, frameAnimation);
			}

		}));
	}

	// / TESTING STUFF

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void resource(String id, final ResourceBuilder resourceBuilder) {
		resourceManager.add(id, new ResourceLoaderImpl(new DataLoader() {
			@Override
			public Object load() {
				return resourceBuilder.build();
			}
		}));
	}

	public SpriteResourceBuilder sprite2(String textureId) {
		Texture texture = resourceManager.getResourceValue(textureId);
		return new SpriteResourceBuilder(texture);
	}

	public AnimationResourceBuilder animation2(String textureId, int x, int y, int frameWidth, int frameHeight, int framesCount) {
		Texture spriteSheet = resourceManager.getResourceValue(textureId);
		return new AnimationResourceBuilder(spriteSheet, x, y, frameWidth, frameHeight, framesCount);
	}

}