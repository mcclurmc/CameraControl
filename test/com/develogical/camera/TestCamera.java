package com.develogical.camera;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value = JMock.class)
public class TestCamera {

    Mockery context = new Mockery();
    private final Sensor sensor = context.mock(Sensor.class);
    private final MemoryCard memoryCard = context.mock(MemoryCard.class);
    private final Camera camera = new Camera(sensor, memoryCard);

    @Test
    public void switchingTheCameraOnPowersUpTheSensor() {
        context.checking(new Expectations(){{
            oneOf(sensor).powerUp();
        }});

        camera.powerOn();
    }

    @Test
    public void switchingTheCameraOffPowersDownTheSensor() {
        context.checking(new Expectations(){{
            oneOf(sensor).powerDown();
        }});

        camera.powerOff();
    }

    @Test
    public void pressingTheShutterWhenThePowerOffDoesNothing() {
        powerOffCamera();

        context.checking(new Expectations(){{
            never(sensor);
        }});

        camera.pressShutter();
    }

     @Test
    public void pressingTheShutter_WithThePowerOn_CopiesData_FromTheSensor_ToTheMemoryCard() {
        powerOnCamera();

        context.checking(new Expectations(){{
            final byte[] result = new byte[0];
            oneOf(sensor).readData();
            will(returnValue(result));
            oneOf(memoryCard).write(result);
        }});

        camera.pressShutter();
    }

    @Test
    public void ifWritingData_SwitchingOffCamera_DoesNotPowerDownSensor() {
        powerOnCamera();

        context.checking(new Expectations(){{
            final byte[] result = new byte[0];

            oneOf(sensor).readData();
            will(returnValue(result));
            oneOf(memoryCard).write(result);
        }});

        camera.pressShutter();
        camera.powerOff();
    }

    @Test
    public void afterPowerOffAndWrite_SensorShutsDown() {
        powerOnCamera();

        context.checking(new Expectations(){{
            final byte[] result = new byte[0];

            oneOf(sensor).readData();
            will(returnValue(result));
            oneOf(memoryCard).write(result);
        }});

        camera.pressShutter();
        camera.powerOff();

        context.checking(new Expectations(){{
            oneOf(sensor).powerDown();
        }});

        camera.writeComplete();
    }

    private void powerOffCamera() {
        context.checking(new Expectations(){{
            oneOf(sensor).powerDown();
        }});

        camera.powerOff();
    }

    private void powerOnCamera() {
        context.checking(new Expectations(){{
            oneOf(sensor).powerUp();
        }});

        camera.powerOn();
    }


}
