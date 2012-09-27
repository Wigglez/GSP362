package jme3test.helloworld;
 
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

// Sample 4 - the game loop

public class tutorial4_HelloLoop extends SimpleApplication{
    
    public static void main(String[] args){
        tutorial4_HelloLoop app = new tutorial4_HelloLoop();
        app.start();
    }
    
    protected Geometry player;
    
    @Override
    public void simpleInitApp(){
        
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        player = new Geometry("blue cube", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        rootNode.attachChild(player);
    }
    
    @Override
    public void simpleUpdate(float tpf){
        player.rotate( 1.5f * tpf, 2* tpf, 0);
        
    }
}