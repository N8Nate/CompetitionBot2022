// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.commands.ShootBall;
import frc.robot.Constants.OIConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.AutonomousDefault;
import frc.robot.commands.ElevateBall;
import frc.robot.commands.ElevateWheelSpin;
import frc.robot.commands.IntakeBall;
import frc.robot.commands.AdjustIntakeHeight;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ElevatorWheelSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.IntakeHeightSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;

/*
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem m_robotDrive = new DriveSubsystem();
  private final ShooterSubsystem m_robotShooter = new ShooterSubsystem();
  private final IntakeSubsystem m_robotIntake = new IntakeSubsystem();
  private final IntakeHeightSubsystem m_adjustIntake = new IntakeHeightSubsystem();
  private final ElevatorSubsystem m_robotElevate = new ElevatorSubsystem();
  private final ElevatorWheelSubsystem m_wheelSpin = new ElevatorWheelSubsystem();

  // The driver's controller
  XboxController m_driverController = new XboxController(OIConstants.kDriverControllerPort);
  XboxController m_mechController = new XboxController(OIConstants.kMechControllerPort);

  // Create SmartDashboard chooser for autonomous routines
  private final SendableChooser<Command> m_chooser = new SendableChooser<>();

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {


    CameraServer.startAutomaticCapture("Usb Camera 0", 0);
    CameraServer.startAutomaticCapture("Usb Camera 1", 1);

    // Configure the button bindings
    configureButtonBindings();

    // Configure default commands
    // Set the default drive command to split-stick arcade drive
    m_robotDrive.setDefaultCommand(
        // A split-stick arcade command, with forward/backward controlled by the left
        // hand, and turning controlled by the right.
        new RunCommand(
            () ->
                m_robotDrive.drive(
                    -m_driverController.getLeftY(),
                    m_driverController.getLeftX(),                   
                    m_driverController.getRightX(),
                    false),
            m_robotDrive));
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link edu.wpi.first.wpilibj.GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then calling passing it to a
   * {@link JoystickButton}.
   */
  private void configureButtonBindings() {
    // Drive at half speed when the right bumper is held
    new JoystickButton(m_driverController, Button.kRightBumper.value)
        .whenPressed(() -> m_robotDrive.setMaxOutput(0.5))
        .whenReleased(() -> m_robotDrive.setMaxOutput(1));

    // extend intake when left bumber is released
    new JoystickButton(m_mechController, Button.kLeftBumper.value)
        .whenReleased(new AdjustIntakeHeight(m_adjustIntake, IntakeConstants.kAdjustIntakeOutput));
    
    // intake intake when right bumper is released
    new JoystickButton(m_mechController, Button.kRightBumper.value)
        .whenReleased(new AdjustIntakeHeight(m_adjustIntake, -IntakeConstants.kAdjustIntakeOutput));

    //Dump when x is pressed
    new JoystickButton(m_mechController, Button.kX.value)
        .toggleWhenPressed(new ShootBall(m_robotShooter, ShooterConstants.kLowShooterOutput));
    
    //Shoot at normal speed when A is pressed
    new JoystickButton(m_mechController, Button.kA.value)
        .toggleWhenPressed(new ShootBall(m_robotShooter, ShooterConstants.kHighShooterOutput));
    
    //Intake and intake elevator wheel
    new JoystickButton(m_mechController, Button.kB.value)
        .whenPressed(new IntakeBall(m_robotIntake, IntakeConstants.kIntakeOutput))
        .whenReleased(new IntakeBall(m_robotIntake, 0))
        .whenPressed(new ElevateWheelSpin(m_wheelSpin, ElevatorConstants.kElevatorMotor3Output))
        .whenReleased(new ElevateWheelSpin(m_wheelSpin, 0));

    //Spin out intake and elevator wheel
    new JoystickButton(m_mechController, Button.kY.value)
        .whenPressed(new IntakeBall(m_robotIntake, -IntakeConstants.kIntakeOutput))
        .whenReleased(new IntakeBall(m_robotIntake, 0));
        .whenPressed(new ElevateWheelSpin(m_wheelSpin, -ElevatorConstants.kElevatorMotor3Output))
        .whenReleased(new ElevateWheelSpin(m_wheelSpin, 0));

    //elevator up
    new POVButton(m_mechController, OIConstants.kUpDPad)
        .whenPressed(new ElevateBall(m_robotElevate, ElevatorConstants.kElevatorOutput))
        .whenReleased(new ElevateBall(m_robotElevate, 0));

    //elevator down
    new POVButton(m_mechController, OIConstants.kDownDPad)
        .whenPressed(new ElevateBall(m_robotElevate, -ElevatorConstants.kElevatorOutput))
        .whenReleased(new ElevateBall(m_robotElevate, 0));



    // Setup SmartDashboard options
    m_chooser.setDefaultOption("Basic Auto", new AutonomousDefault(m_robotDrive, m_robotShooter, m_robotElevate));
    SmartDashboard.putData(m_chooser);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return m_chooser.getSelected();    
  }
}
