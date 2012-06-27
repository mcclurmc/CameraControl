package com.develogical.camera;

import org.jmock.internal.StatePredicate;

public class Camera implements WriteListener {

    private Sensor sensor;
    private boolean isOn;
    private volatile Boolean isWriting = false;
    private MemoryCard memoryCard;
    private WriteListener writeListener;

    public Camera(Sensor sensor, MemoryCard memoryCard) {
        this.sensor = sensor;
        this.memoryCard = memoryCard;
    }

    public void pressShutter() {
        byte[] data;

        if (isOnStartWrite()) {
            data = sensor.readData();
            memoryCard.write(data);
        }
    }

    private boolean isOnStartWrite() {
        synchronized (isWriting){
            isWriting = isOn;
            return isOn;
        }
    }

    public void powerOn() {
        isOn = true;
        sensor.powerUp();
    }

    public void powerOff() {
        synchronized (isWriting) {
            if(isWriting) {
                writeListener = new WriteListener() {
                    @Override
                    public void writeComplete() {
                        sensor.powerDown();
                        isOn = false;
                    }
                };
            }
            else {
                sensor.powerDown();
                isOn = false;
            }
        }
    }

    public void writeComplete() {
        synchronized (isWriting) {
            isWriting = false;
            if(writeListener != null){
                writeListener.writeComplete();
                writeListener = null;
            }
        }
    }
}

