package Enemies;
import Game.Character;
import Game.SpriteAnimation;
import java.util.ArrayList;

//A.K.A Dom

import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

//May be made into pet
public class DomsPinky extends MeleeEnemy {
    
    SpriteAnimation dom;
    private final int count = 2;
    private final int columns = 2;
    private final int offsetX = 0;
    private final int offsetY = 0;
    private final Duration duration = Duration.millis(300);
    private final Animation animation;
    
    //collision rectangles
    Rectangle body;

    public DomsPinky(String img, int health, int coin, int width, int height) {
        super(img, health, coin, width, height);
        super.getChildren().remove(iv);
        dom = new SpriteAnimation(img, count, columns, offsetX, offsetY, width, height, duration);
        animation = dom;
        iv = dom.getIV();
        iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        getChildren().addAll(iv);
        
        collisionRects = new ArrayList();
        body = new Rectangle(this.getTranslateX() + 13, this.getTranslateY() + 20, 38, 37);
        body.setFill(Color.TRANSPARENT);
        collisionRects.add(body);
        hasCollisionRects = true;
    }
    
    public void hitView(Enemy enemy) {
        dom.setOffset(0, 0);
    }
    
    public void move(Character player, double width, double height) {
        animation.play();
        //there is a plus 20 for X since snake width is very small
	if (player.getX() > this.getX() && player.getY() - 20 == this.getY()) { //right
            dom.setOffset(0, 240);
            this.moveX(1, width);
        }
        if (player.getX() < this.getX() && player.getY() - 20 == this.getY()) { //left
            dom.setOffset(0, 162);
            this.moveX(-1, width);
        }
        if (player.getX() == this.getX() && player.getY() - 20 > this.getY()) { //down
            dom.setOffset(0, 0);
            this.moveY(1, height);
        }
        if (player.getX() == this.getX() && player.getY() - 20 < this.getY()) { //up
            dom.setOffset(0, 78);
            this.moveY(-1, height);
        }

        if (player.getX() > this.getX() && player.getY() - 20 < this.getY()) { //quadrant1
            dom.setOffset(0, 240);
            this.moveX(1, width);
            this.moveY(-1, height);
        }
        if (player.getX() < this.getX() && player.getY() - 20 < this.getY()) { //quadrant2
            dom.setOffset(0, 162);
            this.moveX(-1, width);
            this.moveY(-1, height);
        }
        if (player.getX() < this.getX() && player.getY() - 20 > this.getY()) { //quadrant3
            dom.setOffset(0, 162);
            this.moveX(-1, width);
            this.moveY(1, height);
        }
        if (player.getX() > this.getX() && player.getY() - 20 > this.getY()) { //quadrant4
            dom.setOffset(0, 240);
            this.moveX(1, width);
            this.moveY(1, height);
        }
        
        body.setX(this.getX() + 13);
        body.setY(this.getY() + 20);
    }
}
