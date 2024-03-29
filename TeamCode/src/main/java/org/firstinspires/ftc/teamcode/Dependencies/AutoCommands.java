package org.firstinspires.ftc.teamcode.Dependencies;

import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.Autonomous.AutoControlled;

public class AutoCommands extends AutoControlled {
    private Motor fL, fR, bL, bR;
    private Motor shooter, intake, lift;
    private CRServo flicker;
    private SimpleServo grabber;
    private VoltageSensor voltageSensor;
    private RevIMU imu;
    private PIDController pid;

    public double ticksPerInch = 145.6/((96/25.4)*Math.PI);
    public double kP = 0.114;
    public double kI = 0.001;
    public double kD = 0;

    public AutoCommands(Motor fLM, Motor fRM, Motor bLM, Motor bRM, Motor shooterM, Motor intakeM, Motor lifter, SimpleServo grab, CRServo flick, VoltageSensor volt, RevIMU imuParam){
        fL = fLM;
        fR = fRM;
        bL = bLM;
        bR = bRM;
        shooter = shooterM;
        intake = intakeM;
        voltageSensor = volt;
        lift = lifter;
        grabber = grab;
        flicker = flick;
        imu = imuParam;
        pid = new PIDController(kP, kI, kD);
    }

    public void initialize(){
        fL.setInverted(true);
        bL.setInverted(true);
        fL.encoder.setDirection(Motor.Direction.FORWARD);
        bL.encoder.setDirection(Motor.Direction.FORWARD);

        fL.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fR.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bL.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bR.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fL.set(0);
        fR.set(0);
        bL.set(0);
        bR.set(0);
        lift.set(0);
        shooter.set(0);
        grabber.setPosition(0);

        imu.init();
        imu.reset();
    }

    public void resetDriveTrainEncoders(){
        fR.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fL.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bR.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bL.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void dropWobbleGoal(){
        lift.set((13/voltageSensor.getVoltage()) * 0.4);
        sleep(500);
        lift.set(0);
        grabber.setPosition(1);
        sleep(400);
        lift.set((13/voltageSensor.getVoltage()) * -0.5);
        sleep(1000);
        lift.set(0);
    }

    public void pickUpWobbleGoal(){
        lift.set((13/voltageSensor.getVoltage()) * 0.4);
        sleep(500);
        lift.set(0);
        grabber.setPosition(0);
        sleep(1000);
        lift.set((13/voltageSensor.getVoltage()) * -0.5);
        sleep(1000);
        lift.set(0);
    }

    public void setTargetInches(double inchesFL, double inchesFR, double inchesBL, double inchesBR){
        if ((inchesFL < 0 && inchesBR < 0 && inchesFR > 0 && inchesBL > 0) || (inchesFL > 0 && inchesBR > 0 && inchesFR < 0 && inchesBL < 0)) {
            fR.motor.setTargetPosition((int) (inchesFR * ticksPerInch * 1.5 * 1.2972973));
            fL.motor.setTargetPosition((int) (inchesFL * ticksPerInch * 1.5 * 1.2972973));
            bR.motor.setTargetPosition((int) (inchesBR * ticksPerInch * 1.5 * 1.2972973));
            bL.motor.setTargetPosition((int) (inchesBL * ticksPerInch * 1.5 * 1.2972973));
        } else {
            fR.motor.setTargetPosition((int) (inchesFR * ticksPerInch * 1.5 * 1.25));
            fL.motor.setTargetPosition((int) (inchesFL * ticksPerInch * 1.5 * 1.25));
            bR.motor.setTargetPosition((int) (inchesBR * ticksPerInch * 1.5 * 1.25));
            bL.motor.setTargetPosition((int) (inchesBL * ticksPerInch * 1.5 * 1.25));
        }
    }

    public void setTargetFeet(double feetFL, double feetFR, double feetBL, double feetBR){
        setTargetInches(feetFL * 12, feetFR * 12, feetBL * 12, feetBR * 12);
    }

    public void setTargetRotation(int degrees){
        if (degrees < 0) {
            fL.motor.setTargetPosition((int)(-(degrees/10)*Math.PI * ticksPerInch * 1.5));
            fR.motor.setTargetPosition((int)((degrees/10)*Math.PI * ticksPerInch * 1.5));
            bL.motor.setTargetPosition((int)(-(degrees/10)*Math.PI * ticksPerInch * 1.5));
            bR.motor.setTargetPosition((int)((degrees/10)*Math.PI * ticksPerInch * 1.5));
        }
        if (degrees > 0) {
            fL.motor.setTargetPosition((int)((degrees/10)*Math.PI * ticksPerInch * 1.5));
            fR.motor.setTargetPosition((int)(-(degrees/10)*Math.PI * ticksPerInch * 1.5));
            bL.motor.setTargetPosition((int)((degrees/10)*Math.PI * ticksPerInch * 1.5));
            bR.motor.setTargetPosition((int)(-(degrees/10)*Math.PI * ticksPerInch * 1.5));
        }
    }

    public void prepShooter(){
        shooter.set((13/voltageSensor.getVoltage()) * 0.52);
    }

    public void shoot(double speed){
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis() - startTime) < 5000 && opModeIsActive()) {
            pid.setSetPoint(speed);
            shooter.resetEncoder();
            shooter.set(pid.calculate(shooter.getCurrentPosition()));
        }
        double counter = 0;
        while (counter < 3 && opModeIsActive()){
            flicker.set(0);
            sleep(300);
            flicker.set(-1);
            sleep(300);
            flicker.set(0);
            sleep(300);
            flicker.set(1);
            sleep(300);
            flicker.set(0);
            sleep(1000);
            counter++;
        }

        shooter.set(0);
    }

    public void stopMotors(){
        fL.set(0);
        fR.set(0);
        bL.set(0);
        bR.set(0);
    }

    public double[] getForwardSpeed(double speed, Motor m){
        double[] powers = new double[2];
        powers[0] = Math.max(0.15, (speed + (0-(imu.getHeading()/60))) * (1-(Math.abs(Math.abs(m.getCurrentPosition()) - Math.abs(((double)m.motor.getTargetPosition()/2))))/Math.abs(((double)m.motor.getTargetPosition()/2))));
        powers[1] = Math.max(0.15, (speed - (0-(imu.getHeading()/60))) * (1-(Math.abs(Math.abs(m.getCurrentPosition()) - Math.abs(((double)m.motor.getTargetPosition()/2))))/Math.abs(((double)m.motor.getTargetPosition()/2))));
        return powers;
    }

    public double[] getBackwardSpeed(double speed, Motor m){
        double[] powers = new double[2];
        powers[0] = Math.min(-0.15, (speed + (0-(imu.getHeading()/60))));
        powers[1] = Math.min(-0.15, (speed - (0-(imu.getHeading()/60))));
        return powers;
    }

    public void navigate(double speed){
        resetDriveTrainEncoders();
        imu.reset();

        fR.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fL.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bR.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bL.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setInitialSpeed(speed);

        while (fL.motor.isBusy() || fR.motor.isBusy() || bL.motor.isBusy() || bR.motor.isBusy() && opModeIsActive()){
            if (fL.motor.getTargetPosition() < 0 && fR.motor.getTargetPosition() < 0 && bL.motor.getTargetPosition() < 0 && bR.motor.getTargetPosition() < 0) {
                //backward
                fR.set(-speed);
                bL.set(-speed);
                bR.set(-speed);
                fL.set(-speed);

                //fR.set(getBackwardSpeed(-speed, fR)[0]);
                //bL.set(getBackwardSpeed(-speed, bL)[1]);
                //bR.set(getBackwardSpeed(-speed, bR)[0]);
                //fL.set(getBackwardSpeed(-speed, fL)[1]);
            } else if (fL.motor.getTargetPosition() < 0 && bR.motor.getTargetPosition() < 0 && fR.motor.getTargetPosition() > 0 & bL.motor.getTargetPosition() > 0) {
                //strafe left
                fR.set(speed);
                bL.set(speed);
                bR.set(-speed);
                fL.set(-speed);
            } else if (fL.motor.getTargetPosition() > 0 && bR.motor.getTargetPosition() > 0 && fR.motor.getTargetPosition() < 0 & bL.motor.getTargetPosition() < 0) {
                //strafe right
                fR.set(-speed);
                bL.set(-speed);
                bR.set(speed);
                fL.set(speed);
            } else if (fL.motor.getTargetPosition() < 0 && bL.motor.getTargetPosition() < 0 && fR.motor.getTargetPosition() > 0 && bR.motor.getTargetPosition() > 0) {
                //rotate left
                fR.set(speed);
                bL.set(-speed);
                bR.set(speed);
                fL.set(-speed);
            } else if (fL.motor.getTargetPosition() > 0 && bL.motor.getTargetPosition() > 0 && bR.motor.getTargetPosition() < 0 && fR.motor.getTargetPosition() < 0) {
                //rotate right
                fR.set(-speed);
                bL.set(speed);
                bR.set(-speed);
                fL.set(speed);
            } else {
                //forward
                fR.set(getForwardSpeed(speed, fR)[0]);
                bL.set(getForwardSpeed(speed, bL)[1]);
                bR.set(getForwardSpeed(speed, bR)[0]);
                fL.set(getForwardSpeed(speed, fL)[1]);
            }
        }

        stopMotors();
        resetDriveTrainEncoders();
    }

    public void setInitialSpeed(double speed){
        if (fL.motor.getTargetPosition() < 0 && fR.motor.getTargetPosition() < 0 && bL.motor.getTargetPosition() < 0 && bR.motor.getTargetPosition() < 0) {
            fR.set(-speed);
            bL.set(-speed);
            bR.set(-speed);
            fL.set(-speed);
        } else if (fL.motor.getTargetPosition() < 0 && bR.motor.getTargetPosition() < 0 && fR.motor.getTargetPosition() > 0 & bL.motor.getTargetPosition() > 0) {
            fR.set(speed);
            bL.set(speed);
            bR.set(-speed);
            fL.set(-speed);
        } else if (fL.motor.getTargetPosition() > 0 && bR.motor.getTargetPosition() > 0 && fR.motor.getTargetPosition() < 0 & bL.motor.getTargetPosition() < 0) {
            fR.set(-speed);
            bL.set(-speed);
            bR.set(speed);
            fL.set(speed);
        } else if (fL.motor.getTargetPosition() < 0 && bL.motor.getTargetPosition() < 0 && fR.motor.getTargetPosition() > 0 && bR.motor.getTargetPosition() > 0) {
            fR.set(speed);
            bL.set(-speed);
            bR.set(speed);
            fL.set(-speed);
        } else if (fL.motor.getTargetPosition() > 0 && bL.motor.getTargetPosition() > 0 && bR.motor.getTargetPosition() < 0 && fR.motor.getTargetPosition() < 0) {
            fR.set(-speed);
            bL.set(speed);
            bR.set(-speed);
            fL.set(speed);
        } else {
            fR.set(getForwardSpeed(speed, fR)[0]);
            bL.set(getForwardSpeed(speed, bL)[1]);
            bR.set(getForwardSpeed(speed, bR)[0]);
            fL.set(getForwardSpeed(speed, fL)[1]);
        }
    }

    public void finish() {
        fL.stopMotor();
        fR.stopMotor();
        bL.stopMotor();
        bR.stopMotor();
        shooter.stopMotor();
    }
}
