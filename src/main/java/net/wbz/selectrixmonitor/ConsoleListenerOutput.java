package net.wbz.selectrixmonitor;

import net.wbz.selectrix4java.SerialDevice;
import net.wbz.selectrix4java.api.block.BlockListener;
import net.wbz.selectrix4java.api.block.BlockModule;
import net.wbz.selectrix4java.api.device.Device;
import net.wbz.selectrix4java.api.device.DeviceAccessException;
import net.wbz.selectrix4java.api.device.DeviceConnectionListener;
import net.wbz.selectrix4java.api.train.TrainDataListener;
import net.wbz.selectrix4java.api.train.TrainModule;
import net.wbz.selectrix4java.manager.DeviceManager;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class ConsoleListenerOutput {

    public static final String DEVICE_ID = "/dev/tty.usbserial-145";



    public void start(DeviceManager deviceManager) {

        /*
         * Device
         */
        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                out(device + " connected");
            }

            @Override
            public void disconnected(Device device) {
                out(device + " disconnected");
            }
        });
        Device device = deviceManager.registerDevice(DeviceManager.DEVICE_TYPE.COM1, DEVICE_ID, SerialDevice.DEFAULT_BAUD_RATE_FCC);
        try {
            device.connect();
        } catch (DeviceAccessException e) {
            e.printStackTrace();
        }

        /*
         * Block
         */
        try {
            BlockModule blockModule = device.getBlockModule((byte) 10);
            blockModule.addBlockListener(new BlockListener() {
                @Override
                public void blockOccupied(int blockNr) {
                    out("blockOccupied " + blockNr);
                }

                @Override
                public void blockFreed(int blockNr) {
                    out("blockFreed " + blockNr);
                }
            });
        } catch (DeviceAccessException e) {
            e.printStackTrace();
        }

        /*
         * Train
         */
        try {
            final byte trainAddress = 3;
            TrainModule trainModule = device.getTrainModule(trainAddress);
            trainModule.addTrainDataListener(new TrainDataListener() {
                @Override
                public void drivingLevelChanged(int level) {
                    out("drivingLevelChanged " + level + " (" + trainAddress + ")");
                }

                @Override
                public void drivingDirectionChanged(TrainModule.DRIVING_DIRECTION direction) {
                    out("drivingDirectionChanged " + direction + " (" + trainAddress + ")");
                }

                @Override
                public void functionStateChanged(byte address, int functionBit, boolean state) {
                    out("functionStateChanged " + address + "," + functionBit + "," + state + "(" + trainAddress + ")");
                }

                @Override
                public void lightStateChanged(boolean on) {
                    out("lightStateChanged " + on + " (" + trainAddress + ")");
                }

                @Override
                public void hornStateChanged(boolean on) {
                    out("hornStateChanged " + on + " (" + trainAddress + ")");
                }
            });
        } catch (DeviceAccessException e) {
            e.printStackTrace();
        }

    }

    public static void out(String msg) {
        System.out.println(msg);
    }
}