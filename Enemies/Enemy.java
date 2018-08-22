package Enemies;
import Environment.Portal;
import Game.Character;
import Projectiles.Projectile;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Enemy extends Pane {
    
    //Create damage variable for player hit??
    public ImageView iv;
    int offsetX = 0;
    int offsetY = 0;
    int width;
    int height;
    public int x; //Enemy xPos
    public int y; //Enemy yPos
    int coin;
    int score;
    int enemySpeed;
    
    public Rectangle healthBarOutline;
    public Rectangle actualHealth;
    public Rectangle lostHealth;
    public boolean alive = true;
    public int health;
    public int totalHealth;
    public boolean overRun = false;
    
    public List<Rectangle> collisionRects;
    public boolean hasCollisionRects = false;
    
    //currently used only for bosses
    public Label nameLabel;

    public Enemy(String img, int health, int coin, int width, int height) {
	Image enemyImage = new Image(img);
	ImageView enemyIV = new ImageView(enemyImage);
        this.iv = enemyIV;
        this.iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
	this.health = health;
	this.totalHealth = health;
	this.coin = coin;
	this.score = coin;
	this.width = width;
	this.height = height;
        this.getChildren().addAll(iv);
        
	healthBarOutline = new Rectangle(x - 1, y - 6, width + 2, 4);
	healthBarOutline.setFill(Color.TRANSPARENT);
	healthBarOutline.setStroke(Color.BLACK);
	lostHealth = new Rectangle(x, y - 5, width, 3);
	lostHealth.setFill(Color.RED);
	actualHealth = new Rectangle(x, y - 5, width, 3);
	actualHealth.setFill(Color.GREEN);
	actualHealth.toFront();
        
        //so game does not crash
        collisionRects = new ArrayList();
    }
    
    public void setCharacterView(int offsetX, int offsetY) {
        this.iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
    }
    
    public void move(Character player, double width, double height) { //note width and height here are screen size
	//To be overridden by child classes
        //following code is used for when testing enemy in child class without its own design 
        if (player.getX() > this.getX() && player.getY() == this.getY()) { //right
            this.setCharacterView(0, 61);
            this.moveX(1, width);
        }
        if (player.getX() < this.getX() && player.getY() == this.getY()) { //left
            this.setCharacterView(0, 123);
            this.moveX(-1, width);
        }
        if (player.getX() == this.getX() && player.getY() > this.getY()) { //down
            this.setCharacterView(0, 0);
            this.moveY(1, height);
        }
        if (player.getX() == this.getX() && player.getY() < this.getY()) { //up
            this.setCharacterView(0, 183);
            this.moveY(-1, height);
        }

        if (player.getX() > this.getX() && player.getY() < this.getY()) { //quadrant1
            this.setCharacterView(0, 61);
            this.moveX(1, width);
            this.moveY(-1, height);
        }
        if (player.getX() < this.getX() && player.getY() < this.getY()) { //quadrant2
            this.setCharacterView(0, 123);
            this.moveX(-1, width);
            this.moveY(-1, height);
        }
        if (player.getX() < this.getX() && player.getY() > this.getY()) { //quadrant3
            this.setCharacterView(0, 123);
            this.moveX(-1, width);
            this.moveY(1, height);
        }
        if (player.getX() > this.getX() && player.getY() > this.getY()) { //quadrant4
            this.setCharacterView(0, 61);
            this.moveX(1, width);
            this.moveY(1, height);
        }
    }
    
    public void shoot(Character player, List<Projectile> list, Pane root) {
        //To be overridden by child classes
    }
    
    public void hitView(Enemy enemy) {
	//To be overridden by child classes
    }
    
    public void update(Pane root) {
        //To be overridden by child classes
    }
    
    public void moveX(int x, double screenWidth) { //x is horizontal speed
        boolean right = x > 0;
        for (int i = 0; i < Math.abs(x); i++) {
            if (right) {
                this.setTranslateX(this.getTranslateX() + 1);
                this.x++;
            }
            else  {
                this.setTranslateX(this.getTranslateX() - 1);
                this.x--;
            }
	    this.healthPos();
        }
    }
    
    public void moveY(int y, double screenHeight) { //y is vertical speed
        boolean down = y > 0;
        for (int i = 0; i < Math.abs(y); i++) {
            if (down) {
                this.setTranslateY(this.getTranslateY() + 1);
                this.y++;
            }
            else {
                this.setTranslateY(this.getTranslateY() - 1);
                this.y--;
            }
	    this.healthPos();
        }
    }

    public void setX(int x) {
        this.setTranslateX(x);
        this.x = x;
        this.healthPos();
    }

    public void setY(int y) {
        this.setTranslateY(y);
        this.y = y;
        this.healthPos();
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEnemySpeed() {
        return enemySpeed;
    }

    public void setEnemySpeed(int enemySpeed) {
        this.enemySpeed = enemySpeed;
    }
    
    public boolean isAlive() {
	return alive;
    }
    
    public void setAlive(boolean alive) {
	this.alive = alive;
    }
    
    public void hit(Projectile proj) {
	health-= proj.getDamage();
    }
    
    public int getHealth() {
	return health;
    }
    
    public Rectangle updateHealth() {
	actualHealth = new Rectangle(x, y, this.getHealth() * width / this.totalHealth, 3);
	actualHealth.setFill(Color.GREEN);
	return actualHealth;
    }
    
    public int getFullHealth() {
        return this.totalHealth;
    }
    
    public void healthPos() {
	actualHealth.setX(this.x);
	actualHealth.setY(this.y - 5);
	lostHealth.setX(this.x);
	lostHealth.setY(this.y - 5);
	healthBarOutline.setX(this.x - 1);
	healthBarOutline.setY(this.y - 6);
    }
    
    public boolean playerColliding(Character player) {
        if (!hasCollisionRects) {
            for (Rectangle playerRect : player.getCollisionRects()) {
                if (playerRect.getBoundsInParent().intersects(this.getBoundsInParent())) {
                    return true;
                }
            }
        } else {
            for (Rectangle playerRect : player.getCollisionRects()) {
                for (Rectangle enemyRect : this.collisionRects) {
                    if (playerRect.getBoundsInParent().intersects(enemyRect.getBoundsInParent())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean enemyColliding(List<Enemy> enemies) {
	boolean colliding = false;
	for (Enemy enemy : enemies) {
	    if (this.x == enemy.x && this.y == enemy.y) continue;
	    if (this.getBoundsInParent().intersects(enemy.getBoundsInParent())){
		colliding = true;
	    }
	}
	return colliding;
    }
    
    public List<Rectangle> getCollisionRects() {
        return collisionRects;
    }
    
    public void summon(Portal portal) {
	this.setTranslateX((portal.getX() + 26) - (this.width / 2));
        this.setTranslateY((portal.getY() + 55) - (this.height / 2));
        this.x = (portal.getX() + 26) - (this.width / 2);
        this.y = (portal.getY() + 55) - (this.height / 2);
    }
    
    public int getCoin() {
	return coin;
    }
    
    public int getScore() {
	return score;
    }
    
    public void setOffsetX(int offsetX) {
	this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
	this.offsetY = offsetY;
    }
    
    public Rectangle getHealthBarOutline() {
        return healthBarOutline;
    }
    
    public Rectangle getActualHealth() {
        return actualHealth;
    }
    
    public Rectangle getLostHealth() {
        return lostHealth;
    }

    public Label getNameLabel() {
        return nameLabel;
    }
    
    public boolean hasCollisionRects() {
        return hasCollisionRects;
    }

    public boolean doesOverRun() {
        return overRun;
    }
    
    public ImageView getIV() {
        return this.iv;
    }
    
    public void setIV(ImageView iv) {
        this.iv = iv;
        this.iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
    }
}
