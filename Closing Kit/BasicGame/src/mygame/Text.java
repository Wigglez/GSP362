/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author Jordon
 */

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;



public class Text extends SimpleApplication{
    
  
    @Override
    public void simpleInitApp() 
    {
            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            BitmapText Text = new BitmapText(guiFont, false);
            Text.setColor(ColorRGBA.Blue);
            Text.setSize(guiFont.getCharSet().getRenderedSize());
            Text.setText("Hello World");
            Text.setLocalTranslation(300,Text.getLineHeight(), 0);
            guiNode.attachChild(Text);
                        
    }
    
//    public void UserGuide()
//    {
////            System.out.println("Hello");
//          
//    }
    
}
