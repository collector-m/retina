// code by jph
package ch.ethz.idsc.owl.car.core;

import java.io.IOException;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class WheelConfigurationTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    WheelConfiguration wheelConfiguration = //
        Serialization.copy(new WheelConfiguration(Tensors.fromString("{0.46[m], 0[m], 0}"), RimoTireConfiguration.FRONT));
    Tensor tensor = wheelConfiguration.adjoint(Tensors.fromString("{0[m*s^-1],0[m*s^-1],1}"));
    assertEquals(tensor, Tensors.fromString("{0.0, 0.46[m], 1}"));
  }
}