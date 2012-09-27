package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener {
    
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    Spatial ninja;
    private CharacterControl player;
    Spatial playerDebug;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false;
//    Geometry mark;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        app.setShowSettings(false); // splashscreen
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        setUpKeys();
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);        
                
        //Load in the level
        Spatial map = assetManager.loadModel("Models/levelLayout.obj");
        rootNode.attachChild(map);
        
//        Sphere sphere = new Sphere(30, 30, 1f);
//        mark = new Geometry("Boom!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
 //       mark_mat.setColor("Color", ColorRGBA.White);
 //       mark.setMaterial(mark_mat);
//        mark.setLocalTranslation(1f,1f,1f);
//        rootNode.attachChild(mark);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(map);
        landscape = new RigidBodyControl(sceneShape, 0);
        map.addControl(landscape);
        
        //Load Ninja as filler for character model
        ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(0.0153f, 0.0153f, 0.0153f);
        ninja.rotate(0.0f, -1.5f, 0.0f);
        ninja.setLocalTranslation(Vector3f.ZERO);
        //ninja.setMaterial(mark_mat);
        rootNode.attachChild(ninja);
        
        //CollisionShape playerCollision = CollisionShapeFactory.createMeshShape(ninja);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 2f);
        player = new CharacterControl(capsuleShape, .05f);
        player.setJumpSpeed(25);
        player.setFallSpeed(35);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(1f,8f,1f));
        
        playerDebug = player.createDebugShape(assetManager);
        
        rootNode.attachChild(playerDebug);
        
        
        
        
        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().add(landscape);
        
        
//        //Add lights to see the models
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White);
        rootNode.addLight(al);

        cam.setLocation(new Vector3f(player.getPhysicsLocation().x, player.getPhysicsLocation().y + 5, player.getPhysicsLocation().z +40 ));
        cam.lookAt(player.getPhysicsLocation(), Vector3f.UNIT_Y);
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
        walkDirection.set( 0, 0, 0);
        if(left)    { walkDirection.addLocal(camLeft);}
        if(right)   { walkDirection.addLocal(camLeft.negate());}
        player.setWalkDirection(walkDirection);
        
    cam.setLocation(new Vector3f(player.getPhysicsLocation().x, player.getPhysicsLocation().y + 5, player.getPhysicsLocation().z +40 ));
        cam.lookAt(player.getPhysicsLocation() , Vector3f.UNIT_Y);
        
        ninja.setLocalTranslation(player.getPhysicsLocation().x, player.getPhysicsLocation().y -1.5f, player.getPhysicsLocation().z );
        playerDebug.setLocalTranslation(player.getPhysicsLocation());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Jump");
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
}
