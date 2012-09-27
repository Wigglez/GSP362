/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import java.util.List;

/**
 *
 * @author Vince
 */
public class CameraChunk {
    private GhostControl ghost; 
    private Vector3f position;
    private Vector3f camLookAt;
    private Vector3f camPosition;
    private Vector3f camSetBack = new Vector3f( 0, 4, 40);

    CameraChunk() {
    }
    
    CameraChunk(Vector3f position, Node rootNode,BulletAppState bulletAppState ){
        this.position = position;
        
        ghost = new GhostControl(new BoxCollisionShape( new Vector3f(30,12,5)));
       
        Node view = new Node("view");
        view.setLocalTranslation(this.position);
        view.addControl(ghost);
        rootNode.attachChild(view);
        bulletAppState.getPhysicsSpace().add(ghost);
        ghost.setCollisionGroup(2);
        ghost.removeCollideWithGroup(1);
        
        camLookAt = this.position;
         camPosition = this.position.add(camSetBack); 
    }
    
    Vector3f getPosition(){
        return position;
    }
    
    Vector3f CamLookAt(){
        return camLookAt;
    }
    
    Vector3f CamPosition(){
        return camPosition;
    }
    
    boolean testForPlayer(Character Juggernaut)
    {
        List objects = ghost.getOverlappingObjects();
        
        if(objects.contains(Juggernaut)){
            return true;
        }
            
        
        return false;       
        
    }
}
