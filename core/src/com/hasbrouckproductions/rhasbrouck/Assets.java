package com.hasbrouckproductions.rhasbrouck;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
/**
 * Created by hasbrouckr on 6/2/2016.
 * Holds and loads assets
 */
public class Assets {
    //Car and Road Textures
    public static Texture audiImage;
    public static Texture roadImage;
    public static Texture customRoad;
    public static Texture startScreen;
    public static Texture sportCar;
    public static Texture taxiImage;
    public static Texture viperImage;
    public static Texture explosionImage;
    public static Texture ambulanceImage;
    public static Texture truckImage;
    public static Texture policeImage;

    //Sound imports
    public static Sound crashSound;
    public static Music drivingMusic;

    //sprite for cars
    public static Sprite roadSprite;
    public static Sprite sportSprite;
    public static Sprite taxiSprite;
    public static Sprite viperSprite;
    public static Sprite ambulanceSprite;
    public static Sprite policeSprite;
    public static Sprite truckSprite;

    public static Texture startButtonImage;
    public static Texture restartButtonImage;

    public static Texture loadTexture(String file){
        return new Texture(Gdx.files.internal(file));
    }

    public static void load(){
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
        startScreen = new Texture(Gdx.files.internal("startscreen.png"));

        //load car sprites
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
        restartButtonImage = new Texture(Gdx.files.internal("startbutton2.png"));

        startButtonImage = new Texture(Gdx.files.internal("startbutton2.png"));

        //load car sound
        drivingMusic = Gdx.audio.newMusic(Gdx.files.internal("driving_sound.mp3"));
        crashSound = Gdx.audio.newSound(Gdx.files.internal("car-crash.wav"));

        drivingMusic.setLooping(true);

    }
}
