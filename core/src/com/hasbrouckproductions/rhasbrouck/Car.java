package com.hasbrouckproductions.rhasbrouck;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by hasbrouckr on 5/16/2016.
 * This class will hold the car sprite and
 * extends the Rectangle class
 *
 */
public class Car extends Rectangle {

    private Sprite carSprite;
    private boolean isPediddle;

    public Car(){
        isPediddle = false;
    }

    public Sprite getCarSprite() {
        return carSprite;
    }

    public void setCarSprite(Sprite carSprite) {
        this.carSprite = carSprite;
    }

    public boolean isPediddle() {
        return isPediddle;
    }

    public void setPediddle(boolean pediddle) {
        isPediddle = pediddle;
    }
}
