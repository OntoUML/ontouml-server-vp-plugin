package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.model.uml.Property;
import it.unibz.inf.ontouml.vp.utils.Stereotype;

public class SmartModellingController {

  public static void setAggregationKind(IModelElement element) {
    IAssociation association = (IAssociation) element;
    IAssociationEnd compositionFromEnd = (IAssociationEnd) association.getFromEnd();
    IAssociationEnd compositionToEnd = (IAssociationEnd) association.getToEnd();

    if (compositionToEnd.getAggregationKind().equals(IAssociationEnd.AGGREGATION_KIND_NONE)) {
      compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
    }

    compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
  }

  public static void removeAggregationKind(IModelElement element) {
    IAssociationEnd compositionFromEnd = (IAssociationEnd) ((IAssociation) element).getFromEnd();
    IAssociationEnd compositionToEnd = (IAssociationEnd) ((IAssociation) element).getToEnd();

    compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
    compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
  }

  private static boolean setCardinalityIfEmpty(IAssociationEnd end, String cardinality) {

    if (end.getMultiplicity() == null || end.getMultiplicity().equals("Unspecified")) {
      end.setMultiplicity(cardinality);
      return true;
    } else {
      return false;
    }
  }

  public static void setAssociationMetaProperties(IAssociation association) {

    IAssociationEnd source = (IAssociationEnd) association.getFromEnd();
    IAssociationEnd target = (IAssociationEnd) association.getToEnd();

    if (source == null || target == null)
      return;

    String sourceStereotype = Property.getTypeStereotype(source);
    String targetStereotype = Property.getTypeStereotype(target);

    String stereotype = ModelElement.getUniqueStereotypeName(association);

    if (stereotype == null)
      return;

    switch (stereotype) {
      case Stereotype.CHARACTERIZATION:
        // Source: Characterized end
        setCardinalityIfEmpty(source, "1");
        // Target: Mode/Quality end
        setCardinalityIfEmpty(target, "1");
        target.setReadOnly(true);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.COMPARATIVE:
        setCardinalityIfEmpty(source, "0..*");
        setCardinalityIfEmpty(target, "0..*");
        association.setDerived(true);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.COMPONENT_OF:
        setCardinalityIfEmpty(source, "1..*");
        setCardinalityIfEmpty(target, "1");
        setAggregationKind(association);
        return;
      case Stereotype.MATERIAL:
        if (targetStereotype.equals(Stereotype.ROLE)
            || targetStereotype.equals(Stereotype.ROLE_MIXIN))
          setCardinalityIfEmpty(source, "1..*");
        else
          setCardinalityIfEmpty(source, "0..*");

        if (sourceStereotype.equals(Stereotype.ROLE)
            || sourceStereotype.equals(Stereotype.ROLE_MIXIN))
          setCardinalityIfEmpty(target, "1..*");
        else
          setCardinalityIfEmpty(target, "0..*");

        association.setDerived(true);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.EXTERNAL_DEPENDENCE:
        // Source: Mode/Quality end
        setCardinalityIfEmpty(source, "0..*");
        // Target: Dependee end
        setCardinalityIfEmpty(target, "1..*");
        target.setReadOnly(true);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.MEDIATION:
        if (targetStereotype.equals(Stereotype.ROLE)
            || targetStereotype.equals(Stereotype.ROLE_MIXIN))
          setCardinalityIfEmpty(source, "1..*");
        else
          setCardinalityIfEmpty(source, "0..*");

        setCardinalityIfEmpty(target, "1");
        target.setReadOnly(true);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.MEMBER_OF:
        setCardinalityIfEmpty(source, "1..*");
        setCardinalityIfEmpty(target, "1..*");
        setAggregationKind(association);
        return;
      case Stereotype.SUB_COLLECTION_OF:
        setCardinalityIfEmpty(source, "1");
        setCardinalityIfEmpty(target, "1");
        setAggregationKind(association);
        return;
      case Stereotype.SUB_QUANTITY_OF:
        setCardinalityIfEmpty(source, "1");
        setCardinalityIfEmpty(target, "1");
        source.setReadOnly(true);
        setAggregationKind(association);
        return;
      case Stereotype.CREATION:
      case Stereotype.TERMINATION:
        // Source: Endurant end
        setCardinalityIfEmpty(source, "1");
        source.setReadOnly(true);
        // Target: Event end
        setCardinalityIfEmpty(target, "1");
        target.setReadOnly(true);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.HISTORICAL_DEPENDENCE:
        // Source: Depender end
        setCardinalityIfEmpty(source, "0..*");
        // Target: Dependee end
        setCardinalityIfEmpty(target, "1");
        target.setReadOnly(true);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.MANIFESTATION:
        // Source: Mode/Quality/Relator end
        setCardinalityIfEmpty(source, "1..*");
        source.setReadOnly(true);
        // Target: Event end
        setCardinalityIfEmpty(target, "0..*");
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.PARTICIPATION:
        // Source: Endurant end (participant)
        setCardinalityIfEmpty(source, "1..*");
        source.setReadOnly(true);

        // Target: Event end
        if (sourceStereotype.equals(Stereotype.HISTORICAL_ROLE)
            || sourceStereotype.equals(Stereotype.HISTORICAL_ROLE_MIXIN))
          setCardinalityIfEmpty(target, "1..*");
        else
          setCardinalityIfEmpty(target, "0..*");

        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.PARTICIPATIONAL:
        setCardinalityIfEmpty(source, "1..*");
        setCardinalityIfEmpty(target, "1");
        source.setReadOnly(true);
        target.setReadOnly(true);
        setAggregationKind(association);
        return;
      case Stereotype.INSTANTIATION:
        // Source: lower order type
        setCardinalityIfEmpty(source, "0..*");
        source.setReadOnly(false);
        // Target: higher order type
        setCardinalityIfEmpty(target, "1..*");
        target.setReadOnly(false);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.BRINGS_ABOUT:
        // Source: event
        setCardinalityIfEmpty(source, "1");
        source.setReadOnly(true);
        // Target: situation
        setCardinalityIfEmpty(target, "1");
        target.setReadOnly(true);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
      case Stereotype.TRIGGERS:
        // Source: situation
        setCardinalityIfEmpty(source, "1");
        source.setReadOnly(true);
        // Target: event
        setCardinalityIfEmpty(target, "0..1");
        target.setReadOnly(false);
        target.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
        source.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
        removeAggregationKind(association);
        return;
    }
  }

  public static void setClassMetaProperties(IClass _class) {
    if (_class == null)
      return;

    String[] stereotypes = _class.toStereotypeArray();

    if (stereotypes == null || stereotypes.length != 1)
      return;

    switch (stereotypes[0]) {
      case Stereotype.CATEGORY:
        _class.setAbstract(true);
        break;
      case Stereotype.ROLE_MIXIN:
        _class.setAbstract(true);
        break;
      case Stereotype.PHASE_MIXIN:
        _class.setAbstract(true);
        break;
      case Stereotype.MIXIN:
        _class.setAbstract(true);
        break;
    }
  }

  // public static void manageAssociationStereotypes(IAssociation association, VPAction action) {
  //
  // if (!association.getFrom().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
  // return;
  //
  // if (!association.getTo().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
  // return;
  //
  // final IClass source = (IClass) association.getFrom();
  // final IClass target = (IClass) association.getTo();
  //
  // final ArrayList<String> sourceStereotypes = new
  // ArrayList<String>(Arrays.asList(source.toStereotypeArray()));
  // final ArrayList<String> targetStereotypes = new
  // ArrayList<String>(Arrays.asList(target.toStereotypeArray()));
  //
  // if (sourceStereotypes.size() != 1 || targetStereotypes.size() != 1) {
  // // if any end has more than 1 stereotypes nothing is allowed
  // action.setEnabled(false);
  // return;
  // }
  //
  // // continue if both ends has ONLY ONE stereotype in both ends
  // final String sourceStereotype = sourceStereotypes.get(0);
  // final String targetStereotype = targetStereotypes.get(0);
  //// final ArrayList<String> allowedCombinations =
  // AssociationConstraints.allowedCombinations.get(new SimpleEntry<String,
  // String>(sourceStereotype, targetStereotype));
  // final List<String> allowedCombinations =
  // OntoUMLConstraintsManager.getAllowedActionsBasedOnSourceTargetStereotypes(sourceStereotype,
  // targetStereotype);
  //
  // if (allowedCombinations == null || !allowedCombinations.contains(action.getActionId()))
  // action.setEnabled(false);
  //
  // return;
  // }

  // public static void manageClassStereotypes(IClass _class, VPAction action) {
  //
  // final ISimpleRelationship[] relationshipsTo = _class.toToRelationshipArray();
  // final ISimpleRelationship[] relationshipsFrom = _class.toFromRelationshipArray();
  //
  // for (int i = 0; relationshipsTo != null && i < relationshipsTo.length; i++) {
  // final ISimpleRelationship relationshipTo = relationshipsTo[i];
  // final String relationshipTypeTo = relationshipTo.getModelType();
  // final String superClassType = relationshipTo.getFrom() != null ?
  // relationshipTo.getFrom().getModelType() : "";
  //
  // if (!(relationshipTypeTo.equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) ||
  // !(superClassType.equals(IModelElementFactory.MODEL_TYPE_CLASS)))
  // continue;
  //
  // final IClass superClass = (IClass) relationshipTo.getFrom();
  // final ArrayList<String> superClassStereotypes = new
  // ArrayList<String>(Arrays.asList(superClass.toStereotypeArray()));
  //
  // if (superClassStereotypes.size() == 0)
  // continue;
  //
  // if (superClassStereotypes.size() > 1)
  // action.setEnabled(false);
  //
  // final String superStereotype = superClassStereotypes.get(0);
  // final List<String> allowedCombinationsSub =
  // OntoUMLConstraintsManager.getAllowedActionsBasedOnParent(superStereotype);
  //
  // if (allowedCombinationsSub == null || !allowedCombinationsSub.contains(action.getActionId()))
  // action.setEnabled(false);
  // }
  //
  // for (int i = 0; relationshipsFrom != null && i < relationshipsFrom.length; i++) {
  // final ISimpleRelationship relationshipFrom = relationshipsFrom[i];
  // final String relationshipTypeFrom = relationshipFrom.getModelType();
  // final String subClassType = relationshipFrom.getTo() != null ?
  // relationshipFrom.getFrom().getModelType() : "";
  //
  // if (!(relationshipTypeFrom.equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) ||
  // !(subClassType.equals(IModelElementFactory.MODEL_TYPE_CLASS)))
  // continue;
  //
  // final IClass subClass = (IClass) relationshipFrom.getTo();
  // final ArrayList<String> subClassStereotypes = new
  // ArrayList<String>(Arrays.asList(subClass.toStereotypeArray()));
  //
  // if (subClassStereotypes.size() == 0)
  // continue;
  //
  // if (subClassStereotypes.size() > 1)
  // action.setEnabled(false);
  //
  // final String subStereotype = subClassStereotypes.get(0);
  // final List<String> allowedCombinationsSuper =
  // OntoUMLConstraintsManager.getAllowedActionsBasedOnChild(subStereotype);
  //
  // if (allowedCombinationsSuper == null ||
  // !allowedCombinationsSuper.contains(action.getActionId()))
  // action.setEnabled(false);
  // }
  //
  // }

}
