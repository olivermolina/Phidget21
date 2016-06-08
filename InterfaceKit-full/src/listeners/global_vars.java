/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

/**
 *
 * @author moham
 */
public class global_vars {
// value_Read and value_Read2 come from the script  IFKitSensorChangeListener
// value_Read is the value returned by the DAC
public static int value_Read;
    
// Value Index is sensor Index can be 0 to 7
public static int value_Index;

// the following are variables just 
public static int scale=1;
public static int Yoffset;
public static int MaxX=900;

public static int Resolution_ms=300;
}
