package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import java.util.*;

public class StereotypesManager {

  private static Map<String, IStereotype> STEREOTYPE_ELEMENTS = null;

  // Meta-properties names
  public static final String PROPERTY_RESTRICTED_TO = "restrictedTo";
  public static final String PROPERTY_IS_EXTENSIONAL = "isExtensional";
  public static final String PROPERTY_IS_POWERTYPE = "isPowertype";
  public static final String PROPERTY_ORDER = "order";

  public static Set<String> CLASS_TAGGED_VALUES =
      Set.of(
          PROPERTY_RESTRICTED_TO, PROPERTY_IS_EXTENSIONAL, PROPERTY_IS_POWERTYPE, PROPERTY_ORDER);

  /** Method to be called whenever a project is opened to properly install all stereotypes. */
  public static void generate() {
    final IProject project = ApplicationManager.instance().getProjectManager().getProject();
    final IModelElement[] installedStereotypes =
        project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
    final Map<String, IStereotype> stereotypesMap = new HashMap<>();

    // Retrieves IStereotype objects for OntoUML elements
    for (IModelElement stereotype : installedStereotypes) {
      if (Stereotype.isOntoumlStereotype((IStereotype) stereotype)) {
        stereotypesMap.put(stereotype.getName(), (IStereotype) stereotype);
      }
    }

    // Creates missing IStereotype objects for OntoUML classes
    for (String ontoUMLClassStereotype : Stereotype.getOntoumlClassStereotypeNames()) {
      if (stereotypesMap.get(ontoUMLClassStereotype) == null) {
        final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
        newStereotypeElement.setName(ontoUMLClassStereotype);
        newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);
        stereotypesMap.put(ontoUMLClassStereotype, newStereotypeElement);
      }
    }

    for (String ontoUMLAssociationStereotype : Stereotype.getOntoumlAssociationStereotypeNames()) {
      if (stereotypesMap.get(ontoUMLAssociationStereotype) == null) {
        final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
        newStereotypeElement.setName(ontoUMLAssociationStereotype);
        newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
        stereotypesMap.put(ontoUMLAssociationStereotype, newStereotypeElement);
      }
    }

    for (String ontoUMLAttributeStereotype : Stereotype.getOntoumlAttributeStereotypeNames()) {
      if (stereotypesMap.get(ontoUMLAttributeStereotype) == null) {
        final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
        newStereotypeElement.setName(ontoUMLAttributeStereotype);
        newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_ATTRIBUTE);
        stereotypesMap.put(ontoUMLAttributeStereotype, newStereotypeElement);
      }
    }

    // Checks and adds missing tagged value definitions to IStereotype objects
    final List<String> taggedStereotypeNames = Stereotype.getOntoumlClassStereotypeNames();

    for (String stereotypeName : taggedStereotypeNames) {
      final IStereotype stereotype = stereotypesMap.get(stereotypeName);
      ITaggedValueDefinitionContainer definitionsContainer = stereotype.getTaggedValueDefinitions();

      if (definitionsContainer == null) {
        definitionsContainer =
            IModelElementFactory.instance().createTaggedValueDefinitionContainer();
      }

      final ITaggedValueDefinition[] definitionsArray =
          definitionsContainer.toTaggedValueDefinitionArray();
      final Map<String, ITaggedValueDefinition> definitions = new HashMap<>();

      for (int j = 0; definitionsArray != null && j < definitionsArray.length; j++) {
        definitions.put(definitionsArray[j].getName(), definitionsArray[j]);
      }

      // Removes deprecated references to "allowed"
      if (definitions.containsKey("allowed")) {
        final ITaggedValueDefinition allowed = definitions.get("allowed");
        allowed.delete();
      }

      // Adds "restrictedTo" to all IStereotype objects
      if (!definitions.containsKey(PROPERTY_RESTRICTED_TO)) {
        final ITaggedValueDefinition restrictedTo =
            IModelElementFactory.instance().createTaggedValueDefinition();
        restrictedTo.setName(PROPERTY_RESTRICTED_TO);
        restrictedTo.setType(ITaggedValueDefinition.TYPE_TEXT);
        restrictedTo.setDefaultValue(RestrictedTo.getDefaultRestrictedTo(stereotypeName));
        restrictedTo.setTagDefStereotype(stereotype);
        definitionsContainer.addTaggedValueDefinition(restrictedTo);
      }

      // Adds "isExtensional" to all IStereotype where
      // RestrictedTo.COLLECTIVE is a possible value
      List<String> possibleRestrictedToValues =
          RestrictedTo.possibleRestrictedToValues(stereotypeName);
      if (possibleRestrictedToValues.contains(RestrictedTo.COLLECTIVE)
          && !definitions.containsKey(PROPERTY_IS_EXTENSIONAL)) {
        final ITaggedValueDefinition isExtensional =
            IModelElementFactory.instance().createTaggedValueDefinition();
        isExtensional.setName(PROPERTY_IS_EXTENSIONAL);
        isExtensional.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
        isExtensional.setDefaultValue("false");
        isExtensional.setTagDefStereotype(stereotype);
        definitionsContainer.addTaggedValueDefinition(isExtensional);
      }

      // Adds "isPowertype" to all STR_TYPE IStereotype
      if (stereotype.getName().equals(Stereotype.TYPE)
          && !definitions.containsKey(PROPERTY_IS_POWERTYPE)) {
        final ITaggedValueDefinition isPowertype =
            IModelElementFactory.instance().createTaggedValueDefinition();
        isPowertype.setName(PROPERTY_IS_POWERTYPE);
        isPowertype.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
        isPowertype.setDefaultValue("false");
        isPowertype.setTagDefStereotype(stereotype);
        definitionsContainer.addTaggedValueDefinition(isPowertype);
      }

      // Adds "order" to all STR_TYPE IStereotype
      if (stereotype.getName().equals(Stereotype.TYPE)
          && !definitions.containsKey(PROPERTY_ORDER)) {
        final ITaggedValueDefinition order =
            IModelElementFactory.instance().createTaggedValueDefinition();
        order.setName(PROPERTY_ORDER);
        order.setType(ITaggedValueDefinition.TYPE_TEXT);
        order.setDefaultValue("2");
        order.setTagDefStereotype(stereotype);
        definitionsContainer.addTaggedValueDefinition(order);
      }

      stereotype.setTaggedValueDefinitions(definitionsContainer);
    }

    System.out.println("KEYS: " + stereotypesMap.keySet());

    // generated() is called when any project is "newed" (which includes opening)
    // and may trigger a NullPointerException when the project is in fact new
    // so we have to be sure that the map is null until it is properly updated
    STEREOTYPE_ELEMENTS = stereotypesMap;
  }

  public static void applyStereotype(IModelElement element, String stereotypeName) {
    final IStereotype stereotype = getStereotype(stereotypeName);

    if (stereotype == null || !element.getModelType().equals(stereotype.getBaseType())) {
      return;
    }

    System.out.println("Applying stereotype: " + stereotype.getName());
    ITaggedValueDefinitionContainer definitionContainer = stereotype.getTaggedValueDefinitions();
    if (definitionContainer != null) {
      Iterator<?> iterator = definitionContainer.taggedValueDefinitionIterator();
      while (iterator != null && iterator.hasNext()) {
        ITaggedValueDefinition definition = (ITaggedValueDefinition) iterator.next();
        System.out.println("\tDefined tagged value: " + definition.getName());
      }
    }

    IModelElementFactory factory = IModelElementFactory.instance();
    ITaggedValueContainer container = element.getTaggedValues();
    Map<String, Object> taggedValueMap = new HashMap<>();

    if (container == null) {
      container = factory.createTaggedValueContainer();
      element.setTaggedValues(container);
    }

    if (element instanceof IClass) {
      int count = element.getTaggedValues().taggedValueCount();
      System.out.println("Initial number of tagged values: " + count);
      ITaggedValue[] taggedValues = container.toTaggedValueArray();

      // 1. Saves and deletes tagged values associated to a stereotype
      for (ITaggedValue tv : taggedValues) {
        final boolean isAllowedTag = tv.getName().equals("allowed");
        final boolean isOntoUMLTag = CLASS_TAGGED_VALUES.contains(tv.getName());
        final boolean isAssociatedToStereotype = tv.getTagDefinition() != null;

        if (isAllowedTag) {
          System.out.println(tv.getName() + ": allowed");
          taggedValueMap.put(tv.getName(), tv.getValue());
          container.removeTaggedValue(tv);
          tv.delete();
        } else if (isOntoUMLTag) {
          // It does not remove non-OntoUML tags
          System.out.println(tv.getName() + ": has tag definition");

          if (isAssociatedToStereotype) {
            taggedValueMap.put(tv.getName(), tv.getValue());
          }

          container.removeTaggedValue(tv);
          tv.delete();
        }
      }
    }

    // 2. Removes old stereotypes
    if (element.stereotypeCount() > 0) {
      for (IStereotype s : element.toStereotypeModelArray()) {
        element.removeStereotype(s);
      }
    }

    // 3. Adds new stereotype
    element.addStereotype(stereotype);

    // 4. Reapply values of tagged values related to the stereotypes (e.g. restrictedTo, order,
    // isPowertype, isExtensional)
    if (element.getTaggedValues() != null) {
      int count = element.getTaggedValues().taggedValueCount();
      System.out.println("Number of tagged values: " + count);
      ITaggedValue[] taggedValues = element.getTaggedValues().toTaggedValueArray();

      for (ITaggedValue taggedValue : taggedValues) {
        Object oldValue = taggedValueMap.get(taggedValue.getName());
        if (oldValue != null) {
          taggedValue.setValue(oldValue.toString());
        }
      }

      System.out.println("Number of tagged values: " + count);
    }
  }

  public static ITaggedValue reapplyStereotypeAndGetTaggedValue(
      IClass _class, String taggedValueName) {
    reapplyCurrentStereotype(_class);

    ITaggedValue taggedValue = getTaggedValue(_class, taggedValueName);
    if (taggedValue == null) return null;

    // Retrieves desired tagged value
    return getTaggedValue(_class, taggedValueName);
  }

  private static boolean reapplyCurrentStereotype(IClass _class) {
    String stereotype =
        _class.toStereotypeArray() != null && _class.toStereotypeArray().length > 0
            ? _class.toStereotypeArray()[0]
            : null;

    // Escape in case the stereotype is missing or incorrect
    if (stereotype == null || !Stereotype.getOntoumlClassStereotypeNames().contains(stereotype))
      return false;

    System.out.println("Reapplying " + stereotype + " to " + _class.getName());
    // Reapply stereotype making sure that the tagged values are there
    applyStereotype(_class, stereotype);

    return true;
  }

  private static ITaggedValue getTaggedValue(IModelElement element, String name) {
    ITaggedValueContainer container = element.getTaggedValues();

    if (container == null) return null;

    try {
      return container.getTaggedValueByName(name);
    } catch (Exception e) {
      return null;
    }
  }

  private static IStereotype getStereotype(String stereotypeName) {
    IStereotype stereotype =
        STEREOTYPE_ELEMENTS != null ? STEREOTYPE_ELEMENTS.get(stereotypeName) : null;

    if (stereotype == null && Stereotype.getOntoUMLStereotypeNames().contains(stereotypeName)) {
      generate();
      stereotype = STEREOTYPE_ELEMENTS.get(stereotypeName);
    }

    return stereotype;
  }
}
