/* - IFKitAttachListener -
 * Here we'll display the interface kit details as well as determine how many output
 * and input fields to display as well as determine the range of values for 
 * the output simulator slider
 *
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package listeners;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachListener;
import com.phidgets.event.AttachEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class IFKitAttachListener implements AttachListener {
    
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
    
    /** Creates a new instance of IFKitAttachListener */
    public IFKitAttachListener(JFrame appFrame, JTextField attachedTxt, JTextArea nameTxt,
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

    public void attached(AttachEvent ae)
    {
        try {
            InterfaceKitPhidget attached = (InterfaceKitPhidget)ae.getSource();
            attachedTxt.setText(Boolean.toString(attached.isAttached()));
            nameTxt.setText(attached.getDeviceName());
            serialTxt.setText(Integer.toString(attached.getSerialNumber()));
            versionTxt.setText(Integer.toString(attached.getDeviceVersion()));
            numDigiInTxt.setText(Integer.toString(attached.getInputCount()));
            numDigiOutTxt.setText(Integer.toString(attached.getOutputCount()));
            numSensorTxt.setText(Integer.toString(attached.getSensorCount()));
            
            int i;
            
            for(i = 0; i < attached.getInputCount(); i++)
            {
                digiInArray[i].setVisible(true);
            }
            
            for(i = 0; i < attached.getOutputCount(); i++)
            {
                digiOutArray[i].setVisible(true);
                digiOutArray[i].setSelected(false);
                digiOutArray[i].setEnabled(true);
            }
            
            if(attached.getSensorCount() > 0)
            {
                for(i = 0; i < attached.getSensorCount(); i++)
                {
                    sensorInArray[i].setVisible(true);
                }
                
                jLabel8.setVisible(true);
                
                sensitivityScrl.setVisible(true);
                sensitivityScrl.setMaximum(1000);
                sensitivityScrl.setMinimum(0);
                sensitivityScrl.setValue(attached.getSensorChangeTrigger(0));
                sensitivityScrl.setEnabled(true);
                
                sensitivityTxt.setVisible(true);
                sensitivityTxt.setText(Integer.toString(sensitivityScrl.getValue()));
                
                ratioChk.setVisible(true);
                ratioChk.setEnabled(true);
                attached.setRatiometric(true);
                ratioChk.setSelected(attached.getRatiometric());
            }
            else
            {
                jLabel8.setVisible(false);
                sensitivityTxt.setVisible(false);
                sensitivityScrl.setEnabled(false);
                sensitivityScrl.setVisible(false);
                
                ratioChk.setVisible(false);
                ratioChk.setEnabled(false);
            }
            
        }
        catch (PhidgetException ex)
        {
            JOptionPane.showMessageDialog(appFrame, ex.getDescription(), "Phidget error " + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
