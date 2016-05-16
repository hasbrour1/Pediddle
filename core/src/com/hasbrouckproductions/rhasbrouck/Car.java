package com.hasbrouckproductions.rhasbrouck;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by hasbrouckr on 5/16/2016.
 * This class will hold the car information
 *
 */
public class Car extends Rectangle {

    private int xPos;
    private int yPos;
    private Texture carImage;

    public Car(int x, int y, Texture img){
        xPos = x;
        yPos = y;
        carImage = img;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public Texture getCarImage() {
        return carImage;
    }

    public void setCarImage(Texture carImage) {
        this.carImage = carImage;
    }
}
