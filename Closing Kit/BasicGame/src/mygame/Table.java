/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 *
 * @author Jordon
 */
public class Table extends JPanel
{
    JTable table;
    //float score;
    
    
     public Table()
    {
  
        
        String[] colums = {"Name","","Score"};
        
        String[][] row = {{"bob", "", "1000"},
                          {"bob", "", "100"},
                          {"bob", "", "10000"},
                          {"bob", "", "10000"},
                          {"bob", "", "10000"}};
        
        table = new JTable(row,colums)
        {
          public boolean isCellEditable(int row, int colums)
          {
              return false;
          }
          public Component prepareRenderer(TableCellRenderer renderer, int row, int colums)
          {
              Component component = super.prepareRenderer(renderer, row, colums);
              
              if(row %2 == 0)
              {
                  component.setBackground(Color.WHITE);
                  
              }
              else
              {
                  component.setBackground(Color.LIGHT_GRAY);
              }
              
              return component;
          }
        };
        table.setPreferredScrollableViewportSize(new Dimension(450,63));
        table.setFillsViewportHeight(true);
        
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        
        
        
    }
//   public static void main(String[] args)
//   {
//       JFrame jf = new JFrame();
//       Table table = new Table();
//       
//       jf.setTitle("Title");
//       jf.setSize(500,500);
//       jf.setVisible(true);
//       jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//       jf.add(table);
//   }
   
}
