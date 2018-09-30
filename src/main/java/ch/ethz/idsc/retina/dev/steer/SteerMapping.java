// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.Scalar;

public interface SteerMapping {
  /** @param steerColumnInterface
   * @return scalar without unit but with interpretation in radians
   * @throws Exception if {@link SteerColumnInterface#isSteerColumnCalibrated()} returns false */
  Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface);

  /** @param angle of imaginary center front wheel with unit "rad"
   * @return steer column encoder value with unit "SCE" */
  Scalar getSCEfromAngle(Scalar angle);
}