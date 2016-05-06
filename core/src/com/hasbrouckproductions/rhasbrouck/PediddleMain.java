package com.hasbrouckproductions.rhasbrouck;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class PediddleMain extends ApplicationAdapter {

	//Asset import
	private Texture audiImage;
	private Texture roadImage;
	private Sound carSound;
	private Sprite roadSprite;

	//Camera and Sprite Batch
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Rectangle mainCar;

	private Array<Rectangle> roads;
	private long lastRoadTime;

	@Override
	public void create () {
		//load images
		audiImage = new Texture(Gdx.files.internal("Audi.png"));
		roadImage = new Texture(Gdx.files.internal("road.png"));
		roadSprite = new Sprite(roadImage);
		roadSprite.setPosition(0, 0);
		roadSprite.setRotation(-90);


		//load car sound
		carSound = Gdx.audio.newSound(Gdx.files.internal("car-motor.wav"));

		//play music in background
		carSound.setLooping(0, true);
		carSound.play();

		//config camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		//Config main car
		mainCar = new Rectangle();
		mainCar.x = 800/2 - 64 / 2;
		mainCar.y = 40;
		mainCar.width = 120;
		mainCar.height = 120;

		roads = new Array<Rectangle>();
		spawnRoad();
	}

	@Override
	public void render () {
		//render car
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		//use coodinate system defined in camera
		batch.setProjectionMatrix(camera.combined);

		//start rendering opjects
		batch.begin();

		for(Rectangle road: roads){
			roadSprite.setX(road.x);
			roadSprite.setY(road.y);
			roadSprite.draw(batch);
		}
		batch.draw(audiImage, mainCar.x, mainCar.y, 120, 120);

		batch.end();

		//main car movement
		if(Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			mainCar.x = touchPos.x - 64/2;
		}

		//have car stay between road
		if(mainCar.x < 130) mainCar.x = 130;
		if(mainCar.x > 280) mainCar.x = 280;


		//road spawn
		Iterator<Rectangle> iter = roads.iterator();
		while(iter.hasNext()){
			Rectangle road = iter.next();
			road.y -= 200 * Gdx.graphics.getDeltaTime();
			if(road.y + 601 < 0){
				iter.remove();
				spawnRoad();
			}
		}
	}

	public void spawnRoad(){
		Rectangle road = new Rectangle();
		road.x = 0;
		road.y = 601;
		road.width = 64;
		road.height = 64;
		roads.add(road);
	}

	@Override
	public void dispose() {
		super.dispose();
		audiImage.dispose();
		roadImage.dispose();
		carSound.dispose();
		batch.dispose();
	}
}
