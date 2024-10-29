package it.unibz.inf.ontouml.vp;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class Misc {

  @Test
  public void testNullable() {
    String[] array = {};
    List<String> list = Arrays.asList(array);

    assertThat(list.isEmpty()).isTrue();
  }
}
