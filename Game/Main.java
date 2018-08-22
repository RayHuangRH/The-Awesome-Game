package Game;

import Bosses.*;
import Enemies.*;
import Environment.*;
import Friends.*;
import Projectiles.*;
import Upgrades.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

public class Main extends Application {

    //initilizes various elements for scenes
    Scene scene;
    static Pane gameRoot, shopRoot, currentGameRoot, previousOptionsRoot;
    static BorderPane menuRoot, shopBuyingRoot, optionsRoot, gameOptionsRoot, gameOverRoot,
            controlOptionsRoot;
    static VBox areYouSureRoot, exitRoot;

    private final HashMap<KeyCode, Boolean> keys = new HashMap();
    static Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();

    Button yesExit = new Button("Yes");
    Button noExit = new Button("No");
    Button yesReturn = new Button("Yes");
    Button noReturn = new Button("No");

    static Character player;
    static Level level;
    Stairs toShopStair, decUpStair, toGameStair;
    Friends shopKeeper;

    //various lists for current projectiles, enemies and upgrades
    public static List<Projectile> projectiles = new ArrayList();
    private List<Projectile> projToRemove = new ArrayList();
    private long timeOfLastProjectile = 0;
    private long timeOfLastBomb = 0;

    private List<Projectile> enemyProj = new ArrayList();
    private List<Projectile> enemyProjToRemove = new ArrayList();

    private List<Enemy> enemies = new ArrayList();
    private List<Enemy> enemToRemove = new ArrayList();
    private long hitTime = 0, spikeHitTime = 0;

    private List<Enemy> bosses = new ArrayList();

    private List<Portal> portals = new ArrayList();
    private int portalCount = 0;

    public static List<Rectangle> shopRootWalls = new ArrayList();

    private List<Upgrades> shopUpgrades = new ArrayList();
    private List<Upgrades> upgradesToRemove = new ArrayList();
    private List<Upgrades> currentUpgrades = new ArrayList();
    private ListView<String> shopUpgradesView = new ListView();

    private TableView<ListViewObject> controlsView = new TableView();
    KeyCode moveUp, moveDown, moveRight, moveLeft, shootUp, shootDown, shootRight, shootLeft,
            interaction, dropBomb;

    //various elements for health score ect.
    static Rectangle healthBarOutline, actualHealth, lostHealth, shieldHealth;
    Label coinLabel, scoreLabel, shopBuyingHealthLabel, shopBuyingShieldLabel, shopBuyingCoinLabel,
            shopBuyingScoreLabel;
    VBox health, coinAndScore;

    boolean gameplay = false, pause = false, shieldAdded = false, couldGoToShop = true,
            couldGoToMap = false, addShopStair = true, inShopBuyingView = false, onOptions = true;
    private long pauseTime = 0;

    //Main
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        createRoots(primaryStage);

        //Gameplay
        scene.setOnKeyPressed(e -> keys.put(e.getCode(), true));
        scene.setOnKeyReleased(e -> keys.put(e.getCode(), false));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(primaryStage);
            }
        };
        timer.start();

        //Stage
        primaryStage.setTitle("The Awesome Game");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            pause = true;

            currentGameRoot = (Pane) primaryStage.getScene().getRoot();
            if (!currentGameRoot.equals(exitRoot)) {
                primaryStage.getScene().setRoot(exitRoot);

                yesExit.setOnAction(eY -> {
                    Platform.exit();
                    if (gameplay) {
                        clearAll();
                    }
                    gameplay = false;
                });
                noExit.setOnAction(eN -> {
                    primaryStage.getScene().setRoot(currentGameRoot);
                    pause = false;
                });
            }
        });
    }
    //Main

    //Gameplay
    public void update(Stage pStage) {
        long timeNow = System.currentTimeMillis();
        long time = timeNow - pauseTime;

        if (gameplay && !pause) {
            if (player.getHealth() == 0) {
                Text gameOver = new Text("Game Over \n Score:  " + level.getScore());
                gameOver.setFont(Font.font("Arial", 50));
                gameOverRoot.setTop(gameOver);
                BorderPane.setAlignment(gameOver, Pos.CENTER);
                BorderPane.setMargin(gameOver, new Insets(100));
                pStage.getScene().setRoot(gameOverRoot);
                gameplay = false;
            }

            //Controls
            if (isPressed(moveUp)) {
                player.setCharacterView(0, 183);
                player.moveY(-player.getPlayerSpeed(), scene.getHeight());
                player.setOffsetY(183);
                characterShooting();

            } else if (isPressed(moveDown)) {
                player.setCharacterView(0, 0);
                player.moveY(player.getPlayerSpeed(), scene.getHeight());
                player.setOffsetY(0);
                characterShooting();

            } else if (isPressed(moveLeft)) {
                player.setCharacterView(0, 123);
                player.moveX(-player.getPlayerSpeed(), scene.getWidth());
                player.setOffsetY(123);
                characterShooting();

            } else if (isPressed(moveRight)) {
                player.setCharacterView(0, 61);
                player.moveX(player.getPlayerSpeed(), scene.getWidth());
                player.setOffsetY(61);
                characterShooting();

            } else {
                player.setCharacterView(0, player.getOffsetY());
                characterShooting();
            }

            //Updates
            while (portalCount < level.getLevel()) {
                createPortal();
                player.toFront();
                portalCount++;
            }

            //determines when to spawn enemies
            for (Portal portal : portals) {
                if (level.getEnemiesSpawned() < level.getEnemiesToBeat() && portal.summon()
                        && !level.isShopping()) {
                    if (level.getEnemiesLeft() == 1 && bosses.size() >= level.getLevel()) {
                        createBoss(portal);
                    } else {
                        if (level.getEnemiesToBeat() - level.getEnemiesSpawned() != 1
                                || bosses.size() < level.getLevel()) {
                            createEnemy(portal);
                        }
                    }
                }
            }

            shoppingUpdate(pStage);
            shieldUpdate();

            if (time < 0 || time > 150) {
                if (isPressed(KeyCode.ESCAPE)) {
                    pause = true;
                    onOptions = true;
                    currentGameRoot = (Pane) pStage.getScene().getRoot();
                    pStage.getScene().setRoot(gameOptionsRoot);
                }
                pauseTime = timeNow;
            }

            for (Projectile proj : projectiles) {
                updateProj(proj);
            }
            for (Projectile proj : enemyProj) {
                updateEnemyProj(proj);
            }
            for (Enemy enemy : enemies) {
                updateEnemy(enemy);
            }
            for (Spikes spike : Spikes.spikes) {
                updateSpikes(spike);
            }
            for (Enemy enemy : bosses) {
                updateBoss(enemy);
            }

            projectiles.removeAll(projToRemove);
            projToRemove.clear();

            enemyProj.removeAll(enemyProjToRemove);
            enemyProjToRemove.clear();

            enemies.removeAll(enemToRemove);
            enemToRemove.clear();

            Spikes.spikes.removeAll(Spikes.spikeToRemove);
            Spikes.spikeToRemove.clear();

            shopUpgrades.removeAll(upgradesToRemove);
            upgradesToRemove.clear();

        } else if (pause) {
            if (time < 0 || time > 150) {
                if (isPressed(KeyCode.ESCAPE)) {
                    pStage.getScene().setRoot(currentGameRoot);
                    pause = false;
                    onOptions = false;
                }
                pauseTime = timeNow;
            }
        }

        if (onOptions) {
            updateControls();
        }
    }

    public void characterShooting() {
        long timeNow = System.currentTimeMillis();
        long time = timeNow - timeOfLastProjectile;
        long bombTime = timeNow - timeOfLastBomb;

        if (isPressed(shootUp)) {
            player.setCharacterView(128, 183);
            player.setOffsetY(183);
            if (time < 0 || time > player.getShootSpeed()) {
                createProjectile(0, -9);
                timeOfLastProjectile = timeNow;
            }

        } else if (isPressed(shootDown)) {
            player.setCharacterView(128, 0);
            player.setOffsetY(0);
            if (time < 0 || time > player.getShootSpeed()) {
                createProjectile(0, 9);
                timeOfLastProjectile = timeNow;
            }

        } else if (isPressed(shootLeft)) {
            player.setCharacterView(128, 123);
            player.setOffsetY(123);
            if (time < 0 || time > player.getShootSpeed()) {
                createProjectile(-9, 0);
                timeOfLastProjectile = timeNow;
            }

        } else if (isPressed(shootRight)) {
            player.setCharacterView(128, 61);
            player.setOffsetY(61);
            if (time < 0 || time > player.getShootSpeed()) {
                createProjectile(9, 0);
                timeOfLastProjectile = timeNow;
            }
        } else if (isPressed(dropBomb)) {
            if (player.getBomb()) {
                if (bombTime < 0 || bombTime > player.getBombSpeed()) {
                    createBomb();
                    timeOfLastBomb = timeNow;
                }
            }
        }
    }

    public void createPortal() {
        Portal portal = new Portal((int) scene.getWidth() - 36, (int) scene.getHeight() - 60);
        gameRoot.getChildren().add(portal);
        portal.toBack();
        portals.add(portal);
    }

    public void createProjectile(int x, int y) {
        Projectile proj = new Projectile("file:src/Sprites/Shot.png", player.getX() + 28,
                player.getY() + 16, 12, 12, 1);
        proj.setVelocityX(x);
        proj.setVelocityY(y);
        gameRoot.getChildren().addAll(proj);
        proj.toBack();
        projectiles.add(proj);
    }
    
    public void createBomb(){
        Projectile bomb = new Bomb("file:src/Sprites/Bomb.png",player.getX() + 28,
                player.getY() + 16, 16, 21, 1);
        bomb.setVelocityX(0);
        bomb.setVelocityY(0);
        gameRoot.getChildren().addAll(bomb);
        bomb.toBack();
        projectiles.add(bomb);
    }

    public void updateProj(Projectile proj) {
        proj.move(player);

        //removes projectile on enemy hit
        for (Enemy enemy : enemies) {
            if (proj.enemyColliding(enemy)) {
                enemy.hit(proj);
                gameRoot.getChildren().remove(enemy.getActualHealth());
                gameRoot.getChildren().add(enemy.updateHealth());
                proj.setAlive(false);
                //remove bomb from root after explosion regardless of collision
            }
        }

        //removes projectile on screen edge hit
        if (proj.getTranslateX() <= 0 || proj.getTranslateX() >= scene.getWidth()) {
            proj.setAlive(false);
        } else if (proj.getTranslateY() <= 0 || proj.getTranslateY() >= scene.getHeight()) {
            proj.setAlive(false);
        }

        //removes projectile if dead
        if (!proj.isAlive()) {
            gameRoot.getChildren().remove(proj);
            projToRemove.add(proj);
        }
    }

    public void updateEnemyProj(Projectile proj) {
        long timeNow = System.currentTimeMillis();
        long time = timeNow - hitTime;

        //removes enemy projectile in player collision
        if (proj.playerColliding(player)) { //create enemy proj class !note!
            proj.setAlive(false);
            if (time < 0 || time > 1000) {
                player.hit(proj.getDamage());
                playerReceiveHit();
                hitTime = timeNow;
            }
        }

        //translates projectile if not hitting player
        if (!proj.playerColliding(player)) {
            proj.move(player);
        }

        if (proj.getTranslateX() <= 0 || proj.getTranslateX() >= scene.getWidth()) {
            proj.setAlive(false);
        } else if (proj.getTranslateY() <= 0 || proj.getTranslateY() >= scene.getHeight()) {
            proj.setAlive(false);
        }

        if (!proj.isAlive()) {
            gameRoot.getChildren().remove(proj);
            enemyProjToRemove.add(proj);
        }
    }

    public void updateSpikes(Spikes spike) {
        long timeNow = System.currentTimeMillis();
        long time = timeNow - spikeHitTime;

        if (spike.playerShotColliding(projectiles)) {
            Spikes.spikeToRemove.add(spike);
            gameRoot.getChildren().removeAll(spike);
        }

        //removes spike if colliding player
        if (spike.playerColliding(player) && !level.isShopping()) {
            Spikes.spikeToRemove.add(spike);
            gameRoot.getChildren().removeAll(spike);
            if (time < 0 || time > 500) {
                player.hit(spike.getDamage());
                playerReceiveHit();
                spikeHitTime = timeNow;
            }
        }
    }

    public void createEnemy(Portal portal) {
        Enemy enemy = level.generate();
        enemy.summon(portal);
        gameRoot.getChildren().addAll(enemy, enemy.getHealthBarOutline(), enemy.getLostHealth(),
                enemy.getActualHealth());

        //use following code to add, see and fix enemy collision rects
        //comment following method if not being used but do not delete
        /*for (Rectangle rect : enemy.getCollisionRects()) {
            gameRoot.getChildren().addAll(rect);
        }*/
        
        coinAndScore.toFront();
        coinLabel.toFront();
        scoreLabel.toFront();
        enemies.add(enemy);
        health.toFront();
        healthBarOutline.toFront();
        lostHealth.toFront();
        actualHealth.toFront();
        level.enemySpawned();
        if (player.hasShield()) {
            shieldHealth.toFront();
        }
    }

    public void updateEnemy(Enemy enemy) {
        long timeNow = System.currentTimeMillis();
        long time = timeNow - hitTime;
        //changes characterview on player collision
        if (enemy.playerColliding(player)) {
            enemy.hitView(enemy);
            enemy.healthPos();
            if ((time < 0 || time > 1000)) {
                player.hit(1);//update this line if different damage values are implemented for different enemies
                playerReceiveHit();
                hitTime = timeNow;
            }
        }

        if (!enemy.playerColliding(player) || enemy.doesOverRun()) {
            enemy.move(player, scene.getWidth(), scene.getHeight());
        }

        enemy.shoot(player, enemyProj, gameRoot);
        enemy.update(gameRoot);

        if (enemy.getHealth() <= 0) {
            enemy.update(gameRoot);
            enemy.setAlive(false);
        }
        //clears enemy info if dead
        if (!enemy.isAlive()) {
            enemToRemove.add(enemy);
            gameRoot.getChildren().removeAll(enemy, enemy.getActualHealth(), enemy.getLostHealth(),
                    enemy.getHealthBarOutline());
            level.enemyBeat();
            level.coinUp(enemy);
            level.scoreUp(enemy);
            coinLabel.setText("Coins: " + level.getCoin());
            scoreLabel.setText("Score: " + level.getScore());
            for (Rectangle rect : enemy.getCollisionRects()) {
                gameRoot.getChildren().removeAll(rect);
            }
        }
    }

    public void createBoss(Portal portal) {
        //remember to give boss a name or they might be an error
        Enemy enemy = bosses.get(level.getLevel() - 1); //use level to determine index for boss spawn
        enemy.summon(portal); //determine portal to spawn boss from
        gameRoot.getChildren().addAll(enemy, enemy.getHealthBarOutline(), enemy.getLostHealth(),
                enemy.getActualHealth(), enemy.getNameLabel());

        //use following code to add, see and fix enemy collision rects
        //comment following method if not being used but do not delete
        /*for (Rectangle rect : enemy.getCollisionRects()) {
            gameRoot.getChildren().addAll(rect);
        }*/
        
        coinAndScore.toFront();
        coinLabel.toFront();
        scoreLabel.toFront();
        enemies.add(enemy);
        bosses.add(enemy);
        health.toFront();
        healthBarOutline.toFront();
        lostHealth.toFront();
        actualHealth.toFront();
        level.enemySpawned();
        if (player.hasShield()) {
            shieldHealth.toFront();
        }
    }

    public void updateBoss(Enemy boss) {
        //updateEnemy method also includes bosses
        if (!boss.isAlive()) {
            gameRoot.getChildren().removeAll(boss.getNameLabel());
        }
    }

    public void shieldUpdate() {
        //shield info if shield is brought
        if (player.hasShield()) {
            if (!shieldAdded) {
                shieldAdded = true;
                shieldHealth = new Rectangle(screenSize.getWidth() - 120, 10, 100, 22);
                shieldHealth.setFill(Color.web("#00E8FF"));
                shopRoot.getChildren().add(shieldHealth);
                shieldHealth.toFront();
            }
            if (player.getShieldHealth() == 0) {
                player.addShield(false);
                shieldAdded = false;
                gameRoot.getChildren().remove(shieldHealth);
            }
        }
    }

    public static void playerReceiveHit() {
        //determines which bar takes damage
        if (player.hasShield()) {
            gameRoot.getChildren().remove(shieldHealth);
            shieldHealth = new Rectangle(screenSize.getWidth() - 120, 10, player.getShieldHealth()
                    * 33 + 1, 22);
            shieldHealth.setFill(Color.web("#00E8FF"));
            gameRoot.getChildren().add(shieldHealth);
            shieldHealth.toFront();
        } else {
            gameRoot.getChildren().remove(actualHealth);
            actualHealth = new Rectangle(screenSize.getWidth() - 120, 10, player.getHealth() * 20, 22);
            actualHealth.setFill(Color.web("#00F32C"));
            gameRoot.getChildren().add(actualHealth);
            actualHealth.toFront();
        }
    }
    //Gameplay

    //Shop
    public void shoppingUpdate(Stage pStage) {
        //Shopping
        if (level.isShopping()) {
            //opens shoproot
            if (player.isColliding(shopKeeper) && isPressed(interaction)) {
                pStage.getScene().setRoot(shopBuyingRoot);
                updateShopBuyingRoot();
                inShopBuyingView = true;
            }

            //updates info if upgrade is brought
            for (Upgrades upgrade : shopUpgrades) {
                if (upgrade.getBought()) {
                    upgradesToRemove.add(upgrade);
                    currentUpgrades.add(upgrade);
                    level.spend(upgrade.getPrice());
                    coinLabel.setText("Coins: " + level.getCoin());
                }
            }

            //activates abilities on brought upgrades
            for (Upgrades upgrade : currentUpgrades) {
                if (!upgrade.isActive()) {
                    upgrade.activeAbility(player);
                    upgrade.setActive(true);
                    updateShopBuyingRoot();
                    shopRoot.getChildren().remove(actualHealth);
                    actualHealth = new Rectangle(screenSize.getWidth() - 120, 10, player.getHealth()
                            * 20, 22);
                    actualHealth.setFill(Color.web("#00F32C"));
                    shopRoot.getChildren().add(actualHealth);
                    actualHealth.toFront();
                    if (player.hasShield()) {
                        shieldHealth.toFront();
                    }
                }
            }

            //lets player return on game root
            if (player.isColliding(toGameStair) && couldGoToMap) {
                shopRoot.getChildren().clear();
                gameRoot.getChildren().addAll(player, health, healthBarOutline, lostHealth,
                        actualHealth, coinAndScore);
                player.setX((int) screenSize.getWidth() / 2);
                player.setY((int) screenSize.getHeight() / 2);

                //updates game root info based on changes while shopping
                if (player.hasShield()) {
                    gameRoot.getChildren().addAll(shieldHealth);
                }
                for (Portal port : portals) {
                    gameRoot.getChildren().addAll(port);
                }

                couldGoToShop = true;
                couldGoToMap = false;
                addShopStair = true;
                level.increaseLevel();
                level.setShopping(false);
                pStage.getScene().setRoot(gameRoot);
                currentGameRoot = gameRoot;
            }
        }

        //round end
        if (level.getEnemiesLeft() <= 0) {
            if (!level.isShopping() && addShopStair) {
                toShopStair = new Stairs("down", (int) scene.getWidth(), (int) scene.getHeight());
                gameRoot.getChildren().add(toShopStair);
                addShopStair = false;
            }

            if (player.isColliding(toShopStair)) {
                level.setShopping(true);
                pStage.getScene().setRoot(shopRoot);
                currentGameRoot = shopRoot;
                Spikes.spikes.clear();
                enemyProj.clear();

                if (couldGoToShop) {
                    gameRoot.getChildren().clear();
                    shopRoot.getChildren().addAll(player, health, healthBarOutline, lostHealth,
                            actualHealth, coinAndScore, decUpStair, toGameStair, shopKeeper);
                    if (player.hasShield()) {
                        shopRoot.getChildren().addAll(shieldHealth);
                    }
                    couldGoToShop = false;
                    couldGoToMap = true;
                }
                gameRoot.getChildren().remove(toShopStair);
            }
        }
    }

    public void addShopRootWalls() {
        //add transparent rectangles in areas player can not visit
        addWall(68, 116, Color.TRANSPARENT, 413, 32, shopRootWalls, shopRoot);
        addWall(58, 246, Color.TRANSPARENT, 98, 262, shopRootWalls, shopRoot);
        addWall(168, 50, Color.TRANSPARENT, 160, 262, shopRootWalls, shopRoot);
        addWall(68, 64, Color.TRANSPARENT, 260, 312, shopRootWalls, shopRoot);
        addWall(20, 30, Color.TRANSPARENT, 1166, 0, shopRootWalls, shopRoot);
        addWall(12, 38, Color.TRANSPARENT, 1182, 30, shopRootWalls, shopRoot);
        addWall(20, 35, Color.TRANSPARENT, 1202, 65, shopRootWalls, shopRoot);
        addWall(20, 30, Color.TRANSPARENT, 1220, 95, shopRootWalls, shopRoot);
        addWall(10, 20, Color.TRANSPARENT, 1240, 125, shopRootWalls, shopRoot);
        addWall(20, 20, Color.TRANSPARENT, 1260, 145, shopRootWalls, shopRoot);
    }

    //upgrade variables
    HealthPackUpgrade healthUp;
    PlayerShieldUpgrade shieldUp;
    ShootSpeedUpgrade shotUp;
    PlayerSpeedUpgrade speedUp;
    BombUpgrade bombUp;

    public void addShopButtons() {
        healthUp = new HealthPackUpgrade();
        shieldUp = new PlayerShieldUpgrade();
        shotUp = new ShootSpeedUpgrade();
        speedUp = new PlayerSpeedUpgrade();
        bombUp = new BombUpgrade();
        shopUpgrades.add(healthUp);
        shopUpgrades.add(shieldUp);
        shopUpgrades.add(shotUp);
        shopUpgrades.add(speedUp);
        shopUpgrades.add(bombUp);

        for (Upgrades upgrade : shopUpgrades) {
            shopUpgradesView.getItems().addAll(upgrade.getListView());
        }

        //adds icons in front of text
        shopUpgradesView.setCellFactory(e -> new ListCell<String>() {
            private ImageView iv = new ImageView();

            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (name.equals("Health Pack   -   " + healthUp.getPrice())) {
                        iv.setImage(healthUp.getImage());
                    } else if (name.equals("Add Shield   -   " + shieldUp.getPrice())) {
                        iv.setImage(shieldUp.getImage());
                    } else if (name.equals("Player Speed   -   " + speedUp.getPrice())) {
                        iv.setImage(speedUp.getImage());
                    } else if (name.equals("Shooting Speed   -   " + shotUp.getPrice())) {
                        iv.setImage(shotUp.getImage());
                    } else if (name.equals("Bombs   -   " + bombUp.getPrice())) {
                        iv.setImage(bombUp.getImage());
                    }
                    setText(name);
                    setGraphic(iv);
                }
            }
        });

        shopUpgradesView.setId("shopUpView");
        shopBuyingRoot.setCenter(shopUpgradesView);
        BorderPane.setAlignment(shopUpgradesView, Pos.TOP_CENTER);
        BorderPane.setMargin(shopUpgradesView, new Insets(10));
    }

    public void updateShopBuyingRoot() {
        //info for upgrades in shop
        shopBuyingHealthLabel.setText("Health: " + (player.getHealth() * 20) + "%");
        shopBuyingShieldLabel.setText("Shield: " + (player.getShieldHealth() * 33) + "%");
        shopBuyingCoinLabel.setText("Coins: " + level.getCoin());
        shopBuyingScoreLabel.setText("Score: " + level.getScore());
    }

    public void upgradeSelected(String upgrade) {
        String[] upgradeNameSplit = upgrade.split("-");
        String upgradeName = upgradeNameSplit[0];

        //must keep number of spaces correct (currently 3)
        if (upgradeName.equals("Health Pack   ")) {
            if (level.getCoin() >= healthUp.getPrice()) {
                if (player.getHealth() != player.getFullHealth()) {
                    healthUp.setBought(true);
                    removeUpgrade(healthUp);
                } else {
                    //Need to add some kind of pop up message
                }
            }
        } else if (upgradeName.equals("Add Shield   ")) {
            if (level.getCoin() >= shieldUp.getPrice()) {
                if (!player.hasShield() || player.getShieldHealth() < player.getFullShieldHealth()) {
                    shieldUp.setBought(true);
                    removeUpgrade(shieldUp);
                } else {
                    //Need to add some kind of pop up message
                }
            }
        } else if (upgradeName.equals("Player Speed   ")) {
            if (level.getCoin() >= speedUp.getPrice()) {
                speedUp.setBought(true);
                removeUpgrade(speedUp);
            }
        } else if (upgradeName.equals("Shooting Speed   ")) {
            if (level.getCoin() >= shotUp.getPrice()) {
                shotUp.setBought(true);
                removeUpgrade(shotUp);
            }
        } else if (upgradeName.equals("Bombs   ")) {
            if (level.getCoin() >= bombUp.getPrice()) {
                bombUp.setBought(true);
                removeUpgrade(bombUp);
            }
        }
    }

    public void removeUpgrade(Upgrades upgrade) {
        shopUpgradesView.getItems().remove(upgrade.getListView());
    }

    public VBox getPlayerData() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        shopBuyingHealthLabel = new Label();
        shopBuyingHealthLabel.setText("Health: ");
        shopBuyingHealthLabel.setFont(new Font("Arial", 30));
        shopBuyingHealthLabel.setTextFill(Color.BLACK);
        shopBuyingShieldLabel = new Label();
        shopBuyingShieldLabel.setText("Shield: ");
        shopBuyingShieldLabel.setFont(new Font("Arial", 30));
        shopBuyingShieldLabel.setTextFill(Color.BLACK);
        shopBuyingCoinLabel = new Label();
        shopBuyingCoinLabel.setText("Coins: ");
        shopBuyingCoinLabel.setFont(new Font("Arial", 30));
        shopBuyingCoinLabel.setTextFill(Color.BLACK);
        shopBuyingScoreLabel = new Label();
        shopBuyingScoreLabel.setText("Score: ");
        shopBuyingScoreLabel.setFont(new Font("Arial", 30));
        shopBuyingScoreLabel.setTextFill(Color.BLACK);

        vbox.getChildren().addAll(shopBuyingHealthLabel, shopBuyingShieldLabel, shopBuyingCoinLabel,
                shopBuyingScoreLabel);
        return vbox;
    }

    public HBox addShopViewButtons(Stage pStage) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(100);

        Button buyButton = new Button("BUY");
        Button exitButton = new Button("EXIT");

        buyButton.setOnAction(e -> {
            ObservableList<String> upgrade = shopUpgradesView.getSelectionModel().getSelectedItems();
            for (String up : upgrade) {
                upgradeSelected(up);
            }
        });
        exitButton.setOnAction(e -> {
            pStage.getScene().setRoot(shopRoot);
            inShopBuyingView = false;
        });
        hbox.getChildren().addAll(buyButton, exitButton);
        return hbox;
    }
    //Shop

    //General
    public void newGame() {
        level = new Level();
        player = new Character((int) screenSize.getWidth() / 2, (int) screenSize.getHeight() / 2);
        actualHealth = new Rectangle(screenSize.getWidth() - 120, 10, 100, 22);
        actualHealth.setFill(Color.web("#00F32C"));
        gameRoot.getChildren().addAll(player, health, healthBarOutline, lostHealth,
                actualHealth, coinAndScore);
        addShopButtons();
        coinAndScore.toFront();
        coinLabel.toFront();
        scoreLabel.toFront();
        health.toFront();
        healthBarOutline.toFront();
        lostHealth.toFront();
        actualHealth.toFront();
        player.addShield(false);
        gameplay = true;
        pause = false;
        shieldAdded = false;
        couldGoToShop = true;
        couldGoToMap = false;
        addShopStair = true;
        onOptions = false;
        level.fillBoss(bosses);
        currentGameRoot = gameRoot;
    }

    public void clearAll() {
        projectiles.clear();
        projToRemove.clear();
        enemies.clear();
        enemToRemove.clear();
        enemyProj.clear();
        enemyProjToRemove.clear();
        Spikes.spikes.clear();
        Spikes.spikeToRemove.clear();
        bosses.clear();
        portals.clear();
        portalCount = 0;
        currentUpgrades.clear();
        upgradesToRemove.clear();
        shopUpgrades.clear();
        shopUpgradesView.getItems().clear();
        level.clearScore();
        level.clearCoins();
        level.setShopping(false);
        coinLabel.setText("Coins: ");
        scoreLabel.setText("Score: ");
        gameRoot.getChildren().clear();
        shopRoot.getChildren().clear();
    }

    public void addWall(int width, int height, Color color, int offsetX, int offsetY,
            List<Rectangle> walls, Pane root) {
        Rectangle rect = new Rectangle(width, height, color);
        rect.setX(offsetX);
        rect.setY(offsetY);

        walls.add(rect);
        root.getChildren().addAll(rect);
    }

    public boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    public void updateTableViewHeader(TableView table) {
        //used to remove tableview header
        table.widthProperty().addListener((ObservableValue<? extends Number> source,
                Number oldWidth, Number newWidth) -> {
            Pane header = (Pane) table.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });
    }
    //General

    //Controls
    public void resetControls() {
        moveUp = KeyCode.W;
        moveDown = KeyCode.S;
        moveLeft = KeyCode.A;
        moveRight = KeyCode.D;
        shootUp = KeyCode.UP;
        shootDown = KeyCode.DOWN;
        shootLeft = KeyCode.LEFT;
        shootRight = KeyCode.RIGHT;
        interaction = KeyCode.E;
        dropBomb = KeyCode.SHIFT;

        controlsView.getItems().clear();
        controlsView.setItems(getControlList());
        updateTableViewHeader(controlsView);
    }

    public void updateControls() {
        controlsView.setOnKeyPressed(e -> {
            e.consume();
            int index = controlsView.getSelectionModel().getSelectedIndex();
            KeyCode newKey = e.getCode();

            switch (index) {
                case 0:
                    moveUp = newKey;
                    break;
                case 1:
                    moveDown = newKey;
                    break;
                case 2:
                    moveRight = newKey;
                    break;
                case 3:
                    moveLeft = newKey;
                    break;
                case 4:
                    break;
                case 5:
                    shootUp = newKey;
                    break;
                case 6:
                    shootDown = newKey;
                    break;
                case 7:
                    shootRight = newKey;
                    break;
                case 8:
                    shootLeft = newKey;
                    break;
                case 9:
                    break;
                case 10:
                    if (newKey != KeyCode.SPACE) {
                        interaction = newKey;
                    }
                    break;
                case 11:
                    dropBomb = newKey;
                    break;
            }

            controlsView.getItems().clear();
            controlsView.setItems(getControlList());
            updateTableViewHeader(controlsView);
        });
    }

    public ObservableList<ListViewObject> getControlList() {
        ObservableList<ListViewObject> controlList = FXCollections.observableArrayList();
        controlList.addAll(
                new ListViewObject("MOVE UP", "   -   ", moveUp.toString()),
                new ListViewObject("MOVE DOWN", "   -   ", moveDown.toString()),
                new ListViewObject("MOVE RIGHT", "   -   ", moveRight.toString()),
                new ListViewObject("MOVE LEFT", "   -   ", moveLeft.toString()),
                new ListViewObject("", "", ""),
                new ListViewObject("SHOOT UP", "   -   ", shootUp.toString()),
                new ListViewObject("SHOOT DOWN", "   -   ", shootDown.toString()),
                new ListViewObject("SHOOT RIGHT", "   -   ", shootRight.toString()),
                new ListViewObject("SHOOT LEFT", "   -   ", shootLeft.toString()),
                new ListViewObject("", "", ""),
                new ListViewObject("INTERACTION", "   -   ", interaction.toString()),
                new ListViewObject("BOMB", "   -   ", dropBomb.toString()));

        return controlList;
    }

    public void createControlTable() {
        TableColumn<ListViewObject, String> column1 = new TableColumn<>("");
        column1.setMinWidth((int) ((1280 - 600) / 3));
        column1.setCellValueFactory(new PropertyValueFactory<>("t1"));
        TableColumn<ListViewObject, String> column2 = new TableColumn<>("");
        column2.setMinWidth((int) ((1280 - 600) / 3));
        column2.setCellValueFactory(new PropertyValueFactory<>("t2"));
        TableColumn<ListViewObject, String> column3 = new TableColumn<>("");
        column3.setMinWidth((int) ((1280 - 600) / 3));
        column3.setCellValueFactory(new PropertyValueFactory<>("t3"));
        controlsView.getColumns().addAll(column1, column2, column3);
    }
    //Controls

    //Layouts
    public void createRoots(Stage pStage) {
        //Menu Root
        Text title = new Text("THE AWESOME GAME");
        title.setFont(Font.font("Arial", 50));
        VBox vbox = addMenuButtons(pStage);
        vbox.setAlignment(Pos.TOP_CENTER);
        menuRoot = new BorderPane();
        menuRoot.setId("menu");
        menuRoot.setCenter(vbox);
        menuRoot.setTop(title);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        BorderPane.setMargin(title, new Insets(100));

        scene = new Scene(menuRoot, screenSize.getWidth(), screenSize.getHeight());
        scene.getStylesheets().addAll(this.getClass().getResource("Design.css").toExternalForm());

        //Game Root
        gameRoot = new Pane();
        gameRoot.setId("backgroundgame");
        Label healthLabel = new Label("Health: ");
        healthLabel.setFont(new Font("Arial", 20));
        healthLabel.setTextFill(Color.WHITE);
        healthLabel.toFront();
        healthBarOutline = new Rectangle(screenSize.getWidth() - 121, 9, 102, 22);
        healthBarOutline.setFill(Color.TRANSPARENT);
        healthBarOutline.setStroke(Color.BLACK);
        lostHealth = new Rectangle(screenSize.getWidth() - 120, 10, 100, 22);
        lostHealth.setFill(Color.RED);
        actualHealth = new Rectangle(screenSize.getWidth() - 120, 10, 100, 22);
        actualHealth.setFill(Color.web("#00F32C"));
        shieldHealth = new Rectangle(screenSize.getWidth() - 120, 10, 100, 22);
        shieldHealth.setFill(Color.web("#00E8FF"));
        health = new VBox(10);
        health.getChildren().addAll(healthLabel);
        health.setTranslateX(screenSize.getWidth() - 200);
        health.setTranslateY(10);
        coinLabel = new Label("Coins: ");
        coinLabel.setFont(new Font("Arial", 20));
        coinLabel.setTextFill(Color.WHITE);
        scoreLabel = new Label("Score: ");
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setTextFill(Color.WHITE);
        coinAndScore = new VBox(10);
        coinAndScore.getChildren().addAll(coinLabel, scoreLabel);
        coinAndScore.setTranslateX(10);
        coinAndScore.setTranslateY(10);

        //Shop Root
        shopRoot = new Pane();
        shopRoot.setId("backgroundshop");
        decUpStair = new Stairs("up", (int) screenSize.getWidth(), (int) screenSize.getHeight());
        toGameStair = new Stairs("shop", (int) screenSize.getWidth() - 100, (int) screenSize.getHeight() - 100);
        shopKeeper = new ShopKeeper("file:src/Sprites/ShopKeeper.png", 65, 40);
        addShopRootWalls();

        //Shop Buying Root
        Text shopTitle = new Text("SHOP");
        shopTitle.setFont(Font.font("Arial", 50));
        HBox shopButtons = addShopViewButtons(pStage);
        Text itemSummary = new Text("SUMMARY");
        itemSummary.setFont(Font.font("Arial", 30));
        VBox playerData = getPlayerData();
        shopBuyingRoot = new BorderPane();
        shopBuyingRoot.setId("menu");
        shopBuyingRoot.setTop(shopTitle);
        shopBuyingRoot.setBottom(shopButtons);
        shopBuyingRoot.setRight(itemSummary);
        shopBuyingRoot.setLeft(playerData);
        playerData.setPrefWidth((int) screenSize.getWidth() / 5);
        BorderPane.setAlignment(shopTitle, Pos.CENTER);
        BorderPane.setMargin(shopTitle, new Insets(50));
        BorderPane.setAlignment(shopButtons, Pos.TOP_CENTER);
        BorderPane.setMargin(shopButtons, new Insets(20, 0, 20, 0));
        BorderPane.setAlignment(itemSummary, Pos.TOP_LEFT);
        BorderPane.setMargin(itemSummary, new Insets(10, 200, 10, 10));
        BorderPane.setAlignment(playerData, Pos.TOP_LEFT);
        BorderPane.setMargin(playerData, new Insets(10, 10, 10, 20));

        //Options Root
        Text opTitle = new Text("GAME OPTIONS");
        opTitle.setFont(Font.font("Arial", 50));
        VBox optionsBox = addOptionButtons(pStage);
        optionsBox.setAlignment(Pos.TOP_CENTER);
        optionsRoot = new BorderPane();
        optionsRoot.setId("menu");
        optionsRoot.setCenter(optionsBox);
        optionsRoot.setTop(opTitle);
        BorderPane.setAlignment(opTitle, Pos.CENTER);
        BorderPane.setMargin(opTitle, new Insets(100));

        //Game Options Root
        Text gameOpTitle = new Text("GAMEOPTIONS");
        gameOpTitle.setFont(Font.font("Arial", 50));
        VBox gameOptionsBox = addGameOptionsButtons(pStage);
        gameOptionsBox.setAlignment(Pos.TOP_CENTER);
        gameOptionsRoot = new BorderPane();
        gameOptionsRoot.setId("menu");
        gameOptionsRoot.setCenter(gameOptionsBox);
        gameOptionsRoot.setTop(gameOpTitle);
        BorderPane.setAlignment(gameOpTitle, Pos.CENTER);
        BorderPane.setMargin(gameOpTitle, new Insets(100));

        //Control Options Root
        controlOptionsRoot = new BorderPane();
        Text controlTitle = new Text("CONTROLS");
        controlTitle.setFont(Font.font("Arial", 50));
        HBox controlButtons = addControlButtons(pStage);
        controlOptionsRoot.setId("menu");
        controlOptionsRoot.setTop(controlTitle);
        controlOptionsRoot.setBottom(controlButtons);
        BorderPane.setAlignment(controlTitle, Pos.CENTER);
        BorderPane.setMargin(controlTitle, new Insets(50));
        BorderPane.setAlignment(controlButtons, Pos.CENTER);
        BorderPane.setMargin(controlButtons, new Insets(50));
        createControlTable();

        //Game Over Root
        VBox gameOverBox = addGameOverButtons(pStage);
        gameOverBox.setAlignment(Pos.TOP_CENTER);
        gameOverRoot = new BorderPane();
        gameOverRoot.setId("menu");
        gameOverRoot.setCenter(gameOverBox);

        //Exit Root
        exitRoot = new VBox(20);
        Label exitString = new Label("Are you sure you want to exit?");
        exitString.setFont(Font.font("Arial", 25));
        HBox exitButtons = new HBox(10);
        exitButtons.getChildren().addAll(yesExit, noExit);
        exitButtons.setAlignment(Pos.CENTER);
        exitRoot.getChildren().addAll(exitString, exitButtons);
        exitRoot.setId("menu");
        exitRoot.setAlignment(Pos.CENTER);

        //Are You Sure Root
        areYouSureRoot = new VBox(20);
        Label areYouSureString = new Label("Are you sure you want to return to the menu?");
        areYouSureString.setFont(Font.font("Arial", 25));
        HBox returnButtons = new HBox(10);
        returnButtons.getChildren().addAll(yesReturn, noReturn);
        returnButtons.setAlignment(Pos.CENTER);
        areYouSureRoot.getChildren().addAll(areYouSureString, returnButtons);
        areYouSureRoot.setId("menu");
        areYouSureRoot.setAlignment(Pos.CENTER);
    }

    public VBox addMenuButtons(Stage pStage) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(10);

        Button startBtn = new Button("START");
        startBtn.setOnAction(e -> {
            pStage.getScene().setRoot(gameRoot);
            newGame();
        });

        Button optionsBtn = new Button("OPTIONS");
        optionsBtn.setOnAction(e -> {
            pStage.getScene().setRoot(optionsRoot);
            previousOptionsRoot = menuRoot;
        });

        Button exitBtn = new Button("EXIT");
        exitBtn.setOnAction(e -> {
            pStage.getScene().setRoot(exitRoot);

            yesExit.setOnAction(eY -> {
                Platform.exit();
            });
            noExit.setOnAction(eN -> {
                pStage.getScene().setRoot(menuRoot);
            });
        });

        vbox.getChildren().addAll(startBtn, optionsBtn, exitBtn);
        return vbox;
    }

    public VBox addOptionButtons(Stage pStage) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(10);

        CheckBox musicBox = new CheckBox("MUSIC");
        musicBox.setSelected(false);
        musicBox.setOnAction(e -> {

        });

        Button controlBtn = new Button("CONTROLS");
        controlBtn.setOnAction(e -> {
            pStage.getScene().setRoot(controlOptionsRoot);
            previousOptionsRoot = optionsRoot;
        });

        Button backBtn = new Button("BACK TO MENU");
        backBtn.setOnAction(e -> {
            pStage.getScene().setRoot(menuRoot);
        });

        vbox.getChildren().addAll(musicBox, controlBtn, backBtn);
        return vbox;
    }

    public VBox addGameOptionsButtons(Stage pStage) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(10);

        CheckBox musicBox = new CheckBox("MUSIC");
        musicBox.setSelected(false);
        musicBox.setOnAction(e -> {

        });

        Button controlBtn = new Button("CONTROLS");
        controlBtn.setOnAction(e -> {
            pStage.getScene().setRoot(controlOptionsRoot);
            previousOptionsRoot = gameOptionsRoot;
        });

        Button gameBtn = new Button("BACK TO GAME");
        gameBtn.setOnAction(e -> {
            if (!level.isShopping()) {
                pStage.getScene().setRoot(gameRoot);
            } else {
                if (inShopBuyingView) {
                    pStage.getScene().setRoot(shopBuyingRoot);
                } else {
                    pStage.getScene().setRoot(shopRoot);
                }
            }
            pause = false;
        });

        Button backBtn = new Button("BACK TO MENU");
        backBtn.setOnAction(e -> {
            pStage.getScene().setRoot(areYouSureRoot);

            yesReturn.setOnAction(eY -> {
                pStage.getScene().setRoot(menuRoot);
                clearAll();
                gameplay = false;
                pause = false;
            });
            noReturn.setOnAction(eN -> {
                pStage.getScene().setRoot(gameOptionsRoot);
            });
        });

        Button exitBtn = new Button("QUIT");
        exitBtn.setOnAction(e -> {
            pStage.getScene().setRoot(exitRoot);

            yesExit.setOnAction(eY -> {
                Platform.exit();
                gameplay = false;
                clearAll();
            });
            noExit.setOnAction(eN -> {
                pStage.getScene().setRoot(gameOptionsRoot);
            });
        });

        vbox.getChildren().addAll(musicBox, gameBtn, backBtn, controlBtn, exitBtn);
        return vbox;
    }

    public HBox addControlButtons(Stage pStage) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(100);

        //Keep the code below until game save is implemented
        moveUp = KeyCode.W;
        moveDown = KeyCode.S;
        moveLeft = KeyCode.A;
        moveRight = KeyCode.D;
        shootUp = KeyCode.UP;
        shootDown = KeyCode.DOWN;
        shootLeft = KeyCode.LEFT;
        shootRight = KeyCode.RIGHT;
        interaction = KeyCode.E;
        dropBomb = KeyCode.SHIFT;
        //

        controlsView.setItems(getControlList());
        controlsView.setId("controls");
        updateTableViewHeader(controlsView);

        controlOptionsRoot.setCenter(controlsView);
        BorderPane.setAlignment(controlsView, Pos.TOP_CENTER);
        BorderPane.setMargin(controlsView, new Insets(10, 300, 10, 300));

        Button resetBtn = new Button("RESET");
        resetBtn.setOnAction(e -> {
            resetControls();
        });

        Button backBtn = new Button("BACK");
        backBtn.setOnAction(e -> {
            pStage.getScene().setRoot(previousOptionsRoot);
        });

        hbox.getChildren().addAll(resetBtn, backBtn);
        return hbox;
    }

    public VBox addGameOverButtons(Stage pStage) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(10);

        Button newBtn = new Button("NEW GAME");
        newBtn.setOnAction(e -> {
            pStage.getScene().setRoot(gameRoot);
            clearAll();
            newGame();
        });

        Button backBtn = new Button("BACK TO MENU");
        backBtn.setOnAction(e -> {
            pStage.getScene().setRoot(areYouSureRoot);

            yesReturn.setOnAction(eY -> {
                pStage.getScene().setRoot(menuRoot);
                clearAll();
                gameplay = false;
                pause = false;
            });
            noReturn.setOnAction(eN -> {
                pStage.getScene().setRoot(gameOverRoot);
            });
        });

        Button exitBtn = new Button("QUIT");
        exitBtn.setOnAction(e -> {
            pStage.getScene().setRoot(exitRoot);

            yesExit.setOnAction(eY -> {
                Platform.exit();
                gameplay = false;
                clearAll();
            });
            noExit.setOnAction(eN -> {
                pStage.getScene().setRoot(gameOverRoot);
            });
        });

        vbox.getChildren().addAll(newBtn, backBtn, exitBtn);
        return vbox;
    }
    //Layouts
}
