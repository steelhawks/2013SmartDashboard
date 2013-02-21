/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.team2601.smartdashboard.extension;

/**
 *
 * @author priscilla
 */
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.ColorProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class TimerWidget extends StaticWidget implements ActionListener {
    public static final String NAME = "Match Timer";

    public final IntegerProperty startTimeAmt = new IntegerProperty(this, "Start Time", 120);
    public final IntegerProperty warningTime = new IntegerProperty(this, "Warning Time", 60);
    public final IntegerProperty warningTime2 = new IntegerProperty(this, "Warning2 Time", 30);
    public final IntegerProperty textSize = new IntegerProperty(this, "Text Size", 40);
    public final ColorProperty stopColor = new ColorProperty(this, "Not Running Color", Color.black);
    public final ColorProperty runColor = new ColorProperty(this, "Running Color", Color.green);
    public final ColorProperty alarmColor = new ColorProperty(this, "Warning Color", Color.ORANGE);
    public final ColorProperty alarmColor2 = new ColorProperty(this, "Warning2 Color", Color.red);
    public final ColorProperty finColor = new ColorProperty(this, "Finished Color", Color.blue);
    /*public final FileProperty alarmSound = new FileProperty (this, "Warning sound (wav)", "c:\\users\\priscilla\\downloads\\klaxon_a.wav");
    public final FileProperty alarmSound2 = new FileProperty (this, "Warning2 sound (wav)", "c:\\users\\priscilla\\downloads\\pacman2_10s.wav");
    public final FileProperty finSound = new FileProperty (this, "Finished sound (wav)", "c:\\users\\priscilla\\downloads\\at_tarzan.wav");
    */
    private final String filepath = new String("C:\\Program Files\\SmartDashboard\\extensions\\");
    public final StringProperty alarmSound = new StringProperty (this, "Warning Sound (WAV)", "at_tarzan.wav");
    public final StringProperty alarmSound2 = new StringProperty (this,"Warning2 Sound (WAV)", "Warn.wav");
    public final StringProperty finSound = new StringProperty (this,"Finished Sound (WAV)", "gameover.wav");
    
    public final BooleanProperty controlButtons = new BooleanProperty(this, "Control Buttons", false);

    private int timeRemaining = startTimeAmt.getValue();
    private boolean running = false;
    private Timer clock;
    private JLabel timeLa = new JLabel(Integer.toString(timeRemaining));
    private JButton stopBu = new JButton("Stop/Start");
    private JButton resetBu = new JButton("Reset");
       
    public void init() {
        setLayout(new BorderLayout(2,2));
        timeLa.setFont(new Font("Dialog",Font.BOLD, textSize.getValue()));
//        alarmSound.addExtensionFilter("WAV", ".wav");
 //       alarmSound2.addExtensionFilter("WAV", ".wav");
 //       finSound.addExtensionFilter("WAV", ".wav");
        
        if(controlButtons.getValue()){
            add(timeLa, BorderLayout.CENTER);
            add(stopBu, BorderLayout.SOUTH);
            add(resetBu, BorderLayout.NORTH);
        } else {
            add(timeLa, BorderLayout.CENTER);
        }
        stopBu.addActionListener(this);
        resetBu.addActionListener(this);
        clock = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(running){
                    timeRemaining--;
                    timeLa.setText(Integer.toString(timeRemaining));
                    if(timeRemaining <= warningTime.getValue() || 
                       timeRemaining <= warningTime2.getValue()){
                        changeColor();
                        playSound();
                    }
                    if(timeRemaining == 0){
                        clock.stop();
                        running = false;
                        changeColor();
                        playSound();
                    }
                }
            }
        });
    
    }

    public boolean validatePropertyChange(Property property, Object value){
        if(property == startTimeAmt || property == textSize){
            int set = ((Integer)value).intValue();
            if(set <= 0)
                return false;
        }
        return true;
    }

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == stopBu){
            if(running){
                running = false;
                clock.stop();
            }else if(!running && timeRemaining > 0){
                running = true;
                clock.start();
            }
        } else{
            running = false; //Reset to false, in case it is reset while running
            clock.stop();
            timeRemaining = startTimeAmt.getValue();
            timeLa.setText(Integer.toString(startTimeAmt.getValue()));
        }
        changeColor();
    }

    public void propertyChanged(Property property) {
        if(property == textSize){
            timeLa.setFont(new Font("Dialog",Font.BOLD, textSize.getValue()));
        } else if(property instanceof ColorProperty) {
            changeColor();
        } else if(property == controlButtons){
            if(controlButtons.getValue()){
                add(timeLa, BorderLayout.CENTER);
                add(stopBu, BorderLayout.SOUTH);
                add(resetBu, BorderLayout.NORTH);
            } else {
                add(timeLa, BorderLayout.CENTER);
            }
        }
    }

    public void changeColor(){
        if(running) {
            if (timeRemaining <= warningTime2.getValue() ) {
                timeLa.setForeground(alarmColor2.getValue());
            } else if (timeRemaining <= warningTime.getValue()) {
                timeLa.setForeground(alarmColor.getValue());
            } else {  
                timeLa.setForeground(runColor.getValue());
            }
        } else if(timeRemaining == 0) {
            timeLa.setForeground(finColor.getValue());
        } else {
            timeLa.setForeground(stopColor.getValue());
        }
    }

    public void playSound(){
        
        try {
            if (running) {
                if (timeRemaining == warningTime2.getValue()) {
                    AudioPlayer.player.start(new AudioStream(new FileInputStream(filepath + alarmSound2.getValue())));;
                } else if (timeRemaining == warningTime.getValue()) {
                    AudioPlayer.player.start(new AudioStream(new FileInputStream(filepath + alarmSound.getValue())));
                } else {
                    // be quiet
                }
            } else if (timeRemaining == 0) {
                AudioPlayer.player.start(new AudioStream(new FileInputStream(filepath + finSound.getValue())));
            }
        } catch (IOException IOE) {
           
        }
    }

}
