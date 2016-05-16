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

import org.w3c.dom.css.Rect;

public class PediddleMain extends ApplicationAdapter {

	//Asset import
	private Texture audiImage;
	private Texture roadImage;
	private Texture customRoad;
	private Texture sportCar;
	private Texture taxiImage;
	private Texture explosionImage;

	private Sound crashSound;
	private Music drivingMusic;

	private Sprite roadSprite;
	private Sprite sportSprite;
	private Sprite taxiSprite;

	//Camera and Sprite Batch
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Rectangle mainCar;

	private Array<Rectangle> roads;
	private Array<Rectangle> roads2;
	private Array<Rectangle> cars;
	private Array<Rectangle> taxis;

	private long lastRoadTime;
	private long lastCarTime;
	private long lastTaxiTime;

	private State state = State.RUN;

	@Override
	public void create () {
		//load images
		audiImage = new Texture(Gdx.files.internal("Audi.png"));
		roadImage = new Texture(Gdx.files.internal("roadmarker.png"));
		customRoad = new Texture(Gdx.files.internal("roadcustom.png"));
		sportCar = new Texture(Gdx.files.internal("Car.png"));
		taxiImage = new Texture(Gdx.files.internal("taxi.png"));
		explosionImage = new Texture(Gdx.files.internal("explosion.png"));

		sportSprite = new Sprite(sportCar);
		sportSprite.setSize(120, 120);
		sportSprite.setOrigin(0,0);
		sportSprite.setRotation(-180);

		taxiSprite = new Sprite(taxiImage);
		taxiSprite.setSize(120, 120);


		roadSprite = new Sprite(roadImage);

		//load car sound
		drivingMusic = Gdx.audio.newMusic(Gdx.files.internal("driving_sound.mp3"));
		crashSound = Gdx.audio.newSound(Gdx.files.internal("car-crash.wav"));

		drivingMusic.setLooping(true);
		drivingMusic.play();

		//config camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		//Config main car
		mainCar = new Rectangle();
		mainCar.x = 800/2 - 64 / 2;
		mainCar.y = 40;
		mainCar.width = 60;
		mainCar.height = 120;

		roads = new Array<Rectangle>();
		roads2 = new Array<Rectangle>();
		cars = new Array<Rectangle>();
		taxis = new Array<Rectangle>();
		spawnRoad();
		spawnCar();
		spawnTaxi();

		state = State.RUN;
	}

	@Override
	public void render () {

		switch(state){
			case RUN:
				//render car
				Gdx.gl.glClearColor(0, 0, 0.2f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

				camera.update();

				//use coodinate system defined in camera
				batch.setProjectionMatrix(camera.combined);

				//start rendering opjects
				batch.begin();

				batch.draw(customRoad, 0, 0);
				for(Rectangle road: roads){
					roadSprite.setX(road.x);
					roadSprite.setY(road.y);
					roadSprite.draw(batch);
				}
				for(Rectangle road: roads2){
					roadSprite.setX(590);
					roadSprite.setY(road.y);
					roadSprite.draw(batch);
				}
				for(Rectangle car: cars){
					sportSprite.setX(car.x);
					sportSprite.setY(car.y);
					sportSprite.draw(batch);
				}
				for(Rectangle taxi: taxis){
					taxiSprite.setX(taxi.x);
					taxiSprite.setY(taxi.y);
					taxiSprite.draw(batch);
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
				if(mainCar.x < 460) mainCar.x = 460;
				if(mainCar.x > 640) mainCar.x = 640;


				//road spawn
				if(TimeUtils.nanoTime() - lastRoadTime > 1000000000 / 2) spawnRoad();

				if(TimeUtils.nanoTime() - lastCarTime > 1000000000 * 2) spawnCar();

				if(TimeUtils.nanoTime() - lastTaxiTime > 1000000000 * 1.5) spawnTaxi();

				Iterator<Rectangle> iter = roads.iterator();
				while(iter.hasNext()){
					Rectangle road = iter.next();
					road.y -= 200 * Gdx.graphics.getDeltaTime();
					if(road.y + 601 < 0){
						iter.remove();
					}
				}

				Iterator<Rectangle> iter2 = roads2.iterator();
				while(iter2.hasNext()){
					Rectangle road = iter2.next();
					road.y -= 200 * Gdx.graphics.getDeltaTime();
					if(road.y + 601 < 0){
						iter2.remove();
					}
				}

				Iterator<Rectangle> iter3 = cars.iterator();
				while(iter3.hasNext()){
					Rectangle car = iter3.next();
					car.y -= 300 *(Gdx.graphics.getDeltaTime());
					if(car.y + 200 < 0)iter3.remove();
				}

				Iterator<Rectangle> iter4 = taxis.iterator();
				while(iter4.hasNext()){
					Rectangle taxi = iter4.next();
					taxi.y -= 250 * (Gdx.graphics.getDeltaTime());
					if(taxi.y + 200 < 0)iter4.remove();
					if(taxi.overlaps(mainCar)){
						//crash
						crashSound.play();
						state = State.CRASH;
					}
				}
				break;
			case CRASH:

				camera.update();

				//use coordinate system defined in camera
				batch.setProjectionMatrix(camera.combined);

				//start rendering objects
				batch.begin();

				batch.draw(customRoad, 0, 0);
				for(Rectangle road: roads){
					roadSprite.setX(road.x);
					roadSprite.setY(road.y);
					roadSprite.draw(batch);
				}
				for(Rectangle road: roads2){
					roadSprite.setX(590);
					roadSprite.setY(road.y);
					roadSprite.draw(batch);
				}
				for(Rectangle car: cars){
					sportSprite.setX(car.x);
					sportSprite.setY(car.y);
					sportSprite.draw(batch);
				}
				for(Rectangle taxi: taxis){
					taxiSprite.setX(taxi.x);
					taxiSprite.setY(taxi.y);
					taxiSprite.setTexture(explosionImage);
					taxiSprite.draw(batch);
				}

				batch.draw(explosionImage, mainCar.x, mainCar.y, 120, 120);

				batch.end();

				//if screen it touched, relaunch from beginning
				if(Gdx.input.isTouched()){
					create();
				}

				break;
		}
	}

	public void spawnRoad(){
		Rectangle road = new Rectangle();
		road.x = 210;
		road.y = 480;
		road.width = 19;
		road.height = 39;
		roads.add(road);
		roads2.add(road);
		lastRoadTime = TimeUtils.nanoTime();
	}

	public void spawnCar(){
		Rectangle car = new Rectangle();
		//random side of road
		//220 for left 350 for right
		if(Math.random() >= 0.5){
			car.x = 220;
		}else{
			car.x = 350;
		}
		car.y = 600;
		car.width = 120;
		car.height = 120;
		cars.add(car);
		lastCarTime = TimeUtils.nanoTime();
	}

	public void spawnTaxi(){
		Rectangle taxi = new Rectangle();
		//random side of road
		//right side 600 left 480
		if(Math.random() >= 0.5){
			taxi.x = 600;
		}else{
			taxi.x = 480;
		}
		taxi.y = 600;
		taxi.width = 60;
		taxi.height = 120;
		taxis.add(taxi);
		lastTaxiTime = TimeUtils.nanoTime();
	}

	@Override
	public void dispose() {
		super.dispose();
		audiImage.dispose();
		roadImage.dispose();
		drivingMusic.dispose();
		batch.dispose();
	}

	public enum State{
		PAUSE,
		RUN,
		CRASH,
		STOPPED
	}
}
