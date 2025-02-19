// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.FeedbackDevice;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {

  private final TalonSRX m_intakeHeightMotor = 
    new TalonSRX(IntakeConstants.kIntake2Port);

  /** Creates a new intakeHeightSubsytem. */
  public IntakeSubsystem() {
    m_intakeHeightMotor.setInverted(IntakeConstants.kIntakeHeightMotorInverted);
  }

  public void setTargetOutput(double output) {
    m_intakeHeightMotor.set(ControlMode.PercentOutput, output);
  }
  
  public void resetIntakeEncoder() {
    m_intakeHeightMotor.setSelectedSensorPosition(0);
  }
  
  public void getIntakeHeight() {
    final double m_intakeHeightPos = Math.abs(m_intakeHeightMotor.getSelectedSensorPosition());
    return (m_intakeHeightPos);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
