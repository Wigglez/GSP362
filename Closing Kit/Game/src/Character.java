package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


public class Character  implements ActionListener{
    SimpleApplication game;
    // HUD elements
    private static int currentHealth = 0;
    private static int currentEnergy = 0;
    private static int currentArmor = 0;

    private static int maxHealth = 0;
    private static int maxArmor = 0;
    private static int maxEnergy = 0;

    private static int currentLevel = 0;
    private static int currentExperience = 0;
    private static int maxExperience = 0;
    private static int currentScore = 0;

    private static int incomingDamage = 0;

    // Movement
    private static int movementSpeed = 0;
    private static int jumpHeight = 0;

    private static int dashDamage = 0;

    // Booleans
    private static boolean pickUpAdded = false;
    private static boolean isShooting = false;
    private static boolean sprintActive = false;
    private static boolean shieldActive = false;
    private static boolean superJumpActive = false;
    private static boolean hoverActive = false;
    
    
    Spatial ninja;
    private CharacterControl player;

    Spatial playerDebug;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false;
    
    Character(SimpleApplication gameRef, BulletAppState bulletAppState){
        
        this.game = gameRef;
        
        //Load Ninja as filler for character model
        ninja = game.getAssetManager().loadModel("Models/Ninja/Ninja.mesh.xml");
        //ninja.rotate(0, -1.5f, 0);
        ninja.scale(0.02f, 0.02f, 0.02f);
//        ninja.setLocalTranslation(new Vector3f(341, 300, 0));
        //ninja.setMaterial(mark_mat);
        game.getRootNode().attachChild(ninja);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 2f);
        player = new CharacterControl(capsuleShape, .05f);
        player.setJumpSpeed(50);
        player.setFallSpeed(50);
        player.setGravity(120);        
        player.setPhysicsLocation(new Vector3f(1f,8f,1f));
        player.setViewDirection(new Vector3f(-1.0f, 0, 0));
        player.setCollideWithGroups(2);
         
        
        
        playerDebug = player.createDebugShape(game.getAssetManager());
        ninja.addControl(player);
        game.getRootNode().attachChild(playerDebug);

        bulletAppState.getPhysicsSpace().add(player);
        
        setUpKeys();
    }
    
    void Update(float tpf){
        
        walkDirection.set( 0, 0, 0);
        if(left)    { walkDirection.addLocal(Vector3f.UNIT_X.negate().multLocal(0.4f));}
        if(right)   { walkDirection.addLocal(Vector3f.UNIT_X.clone().multLocal(0.4f));}
        player.setWalkDirection(walkDirection);
        if( walkDirection != Vector3f.ZERO){
            player.setViewDirection(walkDirection.negate());
        }

        playerDebug.setLocalTranslation(player.getPhysicsLocation());
    }

    private void setUpKeys() {
        game.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        game.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        game.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_UP));
        game.getInputManager().addListener(this, "Left");
        game.getInputManager().addListener(this, "Right");
        game.getInputManager().addListener(this, "Jump");
    }
    
    public void onAction(String binding, boolean value, float tpf){
        if(binding.equals("Left")){
            left = value;
        } else if(binding.equals("Right")){
            right = value;
        } else if(binding.equals("Jump")){
            player.jump();
        }
    }
    
    public static int currentHealth() {
        int currentHealthValue;

        if (incomingDamage != 0) {
            currentHealthValue = maxHealth - incomingDamage;
        } else {
            currentHealthValue = currentHealth;
        }

        return currentHealthValue;

    }

    public static int healthPercentage() {
        int healthPercent;
        healthPercent = (currentHealth / maxHealth) * 100;

        return healthPercent;

    }

    public static int armorPercentage() {
        int armorPercent;
        armorPercent = (currentArmor / maxArmor) * 100;

        return armorPercent;

    }

    public static int energyPercentage() {
        int energyPercent;
        energyPercent = (currentEnergy / maxEnergy) * 100;

        return energyPercent;

    }
}
