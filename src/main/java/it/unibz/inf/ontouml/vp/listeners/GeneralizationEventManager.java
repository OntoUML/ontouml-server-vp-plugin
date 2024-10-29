package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.Generalization;
import java.beans.PropertyChangeEvent;

public class GeneralizationEventManager extends ModelElementEventManager {

  // Events on generalizations
  private static final String GENERAL_CLASSIFIER_CHANGE_EVENT = "toModel";
  private static final String SPECIFIC_CLASSIFIER_CHANGE_EVENT = "fromModel";

  private final IGeneralization source;

  public GeneralizationEventManager(PropertyChangeEvent event) {
    this.event = event;
    this.source = (IGeneralization) event.getSource();
    this.changeEvent = event.getPropertyName();
    this.newValue = event.getNewValue();
    this.oldValue = event.getOldValue();
  }

  @Override
  public void processEvent() {
    switch (changeEvent) {
      case SPECIFIC_CLASSIFIER_CHANGE_EVENT:
        processSpecificClassifierChange();
        break;
      case GENERAL_CLASSIFIER_CHANGE_EVENT:
        processGeneralClassifierChange();
        break;
    }
  }

  private void processGeneralClassifierChange() {
    propagateParentsRestrictions(oldValue);
    propagateParentsRestrictions(newValue);
  }

  private void processSpecificClassifierChange() {
    propagateParentsRestrictions(oldValue);
    propagateParentsRestrictions(newValue);
    propagateParentsRestrictions(Generalization.getSpecific(source));
  }

  private void propagateParentsRestrictions(Object value) {
    if (!(value instanceof IClass)) return;

    IClass _class = (IClass) value;

    if (!Class.isOntoumlClass(_class) || !Class.isBaseSortal(_class)) return;

    Class.getParents(_class).stream()
        .filter(Class::isOntoumlClass)
        .forEach(Class::propagateRestrictionsToChildren);
  }
}
