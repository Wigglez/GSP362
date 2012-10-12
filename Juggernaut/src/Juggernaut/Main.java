package Juggernaut;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.math.FastMath;

import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.render.image.ImageMode;
import de.lessvoid.nifty.render.image.ImageModeHelper;
import de.lessvoid.nifty.render.image.CompoundImageMode;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import java.util.logging.Level;

public class Main extends SimpleApplication implements PhysicsCollisionListener {
    
    private static StartScreen start;
    private Nifty nifty;
    
    
    
    private static miniGun minigun;
    private Pistol pistol;
    private static laserRifle laserRifle;
    //private static Character JuggernautWeapons;
    
    private BulletAppState bulletAppState;  //Phyics manager
    private RigidBodyControl landscape;     //Phyics mesh for map
    
    private static Character Juggernaut;
    //Game Character
    Enemy[] Enemy = new Enemy[66];
    
    //HealthPickup HealthPickup1; //test Health Pickup
    HealthPickup HealthPickup2; // Boss area Health Pickup
    HealthPickup HealthPickup3; // Boss area Health Pickup
    HealthPickup HealthPickup4; // Boss area Health Pickup
    
    Vector3f spawnLocation;
    Vector3f spawnDirection;

    Geometry elevator1;                     //Elevator 1 - Horizontal motion over lava pit
    RigidBodyControl elvtr1;
    Geometry elevator2;                     //Elevator 2 - Vertical motion to reach new area
    RigidBodyControl elvtr2;
    Geometry elevator3;                     //Elevator 3 - Vertical motion to reach new area
    RigidBodyControl elvtr3;
    
    private float dt, prevTime =0;
    
    Lava[] lava = new Lava[2];
    
    CameraChunk []views = new CameraChunk[54];//holds all the different camera views
    CameraChunk currentView;                  //Stores the current view to update camPos and lookAt

    public static void main(String[] args) {
        
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        app.setShowSettings(false); // splashscreen
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        setDisplayFps(false);
        setDisplayStatView(false);
        //Disable fly cam so it sits in fixed location
        flyCam.setEnabled(false);       
        
        //Creates the Physics state of the game
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);  //Shows wireframes of models
        
        //Create the player character
        Juggernaut = new Character(this, bulletAppState);
         
        //create Health pickups
        //HealthPickup1 = new HealthPickup(Juggernaut,this, bulletAppState, new Vector3f(170f, 0f, 0f));
        HealthPickup2 = new HealthPickup(Juggernaut,this, bulletAppState, new Vector3f(360f, 316f, 0f));
        HealthPickup3 = new HealthPickup(Juggernaut,this, bulletAppState, new Vector3f(350f, 316f, 0f));
        HealthPickup4 = new HealthPickup(Juggernaut,this, bulletAppState, new Vector3f(196f, 316f, 0f));
                
        setUpLava();
        
        // Creates enemies in the world
        setUpEnemies();
                
        //Creates viewing boxes or Camera Chunks in the world
        setUpCameraBoxes();   
        
        //Load in the level
        Spatial map = assetManager.loadModel("Models/levelLayout - Update_cameraPos.obj");
        map.setName("LevleGeo");
        rootNode.attachChild(map);
        
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
 //       mark_mat.setColor("Color", ColorRGBA.White);
 //       mark.setMaterial(mark_mat);
//        mark.setLocalTranslation(1f,1f,1f);
//        rootNode.attachChild(mark);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(map);
        landscape = new RigidBodyControl(sceneShape, 0);
        map.addControl(landscape);
        landscape.setCollisionGroup(1);         //Set collision group for the map
        landscape.removeCollideWithGroup(2);    //Prevent collision events between map and camera chunks(collision group 2)
        
        bulletAppState.getPhysicsSpace().add(landscape);
              
        
        //Add lights to see the models
        DirectionalLight light1 = new DirectionalLight();
        light1.setDirection(new Vector3f(-10f,  -.5f,-1f));
        rootNode.addLight(light1);
        
        DirectionalLight light2 = new DirectionalLight();
        light2.setDirection(new Vector3f(10f,  -.5f,-1f));
        rootNode.addLight(light2);
        
        DirectionalLight light3 = new DirectionalLight();
        light3.setDirection(new Vector3f(0,  1f, -1));
        rootNode.addLight(light3);
        
        DirectionalLight light4 = new DirectionalLight();
        light4.setDirection(new Vector3f(0,  -1f, 1));
        rootNode.addLight(light4);
        
        
        this.CreateHUD();

    }

    @Override
    public void simpleUpdate(float tpf) {
        
        dt = getTimer().getTimeInSeconds() - prevTime;
        prevTime = getTimer().getTimeInSeconds();
        
        //Update player
        Juggernaut.Update(dt);
        
        //Health Pickups
        //HealthPickup1.Update(dt, Juggernaut.getPosition());
        HealthPickup2.Update(dt, Juggernaut.getPosition());
        HealthPickup3.Update(dt, Juggernaut.getPosition());
        HealthPickup4.Update(dt, Juggernaut.getPosition());
        
        for(int i = 0; i < lava.length; i++){            
            if(lava[i].testForPlayer(Juggernaut.getControl())){
                Juggernaut.takeDamage(15f * dt);
            }
        }   
        
       // Update enemies
	for (int itr = 0; itr < Enemy.length; itr++) {
            if(!Enemy[itr].isDead()){
                Enemy[itr].Update(dt, Juggernaut.getPosition());
            }
	}
        
       
        //Step through all views and test if player is in that space
        //If true, set current view to this view
        for(int i = 0; i < views.length; i++){            
            if(views[i].testForPlayer(Juggernaut.getControl())){
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


    
    public void collision(PhysicsCollisionEvent event) {
//        for (int itr = 0; itr < Enemy.length; itr++){
//            if(event.getNodeA().getName().equals("Player") && event.getNodeB().equals(Enemy[itr].getSpatial())){
//                Juggernaut.takeDamage(1);
//            }
//        }
        //For actual physics collisions
        //Maybe used for elevators/Moving platforms
    }

    private void setUpEnemies() {
        // Enemy creations
        // First floor (enemies face left)
        Enemy[0] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(60f, -7f, 0f), new Vector3f(1, 0, 0));
        Enemy[1] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(70f, -7f, 0f), new Vector3f(1, 0, 0));
        Enemy[2] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(89f, -7f, 0f), new Vector3f(1, 0, 0));
        Enemy[3] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(135f, -7f,
                        0f), new Vector3f(1, 0, 0));
        Enemy[4] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(152f, -7f,
                        0f), new Vector3f(1, 0, 0));
        Enemy[5] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(183f, 0f,
                        0f), new Vector3f(1, 0, 0));

        // Second floor (enemies face left)
        Enemy[6] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(213f, 16f, 0f),
                        new Vector3f(1, 0, 0));
        Enemy[7] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(250f, 24f, 0f),
                        new Vector3f(1, 0, 0));
        Enemy[8] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(265f, 24f, 0f),
                        new Vector3f(1, 0, 0));
        Enemy[9] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(285f, 24f, 0f),
                        new Vector3f(1, 0, 0));
        Enemy[10] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(327f, 24f, 0f), new Vector3f(1, 0, 0));
        Enemy[11] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(361f, 24f, 0f), new Vector3f(1, 0, 0));
        Enemy[12] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(392f, 16f, 0f), new Vector3f(1, 0, 0));
        Enemy[13] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(448f, 31.5f,
                        0f), new Vector3f(1, 0, 0));
        Enemy[14] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(464f, 38f,
                        0f), new Vector3f(1, 0, 0));
        Enemy[15] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(494f, 39f, 0f), new Vector3f(1, 0, 0));
        Enemy[16] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(510f, 49f, 0f), new Vector3f(1, 0, 0));
        Enemy[17] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(527f, 49f, 0f), new Vector3f(1, 0, 0));
        Enemy[18] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(527f, 57f, 0f), new Vector3f(1, 0, 0));

        // Third floor (enemies face right)
        Enemy[19] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(490f, 65f, 0f), new Vector3f(-1, 0, 0));
        Enemy[20] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(451f, 70f, 0f), new Vector3f(-1, 0, 0));
        Enemy[21] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(439f, 75f, 0f), new Vector3f(-1, 0, 0));
        Enemy[22] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(402f, 88f, 0f), new Vector3f(-1, 0, 0));
        Enemy[23] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(385f, 88f, 0f), new Vector3f(-1, 0, 0));
        Enemy[24] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(373f, 88f, 0f), new Vector3f(-1, 0, 0));
        Enemy[25] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(356f, 98f, 0f), new Vector3f(-1, 0, 0));
        Enemy[26] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(346f, 108f, 0f), new Vector3f(-1, 0, 0));
        Enemy[27] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(335f, 118f, 0f), new Vector3f(-1, 0, 0));
        Enemy[28] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(315f, 128f, 0f), new Vector3f(-1, 0, 0));
        Enemy[29] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(278f, 128f, 0f), new Vector3f(-1, 0, 0));
        Enemy[30] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(221f, 88f, 0f), new Vector3f(-1, 0, 0));
        Enemy[31] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(201f, 88f, 0f), new Vector3f(-1, 0, 0));
        Enemy[32] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(192f, 97f, 0f), new Vector3f(-1, 0, 0));

        // Fourth floor (enemies face right)
        Enemy[33] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(167f, 178f, 0f), new Vector3f(-1, 0, 0));
        Enemy[34] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(-11f, 116f, 0f), new Vector3f(-1, 0, 0));
        Enemy[35] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(-19f, 116f, 0f), new Vector3f(-1, 0, 0));
        Enemy[36] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(-32f, 116f, 0f), new Vector3f(-1, 0, 0));
        Enemy[37] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(-82f, 116f, 0f), new Vector3f(-1, 0, 0));
        Enemy[38] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-105f, 116f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[39] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-129f, 116f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[40] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-148f, 116f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[41] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-218f, 116f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[42] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-253f, 116f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[43] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-260f, 116f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[44] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-267f, 116f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[45] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-275f, 116f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[46] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-286f, 116f,
                        0f), new Vector3f(-1, 0, 0));

        // Fourth floor up (enemies face right)
        Enemy[47] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-106f, 190f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[48] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-134f, 190f,
                        0f), new Vector3f(-1, 0, 0));
        Enemy[49] = new Enemy( Juggernaut, this, bulletAppState, new Vector3f(-153f, 190f,
                        0f), new Vector3f(-1, 0, 0));

        // Fourth floor down (enemies face left)
        Enemy[50] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(-189f, 68f, 0f), new Vector3f(-1, 0, 0));
        Enemy[51] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(-170f, 68f, 0f), new Vector3f(1, 0, 0));
        Enemy[52] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(-126f, 68f, 0f), new Vector3f(1, 0, 0));
        Enemy[53] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(-113f, 68f, 0f), new Vector3f(1, 0, 0));

        // Fifth floor (enemies face left)
        Enemy[54] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(225f, 236f, 0f), new Vector3f(1, 0, 0));
        Enemy[55] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(254f, 236f, 0f), new Vector3f(1, 0, 0));
        Enemy[56] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(287f, 246f, 0f), new Vector3f(1, 0, 0));
        Enemy[57] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(311f, 236f, 0f), new Vector3f(1, 0, 0));
        Enemy[58] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(345f, 236f, 0f), new Vector3f(1, 0, 0));
        Enemy[59] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(366f, 236f, 0f), new Vector3f(1, 0, 0));

        // Sixth floor (enemies face left)
        Enemy[60] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(304f, 296f, 0f), new Vector3f(1, 0, 0));
        Enemy[61] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(327f, 296f, 0f), new Vector3f(1, 0, 0));
        Enemy[62] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(373f, 296f, 0f), new Vector3f(1, 0, 0));

        // Seventh floor (enemies face right)
        Enemy[63] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(254f, 316f, 0f), new Vector3f(-1, 0, 0));
        Enemy[64] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(225f, 316f, 0f), new Vector3f(-1, 0, 0));
        Enemy[65] = new Enemy( Juggernaut, this, bulletAppState,
                        new Vector3f(210f, 316f, 0f), new Vector3f(-1, 0, 0));
    }
    
    private void setUpLava() {
        lava[0] = new Lava(new Vector3f(304, 13, 0), new Vector3f(60, 1, 15), rootNode, bulletAppState);
        lava[1] = new Lava(new Vector3f(339, 293, 0), new Vector3f(10, 1, 5), rootNode, bulletAppState);
    }
    
    private void setUpCameraBoxes() {
        

        //Create all viewing boxes and place them throughout map
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
        views[37] = new CameraChunk(new Vector3f(-122, 76, 0), rootNode, bulletAppState);
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

        
    }
    public void CreateHUD()
    {
           
            start = new StartScreen();
            stateManager.attach(start);

            minigun = new miniGun(this);
            pistol = new Pistol(this);
            laserRifle = new laserRifle(this);
            


            NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                    assetManager, inputManager, audioRenderer, guiViewPort);
            nifty = niftyDisplay.getNifty();
            guiViewPort.addProcessor(niftyDisplay);


            nifty.loadStyleFile("nifty-default-styles.xml");
            nifty.loadControlFile("nifty-default-controls.xml");

            // Start<screen>
            nifty.addScreen("start", new ScreenBuilder("start") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                backgroundColor("#000f");

                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/juggernaut.png");
                }});

            }});

            ///////////////////////////////////////////////////////////
            //Start Screen
            //////////////////////////////////////////////////////////
            layer(new LayerBuilder("foreground") {{
                    childLayoutVertical();
                    backgroundColor("#0000");

                // panel added
                panel(new PanelBuilder("panel_One") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");

    //                text(new TextBuilder() {{
    //                    text("Start Screen");
    //                    font("Interface/Fonts/Default.fnt");
    //                    wrap(true);
    //                    height("100%");
    //                    width("100%");
    //                 }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f08");
                    height("25%");
                    width("100%");

                    control(new ButtonBuilder("StartButton", "Start") {{
                          alignCenter();
                          valignBottom();
                          height("25%");
                          width("25%");
                          visibleToMouse(true);
                          interactOnClick("goToScreen(hud)");
                          //interactOnClick("startGame()");

                        }});

                }});
                panel(new PanelBuilder("panel_Three") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f09");
                    height("25%");
                    width("100%");

                    control(new ButtonBuilder("OptionButton", "Options") {{
                      alignCenter();
                      valignCenter();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);
                      interactOnClick("goToScreen(OptionsScreen)");

                        }});

                }});

                panel(new PanelBuilder("panel_Four") {{
                    childLayoutCenter();
                    alignCenter();
                   //backgroundColor("#00f8");
                    height("25%");
                    width("100%");


                    control(new ButtonBuilder("QuitButton", "Quit") {{
                      alignCenter();
                      valignTop();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);
                      interactOnClick("quitGame()");
                        }});

                    }}); // panel added
                }});

            }}.build(nifty));

            //////////////////////////////////////////////////////////
            // HUD</screen>
            //////////////////////////////////////////////////////////
            nifty.addScreen("hud", new ScreenBuilder("hud") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                //backgroundColor("#0000");
                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/Hud_Background.png");
                }});
            }});

            layer(new LayerBuilder("foreground") {{
                childLayoutVertical();
                //backgroundColor("#0000");

                // panel added

                panel(new PanelBuilder("panel_One") {{
                    childLayoutHorizontal();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");
                    visibleToMouse(false);
                        panel(new PanelBuilder("Portrait_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#0f05");
                        height("100%");
                        width("10%");
                        visibleToMouse(false);

                         panel(new PanelBuilder("Portrait") {{
                            childLayoutVertical();
    //                        alignLeft();
    //                        valignTop();
                            //backgroundColor("#0f05");
                            height("105");
                            width("128");
                            visibleToMouse(false);
                            image(new ImageBuilder() 
                                    {{
                                        filename("Interface/NO.jpg");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width("100%");
                                    }});

                            }});


                        }});

                        panel(new PanelBuilder("Bar_panel") {{
                            childLayoutVertical();
                            alignLeft();
                            //valignTop();
                            //backgroundColor("#f005");
                            height("100%");
                            width("200");
                            visibleToMouse(false);

                            panel(new PanelBuilder("Health_panel") {{
                                childLayoutHorizontal();
                                alignLeft();
                                //valignTop();
                                //backgroundColor("#00f8");
                                height("35");
                                width("100%");
                                visibleToMouse(false);


                                    image(new ImageBuilder() 
                                    {{
                                        id("PlayerHealth");
                                        filename("Interface/HealthBar.png");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width(start.getHealthPercentage() + "%");
                                    }});


                            }});

                            panel(new PanelBuilder("Armor_panel") {{
                                childLayoutHorizontal();
                                alignLeft();
                                //valignTop();
                                //backgroundColor("#00f7");
                                height("35");
                                width("100%");
                                visibleToMouse(false);

                                    image(new ImageBuilder() 
                                    {{
                                        id("PlayerArmor");
                                        filename("Interface/ArmorBar.png");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width(start.getEnergyPercentage() + "%");
                                    }});



                            }});

                            panel(new PanelBuilder("Energy_panel") {{
                                childLayoutHorizontal();
                                alignLeft();
                                //valignTop();
                                //backgroundColor("#00f6");
                                height("35");
                                width("100%");
                                visibleToMouse(false);


                                    image(new ImageBuilder() 
                                    {{
                                        id("PlayerEnergy");
                                        filename("Interface/EnergyBar.png");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width(start.getEnergyPercentage() + "%");
                                    }});



                            }});
                            

                        }});
                        panel(new PanelBuilder("Remaining_Top") {{
                            
                            childLayoutVertical();
                            //alignRight();
                            //valignTop();
                            //backgroundColor("#f005");
                            height("100%");
                            width("*");
                            visibleToMouse(false);
                            
                            panel(new PanelBuilder("Score_panel") {{
                                childLayoutHorizontal();
                                alignRight();
                                valignTop();
                                //backgroundColor("#00f6");
                                height("35");
                                width("200");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("score");
                                    childLayoutHorizontal();
                                    alignRight();
                                    text((int)start.getScore() + "");
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});
                            }});
                            
                            panel(new PanelBuilder("Skills_Button_Panel") {{
                                childLayoutHorizontal();
                                alignRight();
                                valignTop();
                                //backgroundColor("#00f8");
                                height("50");
                                width("50");
                                visibleToMouse(false);
                                
                                control(new ButtonBuilder("SkillsButton", "Skills") 
                                {{
                                      alignCenter();
                                      focusable(false);
                                      valignBottom();
                                      height("50%");
                                      width("100%");
                                      visibleToMouse(true);
                                      interactOnClick("ShowSkillsPopup()");
                                }});
//                                image(new ImageBuilder() 
//                                {{
//                                        filename("Interface/Key.png");
//                                        valignCenter();
//                                        alignCenter();
//                                        height("100%");
//                                        width("100%");
//                                }});
                            }});
                            panel(new PanelBuilder("Store_Button_Panel") {{
                                childLayoutHorizontal();
                                alignRight();
                                valignTop();
                                //backgroundColor("#00f8");
                                height("50");
                                width("50");
                                visibleToMouse(false);
                                
                                control(new ButtonBuilder("StoreButton", "Store") 
                                {{
                                      alignCenter();
                                      focusable(false);
                                      valignBottom();
                                      height("50%");
                                      width("100%");
                                      visibleToMouse(true);
                                      interactOnClick("ShowStorePopup()");
                                }});
//                                image(new ImageBuilder() 
//                                {{
//                                        filename("Interface/Key.png");
//                                        valignCenter();
//                                        alignCenter();
//                                        height("100%");
//                                        width("100%");
//                                }});
                            }});
                        }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f08");
                    height("25%");
                    width("100%");
                    visibleToMouse(false);
                    
//                    control(new ButtonBuilder("TestHealthButton", "Test Health") {{
//                      alignCenter();
//                      valignBottom();
//                      height("25%");
//                      width("25%");
//                      visibleToMouse(true);
//                      interactOnClick("TestHealth()");
//                        }});
                    



                }});
                panel(new PanelBuilder("panel_Three") {{
                    childLayoutVertical();
                    alignCenter();
                    //backgroundColor("#0f09");
                    height("24%");
                    width("100%");
                    visibleToMouse(false);
                     

                     
                     
                    
                    


                }});

                panel(new PanelBuilder("Experience_Panel") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#00f8");
                    height("2.5%");
                    width("100%");
                    visibleToMouse(false);
                    
                    image(new ImageBuilder() 
                    {{
                        id("PlayerExperience");
                        filename("Interface/experience.png");
                        valignCenter();
                        alignLeft();
                        height("100%");
                        width(start.getExperiencePercentage() + "%");
                    }});

                    }}); // panel added
                panel(new PanelBuilder("top_spacer_Panel") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f06");
                    height("2.8%");
                    width("100%");
                    visibleToMouse(false);

                    }}); // panel added
                panel(new PanelBuilder("panel_Five") {{
                    childLayoutHorizontal();
                    alignCenter();
                    //backgroundColor("#00f5");
                    height("17.9%");
                    width("100%");
                    visibleToMouse(false);

                    panel(new PanelBuilder("Current_Weapon_panel") {{
                        childLayoutHorizontal();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f003");
                        height("100%");
                        width("25%");
                        visibleToMouse(false);

                        panel(new PanelBuilder("weapon_spacer_Panel") {{
                            childLayoutCenter();
                            alignCenter();
                            //backgroundColor("#0f06");
                            height("100%");
                            width("20");
                            visibleToMouse(false);

                        }}); // panel added

                        panel(new PanelBuilder("Weapon_panel") {{
                            childLayoutCenter();
                            alignCenter();
                            //valignTop();
                            //backgroundColor("#00f6");
                            height("100%");
                            width("150");
                            visibleToMouse(false);


                                image(new ImageBuilder() 
                                {{
                                    filename("Interface/Pistol.png");
                                    valignCenter();
                                    alignCenter();
                                    height("100%");
                                    width("100%");
                                    id("CurrentWeapon");
                                }});

                            }});
                         panel(new PanelBuilder("Weapon_Stats_panel") {{
                            childLayoutVertical();
                            alignRight();
                            //valignTop();
                            //backgroundColor("#00f8");
                            height("100%");
                            width("130");
                            visibleToMouse(false);
                            
                            
                            panel(new PanelBuilder("Current_Weapon_Text") {{
                                childLayoutCenter();
                                //valignBottom();
                                //valignTop();
                                alignLeft();
                                //backgroundColor("#0f08");
                                height("20%");
                                width("100%");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("CurrentWeaponText");
                                    childLayoutCenter();
                                    alignLeft();
                                    text("CurrentWeapon");
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});

                            }});
                            
                            panel(new PanelBuilder("Weapon_Damage_Text") {{
                                childLayoutHorizontal();
                                //valignBottom();
                                //valignTop();
                                alignLeft();
                                //backgroundColor("#0f06");
                                height("20%");
                                width("100%");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("WeaponDamageText");
                                    childLayoutHorizontal();
                                    alignLeft();
                                    text("Damage: " + (int)pistol.dammage);
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});

                            }});
                            panel(new PanelBuilder("Weapon_Fire_Rate_Text") {{
                                childLayoutCenter();
                                //valignBottom();
                                //valignTop();
                                //backgroundColor("#0f04");
                                height("20%");
                                width("100%");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("WeaponFireRateText");
                                    childLayoutCenter();
                                    alignLeft();
                                    text("Fire Rate: " + pistol.fireRate);
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});

                            }}); 

                            panel(new PanelBuilder("Weapon_Ammo_Text") {{
                                childLayoutCenter();
                                //valignBottom();
                                //valignTop();
                                //backgroundColor("#f008");
                                height("20%");
                                width("100%");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("Ammo");
                                    childLayoutHorizontal();
                                    alignRight();
                                    text("Ammo: " + (int)pistol.currentAmmo);
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});

                                }});

                            }});

                        }});

                    panel(new PanelBuilder("Inventory1_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#0f04");
                        height("100%");
                        width("15%");
                        visibleToMouse(false);
                         panel(new PanelBuilder("Pistol_panel") {{
                            childLayoutCenter();
                            alignCenter();
                            //valignTop();
                            //backgroundColor("#f004");
                            height("100%");
                            width("80%");
                            interactOnClick("weapon1Clicked()");

                            image(new ImageBuilder() 
                                {{
                                    filename("Interface/Pistol.png");
                                    valignCenter();
                                    alignCenter();
                                    height("129");
                                    width("150");
                                    id("Pistol");

                                }});
                         }});
                        }});
                    panel(new PanelBuilder("Inventory2_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f005");
                        height("100%");
                        width("15%");

                         panel(new PanelBuilder("MiniGun_panel") {{

                            childLayoutCenter();
                            alignCenter();
                            //valignTop();
                            //backgroundColor("#f004");
                            height("100%");
                            width("80%");
                            interactOnClick("weapon2Clicked()");

                            image(new ImageBuilder() 
                                {{
                                    filename("Interface/miniGun.png");
                                    valignCenter();
                                    alignCenter();
                                    height("129");
                                    width("150");
                                    id("MiniGun");
                                }});

                        }});
                        }});
                    panel(new PanelBuilder("Inventory3_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f006");
                        height("100%");
                        width("15%");

                        panel(new PanelBuilder("Cannon_panel") {{
                            childLayoutCenter();
                            alignCenter();
                            //valignTop();
                            //backgroundColor("#f004");
                            height("100%");
                            width("80%");
                            interactOnClick("weapon3Clicked()");


                            image(new ImageBuilder() 
                                {{
                                    filename("Interface/Cannon.png");
                                    valignCenter();
                                    alignCenter();
                                    height("129");
                                    width("150");
                                    id("Cannon");
                                }});
                        }});
                        }});
                    panel(new PanelBuilder("Skill1_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f007");
                        height("100%");
                        width("15%");
                        visibleToMouse(false);

                        image(new ImageBuilder() 
                            {{
                                filename("Interface/running-man.png");
                                valignCenter();
                                alignCenter();
                                height("129");
                                width("150");

                            }});

                        }});
                    panel(new PanelBuilder("Skill2_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f008");
                        height("100%");
                        width("15%");
                        visibleToMouse(false);

                        image(new ImageBuilder() 
                            {{
                                filename("Interface/JetPack.png");
                                valignCenter();
                                alignCenter();
                                height("129");
                                width("150");
                            }});
                        }});

                    }}); // panel added
                    panel(new PanelBuilder("bottom_spacer_Panel") 
                    {{
                        childLayoutHorizontal();
                        alignCenter();
                        valignCenter();
                        //backgroundColor("#0f06");
                        height("2.8%");
                        width("100%");
                        visibleToMouse(false);

                         panel(new PanelBuilder("Bottom_Panel_One") {
                        {
                            childLayoutCenter();
                            width("26.75%");
                            height("100%");
                            //backgroundColor("#f008");
                            alignCenter();
                            valignCenter();

                        }
                        });
                        panel(new PanelBuilder("Bottom_Pistol_Panel") {
                        {
                            childLayoutCenter();
                            width("11.75%");
                            height("100%");
                            //backgroundColor("#f007");
                            alignCenter();
                            valignCenter();

                            text(new TextBuilder() 
                            {{
                                id("Weapon_1");
                                childLayoutHorizontal();
                                alignRight();
                                
                                text("1");
                                font("Interface/Fonts/Default.fnt");
                                //wrap(true);
                                height("100%");
                                width("100%");
                            }});

                        }
                        });
                        panel(new PanelBuilder("Bottom_Panel_Three") {
                        {
                            childLayoutCenter();
                            width("3.25%");
                            height("100%");
                            //backgroundColor("#f006");
                            alignCenter();
                            valignCenter();

                        }
                        });
                        panel(new PanelBuilder("Bottom_MiniGun_Panel") {
                        {
                            childLayoutCenter();
                            width("11.75%");
                            height("100%");
                            //backgroundColor("#f005");
                            alignCenter();
                            valignCenter();
                            text(new TextBuilder() 
                            {{
                                id("Weapon_2");
                                childLayoutHorizontal();
                                alignRight();
                                text("2");
                                font("Interface/Fonts/Default.fnt");
                                //wrap(true);
                                height("100%");
                                width("100%");
                            }});

                        }
                        });
                        panel(new PanelBuilder("Bottom_Panel_Five") {
                        {
                            childLayoutCenter();
                            width("3.25%");
                            height("100%");
                            //backgroundColor("#f004");
                            alignCenter();
                            valignCenter();

                        }
                        });
                        panel(new PanelBuilder("Bottom_LaserRifle_Panel") {
                        {
                            childLayoutCenter();
                            width("11.75%");
                            height("100%");
                            //backgroundColor("#f003");
                            alignCenter();
                            valignCenter();

                            text(new TextBuilder() 
                            {{
                                id("Weapon_3");
                                childLayoutHorizontal();
                                alignRight();
                                text("3");
                                font("Interface/Fonts/Default.fnt");
                                //wrap(true);
                                height("100%");
                                width("100%");
                            }});

                        }
                        });
                        panel(new PanelBuilder("Bottom_Panel_Seven") {
                        {
                            childLayoutCenter();
                            width("3.25%");
                            height("100%");
                            //backgroundColor("#f002");
                            alignCenter();
                            valignCenter();

                        }
                        });
                        panel(new PanelBuilder("Bottom_Ctrl_Ability_Panel") {
                        {
                            childLayoutCenter();
                            width("11.75%");
                            height("100%");
                            //backgroundColor("#f001");
                            alignCenter();
                            valignCenter();

                            text(new TextBuilder() 
                            {{
                                id("Ctrl_Ability");
                                childLayoutHorizontal();
                                alignRight();
                                text("Ctrl Ability");
                                font("Interface/Fonts/Default.fnt");
                                //wrap(true);
                                height("100%");
                                width("100%");
                            }});

                        }
                        });
                        panel(new PanelBuilder("Bottom_Panel_Nine") {
                        {
                            childLayoutCenter();
                            width("3.6%");
                            height("100%");
                           //backgroundColor("#00f8");
                            alignCenter();
                            valignCenter();

                        }
                        });
                        panel(new PanelBuilder("Bottom_Panel_Ten") {
                        {
                            childLayoutCenter();
                            width("11.75%");
                            height("100%");
                            //backgroundColor("#00f5");
                            alignCenter();
                            valignCenter();

                            text(new TextBuilder() 
                            {{
                                id("Shift_Ability");
                                childLayoutHorizontal();
                                alignRight();
                                text("Shift Ability");
                                font("Interface/Fonts/Default.fnt");
                                //wrap(true);
                                height("100%");
                                width("100%");
                            }});

                        }
                        });


                        }}); // panel added
                }});

        }}.build(nifty));


            ////////////////////////////////////////////////////////
            // Options</screen>
            ////////////////////////////////////////////////////////

            nifty.addScreen("OptionsScreen", new ScreenBuilder("OptionsScreen") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                backgroundColor("#000f");
                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/options.png");
                }});
            }});

            layer(new LayerBuilder("foreground") {{
                childLayoutVertical();
                backgroundColor("#0000");

                 panel(new PanelBuilder("panel_One") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");

    //                text(new TextBuilder() {{
    //                    text("Options");
    //                    font("Interface/Fonts/Default.fnt");
    //                    wrap(true);
    //                    height("100%");
    //                    width("100%");
    //                 }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f08");
                    height("25%");
                    width("100%");

                    control(new ButtonBuilder("GuideButton", "Guide") {{
                          alignCenter();
                          valignBottom();
                          height("25%");
                          width("25%");
                          visibleToMouse(true);
                          interactOnClick("goToScreen(GuideScreen)");
                        }});

                }});
                panel(new PanelBuilder("panel_Three") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f09");
                    height("25%");
                    width("100%");

                    control(new ButtonBuilder("ControlsButton", "Controls") {{
                      alignCenter();
                      valignCenter();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);
                      interactOnClick("goToScreen(ControlsScreen)");
                        }});

                }});

                panel(new PanelBuilder("panel_Four") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#00f8");
                    height("25%");
                    width("100%");


                    control(new ButtonBuilder("BackButton", "Back") {{
                      alignCenter();
                      valignTop();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);
                      interactOnClick("goToScreen(start)");
                        }});

                }}); // panel added
            }});
        }}.build(nifty));


            ////////////////////////////////////////////////////////
            //Controls</screen>
            ////////////////////////////////////////////////////////

            nifty.addScreen("ControlsScreen", new ScreenBuilder("ControlsScreen") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                backgroundColor("#000f");
                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/controls.png");
                }});
            }});

            layer(new LayerBuilder("foreground") {{
                childLayoutVertical();
                backgroundColor("#0000");

                 panel(new PanelBuilder("panel_One") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");

    //                text(new TextBuilder() {{
    //                    text("Controls");
    //                    font("Interface/Fonts/Default.fnt");
    //                    wrap(true);
    //                    height("100%");
    //                    width("100%");
    //                 }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                   // backgroundColor("#0f08");
                    height("50%");
                    width("100%");

                    text(new TextBuilder() {{
                        text("W/Up Arrow - Jump \n"+
                             "A/Left Arrow - Move Left \n"+
                             "S/Down Arrow - Crouch \n"+
                             "D/Right Arrow - Move Right \n"+
                             "Spacebar - Shoot \n"+
                             "Shift/Control - Active Abilities \n");
                        font("Interface/Fonts/Default.fnt");
                        //wrap(true);
                        height("100%");
                        width("100%");
                    }});


                }});


                panel(new PanelBuilder("panel_Three") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#00f8");
                    height("25%");
                    width("100%");


                    control(new ButtonBuilder("BackButton", "Back") {{
                      alignCenter();
                      valignTop();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);

                      interactOnClick("goToScreen(OptionsScreen)");
                        }});

                }}); // panel added
            }});
        }}.build(nifty));

            ////////////////////////////////////////////////////////
            //Guide</screen>
            ////////////////////////////////////////////////////////

            nifty.addScreen("GuideScreen", new ScreenBuilder("GuideScreen") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                backgroundColor("#000f");
                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/guide.png");
                }});
            }});

            layer(new LayerBuilder("foreground") {{
                childLayoutVertical();
                backgroundColor("#0000");

                 panel(new PanelBuilder("panel_One") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");

    //                text(new TextBuilder() {{
    //                    text("Guide");
    //                    font("Interface/Fonts/Default.fnt");
    //                    wrap(true);
    //                    height("100%");
    //                    width("100%");
    //                 }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f08");
                    height("50%");
                    width("100%");

                    text(new TextBuilder() {{
                        text("You are a super soldier from the year 2015 and are unfrozen to save the world "
                                + "from the overwhelming mass of aliens in the year 3027. "
                                + "Your goal is to exterminate the aliens by using various weapons, skills, "
                                + "and upgrades found along the way. During the campaign, you will "
                                + "encounter different enemies that will drop ammo and health pickups to assist you with your survivability. ");
                        
                        font("Interface/Fonts/Default.fnt");
                        wrap(true);
                        height("100%");
                        width("100%");
                    }});


                }});


                panel(new PanelBuilder("panel_Three") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#00f8");
                    height("25%");
                    width("100%");


                    control(new ButtonBuilder("BackButton", "Back") {{
                      alignCenter();
                      valignTop();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);

                      interactOnClick("goToScreen(OptionsScreen)");
                        }});

                }}); // panel added
            }});
        }}.build(nifty));
            
        ////////////////////////////////////////////////////////
        //Death</screen>
        ////////////////////////////////////////////////////////

        nifty.addScreen("DeathScreen", new ScreenBuilder("DeathScreen") {{
        controller(new Juggernaut.StartScreen());

        layer(new LayerBuilder("background") {{
            childLayoutCenter();
            backgroundColor("#000f");
            // <!-- ... -->
            image(new ImageBuilder() {{
                filename("Interface/youlose.png");
            }});
        }});

        layer(new LayerBuilder("foreground") {{
            childLayoutVertical();
            backgroundColor("#0000");

             panel(new PanelBuilder("panel_One") {{
                childLayoutCenter();
                alignCenter();
                //backgroundColor("#f008");
                height("25%");
                width("100%");

//                text(new TextBuilder() {{
//                    text("Guide");
//                    font("Interface/Fonts/Default.fnt");
//                    wrap(true);
//                    height("100%");
//                    width("100%");
//                 }});
            }});

            panel(new PanelBuilder("panel_Two") {{
                childLayoutCenter();
                alignCenter();
                //backgroundColor("#0f08");
                height("50%");
                width("100%");

//                    text(new TextBuilder() {{
//                        text("You are a super soldier from the year 2015 and are unfrozen to save the world "
//                                + "from the overwhelming mass of aliens in the year 3027. "
//                                + "Your goal is to exterminate the aliens by using various weapons, skills, "
//                                + "and upgrades found along the way. During the campaign, you will "
//                                + "encounter different enemies that will drop ammo and health pickups to assist you with your survivability. ");
//                        
//                        font("Interface/Fonts/Default.fnt");
//                        wrap(true);
//                        height("100%");
//                        width("100%");
//                    }});


            }});


            panel(new PanelBuilder("panel_Three") {{
                childLayoutCenter();
                alignCenter();
                //backgroundColor("#00f8");
                height("25%");
                width("100%");


//                    control(new ButtonBuilder("BackButton", "Back") {{
//                      alignCenter();
//                      valignTop();
//                      height("25%");
//                      width("25%");
//                      visibleToMouse(true);
//
//                      interactOnClick("goToScreen(OptionsScreen)");
//                        }});

            }}); // panel added
        }});
        }}.build(nifty));
        ////////////////////////////////////////////////////////
        //Guide</screen>
        ////////////////////////////////////////////////////////

        nifty.addScreen("WinScreen", new ScreenBuilder("WinScreen") {{
        controller(new Juggernaut.StartScreen());

        layer(new LayerBuilder("background") {{
            childLayoutCenter();
            backgroundColor("#000f");
            // <!-- ... -->
            image(new ImageBuilder() {{
                filename("Interface/youwin.png");
            }});
        }});

        layer(new LayerBuilder("foreground") {{
            childLayoutVertical();
            backgroundColor("#0000");

             panel(new PanelBuilder("panel_One") {{
                childLayoutCenter();
                alignCenter();
                //backgroundColor("#f008");
                height("25%");
                width("100%");

//                text(new TextBuilder() {{
//                    text("Guide");
//                    font("Interface/Fonts/Default.fnt");
//                    wrap(true);
//                    height("100%");
//                    width("100%");
//                 }});
            }});

            panel(new PanelBuilder("panel_Two") {{
                childLayoutCenter();
                alignCenter();
                //backgroundColor("#0f08");
                height("50%");
                width("100%");

//                    text(new TextBuilder() {{
//                        text("You are a super soldier from the year 2015 and are unfrozen to save the world "
//                                + "from the overwhelming mass of aliens in the year 3027. "
//                                + "Your goal is to exterminate the aliens by using various weapons, skills, "
//                                + "and upgrades found along the way. During the campaign, you will "
//                                + "encounter different enemies that will drop ammo and health pickups to assist you with your survivability. ");
//                        
//                        font("Interface/Fonts/Default.fnt");
//                        wrap(true);
//                        height("100%");
//                        width("100%");
//                    }});


            }});


            panel(new PanelBuilder("panel_Three") {{
                childLayoutCenter();
                alignCenter();
                //backgroundColor("#00f8");
                height("25%");
                width("100%");


//                    control(new ButtonBuilder("BackButton", "Back") {{
//                      alignCenter();
//                      valignTop();
//                      height("25%");
//                      width("25%");
//                      visibleToMouse(true);
//
//                      interactOnClick("goToScreen(OptionsScreen)");
//                        }});

            }}); // panel added
        }});
        }}.build(nifty));
            

        flyCam.setEnabled(false);
        nifty.gotoScreen("start"); // start the screen
        //nifty.setDebugOptionPanelColors(true);


    }
    ////////////////////////////////////////////////////////////////////////////
    //Registers Main Store popup
    ////////////////////////////////////////////////////////////////////////////
    public void registerStorePopup(Nifty nifty) 
    {
        new PopupBuilder("storePopup") {
        {


            childLayoutCenter();
            backgroundColor("#000a");

            panel(new PanelBuilder("Store") {
            {
                childLayoutVertical();
                width("75%");
                height("75%");
                //backgroundColor("#eeee");
                alignCenter();
                valignCenter();
                //style("nifty-panel-red");
                backgroundImage("Interface/store.png");
                //image("Interface/options.png");
    //            image(new ImageBuilder() 
    //            {{
    //                filename("Interface/options.png");
    //                width("100%");
    //                height("100%");
    //                
    //            }});

                panel(new PanelBuilder("Store_Panel_One") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#0f08");
                    alignCenter();
                    valignTop();

                }
                });
                panel(new PanelBuilder("Store_Panel_Two") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("50%");
                    //backgroundColor("#0f05");
                    alignCenter();
                    valignTop();

                    control(new ButtonBuilder("StoreTabButton", "Store") 
                    {{
                        alignCenter();
                        valignCenter();
                        height("12.5%");
                        width("25%");
                        visibleToMouse(true);

                        interactOnClick("ShowStoreTabPopup()");
                    }});

                }
                });
                panel(new PanelBuilder("Store_Panel_Three") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignCenter();
                    
                    control(new ButtonBuilder("Store_ExitButton", "Exit") 
                    {{
                        alignCenter();
                        valignCenter();
                        height("25%");
                        width("25%");
                        visibleToMouse(true);

                        interactOnClick("CloseStorePopup()");
                    }});



                }
                });
            }
            });
          }
        }.registerPopup(nifty);
    }
    ////////////////////////////////////////////////////////////////////////////
    //Register Store Tab
    ////////////////////////////////////////////////////////////////////////////
    public void registerStoreTab(Nifty nifty) 
    {
        
        new PopupBuilder("storeTabPopup") {
        {


            childLayoutCenter();
            backgroundColor("#000a");

            panel(new PanelBuilder("StoreTab") {
            {
                childLayoutVertical();
                width("75%");
                height("75%");
                //backgroundColor("#eeee");
                alignCenter();
                valignCenter();
                backgroundImage("Interface/store.png");

    //            image(new ImageBuilder() 
    //            {{
    //                filename("Interface/options.png");
    //                width("100%");
    //                height("100%");
    //                
    //            }});

                panel(new PanelBuilder("Store_Tab_Panel_One") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("15%");
                    //backgroundColor("#0f08");
                    alignCenter();
                    valignTop();

                }
                });
                panel(new PanelBuilder("Store_Tab_Panel_Two") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("25%");
                    //backgroundColor("#0f05");
                    alignCenter();
                    valignCenter();
                    
                    panel(new PanelBuilder("Health_Panel") {
                    {
                        childLayoutCenter();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#00f8");
                        alignCenter();
                        valignCenter();

                        image(new ImageBuilder() 
                        {{
                            filename("Interface/Health.png");
                            valignCenter();
                            alignCenter();
                            height("50%");
                            width("30%");
                        }});



                    }
                    });
                    panel(new PanelBuilder("Buy_Health_Panel") {
                    {
                        childLayoutCenter();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();

                        control(new ButtonBuilder("Buy_Health_Button", "Buy Health") 
                        {{
                            id("buyHealthButton");
                            alignCenter();
                            valignCenter();
                            height("50%");
                            width("50%");
                            visibleToMouse(true);

                            interactOnClick("BuyHealth()");
                        }});
                   



                    }
                    });
                    panel(new PanelBuilder("Current_Health_Panel") {
                    {
                        childLayoutHorizontal();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#f005");
                        alignCenter();
                        valignCenter();

                        text(new TextBuilder() 
                        {{
                            //System.out.print(currentHealth + "\n");
//                            String Current_Health;
//                            Current_Health = String.valueOf(currentHealth);
                           
                            //backgroundColor("#f008");
                            alignLeft();
                            valignCenter();
                            id("Current_Health");
                            
                            text((int)Character.currentHealth + "");
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("50%");
                            width("47.5%");
                        }});
                        text(new TextBuilder() 
                        {{ 
                            alignCenter();
                            valignCenter();
                            text("/");
                            font("Interface/Fonts/Default.fnt");
                            wrap(true);
                            height("50%");
                            width("5%");
                        }});
                        text(new TextBuilder() 
                        {{
//                            String Max_Health;
//                            Max_Health = String.valueOf(maxHealth);
                            alignRight();
                            valignCenter();
                            id("Max_Health");
                            text((int)Character.maxHealth +"");
                            font("Interface/Fonts/Default.fnt");
                            wrap(true);
                            height("50%");
                            width("47.5%");
                        }});
                        




                    }
                    });



                }
                });
                panel(new PanelBuilder("Store_Tab_Panel_Three") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("25%");
                    //backgroundColor("#f008");
                    alignCenter();
                    valignTop();
                    
                    panel(new PanelBuilder("MiniGun_Ammo_Panel") {
                    {
                        childLayoutCenter();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#00f8");
                        alignCenter();
                        valignCenter();

                        image(new ImageBuilder() 
                        {{
                            
                            filename("Interface/miniGun.png");
                            valignCenter();
                            alignCenter();
                            height("75%");
                            width("50%");
                        }});



                    }
                    });
                    panel(new PanelBuilder("Buy_MiniGun_Ammo_Panel") {
                    {
                        childLayoutCenter();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();

                        control(new ButtonBuilder("Buy_MiniGun_Ammo_Button", "Buy MiniGun Ammo") 
                        {{
                            alignCenter();
                            valignCenter();
                            height("50%");
                            width("50%");
                            visibleToMouse(true);

                            interactOnClick("BuyMiniGunAmmo()");
                        }});
                   



                    }
                    });
                    panel(new PanelBuilder("Current_MiniGun__Ammo_Panel") {
                    {
                        childLayoutHorizontal();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#f005");
                        alignCenter();
                        valignCenter();

                        text(new TextBuilder() 
                        {{
                            //backgroundColor("#f008");
                            alignLeft();
                            valignCenter();
                            id("MiniGun_Current_Ammo");
                            text(Juggernaut.WeaponSlot2().currentAmmo + "");
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("50%");
                            width("47.5%");
                        }});
                        text(new TextBuilder() 
                        {{ 
                            alignCenter();
                            valignCenter();
                            text("/");
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("50%");
                            width("5%");
                        }});
                        text(new TextBuilder() 
                        {{
                            alignRight();
                            valignCenter();
                            id("MiniGun_Max_Ammo");
                            text(Juggernaut.WeaponSlot2().maxAmmo + "");
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("50%");
                            width("47.5%");
                        }});
                        




                    }
                    });



                }
                });
                panel(new PanelBuilder("Store_Tab_Panel_Four") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("25%");
                    //backgroundColor("#f008");
                    alignCenter();
                    valignTop();
                    
                    panel(new PanelBuilder("LaserGun_Ammo_Panel") {
                    {
                        childLayoutCenter();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#00f8");
                        alignCenter();
                        valignCenter();

                        image(new ImageBuilder() 
                        {{
                            filename("Interface/Cannon.png");
                            valignCenter();
                            alignCenter();
                            height("75%");
                            width("50%");
                        }});



                    }
                    });
                    panel(new PanelBuilder("Buy_LaserGun_Ammo_Panel") {
                    {
                        childLayoutCenter();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();

                        control(new ButtonBuilder("Buy_LaserGun_Ammo_Button", "Buy Laser Rifle Ammo") 
                        {{
                            alignCenter();
                            valignCenter();
                            height("50%");
                            width("50%");
                            visibleToMouse(true);

                            interactOnClick("BuyLaserRifleAmmo()");
                        }});
                   



                    }
                    });
                    panel(new PanelBuilder("Current_LaserGun_Ammo_Panel") {
                    {
                        childLayoutHorizontal();
                        width("33.3%");
                        height("100%");
                        //backgroundColor("#f005");
                        alignCenter();
                        valignCenter();

                        text(new TextBuilder() 
                        {{
                            //backgroundColor("#f008");
                            alignLeft();
                            valignCenter();
                            id("LaserGun_Current_Ammo");
                            text(Juggernaut.WeaponSlot3().currentAmmo + "" );
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("50%");
                            width("47.5%");
                        }});
                        text(new TextBuilder() 
                        {{ 
                            alignCenter();
                            valignCenter();
                            text("/");
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("50%");
                            width("5%");
                        }});
                        text(new TextBuilder() 
                        {{
                            alignRight();
                            valignCenter();
                            id("LaserGun_Max_Ammo");
                            text(Juggernaut.WeaponSlot3().maxAmmo +"");
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("50%");
                            width("47.5%");
                        }});
                        




                    }
                    });



                }
                });
                panel(new PanelBuilder("Store_Tab_Panel_Five") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("10%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignTop();

                    control(new ButtonBuilder("Store_Tab_ExitButton", "Exit") 
                    {{
                        alignCenter();
                        valignCenter();
                        height("50%");
                        width("25%");
                        visibleToMouse(true);

                        interactOnClick("CloseStoreTabPopup()");
                    }});


                }
                });
            }
            });
          }
        }.registerPopup(nifty);
    }
    ////////////////////////////////////////////////////////////////////////////
    //Register Skills popup
    ////////////////////////////////////////////////////////////////////////////
    public void registerSkillsTab(Nifty nifty) 
    {
        new PopupBuilder("SkillsPopup") {
        {


            childLayoutCenter();
            backgroundColor("#000a");

            panel(new PanelBuilder("SkillTab") {
            {
                childLayoutVertical();
                width("75%");
                height("75%");
                //backgroundColor("#eeee");
                alignCenter();
                valignCenter();
                backgroundImage("Interface/skills.png");


                panel(new PanelBuilder("Skills_Panel_One") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#0f08");
                    alignCenter();
                    valignTop();


                }
                });
                panel(new PanelBuilder("Skills_Panel_Two") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#0f05");
                    alignCenter();
                    valignCenter();
                    
                    control(new ButtonBuilder("Attributes_Button", "Attributes") 
                    {{
                        alignCenter();
                        valignCenter();
                        height("25%");
                        width("25%");
                        visibleToMouse(true);

                        interactOnClick("ShowAttributesPopup()");
                    }});



                }
                });
                panel(new PanelBuilder("Skills_Panel_Three") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#f008");
                    alignCenter();
                    valignCenter();
                    
                    control(new ButtonBuilder("Abilities_Button", "Abilities") 
                    {{
                        alignCenter();
                        valignCenter();
                        height("25%");
                        width("25%");
                        visibleToMouse(true);

                        interactOnClick("ShowAbilitiesPopup()");
                    }});



                }
                });
                panel(new PanelBuilder("Skills_Panel_Four") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignCenter();

                    control(new ButtonBuilder("Skills_Tab_BackButton", "Back") 
                    {{
                        alignCenter();
                        valignCenter();
                        height("25%");
                        width("25%");
                        visibleToMouse(true);

                        interactOnClick("CloseSkillsPopup()");
                    }});


                }
                });
            }
            });
          }
        }.registerPopup(nifty);
    }
    ////////////////////////////////////////////////////////////////////////////
    //Register Attributes popup
    ////////////////////////////////////////////////////////////////////////////
    public void registerAttributesTab(Nifty nifty) 
    {
        new PopupBuilder("AttributesPopup") {
        {


            childLayoutCenter();
            backgroundColor("#000a");

            panel(new PanelBuilder("AttributesTab") {
            {
                childLayoutVertical();
                width("75%");
                height("75%");
                //backgroundColor("#eeee");
                alignCenter();
                valignCenter();
                backgroundImage("Interface/attributes.png");


                panel(new PanelBuilder("Attributes_Points_Panel") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#0f08");
                    alignCenter();
                    valignBottom();

                    text(new TextBuilder() 
                    {{
                        alignCenter();
                        valignBottom();
                        text("Attribute Points: " + (int)Character.attributePoints);
                        font("Interface/Fonts/Default.fnt");
                        //wrap(true);
                        height("100%");
                        width("100%");
                    }});

                }
                });
                panel(new PanelBuilder("Health_Attributes_Panel") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("12.5%");
                    //backgroundColor("#0f05");
                    alignCenter();
                    valignCenter();
                    
                    panel(new PanelBuilder("Health_Left_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                    }
                    });
                    panel(new PanelBuilder("Current_Health_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            id("CurrentMaxHealth");
                            alignCenter();
                            valignCenter();
                            text("Max Health: " + (int)Character.maxHealth);
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Health_Attribute_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f08");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            //color(red);
                            alignCenter();
                            valignCenter();
                            text("Health");
                            
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Health_Attribute_Button_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        control(new ButtonBuilder("Increase_Health_Button", "+") 
                        {{
                            alignCenter();
                            valignCenter();
                            
                            height("25%");
                            width("");
                            visibleToMouse(true);

                            //interactOnClick("CloseStoreTabPopup()");
                        }});
                        
                    }
                    });
                     panel(new PanelBuilder("Health_Right_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });



                }
                });
                panel(new PanelBuilder("Damage_Attributes_Panel") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("12.5%");
                    //backgroundColor("#f008");
                    alignCenter();
                    valignCenter();
                    
                    panel(new PanelBuilder("Damage_Left_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });
                    panel(new PanelBuilder("Current_Damage_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            id("CurrentDamageModifier");
                            alignCenter();
                            valignCenter();
                            text("Damage: " +  "x" + Character.damageModifier);
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Damage_Attribute_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            //color(orange);
                            alignCenter();
                            valignCenter();
                            text("Damage");
                            
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Damage_Attribute_Button_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        control(new ButtonBuilder("Increase_Damage_Button", "+") 
                        {{
                            alignCenter();
                            valignCenter();
                            
                            height("25%");
                            width("");
                            visibleToMouse(true);

                            //interactOnClick("CloseStoreTabPopup()");
                        }});
                        
                    }
                    });
                     panel(new PanelBuilder("Damage_Right_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });



                }
                });
                panel(new PanelBuilder("FireRate_Attributes_Panel") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("12.5%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignCenter();
                    
                    panel(new PanelBuilder("FireRate_Left_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });
                    panel(new PanelBuilder("Current_FireRate_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            id("CurrentFireRateModifier");
                            alignCenter();
                            valignCenter();
                            text("Fire Rate: "+ (int)Juggernaut.WeaponSlot2().fireRate);
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("FireRate_Attribute_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            //color(green);
                            alignCenter();
                            valignCenter();
                            text("Fire Rate" );
                            
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("FireRate_Attribute_Button_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        control(new ButtonBuilder("Increase_FireRate_Button", "+") 
                        {{
                            alignCenter();
                            valignCenter();
                            
                            height("25%");
                            width("");
                            visibleToMouse(true);

                            //interactOnClick("CloseStoreTabPopup()");
                        }});
                        
                    }
                    });
                     panel(new PanelBuilder("FireRate_Right_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });



                }
                });
                panel(new PanelBuilder("Speed_Attributes_Panel") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("12.5%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignCenter();
                    
                    panel(new PanelBuilder("Speed_Left_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });
                    panel(new PanelBuilder("Current_Speed_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            id("CurrentSpeedModifier");
                            alignCenter();
                            valignCenter();
                            text("Speed: " + (int)Character.movementSpeed);
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Speed_Attribute_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            //color(yellow);
                            alignCenter();
                            valignCenter();
                            text("Speed");
                            
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Speed_Attribute_Button_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        control(new ButtonBuilder("Increase_Speed_Button", "+") 
                        {{
                            alignCenter();
                            valignCenter();
                            
                            height("25%");
                            width("");
                            visibleToMouse(true);

                            //interactOnClick("CloseStoreTabPopup()");
                        }});
                        
                    }
                    });
                     panel(new PanelBuilder("Speed_Right_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });

                    


                }
                });
                panel(new PanelBuilder("Attributes_Panel_Six") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignCenter();

                    control(new ButtonBuilder("Attributes_Tab_BackButton", "Back") 
                    {{
                        alignCenter();
                        valignCenter();
                        height("25%");
                        width("25%");
                        visibleToMouse(true);

                        interactOnClick("CloseAttributesPopup()");
                    }});


                }
                });
            }
            });
          }
        }.registerPopup(nifty);
    }
    ////////////////////////////////////////////////////////////////////////////
    //Register Abilities popup
    ////////////////////////////////////////////////////////////////////////////
    public void registerAbilitiesTab(Nifty nifty) 
    {
        new PopupBuilder("AbilitiesPopup") {
        {


            childLayoutCenter();
            backgroundColor("#000a");

            panel(new PanelBuilder("AbilitiesTab") {
            {
                childLayoutVertical();
                width("75%");
                height("75%");
                //backgroundColor("#eeee");
                alignCenter();
                valignCenter();
                backgroundImage("Interface/abilities.png");


                panel(new PanelBuilder("Abilities_Point_Panel") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#0f08");
                    alignCenter();
                    valignCenter();

                    text(new TextBuilder() 
                    {{
                        alignCenter();
                        valignCenter();
                        text("Ability Points: " + (int)Character.abiltyPoints);
                        font("Interface/Fonts/Default.fnt");
                        //wrap(true);
                        height("100%");
                        width("100%");
                    }});

                }
                });
                panel(new PanelBuilder("Sprint_Abilities_Panel") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("12.5%");
                    //backgroundColor("#0f05");
                    alignCenter();
                    valignTop();
                    
                    panel(new PanelBuilder("Sprint_Left_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });
                    panel(new PanelBuilder("Sprint_Skill_Level_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            id("SprintSkillLevel");
                            alignCenter();
                            valignCenter();
                            text("Current Skill Level: " + Character.sprintSkillLevel);
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Sprint_Attribute_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            //color(yellow);
                            alignCenter();
                            valignCenter();
                            text("Sprint");
                            
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Sprint_Attribute_Button_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        control(new ButtonBuilder("Increase_Sprint_Button", "+") 
                        {{
                            alignCenter();
                            valignCenter();
                            
                            height("25%");
                            width("");
                            visibleToMouse(true);

                            //interactOnClick("CloseStoreTabPopup()");
                        }});
                        
                    }
                    });
                     panel(new PanelBuilder("Sprint_Right_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });



                }
                });
                panel(new PanelBuilder("Dash_Abilities_Panel") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("12.5%");
                    //backgroundColor("#f008");
                    alignCenter();
                    valignTop();
                    
                    panel(new PanelBuilder("Dash_Left_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });
                    panel(new PanelBuilder("Dash_Skill_Level_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            id("DashSkillLevel");
                            alignCenter();
                            valignCenter();
                            text("Current Skill Level: " + Character.dashSkillLevel);
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Dash_Attribute_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                       //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            //color(orange);
                            alignCenter();
                            valignCenter();
                            text("Dash");
                            
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Dash_Attribute_Button_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        control(new ButtonBuilder("Increase_Dash_Button", "+") 
                        {{
                            alignCenter();
                            valignCenter();
                            
                            height("25%");
                            width("");
                            visibleToMouse(true);

                            //interactOnClick("CloseStoreTabPopup()");
                        }});
                        
                    }
                    });
                     panel(new PanelBuilder("Dash_Right_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });



                }
                });
                panel(new PanelBuilder("SuperJump_Abilities_Panel") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("12.5%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignTop();

                    panel(new PanelBuilder("SuperJump_Left_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });
                    panel(new PanelBuilder("SuperJump_Skill_Level_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            id("SuperJumpSkillLevel");
                            alignCenter();
                            valignCenter();
                            text("Current Skill Level: " + Character.superJumpSkillLevel);
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("SuperJump_Attribute_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            //color(red);
                            alignCenter();
                            valignCenter();
                            text("Super Jump");
                            
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("SuperJump_Attribute_Button_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        control(new ButtonBuilder("Increase_SuperJump_Button", "+") 
                        {{
                            alignCenter();
                            valignCenter();
                            
                            height("25%");
                            width("");
                            visibleToMouse(true);

                            //interactOnClick("CloseStoreTabPopup()");
                        }});
                        
                    }
                    });
                     panel(new PanelBuilder("SuperJump_Right_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });


                }
                });
                panel(new PanelBuilder("Hover_Abilities_Panel") {
                {
                    childLayoutHorizontal();
                    width("100%");
                    height("12.5%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignTop();

                    
                    panel(new PanelBuilder("Hover_Left_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });
                    panel(new PanelBuilder("Hover_Skill_Level_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            id("HoverSkillLevel");
                            alignCenter();
                            valignCenter();
                            text("Current Skill Level: " + Character.hoverSkillLevel);
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Hover_Abilitie_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        text(new TextBuilder() 
                        {{
                            //color(blue);
                            alignCenter();
                            valignCenter();
                            text("Hover");
                            
                            font("Interface/Fonts/Default.fnt");
                            //wrap(true);
                            height("100%");
                            width("100%");
                        }});
                        
                    }
                    });
                    panel(new PanelBuilder("Hover_Abilities_Button_Panel") {
                    {
                        childLayoutCenter();
                        width("12.5%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignCenter();
                        
                        control(new ButtonBuilder("Increase_Hover_Button", "+") 
                        {{
                            alignCenter();
                            valignCenter();
                            
                            height("25%");
                            width("");
                            visibleToMouse(true);

                            //interactOnClick("CloseStoreTabPopup()");
                        }});
                        
                    }
                    });
                     panel(new PanelBuilder("Hover_Right_Spacer_Panel") {
                    {
                        childLayoutCenter();
                        width("25%");
                        height("100%");
                        //backgroundColor("#0f05");
                        alignCenter();
                        valignTop();
                    }
                    });
                   


                }
                });
                panel(new PanelBuilder("Abilities_Panel_Six") {
                {
                    childLayoutCenter();
                    width("100%");
                    height("25%");
                    //backgroundColor("#f005");
                    alignCenter();
                    valignTop();

                    control(new ButtonBuilder("Abilities_Tab_BackButton", "Back") 
                    {{
                        alignCenter();
                        valignCenter();
                        height("25%");
                        width("25%");
                        visibleToMouse(true);

                        interactOnClick("CloseAbilitiesPopup()");
                    }});


                }
                });
            }
            });
          }
        }.registerPopup(nifty);
    }
    public StartScreen getHud(){
        return start;
    }
    public Nifty getNifty(){
        return nifty;
    }
}
