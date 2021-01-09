package org.firstinspires.ftc.teamcode.Autonomous;

import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import org.firstinspires.ftc.teamcode.Dependencies.UGRectDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.Dependencies.AutoCommands;

@Autonomous(name="FookingGetItLads")
public class AutoControlled extends LinearOpMode {

    private Motor frontLeft, frontRight, backLeft, backRight, shooter, intake, grabberLift;
    private CRServo flicker;
    private SimpleServo grabber;
    public VoltageSensor voltageSensor;
    public UGRectDetector vision;
    private RevIMU imu;
    private PIDController pid = new PIDController(0.114, 0.001, 0);

    @Override
    public void runOpMode() throws InterruptedException {
        frontRight = new Motor(hardwareMap, "fL");
        frontLeft = new Motor(hardwareMap, "fR");
        backRight = new Motor(hardwareMap, "bL");
        backLeft = new Motor(hardwareMap, "bR");
        shooter = new Motor(hardwareMap, "shooter");
        intake = new Motor(hardwareMap, "intake");
        grabberLift = new Motor(hardwareMap, "grabberLift");
        grabber = new SimpleServo(hardwareMap, "grabber");
        flicker = new CRServo(hardwareMap, "flicker");

        imu = new RevIMU(hardwareMap, "imu");
        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        vision = new UGRectDetector(hardwareMap, "camera");
        vision.init();
        vision.setTopRectangle(0.5486111111111112, 0.29140625);
        vision.setBottomRectangle(0.6458333333333334, 0.303125);
        vision.setRectangleSize(220, 40);

        AutoCommands robot = new AutoCommands(frontLeft, frontRight, backLeft, backRight, shooter, intake, grabberLift,
                grabber, flicker, voltageSensor, imu);
        robot.initialize();

        waitForStart();

        long startTime = System.currentTimeMillis();
        int visionDec = 0;
        while((System.currentTimeMillis() - startTime) < 3500) {
            telemetry.addData("Vision", vision.getStack());
            telemetry.update();
            visionDec = vision.getStack();
        }

        if (visionDec == 4){
            //telemetry for vision
            telemetry.addData("Vision", "4 Rings Detected");
            telemetry.update();
            robot.setTargetFeet(-2.5, 2.5, 2.5,-2.5);
            robot.navigate(0.15);
            robot.setTargetFeet(2.5, -4, -4 ,4);
            robot.navigate(0.15);
            //move forward 10 feet
            robot.setTargetFeet(10, 10, 10, 10);
            robot.navigate(0.6);
            robot.dropWobbleGoal();
        }

        if (visionDec == 0){
            grabber.setPosition(0);
            robot.setTargetFeet(-2.5, 2.5, 2.5,-2.5);
            robot.navigate(0.15);
            robot.setTargetFeet(2.5, -2.5, -2.5,2.5);
            robot.navigate(0.15);
            //telemetry for vision
            telemetry.addData("Vision", "0 Rings Detected");
            telemetry.update();

            //move forward 6 feet
            robot.setTargetFeet(7, 7, 7, 7);
            robot.navigate(0.6);

            //drop the wobble goal
            robot.dropWobbleGoal();


        }

        if (visionDec == 1){
            //telemetry for vision
            robot.setTargetFeet(-2.5, 2.5, 2.5,-2.5);
            robot.navigate(0.15);
            robot.setTargetFeet(2.5, -2.5, -2.5,2.5);
            robot.navigate(0.15);
            telemetry.addData("Vision", "1 Ring Detected");
            telemetry.update();

            //strafe 1 feet right
            robot.setTargetFeet(1, -1, -1, 1);
            robot.navigate(0.1);

            //move forward 8 feet
            robot.setTargetFeet(8, 8, 8, 8);
            robot.navigate(0.3);

            //strafe left 3 feet
            robot.setTargetFeet(-4, 4, 4, -4);
            robot.navigate(0.3);

            //drop the wobble goal
            robot.dropWobbleGoal();
        }


        //stop motors
        robot.stopMotors();
        robot.finish();
    }
}
