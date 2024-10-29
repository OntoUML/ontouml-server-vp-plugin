package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Diagram;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import java.beans.PropertyChangeEvent;
import java.util.Set;

public class AssociationEventManager extends ModelElementEventManager {

  // Events on generalizations
  private static final String TARGET_CHANGE_EVENT = "toModel";
  private static final String SOURCE_CHANGE_EVENT = "fromModel";

  private final IAssociation source;

  AssociationEventManager(PropertyChangeEvent event) {
    this.event = event;
    this.source = (IAssociation) event.getSource();
    this.changeEvent = event.getPropertyName();
    this.newValue = event.getNewValue();
    this.oldValue = event.getOldValue();
  }

  @Override
  public void processEvent() {
    if (Association.holdsBetweenClasses(source) && STEREOTYPE_CHANGE_EVENT.equals(changeEvent))
      processStereotypeChange();
  }

  private void processStereotypeChange() {
    // checkSourcesDiagrams();
    //    checkAssociationConsistency();
    // setDefaultAssociationProperties();
  }

  private boolean shouldCheckAssociationConsistency() {
    return Association.isOntoumlAssociation(source)
        || ModelElement.isPresentInOntoumlDiagram(source);
  }

  private void checkAssociationConsistency() {
    if (shouldCheckAssociationConsistency()) {
      checkAggregationPlacement();
      checkAggregationKind();
      checkNavigability();
      checkMultiplicity();
      checkAssociationEndProperties();
    }
  }

  private void checkMultiplicity() {
    if (isSourceEndMultiplicityUnspecified()) {
      final String mult = Association.getDefaultSourceMultiplicity(source);
      Association.getFromEnd(source).setMultiplicity(mult);
    }
    if (isTargetEndMultiplicityUnspecified()) {
      final String mult = Association.getDefaultTargetMultiplicity(source);
      Association.getToEnd(source).setMultiplicity(mult);
    }
  }

  private boolean isSourceEndMultiplicityUnspecified() {
    final IAssociationEnd targetEnd = Association.getToEnd(source);
    return IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(targetEnd.getMultiplicity());
  }

  private boolean isTargetEndMultiplicityUnspecified() {
    final IAssociationEnd targetEnd = Association.getToEnd(source);
    return IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(targetEnd.getMultiplicity());
  }

  private void checkAggregationKind() {
    final IAssociationEnd targetEnd = Association.getToEnd(source);

    if (shouldSetAggregationKind()) {
      final String defaultAggregationKind = Association.getDefaultAggregationKind(source);
      targetEnd.setAggregationKind(defaultAggregationKind);
    } else if (shouldRemoveAggregationKind()) {
      targetEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
    }
  }

  private boolean shouldSetAggregationKind() {
    final IAssociationEnd targetEnd = Association.getToEnd(source);
    final String aggKind = targetEnd.getAggregationKind();
    final boolean isAggregationKindNone = IAssociationEnd.AGGREGATION_KIND_none.equals(aggKind);

    return Association.hasOntoumlStereotype(source)
        && Association.hasMereologyStereotype(source)
        && isAggregationKindNone;
  }

  private boolean shouldRemoveAggregationKind() {
    final IAssociationEnd targetEnd = Association.getToEnd(source);
    final String aggKind = targetEnd.getAggregationKind();
    final boolean isAggregationKindNone = IAssociationEnd.AGGREGATION_KIND_none.equals(aggKind);

    return Association.hasOntoumlStereotype(source)
        && !Association.hasMereologyStereotype(source)
        && !isAggregationKindNone;
  }

  private void checkAssociationEndProperties() {
    final IAssociationEnd sourceEnd = Association.getFromEnd(source);
    final IAssociationEnd targetEnd = Association.getToEnd(source);
    final boolean isSourceReadOnly = sourceEnd.isReadOnly();
    final boolean isTargetReadOnly = targetEnd.isReadOnly();

    if (Association.isSourceAlwaysReadOnly(source) && !isSourceReadOnly) {
      Association.getFromEnd(source).setReadOnly(true);
    }
    if (Association.isTargetAlwaysReadOnly(source) && !isTargetReadOnly) {
      Association.getToEnd(source).setReadOnly(true);
    }
  }

  private void checkAggregationPlacement() {
    throw new RuntimeException("Oops...");
    //    if (hasAggregationOnSource() && !hasAggregationOnTarget()) {
    //      Association.invertAssociation(source, true);
    //    }
  }

  private boolean hasAggregationOnTarget() {
    final IAssociationEnd targetEnd = Association.getToEnd(source);
    return !IAssociationEnd.AGGREGATION_KIND_none.equals(targetEnd.getAggregationKind());
  }

  private boolean hasAggregationOnSource() {
    final IAssociationEnd sourceEnd = Association.getFromEnd(source);
    return !IAssociationEnd.AGGREGATION_KIND_none.equals(sourceEnd.getAggregationKind());
  }

  private void checkNavigability() {
    final IAssociationEnd sourceEnd = Association.getFromEnd(source);
    final IAssociationEnd targetEnd = Association.getToEnd(source);

    if (isSourceEndNavigable()) sourceEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);

    if (hasAggregationOnTarget()) targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
    else targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAVIGABLE);
  }

  private boolean isSourceEndNavigable() {
    return IAssociationEnd.NAVIGABLE_NAVIGABLE == Association.getFromEnd(source).getNavigable();
  }

  private boolean isAssociationView(Object obj) {
    return obj instanceof IAssociationUIModel;
  }

  private void checkAddedAssociationView() {
    if (!isAssociationView(newValue)) return;

    final IDiagramUIModel newDiagram = ((IAssociationUIModel) newValue).getDiagramUIModel();

    if (shouldSetOntoumlDiagram(newDiagram)) {
      Diagram.setOntoumlDiagram(newDiagram);
    }
  }

  private void checkRemovedAssociationView() {
    if (!isAssociationView(oldValue)) return;

    final IDiagramUIModel oldDiagram = ((IAssociationUIModel) oldValue).getDiagramUIModel();

    if (shouldUnsetOntoumlDiagram(oldDiagram)) Diagram.unsetOntoumlDiagram(oldDiagram);
  }

  private boolean shouldUnsetOntoumlDiagram(IDiagramUIModel oldDiagram) {
    return Association.isOntoumlAssociation(source)
        && Diagram.isOntoUMLDiagram(oldDiagram)
        && !Diagram.containsOntoumlElements(oldDiagram);
  }

  private boolean shouldSetOntoumlDiagram(IDiagramUIModel newDiagram) {
    return Association.isOntoumlAssociation(source) && !Diagram.isOntoUMLDiagram(newDiagram);
  }

  private void setSourceDiagramsToOntouml(Set<IDiagramUIModel> diagrams) {
    diagrams.stream().filter(d -> !Diagram.isOntoUMLDiagram(d)).forEach(Diagram::setOntoumlDiagram);
  }

  private void unsetSourceDiagramsToOntouml(Set<IDiagramUIModel> diagrams) {
    diagrams.stream()
        .filter(d -> Diagram.isOntoUMLDiagram(d) && !Diagram.containsOntoumlElements(d))
        .forEach(d -> Diagram.unsetOntoumlDiagram(d));
  }

  private void checkSourcesDiagrams() {
    Set<IDiagramUIModel> diagrams = ModelElement.getDiagrams(source);

    if (Association.isOntoumlAssociation(source)) {
      setSourceDiagramsToOntouml(diagrams);
    } else {
      unsetSourceDiagramsToOntouml(diagrams);
    }
  }
}
