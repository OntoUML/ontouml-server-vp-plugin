package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.utils.ConfigurationsUtils;
import it.unibz.inf.ontouml.vp.utils.RestrictedTo;
import it.unibz.inf.ontouml.vp.utils.SmartColoringUtils;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.beans.PropertyChangeEvent;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassEventManager extends ModelElementEventManager {

  // Names of property change events to be handled
  private static final String RESTRICTED_TO_CHANGE_EVENT =
      StereotypesManager.PROPERTY_RESTRICTED_TO;
  private static final String IS_EXTENSIONAL_CHANGE_EVENT =
      StereotypesManager.PROPERTY_IS_EXTENSIONAL;

  // Other constants

  protected final IClass source;

  ClassEventManager(PropertyChangeEvent event) {
    this.event = event;
    this.source = (IClass) event.getSource();
    this.changeEvent = event.getPropertyName();
    this.newValue = event.getNewValue();
    this.oldValue = event.getOldValue();
  }

  @Override
  public void processEvent() {
    switch (changeEvent) {
      case STEREOTYPE_CHANGE_EVENT:
        processStereotypeChange();
        break;
      case RESTRICTED_TO_CHANGE_EVENT:
        processRestrictedToChange();
        break;
      case IS_EXTENSIONAL_CHANGE_EVENT:
        processIsExtensionalChange();
        break;
      case VIEW_ADDED_EVENT:
        processViewAdded();
        break;
      case VIEW_REMOVED_EVENT:
        processViewRemoved();
        break;
    }
  }

  private void processStereotypeChange() {
    if (ConfigurationsUtils.isSmartModelingEnabled()) {
      enforceAndPropagateRestrictedTo();
    }
    // TODO: add set "OntoUML Diagram" check
  }

  private void processRestrictedToChange() {
    if (ConfigurationsUtils.isSmartModelingEnabled()) {
      enforceAndPropagateRestrictedTo();
      resetIsExtensionalValueForNonCollectiveSource();
    }
    paint();
  }

  private void processIsExtensionalChange() {
    if (ConfigurationsUtils.isSmartModelingEnabled()) {
      preventManualChangeToIsExtensionalForNonCollectiveSource();
    }
  }

  private void processViewAdded() {
    paint();
    // TODO: add set "OntoUML Diagram" check
  }

  private void processViewRemoved() {
    // TODO: add unset "OntoUML Diagram" check
  }

  private void paint() {
    if (hasEventChangedValues() && ConfigurationsUtils.isAutomaticColoringEnabled()) {
      SmartColoringUtils.paint(source);
    }
  }

  private void resetIsExtensionalValueForNonCollectiveSource() {
    if (hasEventChangedValues() && !RestrictedTo.COLLECTIVE.equals(newValue)) {
      Class.isExtensional(source, false);
    }
  }

  private void preventManualChangeToIsExtensionalForNonCollectiveSource() {
    if (hasEventChangedValues() && !Class.isRestrictedTo(source, Set.of(RestrictedTo.COLLECTIVE))) {
      Class.isExtensional(source, false);
    }
  }

  private void enforceAndPropagateRestrictedTo() {
    if (!Class.isOntoumlClass(source)) {
      return;
    }

    if (Class.doesItHaveFixedRestrictions(source) && hasEventChangedValues()) {
      enforceFixedRestrictions();
      Class.propagateRestrictionsToChildren(source);
      return;
    }

    if (Class.doesItInheritItsRestrictions(source) && hasEventChangedValues()) {
      inheritRestrictedTo(source);
      Class.propagateRestrictionsToChildren(source);
      return;
    }

    if (Class.canItHaveMultipleRestrictions(source) && hasEventChangedValues()) {
      Class.propagateRestrictionsToChildren(source);
      return;
    }

    if (!Class.doesItHaveFixedRestrictions(source)
        && !Class.doesItInheritItsRestrictions(source)
        && !Class.canItHaveMultipleRestrictions(source)) {
      throwUnaccountedOntoumlStereotype();
    }
  }

  private void throwUnaccountedOntoumlStereotype() {
    throw new RuntimeException("OntoUML not processed due to unaccounted stereotype");
  }

  private void enforceFixedRestrictions() {
    Class.setDefaultRestrictedTo(source);
  }

  public void inheritRestrictedTo(IClass _class) {
    if (!Class.doesItInheritItsRestrictions(_class)) return;

    final Set<IClass> parents =
        Class.getParents(_class).stream().filter(Class::isSortal).collect(Collectors.toSet());
    final String parentsRestrictions = Class.combineClassesRestrictions(parents);
    final String classRestrictions = Class.getRestrictedTo(_class);

    if (!parentsRestrictions.equals(classRestrictions)) {
      Class.setRestrictedTo(_class, parentsRestrictions);
    }
  }
}
