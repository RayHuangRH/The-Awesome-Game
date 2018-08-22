package Game;
import Environment.Stairs;
import Friends.*;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Character extends Pane {

    ImageView iv;
    int offsetX = 0;
    int offsetY = 0;
    int width = 65;
    int height = 33;
    int x; //Character xPos
    int y; //Character yPos
    int playerSpeed;
    int shootSpeed;
    int bombSpeed = 1000;

    int health = 5;
    int shieldHealth = 0;
    final int fullHealth = 5;
    final int fullShieldHealth = 3;
    boolean alive = true;
    boolean shield = false;
    boolean stop;
    boolean bomb = false;
    
    public List<Rectangle> collisionRects;
    Rectangle leftSide, rightSide, head, body;

    public Character(int posX, int posY) {
	Image charImage = new Image("file:src/Sprites/Greenies.png");
	ImageView charIV = new ImageView(charImage);
	this.iv = charIV;
	this.iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
	this.setTranslateX(posX);
	this.setTranslateY(posY);
	this.x = posX;
	this.y = posY;
	this.getChildren().addAll(iv);
        this.playerSpeed = 3;
	this.shootSpeed = 500;
        
        collisionRects = new ArrayList();
        head = new Rectangle(this.getTranslateX() + 20, this.getTranslateY(), 26, 5);
        body = new Rectangle(this.getTranslateX() + 13, this.getTranslateY() + 5, 40, 27);
        leftSide = new Rectangle(this.getTranslateX() + 3, this.getTranslateY() + 18, 10, 14);
        rightSide = new Rectangle(this.getTranslateX() + 53, this.getTranslateY() + 18, 10, 14);
        head.setFill(Color.GREEN);
        body.setFill(Color.GREEN);
        leftSide.setFill(Color.GREEN);
        rightSide.setFill(Color.GREEN);
        collisionRects.add(head);
        collisionRects.add(body);
        collisionRects.add(leftSide);
        collisionRects.add(rightSide);
    }

    public void setCharacterView(int offsetX, int offsetY) {
	this.iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
    }

    public void moveX(int x, double screenWidth) {
	boolean right = x > 0;
	for (int i = 0; i < Math.abs(x); i++) {
	    if (right) {
		if (this.x > screenWidth - this.width) {
		    this.setTranslateX(screenWidth - this.width);
		} else {
		    this.setTranslateX(this.getTranslateX() + 1);
		    this.x++;
                    
                    head.setX(this.getTranslateX() + 20 + 1);
                    body.setX(this.getTranslateX() + 13 + 1);
                    leftSide.setX(this.getTranslateX() + 3 + 1);
                    rightSide.setX(this.getTranslateX() + 53 + 1);
		}
	    } else {
		if (this.x < 0) {
		    this.setTranslateX(0);
		} else {
		    this.setTranslateX(this.getTranslateX() - 1);
		    this.x--;
                    
                    head.setX(this.getTranslateX() + 20 - 1);
                    body.setX(this.getTranslateX() + 13 - 1);
                    leftSide.setX(this.getTranslateX() + 3 - 1);
                    rightSide.setX(this.getTranslateX() + 53 - 1);
		}
	    }
            
            boolean wall = isWall();
            if (right && wall) {
                this.setTranslateX(this.getTranslateX() - 1);
                this.x--;

                head.setX(this.getTranslateX() + 20 - 1);
                body.setX(this.getTranslateX() + 13 - 1);
                leftSide.setX(this.getTranslateX() + 3 - 1);
                rightSide.setX(this.getTranslateX() + 53 - 1);
                
            } else if (!right && wall) {
                this.setTranslateX(this.getTranslateX() + 1);
                this.x++;

                head.setX(this.getTranslateX() + 20 + 1);
                body.setX(this.getTranslateX() + 13 + 1);
                leftSide.setX(this.getTranslateX() + 3 + 1);
                rightSide.setX(this.getTranslateX() + 53 + 1);
            }
	}
    }

    public void moveY(int y, double screenHeight) {
	boolean down = y > 0;
	for (int i = 0; i < Math.abs(y); i++) {
	    if (down) {
		if (this.y > screenHeight - this.height) {
		    this.setTranslateY(screenHeight - this.height);
		} else {
		    this.setTranslateY(this.getTranslateY() + 1);
		    this.y++;
                    
                    head.setY(this.getTranslateY() + 1);
                    body.setY(this.getTranslateY() + 5 + 1);
                    leftSide.setY(this.getTranslateY() + 18 + 1);
                    rightSide.setY(this.getTranslateY() + 18 + 1);
		}
	    } else {
		if (this.y < 0) {
		    this.setTranslateY(0);
		} else {
		    this.setTranslateY(this.getTranslateY() - 1);
		    this.y--;
                    
                    head.setY(this.getTranslateY() - 1);
                    body.setY(this.getTranslateY() + 5 - 1);
                    leftSide.setY(this.getTranslateY() + 18 - 1);
                    rightSide.setY(this.getTranslateY() + 18 - 1);
		}
	    }
            
            boolean wall = isWall();
            if (down && wall) {
                this.setTranslateY(this.getTranslateY() - 1);
                this.y--;

                head.setY(this.getTranslateY() - 1);
                body.setY(this.getTranslateY() + 5 - 1);
                leftSide.setY(this.getTranslateY() + 18 - 1);
                rightSide.setY(this.getTranslateY() + 18 - 1);
                
            } else if (!down && wall) {
                this.setTranslateY(this.getTranslateY() + 1);
                this.y++;

                head.setY(this.getTranslateY() + 1);
                body.setY(this.getTranslateY() + 5 + 1);
                leftSide.setY(this.getTranslateY() + 18 + 1);
                rightSide.setY(this.getTranslateY() + 18 + 1);
            }
	}
    }

    public void setOffsetX(int offsetX) {
	this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
	this.offsetY = offsetY;
    }

    public int getOffsetX() {
	return offsetX;
    }

    public int getOffsetY() {
	return offsetY;
    }

    public void setX(int x) {
        this.setTranslateX(x);
	this.x = x;
    }

    public void setY(int y) {
        this.setTranslateY(y);
	this.y = y;
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public void hit(int dmg) {
        if (shield) {
            shieldHealth -= dmg;
        } else {
            health -= dmg;
        }
    }
    
    public void setHealth(int i) {
	health = i;
    }
    
    public int getHealth() {
	return health;
    }
    
    public int getFullHealth() {
        return fullHealth;
    }

    public boolean isAlive() {
	alive = health != 0;
	return alive;
    }
    
    public boolean isColliding(Stairs stair) {
        for (Rectangle rect : collisionRects) {
            if (stair.getBoundsInParent().intersects(rect.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isColliding(Friends friend) {
        for (Rectangle rect : collisionRects) {
            if (friend.getBoundsInParent().intersects(rect.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }
    
    public List<Rectangle> getCollisionRects() {
        return collisionRects;
    }
    
    public void setPlayerSpeed(int playerSpeed) {
        this.playerSpeed = playerSpeed;
    }
    
    public int getPlayerSpeed() {
        return playerSpeed;
    }
    
    public void setShootSpeed(int shootSpeed) {
	this.shootSpeed = shootSpeed;
    }
    
    public int getShootSpeed() {
	return shootSpeed;
    }
    
    public void addShield(boolean a) {
        shield = a;
        if (a) shieldHealth = fullShieldHealth;
        else shieldHealth = 0;
    }
    
    public boolean hasShield() {
        return shield;
    }
    
    public int getShieldHealth() {
        return shieldHealth;
    }
    
    public int getFullShieldHealth() {
        return fullShieldHealth;
    }
    
    public void setBomb(boolean a){
        bomb  = a;
    }
    
    public boolean getBomb(){
        return bomb;
    }
    
    public int getBombSpeed(){
        return bombSpeed;
    }
    
    public boolean isWall() {
        stop = false;
        for (Rectangle rect : Main.shopRootWalls) {
            for (Rectangle playerRect : collisionRects) {
                if (playerRect.getBoundsInParent().intersects(rect.getBoundsInParent()) 
                        && Main.level.isShopping()) {
                    stop = true;
                }
            }
        }
        return stop;
    }
}
