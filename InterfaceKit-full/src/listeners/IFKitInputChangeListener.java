/* - IFKitInputChangeListener -
 * Here we check or uncheck the corresponding input checkbox based on the
 * index of the digital input that generated the event
 *
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package listeners;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.InputChangeListener;
import com.phidgets.event.InputChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class IFKitInputChangeListener implements InputChangeListener{
    
    private JFrame appFrame;
    private JCheckBox digiInArray[];
    
    /**
     * Creates a new instance of IFKitInputChangeListener
     */
    public IFKitInputChangeListener(JFrame appFrame, JCheckBox digiInArray[])
    {
        this.appFrame = appFrame;
        this.digiInArray = digiInArray;
    }

    public void inputChanged(InputChangeEvent inputChangeEvent)
    {
        digiInArray[inputChangeEvent.getIndex()].setSelected(inputChangeEvent.getState());
    }
    
}
