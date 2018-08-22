package Environment;
import Bosses.*;
import Enemies.*;

import java.util.List;

public class Level {

    public static int currentLevel;
    public static int enemiesToBeat;
    public static int enemiesLeft;
    public static int enemiesSpawned;
    public static int coins;
    public static int highScore;
    static boolean shopping;

    public Level() {
        currentLevel = 1;
        enemiesToBeat = 3;
        enemiesLeft = 3;
        enemiesSpawned = 0;
        shopping = false;
    }

    public void increaseLevel() {
        currentLevel++;
        enemiesSpawned = 0;
        enemiesToBeat *= 2;
        enemiesLeft = enemiesToBeat;
    }

    public int getEnemiesToBeat() {
        return enemiesToBeat;
    }

    public void enemySpawned() {
        enemiesSpawned++;
    }

    public int getEnemiesSpawned() {
        return enemiesSpawned;
    }

    public void enemyBeat() {
        enemiesLeft--;
    }

    public int getEnemiesLeft() {
        return enemiesLeft;
    }

    public int getLevel() {
        return currentLevel;
    }

    public void coinUp(Enemy enemy) {
        coins += enemy.getCoin();
    }

    public void scoreUp(Enemy enemy) {
        highScore += enemy.getScore();
    }

    public int getCoin() {
        return coins;
    }

    public void spend(int i) {
        coins -= i;
    }

    public int getScore() {
        return highScore;
    }

    public void clearScore() {
        highScore = 0;
    }

    public void clearCoins() {
        coins = 0;
    }

    public void setShopping(boolean a) {
        shopping = a;
    }

    public boolean isShopping() {
        return shopping;
    }

    public void fillBoss(List<Enemy> bosses) {
        //testing phase - we will decide bosses order eventually
        //bosses.add(new TKTimmy("file:src/Sprites/TKTimmyBasic.png", 3, 5, 190, 190));
        //bosses.add(new HomingBoss("file:src/Sprites/Redies.png", 3, 20, 66, 33, 2000,
                //"file:src/Sprites/HomingShot.png"));
        bosses.add(new ChargeBoss("file:src/Sprites/ChargeBoss.png", 3, 20, 176, 102, 10));
        bosses.add(new SpiderBoss("file:src/Sprites/SpiderBoss.png", 5, 20, 158, 240));
        bosses.add(new RandShotBoss("file:src/Sprites/ShotBoss.png", 5, 20, 258, 256, 50,
                "file:src/Sprites/EnemyShot.png"));
    }

    public Enemy generate() {
        int randomNum = 10;
        while(randomNum > 8) {
            randomNum = (int) (Math.random() * this.getLevel() + 1);
        }
        Enemy enemy = null;
        
        switch (randomNum) { //for testing reasons numbers will vary
            case 5:
                enemy = new WillyWorm("file:src/Sprites/BlueCannon.png", 1, 1, 28, 51, 2000,
                        "file:src/Sprites/CannonShot.png");//ranged
                break;
            case 7 :
                enemy = new BasicShooter("file:src/Sprites/BlueCannon.png", 1, 1, 28, 51, 2000,
                        "file:src/Sprites/CannonShot.png"); //ranged
                break;
            case 3 : 
                enemy = new Licker("file:src/Sprites/CharlesSpriteSheet.png", 3, 1, 80, 80); //melee
                break;
            case 2 :
                enemy = new SpikeEnemy("file:src/Sprites/SpikeySpriteSheet.png", 3, 1, 71, 65, 
                        3000, "file:src/Sprites/Spikes.png"); //ranged
                break;
            case 8 :
                enemy = new FourWayShooter("file:src/Sprites/Redies.png", 1, 1, 66, 33, 3000,
                "file:src/Sprites/EnemyShot.png"); //ranged
                //need to make new design
                break;
            case 6 :
                enemy = new MrSnake("file:src/Sprites/SnakeSpriteSheet.png", 2, 1, 27, 35); //melee
                break;
            case 1 :
                enemy = new DomsPinky("file:src/Sprites/Pinky.png", 3, 1, 67, 78); //melee
                break;
            case 4 :
                enemy = new JuppsTheo("file:src/Sprites/JuppTheo.png", 3, 1, 30, 68); //melee
                break;
        }
        return enemy;
    }
}
