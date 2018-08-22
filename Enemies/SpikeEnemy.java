package Enemies;
import Game.Character;
import Projectiles.Projectile;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SpikeEnemy extends RangedEnemy {
    
    Rectangle body;

    public SpikeEnemy(String img, int health, int coin, int width, int height, int shootSpeed,
            String shotImg) {
        super(img, health, coin, width, height, shootSpeed, shotImg);
        
        collisionRects = new ArrayList();
        body = new Rectangle(this.getTranslateX() + 12, this.getTranslateY() + 11, 46, 40);
        body.setFill(Color.TRANSPARENT);
        collisionRects.add(body);
        hasCollisionRects = true;
    }
    
    public void move(Character player, double width, double height) {
	if (player.getX() > this.getX() && player.getY() - 20 == this.getY()) { //right
            this.setCharacterView(3, 194);
            this.moveX(1, width);
        }
        if (player.getX() < this.getX() && player.getY() - 20 == this.getY()) { //left
            this.setCharacterView(3, 65);
            this.moveX(-1, width);
        }
        if (player.getX() == this.getX() && player.getY() - 20 > this.getY()) { //down
            this.setCharacterView(3, 0);
            this.moveY(1, height);
        }
        if (player.getX() == this.getX() && player.getY() - 20 < this.getY()) { //up
            this.setCharacterView(3, 130);
            this.moveY(-1, height);
        }

        if (player.getX() > this.getX() && player.getY() - 20 < this.getY()) { //quadrant1
            this.setCharacterView(74, 130);
            this.moveX(1, width);
            this.moveY(-1, height);
        }
        if (player.getX() < this.getX() && player.getY() - 20 < this.getY()) { //quadrant2
            this.setCharacterView(74, 65);
            this.moveX(-1, width);
            this.moveY(-1, height);
        }
        if (player.getX() < this.getX() && player.getY() - 20 > this.getY()) { //quadrant3
            this.setCharacterView(74, 0);
            this.moveX(-1, width);
            this.moveY(1, height);
        }
        if (player.getX() > this.getX() && player.getY() - 20 > this.getY()) { //quadrant4
            this.setCharacterView(74, 194);
            this.moveX(1, width);
            this.moveY(1, height);
        }
        
        body.setX(this.getX() + 12);
        body.setY(this.getY() + 11);
    }
    
    public void shoot(Character player, List<Projectile> projectiles, Pane gameRoot) {
        long timeNow = System.currentTimeMillis();
        long time = timeNow - timeOfLastProjectile;

        if (time < 0 || time > this.getShootSpeed()) {
            Spikes spike = new Spikes(this.x + 23, this.y + 15, gameRoot, 1);
            gameRoot.getChildren().addAll(spike);
            spike.toBack();
            timeOfLastProjectile = timeNow;
        }
    }
    
    public void hitView(Enemy enemy) {
        this.setCharacterView(3, 0);
    }
}
