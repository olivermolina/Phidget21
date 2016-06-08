/* - IFKitDetachListener - 
 * Here we display the status, which will be false as the device is not attached.
 * We will also clear the display fields and hide the inputs and outputs.
 *
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package listeners;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.DetachListener;
import com.phidgets.event.DetachEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class IFKitDetachListener implements DetachListener{
    
    private JFrame appFrame;
    private JTextField attachedTxt;
    private JTextArea nameTxt;
    private JTextField serialTxt;
    private JTextField versionTxt;
    private JTextField numDigiInTxt;
    private JTextField numDigiOutTxt;
    private JTextField numSensorTxt;
    private JCheckBox digiInArray[];
    private JCheckBox digiOutArray[];
    private JTextField sensorInArray[];
    private JCheckBox ratioChk;
    private JLabel jLabel8;
    private JTextField sensitivityTxt;
    private JSlider sensitivityScrl;
    
    /** Creates a new instance of IFKitDetachListener */
    public IFKitDetachListener(JFrame appFrame, JTextField attachedTxt, JTextArea nameTxt,
            JTextField serialTxt, JTextField versionTxt, JTextField numDigiInTxt, 
            JTextField numDigiOutTxt, JTextField numSensorTxt, 
            JCheckBox digiInArray[], JCheckBox digiOutArray[], JTextField sensorInArray[],
            JCheckBox ratioChk, JLabel jLabel8, JTextField sensitivityTxt, JSlider sensitivityScrl)
    {
        this.appFrame = appFrame;
        this.attachedTxt = attachedTxt;
        this.nameTxt = nameTxt;
        this.serialTxt = serialTxt;
        this.versionTxt = versionTxt;
        this.numDigiInTxt = numDigiInTxt;
        this.numDigiOutTxt = numDigiOutTxt;
        this.numSensorTxt = numSensorTxt;
        this.digiInArray = digiInArray;
        this.digiOutArray = digiOutArray;
        this.sensorInArray = sensorInArray;
        this.ratioChk = ratioChk;
        this.jLabel8 = jLabel8;
        this.sensitivityTxt = sensitivityTxt;
        this.sensitivityScrl = sensitivityScrl;
    }

    public void detached(DetachEvent ae)
    {
         try
         {
            InterfaceKitPhidget detached = (InterfaceKitPhidget)ae.getSource();
            attachedTxt.setText(Boolean.toString(detached.isAttached()));
            nameTxt.setText("");
            serialTxt.setText("");
            versionTxt.setText("");
            numDigiInTxt.setText("");
            numDigiOutTxt.setText("");
            numSensorTxt.setText("");
            
            int i;
            
            for(i = 0; i < 16; i++)
            {
                digiInArray[i].setVisible(false);
                digiInArray[i].setSelected(false);
            }
            
            for(i = 0; i < 16; i++)
            {
                digiOutArray[i].setEnabled(false);
                digiOutArray[i].setVisible(false);
                digiOutArray[i].setSelected(false);
            }
            
            for(i = 0; i < 8; i++)
            {
                sensorInArray[i].setVisible(false);
                sensorInArray[i].setText("");
            }
            
            jLabel8.setVisible(false);
            sensitivityTxt.setVisible(false);
            
            sensitivityScrl.setEnabled(false);
            sensitivityScrl.setVisible(false);
            sensitivityScrl.setValue(0);
            sensitivityTxt.setText("");

            ratioChk.setEnabled(false);
            ratioChk.setVisible(false);
            ratioChk.setSelected(false);
         }
         catch (PhidgetException ex)
         {
             JOptionPane.showMessageDialog(appFrame, ex.getDescription(), "Phidget error " + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
         }
    }
    
}
