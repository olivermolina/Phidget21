/* - IFKitSensorChangeListener - 
 * Set the textbox content based on the input index that is communicating
 * with the interface kit
 *Modified by Moh
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package listeners;

import com.phidgets.event.SensorChangeListener;
import com.phidgets.event.SensorChangeEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.io.*;
import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

/**
 *public class Test extends JFrame {
 * @author Owner
 */

public class IFKitSensorChangeListener implements SensorChangeListener{

    private JFrame appFrame;
    private JTextField sensorInArray[];
    
    
    /** Creates a new instance of IFKitSensorChangeListener */
    public IFKitSensorChangeListener(JFrame appFrame, JTextField sensorInArray[])
    {
        this.appFrame = appFrame;
        this.sensorInArray = sensorInArray;
    }

    public void sensorChanged(SensorChangeEvent sensorChangeEvent)
    {
      //global_vars.value_Index=0;
      //global_vars.value_Read=0;
      int i=0;
      File file_new = new File("VSWR.txt");

        sensorInArray[sensorChangeEvent.getIndex()].setText(Integer.toString(sensorChangeEvent.getValue()));
        global_vars.value_Index=sensorChangeEvent.getIndex();
       
       if (global_vars.value_Index==0)
        global_vars.value_Read=sensorChangeEvent.getValue();
        
         
      
        
        /*      getting date to get time in ms sine 1970 System.currentTimeMillis()*/
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.S");
        Date dateobj = new Date();
            
         
         String s = String.format("%s\t%d %d\n",df.format(dateobj),global_vars.value_Read,global_vars.value_Index);
         
        // System.out.printf ("%s",s);
        
         try 
         {
             
          PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file_new, true)));
          out.println(s);
        out.close();    
	} 
        catch (IOException e) {
			e.printStackTrace();
		}         
         
       
        
        
    }
   
}
  
 