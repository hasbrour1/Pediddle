package com.hasbrouckproductions.rhasbrouck;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class PediddleMain extends ApplicationAdapter {

	//Asset import
	private Texture audiImage;
	private Texture roadImage;
	private Texture customRoad;
	private Texture sportCar;
	private Texture taxiImage;
	private Texture viperImage;
	private Texture explosionImage;
	private Texture ambulanceImage;
	private Texture truckImage;
	private Texture policeImage;

	private Sound crashSound;
	private Music drivingMusic;

	private Sprite roadSprite;
	private Sprite sportSprite;
	private Sprite taxiSprite;
	private Sprite viperSprite;
	private Sprite ambulanceSprite;
	private Sprite policeSprite;
	private Sprite truckSprite;

	//Camera and Sprite Batch
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Rectangle mainCar;

	private Array<Rectangle> roads;
	private Array<Rectangle> roads2;

	private ArrayList<Car> leftLaneArray;
	private ArrayList<Car> rightLaneArray;

	private long lastRoadTime;
	private long lastCarTime;
	private long lastTaxiTime;

	private CustomButton mCustomButton;
	private Texture restartButtonImage;

	private CustomButton mStartButton;
	private Texture startButtonImage;

	private State state = State.RUN;

	@Override
	public void create () {
		//load images
		audiImage = new Texture(Gdx.files.internal("Audi.png"));
		roadImage = new Texture(Gdx.files.internal("roadmarker.png"));
		customRoad = new Texture(Gdx.files.internal("roadcustom.png"));
		sportCar = new Texture(Gdx.files.internal("Car.png"));
		taxiImage = new Texture(Gdx.files.internal("taxi.png"));
		viperImage = new Texture(Gdx.files.internal("Black_viper.png"));
		ambulanceImage = new Texture(Gdx.files.internal("Ambulance.png"));
		truckImage = new Texture(Gdx.files.internal("Mini_truck.png"));
		policeImage = new Texture(Gdx.files.internal("Police.png"));
		explosionImage = new Texture(Gdx.files.internal("explosion.png"));

		sportSprite = new Sprite(sportCar);
		sportSprite.setSize(120, 120);
		sportSprite.setOrigin(0,0);
		sportSprite.setRotation(-180);

		taxiSprite = new Sprite(taxiImage);
		taxiSprite.setSize(120, 120);

		viperSprite = new Sprite(viperImage);
		viperSprite.setSize(120,120);

		ambulanceSprite = new Sprite(ambulanceImage);
		ambulanceSprite.setSize(120, 120);
		ambulanceSprite.setOrigin(0,0);
		ambulanceSprite.setRotation(-180);

		truckSprite = new Sprite(truckImage);
		truckSprite.setSize(120, 120);
		truckSprite.setOrigin(0,0);
		truckSprite.setRotation(-180);

		policeSprite = new Sprite(policeImage);
		policeSprite.setSize(120, 120);

		roadSprite = new Sprite(roadImage);

		//Restart Button
		restartButtonImage = new Texture(Gdx.files.internal("restartbutton.png"));
		mCustomButton = new CustomButton(restartButtonImage, 350, 240, 120, 50);

		//Start Button
		startButtonImage = new Texture(Gdx.files.internal("startbutton.png"));
		mStartButton = new CustomButton(startButtonImage, 350, 240, 120, 50);

		//create Car holder
		leftLaneArray = new ArrayList<Car>();
		rightLaneArray = new ArrayList<Car>();

		//load car sound
		drivingMusic = Gdx.audio.newMusic(Gdx.files.internal("driving_sound.mp3"));
		crashSound = Gdx.audio.newSound(Gdx.files.internal("car-crash.wav"));

		drivingMusic.setLooping(true);

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

		spawnRoad();
		spawnLeftLane();
		spawnRightLane();

		state = State.START;
	}

	public void resume(){
		//create Car holder
		leftLaneArray = new ArrayList<Car>();
		rightLaneArray = new ArrayList<Car>();

		//config camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		roads = new Array<Rectangle>();
		roads2 = new Array<Rectangle>();

		spawnRoad();
		spawnLeftLane();
		spawnRightLane();

		state = State.RUN;
	}

	@Override
	public void render () {

		switch(state){
			case START:
				camera.update();

				//use coordinate system defined in camera
				batch.setProjectionMatrix(camera.combined);

				//start rendering objects
				batch.begin();

				batch.draw(customRoad, 0, 0);
				mStartButton.update(batch);

				batch.end();

				//if screen it touched, relaunch from beginning
				if(Gdx.input.isTouched()){
					Vector3 touchPos = new Vector3();
					touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					camera.unproject(touchPos);
					if(mStartButton.checkIfClicked(touchPos.x, touchPos.y)){
						drivingMusic.play();
						state = state.RUN;
					}
				}
				break;

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
				for(Car car: rightLaneArray){
					car.getCarSprite().setX(car.getX());
					car.getCarSprite().setY(car.getY());
					car.getCarSprite().draw(batch);
				}//change this to cars
				for(Car car: leftLaneArray){
					car.getCarSprite().setX(car.getX());
					car.getCarSprite().setY(car.getY());
					car.getCarSprite().draw(batch);
				}

				batch.draw(audiImage, mainCar.x, mainCar.y, 120, 120);

				batch.end();

				//main car movement
				if(Gdx.input.isTouched()){
					Vector3 touchPos = new Vector3();
					touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					camera.unproject(touchPos);
					//main car movement
					if(touchPos.x - 64/2 > mainCar.x && touchPos.x - 64/2 >= 10 + mainCar.x){
						mainCar.x += 5;
					}else if(touchPos.x - 64/2 < mainCar.x && touchPos.x - 64/2 <= 10 + mainCar.x){
						mainCar.x -= 5;
					}
				}

				//have car stay between road
				if(mainCar.x < 460) mainCar.x = 460;
				if(mainCar.x > 640) mainCar.x = 640;


				//road spawn
				if(TimeUtils.nanoTime() - lastRoadTime > 1000000000 / 2) spawnRoad();

				if(TimeUtils.nanoTime() - lastCarTime > 1000000000 * 2) spawnLeftLane();

				if(TimeUtils.nanoTime() - lastTaxiTime > 1000000000 * 1.5) spawnRightLane();

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

				Iterator<Car> iter3 = rightLaneArray.iterator();
				while(iter3.hasNext()){
					Rectangle car = iter3.next();
					car.y -= 300 *(Gdx.graphics.getDeltaTime());
					if(car.y + 200 < 0)iter3.remove();
				}

				Iterator<Car> iter4 = leftLaneArray.iterator();
				while(iter4.hasNext()){
					Car car = iter4.next();
					car.y -= 250 * (Gdx.graphics.getDeltaTime());
					if(car.y + 200 < 0)iter4.remove();
					if(car.overlaps(mainCar)){
						//crash
						car.setCarSprite(new Sprite(explosionImage));
						drivingMusic.pause();
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
				for(Car car: rightLaneArray){
					car.getCarSprite().setX(car.getX());
					car.getCarSprite().setY(car.getY());
					car.getCarSprite().draw(batch);
				}//change this to cars
				for(Car car: leftLaneArray){
					car.getCarSprite().setX(car.getX());
					car.getCarSprite().setY(car.getY());
					car.getCarSprite().draw(batch);
				}

				batch.draw(explosionImage, mainCar.x, mainCar.y, 120, 120);

				mCustomButton.update(batch);

				batch.end();

				//if screen it touched, relaunch from beginning
				if(Gdx.input.isTouched()){
					Vector3 touchPos = new Vector3();
					touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					camera.unproject(touchPos);
					if(mCustomButton.checkIfClicked(touchPos.x, touchPos.y)){
						drivingMusic.play();
						resume();
					}
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

	public void spawnLeftLane(){
		Car car = new Car();
		//random side of road
		//220 for left 350 for right
		if(Math.random() >= 0.5){
			car.x = 220;
		}else{
			car.x = 350;
		}
		car.y = 600;
		car.width = 60;
		car.height = 120;

		//set Image for car
		double randResult = Math.random();
		if(randResult <= 0.3){
			car.setCarSprite(sportSprite);
		}else if(randResult > 0.3 && randResult <= 0.7){
			car.setCarSprite(truckSprite);
		}else{
			car.setCarSprite(ambulanceSprite);
		}

		rightLaneArray.add(car);
		lastCarTime = TimeUtils.nanoTime();
	}

	public void spawnRightLane(){
		Car car = new Car();

		//random side of road
		//right side 600 left 480
		if(Math.random() >= 0.5){
			car.x = 600;
		}else{
			car.x = 480;
		}
		car.y = 600;
		car.width = 60;
		car.height = 120;

		//set Image for car
		double randResult = Math.random();
		if(randResult <= 0.3){
			car.setCarSprite(viperSprite);
		}else if(randResult > 0.3 && randResult <= 0.7){
			car.setCarSprite(taxiSprite);
		}else{
			car.setCarSprite(policeSprite);
		}

		leftLaneArray.add(car);
		lastTaxiTime = TimeUtils.nanoTime();
	}

	@Override
	public void dispose() {
		super.dispose();
		audiImage.dispose();
		roadImage.dispose();
		policeImage.dispose();
		ambulanceImage.dispose();
		sportCar.dispose();
		crashSound.dispose();
		explosionImage.dispose();
		truckImage.dispose();
		drivingMusic.dispose();
		batch.dispose();
	}

	public enum State{
		START,
		PAUSE,
		RUN,
		CRASH,
		STOPPED
	}
}
