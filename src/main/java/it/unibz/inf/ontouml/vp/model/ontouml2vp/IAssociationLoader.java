package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.isWholeEnd;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.loadName;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.logElementCreation;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.Element;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Property;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.util.Optional;

public class IAssociationLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IAssociation importElement(Relation fromRelation) {
    logElementCreation(fromRelation);

    IAssociation toRelation = getOrCreateAssociation(fromRelation);
    fromRelation.setId(toRelation.getId());

    loadName(fromRelation, toRelation);

    loadSource(fromRelation, toRelation);
    loadTarget(fromRelation, toRelation);

    boolean isDerived = fromRelation.isDerived();
    toRelation.setDerived(isDerived);

    boolean isAbstract = fromRelation.isAbstract();
    toRelation.setAbstract(isAbstract);

    fromRelation
        .getStereotype()
        .ifPresent(stereotype -> StereotypesManager.applyStereotype(toRelation, stereotype));

    Property relationSource = fromRelation.getSourceEnd();
    Property relationTarget = fromRelation.getTargetEnd();
    IAssociationEnd sourceEnd = getSourceEnd(relationSource, toRelation);
    IAssociationEnd targetEnd = getTargetEnd(relationTarget, toRelation);

    loadEndProperties(relationSource, sourceEnd);
    loadEndProperties(relationTarget, targetEnd);
    enforceNavigability(toRelation);

    ITaggedValueLoader.loadTaggedValues(fromRelation, toRelation);

    return toRelation;
  }

  private static IAssociationEnd getSourceEnd(Property sourceProperty, IAssociation relation) {
    IAssociationEnd fromEnd = (IAssociationEnd) relation.getFromEnd();
    IAssociationEnd toEnd = (IAssociationEnd) relation.getToEnd();
    String fromId =
        Optional.ofNullable(relation.getFrom()).map(IModelElement::getId).orElse("noend");
    String toId =
        Optional.ofNullable(relation.getToEnd()).map(IModelElement::getId).orElse("noend");
    String classId = sourceProperty.getPropertyType().map(Element::getId).orElse("noid");

    if (fromId.equals(classId) && !toId.equals(classId)) return fromEnd;
    if (toId.equals(classId) && !fromId.equals(classId)) return toEnd;
    return fromEnd;
  }

  private static IAssociationEnd getTargetEnd(Property targetProperty, IAssociation relation) {
    IAssociationEnd fromEnd = (IAssociationEnd) relation.getFromEnd();
    IAssociationEnd toEnd = (IAssociationEnd) relation.getToEnd();
    String fromId =
        Optional.ofNullable(relation.getFrom()).map(IModelElement::getId).orElse("noend");
    String toId = Optional.ofNullable(relation.getTo()).map(IModelElement::getId).orElse("noend");
    String classId = targetProperty.getPropertyType().map(Element::getId).orElse("noid");

    if (toId.equals(classId) && !fromId.equals(classId)) return toEnd;
    if (fromId.equals(classId) && !toId.equals(classId)) return fromEnd;
    return toEnd;
  }

  private static void enforceNavigability(IAssociation association) {
    IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
    IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

    fromEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);

    if (!isWholeEnd(toEnd) && !isWholeEnd(fromEnd)) {
      toEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAVIGABLE);
    } else {
      toEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
    }
  }

  private static void loadEndProperties(Property fromProperty, IAssociationEnd toProperty) {
    loadName(fromProperty, toProperty);

    IMultiplicity detail = toProperty.getMultiplicityDetail();
    if (detail == null) {
      detail = IModelElementFactory.instance().createMultiplicity();
      toProperty.setMultiplicityDetail(detail);
    }

    boolean isDerived = fromProperty.isDerived();
    toProperty.setDerived(isDerived);

    boolean isReadOnly = fromProperty.isReadOnly();
    toProperty.setReadOnly(isReadOnly);

    boolean isOrdered = fromProperty.isOrdered();
    toProperty.getMultiplicityDetail().setOrdered(isOrdered);

    fromProperty
        .getAggregationKind()
        .map(agg -> agg.getName().toLowerCase())
        .ifPresent(agg -> toProperty.setAggregationKind(agg));

    fromProperty.getCardinalityValue().ifPresent(value -> toProperty.setMultiplicity(value));
    ITaggedValueLoader.loadTaggedValues(fromProperty, toProperty);
  }

  private static void loadSource(Relation relation, IAssociation association) {
    Classifier<?, ?> relationSource = relation.getSource();
    IModelElement associationSource = vpProject.getModelElementById(relationSource.getId());

    String associationSourceId =
        Optional.ofNullable(associationSource.getId()).orElse("nosourceid");
    String associationFromId =
        Optional.ofNullable(association.getFrom()).map(IModelElement::getId).orElse("nofromid");

    if (associationFromId.equals(associationSourceId)) association.setFrom(associationSource);
    else association.setTo(associationSource);
  }

  private static void loadTarget(Relation relation, IAssociation association) {
    Classifier<?, ?> relationTarget = relation.getTarget();
    IModelElement associationTarget = vpProject.getModelElementById(relationTarget.getId());

    String associationTargetId =
        Optional.ofNullable(associationTarget.getId()).orElse("notargetid");
    String associationToId =
        Optional.ofNullable(association.getTo()).map(IModelElement::getId).orElse("notoid");

    if (associationToId.equals(associationTargetId)) association.setTo(associationTarget);
    else association.setFrom(associationTarget);
  }

  private static IAssociation getOrCreateAssociation(Relation fromRelation) {
    IModelElement toRelation = vpProject.getModelElementById(fromRelation.getId());

    if (toRelation instanceof IAssociation) {
      System.out.println("Relation " + fromRelation.getId() + " exists! Let's update it!");
    } else {
      System.out.println("Relation " + fromRelation.getId() + " not found! Let's create it");
      toRelation = IModelElementFactory.instance().createAssociation();
    }

    return (IAssociation) toRelation;
  }
}
