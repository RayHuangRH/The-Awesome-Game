package Bosses;
import Game.Character;
import Game.SpriteAnimation;
import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

//need to make better code for collision rectangles for rotating enemies
public class ChargeBoss extends MeleeBoss {

    SpriteAnimation bull;
    private final int count = 2;
    private final int columns = 2;
    private final int offsetX = 0;
    private final int offsetY = 0;
    private final Duration duration = Duration.millis(500);
    private final Animation animation;
    
    long stunTime = 1000;
    long timeIndex = 0;
    long lastStunTime = 0;
    boolean charging = true;
    boolean lock = false;
    boolean charged = false;
    int targetX;
    int targetY;
    double angle;
    double vx;
    double vy;
    int speed;
    
    //collision rectangles
    Rectangle body;

    public ChargeBoss(String img, int health, int coin, int width, int height, int speed) {
        super(img, health, coin, width, height, "MULLER THE BULL");
        super.getChildren().remove(iv);
        bull = new SpriteAnimation(img, count, columns, offsetX, offsetY, width, height, duration);
        animation = bull;
        iv = bull.getIV();
        iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        getChildren().addAll(iv);
        this.speed = speed;
        overRun = true;
        
        collisionRects = new ArrayList();
        body = new Rectangle(this.getTranslateX() + 6, this.getTranslateY() + 28, 164, 44);
        body.setFill(Color.TRANSPARENT);
        collisionRects.add(body);
        hasCollisionRects = true;
    }

    public void move(Character player, double width, double height) {
        animation.play();
        
        long timeNow = System.currentTimeMillis();
        
        if (charging) {
            if (this.getX() + 88 <= 0) {
                charging = false;
                timeIndex = System.currentTimeMillis();
                lock = false;
                this.setX(this.getX() + 20);
            }
            if (this.getX() + 88 >= width) {
                charging = false;
                timeIndex = System.currentTimeMillis();
                lock = false;
                this.setX(this.getX() - 20);
            }
            if (this.getY() + 51 <= 0) {
                charging = false;
                timeIndex = System.currentTimeMillis();
                lock = false;
                this.setY(this.getY() + 15);
            }
            if (this.getY() + 51 >= height) {
                charging = false;
                timeIndex = System.currentTimeMillis();
                lock = false;
                this.setY(this.getY() - 15);
            }
        }
        
        if (!charging) {
            if (timeNow - stunTime >= timeIndex) {
                charging = true;
            }
        }
        if (!lock) {
            targetX = player.getX() - this.getX();
            targetY = player.getY() - this.getY();
            angle = Math.atan2(targetY, targetX) * 180 / Math.PI;
            this.setRotate(angle);
            for (Rectangle rect : collisionRects) {
                rect.setRotate(angle);
            }
            lock = true;
        }
        
        if (charging) {
            vx = speed * (90 - Math.abs(angle)) / 90;
            if (angle < 0) {
                vy = -speed + Math.abs(vx);
            } else {
                vy = speed - Math.abs(vx);
            }
            this.setTranslateX(this.getTranslateX() + (int)vx);
            this.setTranslateY(this.getTranslateY() + (int)vy);
            this.x += (int)vx;
            this.y += (int)vy;
            this.healthPos();
        }
        
        //create charged boolean to record if a successful charge has been completed?
        
        
        body.setX(this.getX() + 6);
        body.setY(this.getY() + 28);
    }
}
