package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Stereotype {

  // Class stereotypes
  public static final String TYPE = "type";
  public static final String HISTORICAL_ROLE = "historicalRole";
  public static final String HISTORICAL_ROLE_MIXIN = "historicalRoleMixin";
  public static final String EVENT = "event";
  public static final String SITUATION = "situation";
  public static final String CATEGORY = "category";
  public static final String MIXIN = "mixin";
  public static final String ROLE_MIXIN = "roleMixin";
  public static final String PHASE_MIXIN = "phaseMixin";
  public static final String KIND = "kind";
  public static final String COLLECTIVE = "collective";
  public static final String QUANTITY = "quantity";
  public static final String RELATOR = "relator";
  public static final String QUALITY = "quality";
  public static final String MODE = "mode";
  public static final String SUBKIND = "subkind";
  public static final String ROLE = "role";
  public static final String PHASE = "phase";
  public static final String ENUMERATION = "enumeration";
  public static final String DATATYPE = "datatype";
  public static final String ABSTRACT = "abstract";

  // Association stereotypes
  public static final String MATERIAL = "material";
  public static final String DERIVATION = "derivation";
  public static final String COMPARATIVE = "comparative";
  public static final String MEDIATION = "mediation";
  public static final String CHARACTERIZATION = "characterization";
  public static final String EXTERNAL_DEPENDENCE = "externalDependence";
  public static final String COMPONENT_OF = "componentOf";
  public static final String MEMBER_OF = "memberOf";
  public static final String SUB_COLLECTION_OF = "subCollectionOf";
  public static final String SUB_QUANTITY_OF = "subQuantityOf";
  public static final String INSTANTIATION = "instantiation";
  public static final String TERMINATION = "termination";
  public static final String PARTICIPATIONAL = "participational";
  public static final String PARTICIPATION = "participation";
  public static final String HISTORICAL_DEPENDENCE = "historicalDependence";
  public static final String CREATION = "creation";
  public static final String MANIFESTATION = "manifestation";
  public static final String BRINGS_ABOUT = "bringsAbout";
  public static final String TRIGGERS = "triggers";

  // Attribute stereotypes
  public static final String BEGIN = "begin";
  public static final String END = "end";

  public static List<String> getOntoumlClassStereotypeNames() {
    return new ArrayList<>(
        Arrays.asList(
            TYPE,
            HISTORICAL_ROLE,
            HISTORICAL_ROLE_MIXIN,
            EVENT,
            SITUATION,
            ENUMERATION,
            DATATYPE,
            ABSTRACT,
            CATEGORY,
            MIXIN,
            ROLE_MIXIN,
            PHASE_MIXIN,
            KIND,
            COLLECTIVE,
            QUANTITY,
            RELATOR,
            QUALITY,
            MODE,
            SUBKIND,
            ROLE,
            PHASE));
  }

  public static List<String> getOntoumlAssociationStereotypeNames() {
    return new ArrayList<>(
        Arrays.asList(
            INSTANTIATION,
            TERMINATION,
            PARTICIPATIONAL,
            PARTICIPATION,
            HISTORICAL_DEPENDENCE,
            CREATION,
            MANIFESTATION,
            BRINGS_ABOUT,
            TRIGGERS,
            MATERIAL,
            MEDIATION,
            COMPARATIVE,
            CHARACTERIZATION,
            EXTERNAL_DEPENDENCE,
            COMPONENT_OF,
            MEMBER_OF,
            SUB_COLLECTION_OF,
            SUB_QUANTITY_OF));
  }

  public static List<String> getOntoumlAttributeStereotypeNames() {
    return new ArrayList<>(Arrays.asList(BEGIN, END));
  }

  public static List<String> getOntoUMLStereotypeNames() {
    final List<String> str_names = new ArrayList<>();

    str_names.addAll(getOntoumlAssociationStereotypeNames());
    str_names.addAll(getOntoumlAttributeStereotypeNames());
    str_names.addAll(getOntoumlClassStereotypeNames());

    return str_names;
  }

  public static List<String> getNonSortalStereotypeNames() {
    return new ArrayList<>(Arrays.asList(CATEGORY, MIXIN, ROLE_MIXIN, PHASE_MIXIN));
  }

  public static List<String> getUltimateSortalStereotypeNames() {
    return new ArrayList<>(Arrays.asList(KIND, COLLECTIVE, QUANTITY, RELATOR, QUALITY, MODE));
  }

  public static List<String> getBaseSortalStereotypeNames() {
    return new ArrayList<>(Arrays.asList(SUBKIND, ROLE, PHASE, HISTORICAL_ROLE));
  }

  public static Set<String> getOntoUMLMereologyStereotypeNames() {
    return Set.of(PARTICIPATIONAL, COMPONENT_OF, MEMBER_OF, SUB_COLLECTION_OF, SUB_QUANTITY_OF);
  }

  public static boolean isNonSortal(String stereotype) {
    return getNonSortalStereotypeNames().contains(stereotype);
  }

  public static boolean isBaseSortal(String stereotype) {
    return getBaseSortalStereotypeNames().contains(stereotype);
  }

  public static boolean isUltimateSortal(String stereotype) {
    return getUltimateSortalStereotypeNames().contains(stereotype);
  }

  public static boolean isSortal(String stereotype) {
    return isUltimateSortal(stereotype) || isBaseSortal(stereotype);
  }

  public static boolean isOntoumlStereotype(IStereotype stereotype) {
    return isOntoumlClassStereotype(stereotype)
        || isOntoumlAssociationStereotype(stereotype)
        || isOntoumlAttributeStereotype(stereotype);
  }

  private static boolean isOntoumlAttributeStereotype(IStereotype stereotype) {
    return stereotype != null
        && IModelElementFactory.MODEL_TYPE_ATTRIBUTE.equals(stereotype.getBaseType())
        && getOntoumlAttributeStereotypeNames().contains(stereotype.getName());
  }

  private static boolean isOntoumlAssociationStereotype(IStereotype stereotype) {
    return stereotype != null
        && IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(stereotype.getBaseType())
        && getOntoumlAssociationStereotypeNames().contains(stereotype.getName());
  }

  private static boolean isOntoumlClassStereotype(IStereotype stereotype) {
    return stereotype != null
        && IModelElementFactory.MODEL_TYPE_CLASS.equals(stereotype.getBaseType())
        && getOntoumlClassStereotypeNames().contains(stereotype.getName());
  }
}
