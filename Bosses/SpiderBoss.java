package Bosses;
import Enemies.Enemy;
import Game.Character;
import Game.SpriteAnimation;

import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SpiderBoss extends MeleeBoss {

    SpriteAnimation spidey;
    private final int count = 4;
    private final int columns = 4;
    private final int offsetX = 0;
    private final int offsetY = 0;
    private final Duration duration = Duration.millis(200);
    private final Animation animation;
    
    long timeUntilNextAttack;
    int currentPauseTime;
    int randomDirection = 0;
    boolean randLocationSet;
    boolean ready;
    
    //collision rectangles
    Rectangle head;
    Rectangle body;
    
    public SpiderBoss(String img, int health, int coin, int width, int height) {
        super(img, health, coin, width, height, "INSECTO BUG");
        super.getChildren().remove(iv);
        spidey = new SpriteAnimation(img, count, columns, offsetX, offsetY, width, height, duration);
        animation = spidey;
        iv = spidey.getIV();
        iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        getChildren().addAll(iv);
        
        currentPauseTime = 3000;
        randLocationSet = false;
        ready = false;
        overRun = true;
        
        collisionRects = new ArrayList();
        head = new Rectangle(this.getTranslateX() + 56, this.getTranslateY(), 52, 56);
        body = new Rectangle(this.getTranslateX() + 24, this.getTranslateY() + 56, 112, 160);
        head.setFill(Color.TRANSPARENT);
        body.setFill(Color.TRANSPARENT);
        collisionRects.add(head);
        collisionRects.add(body);
        hasCollisionRects = true;
    }

    public void move(Character player, double width, double height) {
        animation.play();
        
        if (!ready) {
            if (this.getY() <= -250) {
                ready = true;
            }
            this.moveY(-5, 240);
            head.setX(this.getX() + 53);
            head.setY(this.getY() + 8);
            body.setX(this.getX() + 23);
            body.setY(this.getY() + 52);
        }
        else {
            long timeNow = System.currentTimeMillis();
            long time = timeNow - timeUntilNextAttack;

            if (time < 0 || time > currentPauseTime) {
                randomDirection = (int) (Math.random() * 4 + 1);
                if (randomDirection == 1) { //going down
                    int randX = (int) (Math.random() * 1100 + 10);
                    this.setX(randX);
                    this.setY(-250);
                    this.setRotate(180);
                    
                    for (Rectangle rect : collisionRects) {
                        rect.setRotate(180);
                    }

                } else if (randomDirection == 2) { //going right
                    int randY = (int) (Math.random() * 545 + 10);
                    this.setX(-250);
                    this.setY(randY);
                    this.setRotate(90);
                    
                    for (Rectangle rect : collisionRects) {
                        rect.setRotate(90);
                    }

                } else if (randomDirection == 3) { //going up
                    int randX = (int) (Math.random() * 1100 + 10);
                    this.setX(randX);
                    this.setY(970);
                    this.setRotate(360);
                    
                    for (Rectangle rect : collisionRects) {
                        rect.setRotate(360);
                    }

                } else if (randomDirection == 4) { //going left
                    int randY = (int) (Math.random() * 545 + 10);
                    this.setX(1300);
                    this.setY(randY);
                    this.setRotate(270);
                    
                    for (Rectangle rect : collisionRects) {
                        rect.setRotate(270);
                    }
                }

                timeUntilNextAttack = timeNow;
            }
            
            switch(randomDirection) {
                case 1 : this.moveY(8, height); 
                    head.setX(this.getX() + 53);
                    head.setY(this.getY() + 175);
                    body.setX(this.getX() + 23);
                    body.setY(this.getY() + 28);
                    break;
                case 2 : this.moveX(12, width); 
                    head.setX(this.getX() + 136);
                    head.setY(this.getY() + 92);
                    body.setX(this.getX() + 10);
                    body.setY(this.getY() + 39);
                    break;
                case 3 : this.moveY(-8, height); 
                    head.setX(this.getX() + 53);
                    head.setY(this.getY() + 8);
                    body.setX(this.getX() + 23);
                    body.setY(this.getY() + 52);
                    break;
                case 4 : this.moveX(-12, width); 
                    head.setX(this.getX() - 34);
                    head.setY(this.getY() + 92);
                    body.setX(this.getX() + 36);
                    body.setY(this.getY() + 40);
                    break;
            }
        }
    }
    
    public void hitView(Enemy enemy) {
        animation.play();
    }
}
