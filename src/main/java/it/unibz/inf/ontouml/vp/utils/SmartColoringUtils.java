package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the coloring feature
 *
 * @author Victor Viola
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 */
public class SmartColoringUtils {

  private static final Color GREEN = new Color(153, 255, 153);
  private static final Color LIGHT_GREEN = new Color(211, 255, 211);
  private static final Color PINK = new Color(255, 153, 163);
  private static final Color LIGHT_PINK = new Color(255, 218, 221);
  private static final Color BLUE = new Color(112, 215, 255);
  private static final Color LIGHT_BLUE = new Color(192, 237, 255);
  private static final Color WHITE = new Color(255, 255, 255);
  private static final Color YELLOW = new Color(252, 252, 212);
  private static final Color ORANGE = new Color(252, 224, 192);
  private static final Color PURPLE = new Color(211, 211, 252);
  private static final Color GREY = new Color(224, 224, 224);

  private static final Color COLOR_FUNCTIONAL_COMPLEX = PINK;
  private static final Color COLOR_FUNCTIONAL_COMPLEX_ALTERNATIVE = LIGHT_PINK;
  private static final Color COLOR_COLLECTIVE = PINK;
  private static final Color COLOR_COLLECTIVE_ALTERNATIVE = LIGHT_PINK;
  private static final Color COLOR_QUANTITY = PINK;
  private static final Color COLOR_QUANTITY_ALTERNATIVE = LIGHT_PINK;
  private static final Color COLOR_RELATOR = GREEN;
  private static final Color COLOR_RELATOR_ALTERNATIVE = LIGHT_GREEN;
  private static final Color COLOR_EXTRINSIC_MODE = BLUE;
  private static final Color COLOR_EXTRINSIC_MODE_ALTERNATIVE = LIGHT_BLUE;
  private static final Color COLOR_INTRINSIC_MODE = BLUE;
  private static final Color COLOR_INTRINSIC_MODE_ALTERNATIVE = LIGHT_BLUE;
  private static final Color COLOR_QUALITY = BLUE;
  private static final Color COLOR_QUALITY_ALTERNATIVE = LIGHT_BLUE;
  private static final Color COLOR_ABSTRACT = WHITE;
  private static final Color COLOR_EVENT = YELLOW;
  private static final Color COLOR_SITUATION = ORANGE;
  private static final Color COLOR_TYPE = PURPLE;
  private static final Color COLOR_NON_SPECIFIC = GREY;

  private static final Map<String, Color> mainColorMap;
  private static final Map<String, Color> alternativeColorMap;

  static {
    mainColorMap = new HashMap<>();
    mainColorMap.put(RestrictedTo.RELATOR, COLOR_RELATOR);
    mainColorMap.put(RestrictedTo.EXTRINSIC_MODE, COLOR_EXTRINSIC_MODE);
    mainColorMap.put(RestrictedTo.INTRINSIC_MODE, COLOR_INTRINSIC_MODE);
    mainColorMap.put(RestrictedTo.QUALITY, COLOR_QUALITY);
    mainColorMap.put(RestrictedTo.FUNCTIONAL_COMPLEX, COLOR_FUNCTIONAL_COMPLEX);
    mainColorMap.put(RestrictedTo.COLLECTIVE, COLOR_COLLECTIVE);
    mainColorMap.put(RestrictedTo.QUANTITY, COLOR_QUANTITY);
    mainColorMap.put(RestrictedTo.EVENT, COLOR_EVENT);
    mainColorMap.put(RestrictedTo.SITUATION, COLOR_SITUATION);
    mainColorMap.put(RestrictedTo.ABSTRACT, COLOR_ABSTRACT);
    mainColorMap.put(RestrictedTo.TYPE, COLOR_TYPE);

    alternativeColorMap = new HashMap<>();
    alternativeColorMap.put(RestrictedTo.RELATOR, COLOR_RELATOR_ALTERNATIVE);
    alternativeColorMap.put(RestrictedTo.EXTRINSIC_MODE, COLOR_EXTRINSIC_MODE_ALTERNATIVE);
    alternativeColorMap.put(RestrictedTo.INTRINSIC_MODE, COLOR_INTRINSIC_MODE_ALTERNATIVE);
    alternativeColorMap.put(RestrictedTo.QUALITY, COLOR_QUALITY_ALTERNATIVE);
    alternativeColorMap.put(RestrictedTo.FUNCTIONAL_COMPLEX, COLOR_FUNCTIONAL_COMPLEX_ALTERNATIVE);
    alternativeColorMap.put(RestrictedTo.COLLECTIVE, COLOR_COLLECTIVE_ALTERNATIVE);
    alternativeColorMap.put(RestrictedTo.QUANTITY, COLOR_QUANTITY_ALTERNATIVE);
    alternativeColorMap.put(RestrictedTo.EVENT, COLOR_EVENT);
    alternativeColorMap.put(RestrictedTo.SITUATION, COLOR_SITUATION);
    alternativeColorMap.put(RestrictedTo.ABSTRACT, COLOR_ABSTRACT);
    alternativeColorMap.put(RestrictedTo.TYPE, COLOR_TYPE);
  }

  /**
   * Returns the color of a class based on its nature
   *
   * @param _class the class to be painted
   * @return the color of the class
   */
  private static Color getColor(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    final List<String> allStereotypes = Stereotype.getOntoumlClassStereotypeNames();

    if (!allStereotypes.contains(stereotype)) {
      return null;
    }

    final List<String> restrictedTo = Class.getRestrictedToList(_class);
    final boolean isUltimateSortal =
        Stereotype.getUltimateSortalStereotypeNames().contains(stereotype);

    if (restrictedTo.isEmpty()) {
      return COLOR_NON_SPECIFIC;
    }

    return restrictedTo.stream()
        .map(
            restriction ->
                isUltimateSortal
                    ? mainColorMap.get(restriction)
                    : alternativeColorMap.get(restriction))
        .reduce(
            null,
            (previousColor, currentColor) -> {
              if (previousColor == null) {
                return currentColor;
              } else {
                return previousColor.equals(currentColor) ? currentColor : COLOR_NON_SPECIFIC;
              }
            });
  }

  /**
   * Paints occurrences of a class based on the "restrictTo" meta-property. Affects class
   * occurrences in all diagrams. No effect whenever auto-coloring is disabled or color is <code>
   * null</code>.
   *
   * @param _class
   */
  public static void paint(IClass _class) {
    if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
      return;
    }

    final Color defaultColor = getColor(_class);

    if (defaultColor == null) {
      return;
    }

    for (IDiagramElement classView : _class.getDiagramElements()) {
      if (classView instanceof IClassUIModel) {
        ((IClassUIModel) classView).getFillColor().setColor1(defaultColor);
      }
    }
  }

  public static void paint(IClassUIModel classDiagramElement) {
    if (classDiagramElement == null) return;

    IModelElement modelElement = classDiagramElement.getModelElement();

    if (!(modelElement instanceof IClass)) return;

    final Color defaultColor = getColor((IClass) modelElement);

    if (defaultColor != null) {
      classDiagramElement.getFillColor().setColor1(defaultColor);
    }
  }
}
