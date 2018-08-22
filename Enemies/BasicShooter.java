package Enemies;
import Game.Character;
import Game.SpriteAnimation;
import Projectiles.Projectile;
import java.util.ArrayList;

//A.K.A Cannon

import java.util.List;
import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BasicShooter extends RangedEnemy {

    SpriteAnimation cannon;
    private final int count = 4;
    private final int columns = 4;
    private final int offsetX = 0;
    private final int offsetY = 0;
    private final Duration duration = Duration.millis(shootSpeed);
    private final Animation animation;
    
    int angularDir = 270;
    Rectangle body;
    
    public BasicShooter(String img, int health, int coin, int width, int height, int shootSpeed, 
            String shotImg) {
        super(img, health, coin, width, height, shootSpeed, shotImg);
        super.getChildren().remove(iv);
        cannon = new SpriteAnimation(img, count, columns, offsetX, offsetY, width, height, duration);
        animation = cannon;
        iv = cannon.getIV();
        iv.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        getChildren().addAll(iv);
        
        collisionRects = new ArrayList();
        body = new Rectangle(this.getTranslateX() + 4, this.getTranslateY() + 14, 19, 29);
        body.setFill(Color.TRANSPARENT);
        collisionRects.add(body);
        hasCollisionRects = true;
    }

    public void move(Game.Character player, double width, double height) {
        animation.play();
        
	if (player.getX() + 18 > this.getX() && player.getY() - 15 == this.getY()) { //right
            this.setRotate(270);
            this.moveX(1, width);
            
            for (Rectangle rect : collisionRects) {
                rect.setRotate(270);
            }
            angularDir = 270;
        }
        if (player.getX() + 18 < this.getX() && player.getY() - 15 == this.getY()) { //left
            this.setRotate(90);
            this.moveX(-1, width);
            
            for (Rectangle rect : collisionRects) {
                rect.setRotate(90);
            }
            angularDir = 90;
        }
        if (player.getX() + 18 == this.getX() && player.getY() - 15 > this.getY()) { //down
            this.setRotate(360);
            this.moveY(1, height);
            for (Rectangle rect : collisionRects) {
                rect.setRotate(360);
            }
            angularDir = 360;
        }
        if (player.getX() + 18 == this.getX() && player.getY() - 15 < this.getY()) { //up
            this.setRotate(180);
            this.moveY(-1, height);
            for (Rectangle rect : collisionRects) {
                rect.setRotate(180);
            }
            angularDir = 180;
        }

        if (player.getX() + 18 > this.getX() && player.getY() - 15 < this.getY()) { //quadrant1
            this.moveX(1, width);
            this.moveY(-1, height);
        }
        if (player.getX() + 18 < this.getX() && player.getY() - 15 < this.getY()) { //quadrant2
            this.moveX(-1, width);
            this.moveY(-1, height);
        }
        if (player.getX() + 18 < this.getX() && player.getY() - 15 > this.getY()) { //quadrant3
            this.moveX(-1, width);
            this.moveY(1, height);
        }
        if (player.getX() + 18 > this.getX() && player.getY() - 15 > this.getY()) { //quadrant4
            this.moveX(1, width);
            this.moveY(1, height);
        }
        
        if (angularDir == 270) {
            body.setX(this.getX() + 7);
            body.setY(this.getY() + 11);
        } else if (angularDir == 360) {
            body.setX(this.getX() + 3);
            body.setY(this.getY() + 14);
        } else if (angularDir == 90) {
            body.setX(this.getX() + 4);
            body.setY(this.getY() + 10);
        } else {
            body.setX(this.getX() + 5);
            body.setY(this.getY() + 9);
        }
    }
    
    public void shoot(Character player, List<Projectile> projectiles, Pane gameRoot) {
        long timeNow = System.currentTimeMillis();
        long time = timeNow - timeOfLastProjectile;
        String dist = distance(player);

        if (dist.equals("up")) { //shoot up
            if (time < 0 || time > this.getShootSpeed()) {
                cannon.setOffset(0, 52);
                this.setRotate(180);
                angularDir = 180;
                createProjectile(0, -5, projectiles, gameRoot, shotIVFile, 20, 20, 1);
                timeOfLastProjectile = timeNow;
                
                for (Rectangle rect : collisionRects) {
                    rect.setRotate(180);
                }
            }

        } else if (dist.equals("down")) { //shoot down
            if (time < 0 || time > this.getShootSpeed()) {
                cannon.setOffset(0, 52);
                this.setRotate(360);
                angularDir = 360;
                createProjectile(0, 5, projectiles, gameRoot, shotIVFile, 20, 20, 1);
                timeOfLastProjectile = timeNow;
                
                for (Rectangle rect : collisionRects) {
                    rect.setRotate(360);
                }
            }

        } else if (dist.equals("left")) { //shoot left
            if (time < 0 || time > this.getShootSpeed()) {
                cannon.setOffset(0, 52);
                this.setRotate(90);
                angularDir = 90;
                createProjectile(-5, 0, projectiles, gameRoot, shotIVFile, 20, 20, 1);
                timeOfLastProjectile = timeNow;
                
                for (Rectangle rect : collisionRects) {
                    rect.setRotate(90);
                }
            }

        } else if (dist.equals("right")) { //shoot right
            if (time < 0 || time > this.getShootSpeed()) {
                cannon.setOffset(0, 52);
                this.setRotate(270);
                angularDir = 270;
                createProjectile(5, 0, projectiles, gameRoot, shotIVFile, 20, 20, 1);
                timeOfLastProjectile = timeNow;
                
                for (Rectangle rect : collisionRects) {
                    rect.setRotate(270);
                }
            }
        }
    }
    
    public void createProjectile(int x, int y, List<Projectile> projectiles, Pane root, 
                                 String img, int width, int height, int dmg) {
        Projectile proj;
        if (angularDir == 270) {
            proj = new Projectile(img, this.getX() + 22, this.getY() + 18, width, height, dmg);
        } else if (angularDir == 360) {
            proj = new Projectile(img, this.getX() + 4, this.getY() + 20, width, height, dmg);
        } else if (angularDir == 90) {
            proj = new Projectile(img, this.getX() - 12, this.getY() + 13, width, height, dmg);
        } else {
            proj = new Projectile(img, this.getX() + 4, this.getY() + 22, width, height, dmg);
        }
        
        proj.setVelocityX(x);
        proj.setVelocityY(y);
        root.getChildren().addAll(proj);
        proj.toBack();
        projectiles.add(proj);
    }
}
