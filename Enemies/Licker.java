package Enemies;
import Game.Character;
import Game.SpriteAnimation;

//A.K.A Charles

import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

public class Licker extends MeleeEnemy {

    SpriteAnimation charles;
    private final int count = 20;
    private final int columns = 10;
    private final int offsetX = 0;
    private final int offsetY = 0;
    private final Duration duration = Duration.millis(900);
    private final Animation animation;
    
    public Licker(String img, int health, int coin, int width, int height) {
        super(img, health, coin, width, height);
        super.getChildren().remove(iv);
        charles = new SpriteAnimation(img, count, columns, offsetX, offsetY, width, height, duration);         
        animation = charles;
        iv = charles.getIV();
        iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        getChildren().addAll(iv);
        
        //change below to true if collision rectangles are added
        hasCollisionRects = false;    
    }
    
    public void hitView(Enemy enemy) {
        charles.setOffset(0, 0);
    }
    
    public void move(Character player, double width, double height) {
        animation.play();
        //X - 10 and Y - 30 is so it looks like Charles is aiming for middle of player
	if (player.getX() - 10 > this.getX() && player.getY() - 30 == this.getY()) { //right
            charles.setOffset(0, 320);
            this.moveX(1, width);
        }
        if (player.getX() - 10 < this.getX() && player.getY() - 30 == this.getY()) { //left
            charles.setOffset(0, 480);
            this.moveX(-1, width);
        }
        if (player.getX() - 10 == this.getX() && player.getY() - 30 > this.getY()) { //down
            charles.setOffset(0, 0);
            this.moveY(1, height);
        }
        if (player.getX() - 10 == this.getX() && player.getY() - 30 < this.getY()) { //up
            charles.setOffset(0, 160);
            this.moveY(-1, height);
        }

        if (player.getX() - 10 > this.getX() && player.getY() - 30 < this.getY()) { //quadrant1
            charles.setOffset(0, 320);
            this.moveX(1, width);
            this.moveY(-1, height);
        }
        if (player.getX() - 10 < this.getX() && player.getY() - 30 < this.getY()) { //quadrant2
            charles.setOffset(0, 480);
            this.moveX(-1, width);
            this.moveY(-1, height);
        }
        if (player.getX() - 10 < this.getX() && player.getY() - 30 > this.getY()) { //quadrant3
            charles.setOffset(0, 480);
            this.moveX(-1, width);
            this.moveY(1, height);
        }
        if (player.getX() - 10 > this.getX() && player.getY() - 30 > this.getY()) { //quadrant4
            charles.setOffset(0, 320);
            this.moveX(1, width);
            this.moveY(1, height);
        }
    }
}
