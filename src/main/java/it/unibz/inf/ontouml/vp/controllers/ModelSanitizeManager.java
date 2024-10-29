package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ApplicationManagerUtils;
import it.unibz.inf.ontouml.vp.utils.OntoUMLConstraintsManager;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelSanitizeManager {

  private Set<IAttribute> attributes;
  private Set<IAssociation> associations;
  private Set<IClass> classes;
  private Map<String, String> associationStereotypesMap;
  private Map<String, String> attributeStereotypesMap;
  private Map<String, String> classStereotypesMap;

  public static void run() {
    ModelSanitizeManager sanitizer = new ModelSanitizeManager();

    sanitizer.retrieveModelElements();
    sanitizer.fixMissingTypeOnAssociationEnds();
    sanitizer.filterAssociationBetweenClasses();
    sanitizer.fixElementsStereotypes();
    sanitizer.fixAssociationEnds();
  }

  private void filterAssociationBetweenClasses() {
    associations =
        associations.stream().filter(Association::holdsBetweenClasses).collect(Collectors.toSet());
  }

  private void fixAssociationEnds() {
    associations.forEach(
        a -> {
          IAssociationEnd sourceEnd = Association.getSourceEnd(a);
          IAssociationEnd targetEnd = Association.getTargetEnd(a);
          String aggKind = targetEnd.getAggregationKind();

          if (shouldInvert(a)) {
            Association.setSourceEndProperties(a, targetEnd);
            Association.setTargetEndProperties(a, sourceEnd);

            if (shouldPreserveAggregation(a, aggKind)) {
              sourceEnd.setAggregationKind(aggKind);
              sourceEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
            }
          } else {
            Association.setSourceEndProperties(a, sourceEnd);
            Association.setTargetEndProperties(a, targetEnd);

            if (shouldPreserveAggregation(a, aggKind)) {
              targetEnd.setAggregationKind(aggKind);
              targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
            }
          }
        });
  }

  private boolean shouldInvert(IAssociation association) {
    String str = ModelElement.getUniqueStereotypeName(association);

    return !OntoUMLConstraintsManager.isStereotypeAllowed(association, str)
        && OntoUMLConstraintsManager.isStereotypeAllowedIfInverted(association, str);
  }

  private boolean shouldPreserveAggregation(IAssociation association, String originalAggregation) {
    String str = ModelElement.getUniqueStereotypeName(association);

    return (!Association.hasOntoumlStereotype(association)
            || Association.hasMereologyStereotype(association))
        && isAggregation(originalAggregation);
  }

  private boolean isAggregation(String aggregationKind) {
    return IAssociationEnd.AGGREGATION_KIND_shared.equals(aggregationKind)
        || IAssociationEnd.AGGREGATION_KIND_composite.equals(aggregationKind);
  }

  private void fixMissingTypeOnAssociationEnds() {
    associations.forEach(
        a -> {
          if (!doesFromMatchEndType(a)) {
            fixFromEndType(a);
          }

          if (!doesToMatchEndType(a)) {
            fixToEndType(a);
          }
        });
  }

  private boolean doesFromMatchEndType(IAssociation association) {
    IModelElement from = association.getFrom();
    IAssociationEnd fromEnd = Association.getFromEnd(association);
    IModelElement fromEndType = fromEnd.getTypeAsElement();

    return fromEndType != null && fromEndType.getId().equals(from.getId());
  }

  private boolean doesToMatchEndType(IAssociation association) {
    IModelElement to = association.getTo();
    IAssociationEnd toEnd = Association.getToEnd(association);
    IModelElement toEndType = toEnd.getTypeAsElement();

    return toEndType != null && toEndType.getId().equals(to.getId());
  }

  private void fixFromEndType(IAssociation association) {
    IModelElement from = association.getFrom();
    IAssociationEnd fromEnd = Association.getFromEnd(association);
    fromEnd.setType(from);
  }

  private void fixToEndType(IAssociation association) {
    IModelElement to = association.getTo();
    IAssociationEnd toEnd = Association.getToEnd(association);
    toEnd.setType(to);
  }

  private void fixElementsStereotypes() {
    classes.forEach(this::fixElementsStereotypes);
    attributes.forEach(this::fixElementsStereotypes);
    associations.forEach(this::fixAssociationsStereotypes);
  }

  private void fixElementsStereotypes(IModelElement element) {
    final String stereotype = ModelElement.getUniqueStereotypeName(element);
    final String recognizedStr = getRecognizedStereotypes(element);

    if (hasReplacement(stereotype, recognizedStr)) applyStereotype(element, recognizedStr);
  }

  private void fixAssociationsStereotypes(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final String recognizedStr = getRecognizedStereotypes(association);

    if (hasReplacement(stereotype, recognizedStr)) applyStereotype(association, recognizedStr);
  }

  private String getRecognizedStereotypes(IModelElement element) {
    final String normalizedStr = getNormalizedStereotype(element);
    final String type = element.getModelType();
    Map<String, String> stereotypesMap = getStereotypesMap(type);

    return stereotypesMap.get(normalizedStr);
  }

  private Map<String, String> getStereotypesMap(String modelType) {
    switch (modelType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        return getAssociationStereotypesMap();
      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        return getAttributeStereotypesMap();
      case IModelElementFactory.MODEL_TYPE_CLASS:
        return getClassStereotypesMap();
      default:
        throw new RuntimeException("Unexpected model element of type '" + modelType + "'.");
    }
  }

  private void applyStereotype(IModelElement element, String replacementStereotype) {
    final String modelType = element != null ? element.getModelType() : null;

    if (!isTypeExpected(modelType)) {
      throw new RuntimeException("Unexpected model element of type '" + modelType + "'.");
    }

    StereotypesManager.applyStereotype(element, replacementStereotype);
  }

  private boolean isTypeExpected(String modelType) {
    return IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(modelType)
        || IModelElementFactory.MODEL_TYPE_ATTRIBUTE.equals(modelType)
        || IModelElementFactory.MODEL_TYPE_CLASS.equals(modelType);
  }

  private boolean hasReplacement(String originalStereotype, String recognizedStereotype) {
    // TODO: Review whether we should reapplying even when original and recognized are equal
    return recognizedStereotype != null && !recognizedStereotype.isEmpty();
    //    return recognizedStereotype != null
    //        && !recognizedStereotype.isEmpty()
    //        && !recognizedStereotype.equals(originalStereotype);
  }

  private String getNormalizedStereotype(String stereotype) {
    return stereotype != null ? stereotype.toLowerCase().replaceAll("(\\s|-)+", "") : "";
  }

  private String getNormalizedStereotype(IModelElement element) {
    final String stereotype = ModelElement.getUniqueStereotypeName(element);
    return getNormalizedStereotype(stereotype);
  }

  private Map<String, String> getClassStereotypesMap() {
    if (classStereotypesMap == null || classStereotypesMap.isEmpty())
      initializeClassStereotypeMap();
    return classStereotypesMap;
  }

  private Map<String, String> getAssociationStereotypesMap() {
    if (associationStereotypesMap == null || associationStereotypesMap.isEmpty())
      initializeAssociationStereotypeMap();
    return associationStereotypesMap;
  }

  private Map<String, String> getAttributeStereotypesMap() {
    if (attributeStereotypesMap == null || attributeStereotypesMap.isEmpty())
      initializeAttributeStereotypeMap();
    return attributeStereotypesMap;
  }

  private void initializeClassStereotypeMap() {
    classStereotypesMap = new HashMap<>();

    Stereotype.getOntoumlClassStereotypeNames()
        .forEach(
            str -> {
              String normalizedStr = getNormalizedStereotype(str);
              classStereotypesMap.put(normalizedStr, str);
            });

    classStereotypesMap.put("hou", Stereotype.TYPE);
    classStereotypesMap.put("highordertype", Stereotype.TYPE);
    classStereotypesMap.put("higherordertype", Stereotype.TYPE);
    classStereotypesMap.put("powertype", Stereotype.TYPE);
    classStereotypesMap.put("universal", Stereotype.TYPE);
    classStereotypesMap.put("2ndot", Stereotype.TYPE);
    classStereotypesMap.put("collectivekind", Stereotype.COLLECTIVE);
    classStereotypesMap.put("quantitykind", Stereotype.QUANTITY);
    classStereotypesMap.put("relatorkind", Stereotype.RELATOR);
    classStereotypesMap.put("qualitykind", Stereotype.QUALITY);
    classStereotypesMap.put("modekind", Stereotype.MODE);
    classStereotypesMap.put("enum", Stereotype.ENUMERATION);
  }

  private void initializeAssociationStereotypeMap() {
    associationStereotypesMap = new HashMap<>();

    Stereotype.getOntoumlAssociationStereotypeNames()
        .forEach(
            str -> {
              String normalizedStr = getNormalizedStereotype(str);
              associationStereotypesMap.put(normalizedStr, str);
            });

    associationStereotypesMap.put("characterizes", Stereotype.CHARACTERIZATION);
    associationStereotypesMap.put("externaldependenceon", Stereotype.EXTERNAL_DEPENDENCE);
    associationStereotypesMap.put("externallydepends", Stereotype.EXTERNAL_DEPENDENCE);
    associationStereotypesMap.put("externallydependson", Stereotype.EXTERNAL_DEPENDENCE);
    associationStereotypesMap.put("mediates", Stereotype.MEDIATION);
    associationStereotypesMap.put("iof", Stereotype.INSTANTIATION);
    associationStereotypesMap.put("instanceof", Stereotype.INSTANTIATION);
    associationStereotypesMap.put("terminates", Stereotype.TERMINATION);
    associationStereotypesMap.put("participates", Stereotype.PARTICIPATION);
    associationStereotypesMap.put("historicallydepends", Stereotype.HISTORICAL_DEPENDENCE);
    associationStereotypesMap.put("creates", Stereotype.CREATION);
    associationStereotypesMap.put("manifests", Stereotype.MANIFESTATION);
  }

  private void initializeAttributeStereotypeMap() {
    attributeStereotypesMap = new HashMap<>();

    Stereotype.getOntoumlAttributeStereotypeNames()
        .forEach(
            str -> {
              String normalizedStr = getNormalizedStereotype(str);
              attributeStereotypesMap.put(normalizedStr, str);
            });
  }

  private void retrieveModelElements() {
    retrieveClasses();
    retrieveAssociations();
    retrieveAttributes();
  }

  private void retrieveAssociations() {
    IProject project = ApplicationManagerUtils.getCurrentProject();

    associations =
        Optional.ofNullable(
                project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION))
            .stream()
            .flatMap(Arrays::stream)
            .map(IAssociation.class::cast)
            .collect(Collectors.toSet());
  }

  private void retrieveAttributes() {
    //    attributes = new HashSet<>();
    //    classes.stream().flatMap(c -> Class.getAttributes(c).stream()).forEach(attributes::add);
    attributes =
        classes.stream().map(Class::getAttributes).flatMap(Set::stream).collect(Collectors.toSet());
  }

  private void retrieveClasses() {
    //    classes = new HashSet<>();
    //    final Iterator<?> iter =
    //
    // ApplicationManagerUtils.getAllLevelModelElements(IModelElementFactory.MODEL_TYPE_CLASS);
    //
    //    while (iter != null && iter.hasNext()) {
    //      classes.add((IClass) iter.next());
    //    }
    IProject project = ApplicationManagerUtils.getCurrentProject();

    classes =
        Optional.ofNullable(
                project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS))
            .stream()
            .flatMap(Arrays::stream)
            .map(IClass.class::cast)
            .collect(Collectors.toSet());
  }
}
