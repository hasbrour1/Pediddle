package com.hasbrouckproductions.rhasbrouck;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


/*
 *
 * 	PediddleMain runs the main game and
 * 	Changes between game states
 * 	use https://github.com/libgdx/libgdx/wiki/Accelerometer
 *	to capture motion
 *
 */
public class PediddleMain extends ApplicationAdapter {

	//Camera and Sprite Batch
	private SpriteBatch batch;
	private OrthographicCamera camera;

	//Main car Rectangle
	private Rectangle mainCar;

	//Arrays for road lines
	private Array<Rectangle> roads;
	private Array<Rectangle> roads2;

	//Arrays for cars appearing on both lanes
	private ArrayList<Car> rightLaneArray;
	private ArrayList<Car> leftLaneArray;

	//Times for spawning;
	private long lastRoadTime;
	private long lastCarTime;
	private long lastTaxiTime;

	private CustomButton mCustomButton;
	private CustomButton mStartButton;
	private CustomButton mResetScoreButton;

	//State keeps track of current game state
	private State state;

	//Score for how many pediddles
	private int score;

	//keeps track of elapsed time
	private long startTime;
	private long estimatedTime;
	private BitmapFont font;

	//Accelerometer Variables
	private float accelZ;

	private long lastPediddle;

	@Override
	public void create () {

		//Load Settings
		Settings.load();

		//Load Assets
		Assets.load();

		//Buttons
		mCustomButton = new CustomButton(Assets.restartButtonImage, 350, 240, 120, 50);
		mStartButton = new CustomButton(Assets.startButtonImage, 350, 240, 120, 50);
		mResetScoreButton = new CustomButton(Assets.resetScoreButtonImage, 620, 50, 120, 50);

		//create Car holder
		rightLaneArray = new ArrayList<Car>();
		leftLaneArray = new ArrayList<Car>();

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

		//initialize road arrays
		roads = new Array<Rectangle>();
		roads2 = new Array<Rectangle>();

		font = new BitmapFont(Gdx.files.internal("pediddle.fnt"),
				Gdx.files.internal("pediddle.png"), false);

		//Start game
		state = State.START;
	}

	//Resume will restart a new game after a crash
	public void resume(){
		//create Car holder
		rightLaneArray = new ArrayList<Car>();
		leftLaneArray = new ArrayList<Car>();

		//config camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		roads = new Array<Rectangle>();
		roads2 = new Array<Rectangle>();

		spawnRoad();
		spawnLeftLane();
		spawnRightLane();

		score = 0;
		startTime = System.currentTimeMillis();
		lastPediddle = TimeUtils.nanoTime();
		state = State.RUN;
	}

	@Override
	public void render () {

		//Render according to state of game
		switch(state){
			case START:
				camera.update();

				//use coordinate system defined in camera
				batch.setProjectionMatrix(camera.combined);

				//start rendering objects
				batch.begin();

				batch.draw(Assets.startScreen, 0, 0);
				mStartButton.update(batch);
				mResetScoreButton.update(batch);
				font.draw(batch, "Longest Time: " + Settings.longestTime + " Seconds" , 10, 100);
				font.draw(batch, "High Score: " + Settings.highScore + " Pediddles" , 10, 60);

				batch.end();

				//if screen it touched, relaunch from beginning
				if(Gdx.input.isTouched()){
					Vector3 touchPos = new Vector3();
					touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					camera.unproject(touchPos);

					//Start Game
					if(mStartButton.checkIfClicked(touchPos.x, touchPos.y)){
						Assets.drivingMusic.play();
						score = 0;
						//Spawn first roads and cars
						spawnRoad();
						spawnLeftLane();
						spawnRightLane();
						startTime = System.currentTimeMillis();
						state = state.RUN;
					}

					//Reset Score
					if(mResetScoreButton.checkIfClicked(touchPos.x, touchPos.y)){
						Settings.highScore = 0;
						Settings.longestTime = 0;
						Settings.save();
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

				//Draw Roads and Cars
				batch.draw(Assets.customRoad, 0, 0);
				for(Rectangle road: roads){
					Assets.roadSprite.setX(road.x);
					Assets.roadSprite.setY(road.y);
					Assets.roadSprite.draw(batch);
				}
				for(Rectangle road: roads2){
					Assets.roadSprite.setX(590);
					Assets.roadSprite.setY(road.y);
					Assets.roadSprite.draw(batch);
				}
				for(Car car: leftLaneArray){
					car.getCarSprite().setX(car.getX());
					car.getCarSprite().setY(car.getY());
					car.getCarSprite().draw(batch);
				}//change this to cars
				for(Car car: rightLaneArray){
					car.getCarSprite().setX(car.getX());
					car.getCarSprite().setY(car.getY());
					car.getCarSprite().draw(batch);
				}


				//Draw Score and Time
				estimatedTime = System.currentTimeMillis() - startTime;
				estimatedTime /= 100;

				font.draw(batch, estimatedTime + "", 10, 40);
				font.draw(batch, "Score: " + score, 10, 100);

				//Draw Main Car
				batch.draw(Assets.audiImage, mainCar.x, mainCar.y, 120, 120);

				batch.end();

				//If phone is raised then check if pediddle
				//Don't check if last check was less than a second ago
				accelZ = Gdx.input.getAccelerometerZ();
				int tempScore;
				Gdx.app.log("ACCELZ", "Starting accelz Check" + accelZ + " " + (TimeUtils.nanoTime() - lastPediddle));
				if((accelZ < -5) && (TimeUtils.nanoTime() - lastPediddle > 2000000000)){
					lastPediddle = TimeUtils.nanoTime();
					tempScore = 0;
					Gdx.app.log("ACCELZ", "Is below -5");
					for(Car car: leftLaneArray){
						Gdx.app.log("ACCELZ", "checking car");
						if(car.isPediddle() == true){
							Gdx.app.log("ACCELZ", "add to score");
							tempScore++;
						}
					}
					Gdx.app.log("ACCELZ", "finished check");
					if(tempScore == 0){
						Gdx.app.log("ACCELZ", "score sub = " + score);
						score --;
					}else{
						Gdx.app.log("ACCELZ", "score add = " + tempScore);
						score += tempScore;
					}
				}


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

				Iterator<Car> iter3 = leftLaneArray.iterator();
				while(iter3.hasNext()){
					Rectangle car = iter3.next();
					car.y -= 300 *(Gdx.graphics.getDeltaTime());
					if(car.y + 200 < 0)iter3.remove();
				}

				Iterator<Car> iter4 = rightLaneArray.iterator();
				while(iter4.hasNext()){
					Car car = iter4.next();
					car.y -= 250 * (Gdx.graphics.getDeltaTime());
					if(car.y + 200 < 0)iter4.remove();
					if(car.overlaps(mainCar)){
						//crash
						car.setCarSprite(new Sprite(Assets.explosionImage));
						Assets.drivingMusic.pause();
						Assets.crashSound.play();
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

				batch.draw(Assets.customRoad, 0, 0);
				for(Rectangle road: roads){
					Assets.roadSprite.setX(road.x);
					Assets.roadSprite.setY(road.y);
					Assets.roadSprite.draw(batch);
				}
				for(Rectangle road: roads2){
					Assets.roadSprite.setX(590);
					Assets.roadSprite.setY(road.y);
					Assets.roadSprite.draw(batch);
				}
				for(Car car: leftLaneArray){
					car.getCarSprite().setX(car.getX());
					car.getCarSprite().setY(car.getY());
					car.getCarSprite().draw(batch);
				}
				for(Car car: rightLaneArray){
					car.getCarSprite().setX(car.getX());
					car.getCarSprite().setY(car.getY());
					car.getCarSprite().draw(batch);
				}

				font.draw(batch, estimatedTime + "", 10, 40);
				font.draw(batch, "Score: " + score, 10, 100);

				batch.draw(Assets.explosionImage, mainCar.x, mainCar.y, 120, 120);

				mCustomButton.update(batch);
				font.draw(batch, "You Lasted: " + estimatedTime + " Seconds" , 330, 215);
				font.draw(batch, "You Scored: " + score + " Pediddles" , 330, 180);

				//High Score Display
				if(Settings.longestTime < estimatedTime){
					Settings.addTime(estimatedTime);
					Settings.save();
				}
				if(Settings.highScore < score){
					Settings.addHighScore(score);
				}

				font.draw(batch, "Longest Time: " + Settings.longestTime + " Seconds" , 10, 460);
				font.draw(batch, "High Score: " + Settings.highScore + " Pediddles" , 10, 420);

				//Testing Accellerometer

				accelZ = Gdx.input.getAccelerometerZ();
				if(accelZ < -5){
					font.draw(batch, "Pediddle!" + accelZ, 300, 390);
				}

				batch.end();

				//if screen it touched, relaunch from beginning
				if(Gdx.input.isTouched()){
					Vector3 touchPos = new Vector3();
					touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					camera.unproject(touchPos);
					if(mCustomButton.checkIfClicked(touchPos.x, touchPos.y)){
						Assets.drivingMusic.play();
						resume();
					}
				}

				break;
		}
	}

	//Spawns road lines
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

	//Spawns cars on left lane
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
			car.setCarSprite(Assets.pediddleSprite);
			car.setPediddle(true);
		}
		else if(randResult > 0.3 && randResult <= 0.6){
			car.setCarSprite(Assets.sportSprite);
		}else if(randResult > 0.6 && randResult <= 0.8){
			car.setCarSprite(Assets.truckSprite);
		}else{
			car.setCarSprite(Assets.ambulanceSprite);
		}

		leftLaneArray.add(car);
		lastCarTime = TimeUtils.nanoTime();
	}

	//spawns cars on right lane
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
			car.setCarSprite(Assets.viperSprite);
		}else if(randResult > 0.3 && randResult <= 0.7){
			car.setCarSprite(Assets.taxiSprite);
		}else{
			car.setCarSprite(Assets.policeSprite);
		}

		rightLaneArray.add(car);
		lastTaxiTime = TimeUtils.nanoTime();
	}

	@Override
	public void dispose() {
		super.dispose();
		Assets.audiImage.dispose();
		Assets.roadImage.dispose();
		Assets.policeImage.dispose();
		Assets.ambulanceImage.dispose();
		Assets.sportCar.dispose();
		Assets.crashSound.dispose();
		Assets.explosionImage.dispose();
		Assets.truckImage.dispose();
		Assets.drivingMusic.dispose();
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
