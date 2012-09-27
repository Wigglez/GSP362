package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.math.FastMath;


/**
 * test
 * @author normenhansen
 */
//ActionListener ,
public class Main extends SimpleApplication implements PhysicsCollisionListener {
    
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    
    Character Juggernaut;
//    Spatial ninja;
//    private CharacterControl player;
//
//    Spatial playerDebug;
//    
//    private Vector3f walkDirection = new Vector3f();
//    private boolean left = false, right = false;

    Geometry elevator1;
    RigidBodyControl elvtr1;
    Geometry elevator2;
    RigidBodyControl elvtr2;
    Geometry elevator3;
    RigidBodyControl elvtr3;
    
    
    CameraChunk []views = new CameraChunk[54];
    CameraChunk currentView;

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
        
        
//        setUpKeys();
        flyCam.setEnabled(false);       
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);  
        
        Juggernaut = new Character(this, bulletAppState);
        
        setUpCameraBoxes();        
        //Load in the level
       // Material m = assetManager.loadMaterial("Models/levelLayout - Update.mtl");
        Spatial map = assetManager.loadModel("Models/levelLayout - Update_cameraPos.obj");
        rootNode.attachChild(map);
        
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
 //       mark_mat.setColor("Color", ColorRGBA.White);
 //       mark.setMaterial(mark_mat);
//        mark.setLocalTranslation(1f,1f,1f);
//        rootNode.attachChild(mark);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(map);
        landscape = new RigidBodyControl(sceneShape, 0);
        map.addControl(landscape);
        landscape.setCollisionGroup(1);
        landscape.removeCollideWithGroup(2);
        
//        //Load Ninja as filler for character model
//        ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
//        //ninja.rotate(0, -1.5f, 0);
//        ninja.scale(0.02f, 0.02f, 0.02f);
//        ninja.setLocalTranslation(new Vector3f(341, 300, 0));
//        //ninja.setMaterial(mark_mat);
//        rootNode.attachChild(ninja);
//        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 2f);
//        player = new CharacterControl(capsuleShape, .05f);
//        player.setJumpSpeed(50);
//        player.setFallSpeed(50);
//        player.setGravity(120);        
//        player.setPhysicsLocation(new Vector3f(1f,8f,1f));
//        player.setViewDirection(new Vector3f(-1.0f, 0, 0));
//        player.setCollideWithGroups(2);
//         
//        
//        
//        playerDebug = player.createDebugShape(assetManager);
//        ninja.addControl(player);
//        rootNode.attachChild(playerDebug);
//
//        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().add(landscape);
        
        //Elevator 1
        elevator1 = new Geometry("Elevator1", new Box(4, 1, 5));
        elevator1.setLocalTranslation(304, 20, 0);
        elevator1.setMaterial(mark_mat);
        elvtr1 = new RigidBodyControl(10);
        
        elevator1.addControl(elvtr1);
        elvtr1.setFriction(1000.0f);
        elvtr1.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(elvtr1);
        rootNode.attachChild(elevator1);
        
        //Elevator 2
        elevator2 = new Geometry("Elevator2", new Box(4, 1, 5));
        elevator2.setLocalTranslation(192, 208, 0);
        elevator2.setMaterial(mark_mat);
        elvtr2 = new RigidBodyControl(1000000000);
        
        
        
        elevator2.addControl(elvtr2);
        elvtr2.setAngularDamping(100000.0f);
        elvtr2.setFriction(1000.0f);
//        elvtr2.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(elvtr2);
        rootNode.attachChild(elevator2);
                
        
        //Add lights to see the models
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White);
        rootNode.addLight(al);

//        cam.setLocation(new Vector3f(player.getPhysicsLocation().x, player.getPhysicsLocation().y + 5, player.getPhysicsLocation().z +40 ));
//        cam.lookAt(player.getPhysicsLocation(), Vector3f.UNIT_Y);
        cam.setLocation(new Vector3f(0, 5, 40));
        cam.lookAt(new Vector3f(0, 5,0), Vector3f.UNIT_Y);
    }

    @Override
    public void simpleUpdate(float tpf) {
        Juggernaut.Update(tpf);
        //Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
//        walkDirection.set( 0, 0, 0);
//        if(left)    { walkDirection.addLocal(Vector3f.UNIT_X.negate().multLocal(0.4f));}
//        if(right)   { walkDirection.addLocal(Vector3f.UNIT_X.clone().multLocal(0.4f));}
//        player.setWalkDirection(walkDirection);
//        if( walkDirection != Vector3f.ZERO){
//            player.setViewDirection(walkDirection.negate());
//        }
//
//        playerDebug.setLocalTranslation(player.getPhysicsLocation());
        elevator1.setLocalTranslation( 304 + 20*FastMath.cos(timer.getTimeInSeconds()), 20, 0);
        elvtr1.setPhysicsLocation(elevator1.getLocalTranslation()); 

//        elevator2.setLocalTranslation( 192, 208 + 35*FastMath.cos(timer.getTimeInSeconds()), 0);
//        elvtr2.setPhysicsLocation(elevator2.getLocalTranslation()); 
        elvtr2.setLinearVelocity(new Vector3f(0, 25*FastMath.cos(timer.getTimeInSeconds()), 0));
        elvtr2.setPhysicsRotation(Matrix3f.IDENTITY);
        System.out.print(landscape.getCollideWithGroups() + "\n");
        
        for(int i = 0; i < views.length; i++){            
            if(views[i].testForPlayer(Juggernaut)){
                currentView = views[i];
            }
                
        }   
        
        cam.setLocation(currentView.CamPosition());
        cam.lookAt(currentView.CamLookAt(), Vector3f.UNIT_Y);

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

//    private void setUpKeys() {
//        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
//        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
//        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_UP));
//        inputManager.addListener(this, "Left");
//        inputManager.addListener(this, "Right");
//        inputManager.addListener(this, "Jump");
//    }
    
//    public void onAction(String binding, boolean value, float tpf){
//        if(binding.equals("Left")){
//            left = value;
//        } else if(binding.equals("Right")){
//            right = value;
//        } else if(binding.equals("Jump")){
//            player.jump();
//        }
//    }
    
    public void collision(PhysicsCollisionEvent event) {
//        cam.setLocation(new Vector3f(player.getPhysicsLocation().x, player.getPhysicsLocation().y + 5, player.getPhysicsLocation().z +40 ));
//        cam.lookAt(player.getPhysicsLocation() , Vector3f.UNIT_Y);
            
//        if ( event.getNodeB().getName().equals("Ninja-ogremesh") && event.getNodeA().getName().equals("view") )
//                 {
////           final Node node = (Node) event.getNodeA();
////            event.get
//            /** ... do something with the node ... */
//                     
//            cam.setLocation(new Vector3f(10, 4, 40));
//            cam.lookAt(new Vector3f(10, 0,0), Vector3f.UNIT_Y);
//        } else if(event.getNodeA().getName().equals("view2") 
//                || event.getNodeB().getName().equals("view2")){
//                cam.setLocation(new Vector3f(0, 4, 40));
//                cam.lookAt(new Vector3f(70, 0,0), Vector3f.UNIT_Y);
//        }else{
//            
//            //final Node node = (Node) event.getNodeB();
//            /** ... do something with the node ... */
//        }
    }

    private void setUpCameraBoxes() {
        
//        GhostControl ghost = new GhostControl(new BoxCollisionShape( new Vector3f(30,12,5)));
//       
//        Node view1 = new Node("view1");
//        view1.setLocalTranslation(new Vector3f(10, 0, 0));
//        view1.addControl(ghost);
//        rootNode.attachChild(view1);
//        bulletAppState.getPhysicsSpace().add(ghost);
//        ghost.setCollisionGroup(2);
//        ghost.removeCollideWithGroup(1);
        currentView = new CameraChunk(new Vector3f(10, 0, 0), rootNode, bulletAppState);
        views[0] = currentView;
        views[1] = new CameraChunk(new Vector3f(70, 0, 0), rootNode, bulletAppState);
        views[2] = new CameraChunk(new Vector3f(130, 0, 0), rootNode, bulletAppState);
        views[3] = new CameraChunk(new Vector3f(190, 0, 0), rootNode, bulletAppState);
        views[4] = new CameraChunk(new Vector3f(200, 24,  0), rootNode, bulletAppState);
        views[5] = new CameraChunk(new Vector3f(260, 24,  0), rootNode, bulletAppState);
        views[6] = new CameraChunk(new Vector3f(320, 24, 0), rootNode, bulletAppState);
        views[7] = new CameraChunk(new Vector3f(380, 24, 0), rootNode, bulletAppState);
        views[8] = new CameraChunk(new Vector3f(440, 32, 0), rootNode, bulletAppState);
        views[9] = new CameraChunk(new Vector3f(500, 48, 0), rootNode, bulletAppState);
        views[10] = new CameraChunk(new Vector3f(510, 73, 0), rootNode, bulletAppState);
        views[11] = new CameraChunk(new Vector3f(450, 73, 0), rootNode, bulletAppState);
        views[12] = new CameraChunk(new Vector3f(394, 92, 0), rootNode, bulletAppState);
        views[13] = new CameraChunk(new Vector3f(345, 107, 0), rootNode, bulletAppState);
        views[14] = new CameraChunk(new Vector3f(334, 125, 0), rootNode, bulletAppState);
        views[15] = new CameraChunk(new Vector3f(304, 129, 0), rootNode, bulletAppState);
        views[16] = new CameraChunk(new Vector3f(274, 125, 0), rootNode, bulletAppState);
        views[17] = new CameraChunk(new Vector3f(254, 107, 0), rootNode, bulletAppState);
        views[18] = new CameraChunk(new Vector3f(211, 95, 0), rootNode, bulletAppState);
        views[19] = new CameraChunk(new Vector3f(192, 72, 0), rootNode, bulletAppState);
        views[20] = new CameraChunk(new Vector3f(192, 48, 0), rootNode, bulletAppState);
        views[21] = new CameraChunk(new Vector3f(192, 114, 0), rootNode, bulletAppState);
        views[22] = new CameraChunk(new Vector3f(192, 138, 0), rootNode, bulletAppState);
        views[23] = new CameraChunk(new Vector3f(192, 162, 0), rootNode, bulletAppState);
        views[24] = new CameraChunk(new Vector3f(192, 186, 0), rootNode, bulletAppState);
        views[25] = new CameraChunk(new Vector3f(192, 210, 0), rootNode, bulletAppState);
        views[26] = new CameraChunk(new Vector3f(192, 234, 0), rootNode, bulletAppState);
        views[27] = new CameraChunk(new Vector3f(132, 169, 0), rootNode, bulletAppState);
        views[28] = new CameraChunk(new Vector3f(90, 145, 0), rootNode, bulletAppState);
        views[29] = new CameraChunk(new Vector3f(40, 124, 0), rootNode, bulletAppState);
        views[30] = new CameraChunk(new Vector3f(-20, 124, 0), rootNode, bulletAppState);
        views[31] = new CameraChunk(new Vector3f(-80, 124, 0), rootNode, bulletAppState);
        views[32] = new CameraChunk(new Vector3f(-140, 124, 0), rootNode, bulletAppState);
        views[33] = new CameraChunk(new Vector3f(-200, 124, 0), rootNode, bulletAppState);
        views[34] = new CameraChunk(new Vector3f(-260, 124,  0), rootNode, bulletAppState);
        views[35] = new CameraChunk(new Vector3f(-182, 100,  0), rootNode, bulletAppState);
        views[36] = new CameraChunk(new Vector3f(-182, 76, 0), rootNode, bulletAppState);
        views[37] = new CameraChunk(new Vector3f(-162, 76, 0), rootNode, bulletAppState);
        views[38] = new CameraChunk(new Vector3f(-82, 148, 0), rootNode, bulletAppState);
        views[39] = new CameraChunk(new Vector3f(-82, 172, 0), rootNode, bulletAppState);
        views[40] = new CameraChunk(new Vector3f(-82, 196, 0), rootNode, bulletAppState);
        views[41] = new CameraChunk(new Vector3f(-142, 196, 0), rootNode, bulletAppState);
        views[42] = new CameraChunk(new Vector3f(232, 245, 0), rootNode, bulletAppState);
        views[43] = new CameraChunk(new Vector3f(292, 245, 0), rootNode, bulletAppState);
        views[44] = new CameraChunk(new Vector3f(352, 245,  0), rootNode, bulletAppState);
        views[45] = new CameraChunk(new Vector3f(281, 269,  0), rootNode, bulletAppState);
        views[46] = new CameraChunk(new Vector3f(281, 293, 0), rootNode, bulletAppState);
        views[47] = new CameraChunk(new Vector3f(281, 317, 0), rootNode, bulletAppState);
        views[48] = new CameraChunk(new Vector3f(341, 300, 0), rootNode, bulletAppState);
        views[49] = new CameraChunk(new Vector3f(381, 300, 0), rootNode, bulletAppState);
        views[50] = new CameraChunk(new Vector3f(381, 280, 0), rootNode, bulletAppState);
        views[51] = new CameraChunk(new Vector3f(221, 323, 0), rootNode, bulletAppState);
        views[52] = new CameraChunk(new Vector3f(341, 323, 0), rootNode, bulletAppState);
        views[53] = new CameraChunk(new Vector3f(401, 323, 0), rootNode, bulletAppState);
//        GhostControl ghost2 = new GhostControl(new BoxCollisionShape( new Vector3f(30,12,5)));
//       
//        Node view2 = new Node("view2");
//        view2.setLocalTranslation(new Vector3f(70, 0, 0));
//        view2.addControl(ghost2);
//        rootNode.attachChild(view2);
//        bulletAppState.getPhysicsSpace().add(ghost2);
//        ghost2.setCollisionGroup(2);
//        ghost2.removeCollideWithGroup(1);
//        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
    }
}
