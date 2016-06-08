/* - IFKitOutputChangeListener - 
 * Here we check or uncheck the corresponding output checkbox based on the
 * index of the output that generated the event
 *
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package listeners;

import com.phidgets.event.OutputChangeListener;
import com.phidgets.event.OutputChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class IFKitOutputChangeListener implements OutputChangeListener{
    
    private JFrame appFrame;
    private JCheckBox digiOutArray[];
    
    /**
     * Creates a new instance of IFKitOutputChangeListener
     */
    public IFKitOutputChangeListener(JFrame appFrame, JCheckBox digiOutArray[])
    {
        this.appFrame = appFrame;
        this.digiOutArray = digiOutArray;
    }

    public void outputChanged(OutputChangeEvent outputChangeEvent)
    {
        digiOutArray[outputChangeEvent.getIndex()].setSelected(outputChangeEvent.getState());
    }
    
}
