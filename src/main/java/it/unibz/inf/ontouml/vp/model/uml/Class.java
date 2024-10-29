package it.unibz.inf.ontouml.vp.model.uml;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.utils.RestrictedTo;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of ModelElement to handle IClass and IDataType objects to be serialized as
 * ontouml-schema/Class
 *
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 */
public class Class implements ModelElement {

  private static final Set<String> STEREOTYPES_WITH_FIXED_RESTRICTED_TO_VALUE =
      Set.of(
          Stereotype.EVENT,
          Stereotype.SITUATION,
          Stereotype.TYPE,
          Stereotype.ABSTRACT,
          Stereotype.DATATYPE,
          Stereotype.ENUMERATION,
          Stereotype.KIND,
          Stereotype.COLLECTIVE,
          Stereotype.QUANTITY,
          Stereotype.RELATOR,
          Stereotype.MODE,
          Stereotype.QUALITY);
  private static final Set<String> STEREOTYPES_WITH_MULTIPLE_RESTRICTED_TO_VALUES =
      Set.of(
          Stereotype.CATEGORY,
          Stereotype.MIXIN,
          Stereotype.PHASE_MIXIN,
          Stereotype.ROLE_MIXIN,
          Stereotype.HISTORICAL_ROLE_MIXIN);
  private static final Set<String> STEREOTYPES_WITH_INHERITED_RESTRICTED_TO_VALUE =
      Set.of(Stereotype.SUBKIND, Stereotype.ROLE, Stereotype.PHASE, Stereotype.HISTORICAL_ROLE);

  private final IModelElement sourceModelElement;

  @SerializedName("type")
  @Expose
  private final String type;

  @SerializedName("id")
  @Expose
  private final String id;

  @SerializedName("name")
  @Expose
  private String name;

  @SerializedName("description")
  @Expose
  private String description;

  @SerializedName("properties")
  @Expose
  private Set<Property> properties;

  @SerializedName("literals")
  @Expose
  private LinkedList<Literal> literals;

  @SerializedName("propertyAssignments")
  @Expose
  private JsonObject propertyAssignments;

  @SerializedName("stereotypes")
  @Expose
  private List<String> stereotypes;

  @SerializedName("isAbstract")
  @Expose
  private boolean isAbstract;

  @SerializedName("isDerived")
  @Expose
  private boolean isDerived;

  @SerializedName("allowed")
  @Expose
  private JsonArray restrictedTo;

  @SerializedName("isExtensional")
  @Expose
  private JsonElement isExtensional;

  @SerializedName("isPowertype")
  @Expose
  private JsonElement isPowertype;

  @SerializedName("order")
  @Expose
  private String order;

  private Class(IModelElement source) {
    this.sourceModelElement = source;
    this.type = ModelElement.TYPE_CLASS;
    this.id = source.getId();
    this.properties = null;
    this.stereotypes = null;
    this.literals = null;
  }

  public Class(IClass source) {
    this((IModelElement) source);

    final IAttribute[] attributes = source.toAttributeArray();
    for (int i = 0; attributes != null && i < attributes.length; i++) {
      addProperties(new Property(attributes[i]));
    }

    final String[] stereotypes = source.toStereotypeArray();
    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      addStereotype(stereotypes[i]);
    }

    if (this.stereotypes != null && this.stereotypes.contains(Stereotype.ENUMERATION)) {
      IEnumerationLiteral[] literalArray = source.toEnumerationLiteralArray();
      for (int i = 0; literalArray != null && i < literalArray.length; i++)
        addLiteral(new Literal(literalArray[i]));
    }

    setAbstract(source.isAbstract());
    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));

    if (source.getName().trim().startsWith("/")) {
      setName(source.getName().substring(1));
      this.isDerived = true;
    } else {
      setName(source.getName().trim());
    }

    setDescription(source.getDescription());

    loadTags(source);
  }

  public Class(IDataType source) {
    this((IModelElement) source);

    addStereotype(Stereotype.DATATYPE);
    setAbstract(false);
    setDerived(false);

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));

    if (source.getName().trim().startsWith("/")) {
      setName(source.getName().substring(1));
      this.isDerived = true;
    } else {
      setName(source.getName().trim());
    }

    // TODO: change this to apply only to default VP datatypes
    if (restrictedTo == null) {
      restrictedTo = new JsonArray();
      restrictedTo.add("abstract");
    }
  }

  public Class(IClass source, HashSet<String> modelElements) {
    this((IModelElement) source);

    final IAttribute[] attributes = source.toAttributeArray();
    for (int i = 0; attributes != null && i < attributes.length; i++) {
      if (modelElements.contains(attributes[i].getId())) addProperties(new Property(attributes[i]));
    }

    final String[] stereotypes = source.toStereotypeArray();
    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      addStereotype(stereotypes[i]);
    }

    if (this.stereotypes != null && this.stereotypes.contains(Stereotype.ENUMERATION)) {
      IEnumerationLiteral[] literalArray = source.toEnumerationLiteralArray();
      for (int i = 0; literalArray != null && i < literalArray.length; i++)
        addLiteral(new Literal(literalArray[i]));
    }

    setAbstract(source.isAbstract());
    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));

    if (source.getName().trim().startsWith("/")) {
      setName(source.getName().substring(1));
      this.isDerived = true;
    } else {
      setName(source.getName().trim());
    }

    setDescription(source.getDescription());

    loadTags(source);
  }

  public static Set<IAttribute> getAttributes(IClass _class) {
    return _class.attributeCount() > 0
        ? new HashSet<>(Arrays.asList(_class.toAttributeArray()))
        : Collections.emptySet();
  }

  public static boolean isClass(IModelElement element) {
    return element != null && IModelElementFactory.MODEL_TYPE_CLASS.equals(element.getModelType());
  }

  private void loadTags(IClass source) {
    if (source.getTaggedValues() != null) {
      final JsonParser parser = new JsonParser();

      if (hasRestrictedTo(source)) {
        String restrictedTo = getRestrictedTo(source);

        if (restrictedTo == null) {
          this.restrictedTo = null;
        } else {
          restrictedTo = restrictedTo.trim().replaceAll(" +", ",").replaceAll(",", "\",\"");

          final JsonElement restrictedToArray =
              !restrictedTo.equals("")
                  ? parser.parse("[\"" + restrictedTo + "\"]")
                  : parser.parse("[]");
          this.restrictedTo =
              restrictedToArray.isJsonArray() && ((JsonArray) restrictedToArray).size() > 0
                  ? (JsonArray) restrictedToArray
                  : null;
        }
      }

      if (hasIsExtensional(source)) {
        this.isExtensional = parser.parse("" + isExtensional(source));
      } else {
        this.isExtensional = parser.parse("null");
      }

      if (hasIsPowertype(source)) {
        this.isPowertype = parser.parse("" + isPowertype(source));
      } else {
        this.isPowertype = parser.parse("null");
      }

      if (hasOrder(source)) {
        this.order = getOrder(source);
      }
    }
  }

  @Override
  public IModelElement getSourceModelElement() {
    return this.sourceModelElement;
  }

  @Override
  public String getId() {
    return getSourceModelElement().getId();
  }

  @Override
  public String getOntoUMLType() {
    return this.type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = ModelElement.safeGetString(name);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = ModelElement.safeGetString(description);
    ;
  }

  public Set<Property> getProperties() {
    return properties;
  }

  public void addProperties(Property property) {
    if (this.properties == null) this.properties = new HashSet<>();

    this.properties.add(property);
  }

  public void removeProperties(Property property) {
    if (this.properties != null) this.properties.remove(property);
  }

  public JsonObject getPropertyAssignments() {
    return propertyAssignments;
  }

  public void setPropertyAssignments(JsonObject propertyAssignments) {
    this.propertyAssignments = propertyAssignments;
  }

  public List<String> getStereotypes() {
    return this.stereotypes;
  }

  public void setStereotypes(List<String> stereotypes) {
    this.stereotypes = stereotypes;
  }

  public String getStereotype(int position) {
    return this.stereotypes.get(position);
  }

  public void addStereotype(String name) {
    if (this.stereotypes == null) this.stereotypes = new ArrayList<>();

    this.stereotypes.add(name);
  }

  public void removeStereotype(String name) {
    if (this.stereotypes != null) this.stereotypes.remove(name);
  }

  public boolean isAbstract() {
    return this.isAbstract;
  }

  public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }

  public boolean isDerived() {
    return this.isDerived;
  }

  public void setDerived(boolean isDerived) {
    this.isDerived = isDerived;
  }

  public LinkedList<Literal> getLiterals() {
    return literals;
  }

  public void setLiterals(LinkedList<Literal> literals) {
    this.literals = literals;
  }

  public void addLiteral(Literal literal) {
    if (getLiterals() == null) {
      setLiterals(new LinkedList<>());
    }

    this.literals.add(literal);
  }

  public static Set<IClass> getParents(IClass _class) {
    final Set<IClass> parents = new HashSet<>();
    final ISimpleRelationship[] relationships = _class.toToRelationshipArray();

    for (int i = 0; relationships != null && i < relationships.length; i++) {
      if (relationships[i] instanceof IGeneralization) {
        final IGeneralization generalization = (IGeneralization) relationships[i];
        final IModelElement parent = Generalization.getGeneral(generalization);

        if (parent instanceof IClass) {
          parents.add((IClass) parent);
        }
      }
    }

    return parents;
  }

  public static Set<IClass> getChildren(IClass _class) {
    final Set<IClass> children = new HashSet<>();
    final ISimpleRelationship[] relationships = _class.toFromRelationshipArray();

    for (int i = 0; relationships != null && i < relationships.length; i++) {
      if (relationships[i] instanceof IGeneralization) {
        final IGeneralization generalization = (IGeneralization) relationships[i];
        final IModelElement child = Generalization.getSpecific(generalization);

        if (child instanceof IClass) {
          children.add((IClass) child);
        }
      }
    }

    return children;
  }

  public static Set<IClass> getAncestors(IClass _class) {
    final Set<IClass> ancestors = new HashSet<>();
    final Set<IClass> parents = getChildren(_class);

    for (IClass parent : parents) {
      ancestors.addAll(getDescendants(parent));
      ancestors.add(parent);
    }

    return ancestors;
  }

  public static Set<IClass> getDescendants(IClass _class) {
    final Set<IClass> descendants = new HashSet<>();
    final Set<IClass> children = getChildren(_class);

    for (IClass child : children) {
      descendants.addAll(getDescendants(child));
      descendants.add(child);
    }

    return descendants;
  }

  public static void applyOnChildren(IClass _class, Consumer<IClass> function) {
    final Set<IClass> children = getChildren(_class);

    for (IClass child : children) {
      function.accept(child);
    }
  }

  public static void applyOnParents(IClass _class, Consumer<IClass> function) {
    final Set<IClass> parents = getParents(_class);

    for (IClass parent : parents) {
      function.accept(parent);
    }
  }

  public static void applyOnDescendants(IClass _class, Function<IClass, Boolean> function) {
    final Set<IClass> children = getChildren(_class);

    for (IClass child : children) {
      final boolean shouldContinue = function.apply(child);
      if (shouldContinue) {
        applyOnDescendants(child, function);
      }
    }
  }

  public static void applyOnAncestors(IClass _class, Function<IClass, Boolean> function) {
    final Set<IClass> parents = getParents(_class);

    for (IClass parent : parents) {
      final boolean shouldContinue = function.apply(parent);
      if (shouldContinue) {
        applyOnAncestors(parent, function);
      }
    }
  }

  public static void setRestrictedTo(IClass _class, String restrictions) {
    if (_class.getTaggedValues() == null) {
      return;
    }

    System.out.println("Class '" + _class.getName() + "': Setting restrictedTo to " + restrictions);

    Iterator<?> values = _class.getTaggedValues().taggedValueIterator();

    while (values != null && values.hasNext()) {
      final ITaggedValue value = (ITaggedValue) values.next();

      if (value.getName().equals(StereotypesManager.PROPERTY_RESTRICTED_TO)) {
        final String notNull = restrictions != null ? restrictions : "";
        final List<String> sortList = Arrays.asList(notNull.split("\\s+"));
        Collections.sort(sortList);
        final Set<String> noDuplicates = new LinkedHashSet<>(sortList);
        final String newRestrictions = noDuplicates.toString().replaceAll("[\\[\\],]", "").trim();
        final String currentRestrictions =
            value.getValueAsString() != null ? value.getValueAsString() : "";

        if (!currentRestrictions.equals(newRestrictions)) {
          value.setValue(newRestrictions);
        }

        System.out.println(
            "Class '" + _class.getName() + "': restrictedTo set to " + value.getValueAsText());

        return;
      }
    }
  }

  private static void setDefaultRestrictedTo(IClass element, String stereotypeName) {
    List<String> currentRestrictedTo = getRestrictedToList(element);

    if (RestrictedTo.shouldOverrideRestrictedTo(stereotypeName, currentRestrictedTo)) {
      final String defaultNature = RestrictedTo.getDefaultRestrictedTo(stereotypeName);
      setRestrictedTo(element, defaultNature);
    }
  }

  public static void setDefaultRestrictedTo(IClass _class) {
    setDefaultRestrictedTo(_class, ModelElement.getUniqueStereotypeName(_class));
  }

  public static String getRestrictedTo(IClass _class) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue restrictedTo =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_RESTRICTED_TO)
            : null;

    return restrictedTo != null ? restrictedTo.getValueAsString() : null;
  }

  public static String getRestrictedTo(Set<IClass> classes) {
    String classesRestrictions = "";

    for (IClass _class : classes) {
      final String _classRestrictions = getRestrictedTo(_class);
      if (_classRestrictions != null) {
        classesRestrictions += " " + _classRestrictions;
      }
    }

    classesRestrictions =
        Arrays.stream(classesRestrictions.split("\\s+"))
            .distinct()
            .collect(Collectors.joining(" "));

    return !classesRestrictions.equals("") ? classesRestrictions : null;
  }

  public static List<String> getRestrictedToList(String restrictedTo) {
    if (restrictedTo == null || restrictedTo.equals("")) return Collections.emptyList();

    return Arrays.asList(restrictedTo.split("\\s+"));
  }

  public static List<String> getRestrictedToList(IClass _class) {
    return getRestrictedToList(getRestrictedTo(_class));
  }

  public static boolean hasRestrictedTo(IClass _class) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue restrictedTo =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_RESTRICTED_TO)
            : null;

    return restrictedTo != null;
  }

  public static String getOrder(IClass _class) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue order =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_ORDER)
            : null;

    return order != null ? order.getValueAsString() : null;
  }

  public static Optional<String> getOrderOr(IClass _class) {
    return Optional.ofNullable(getOrder(_class));
  }

  public static void setOrder(IClass _class, String newOrder) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue order =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_ORDER)
            : null;

    if (order != null) {
      order.setValue(newOrder);
    }
  }

  public static boolean hasOrder(IClass _class) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue order =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_ORDER)
            : null;

    return order != null;
  }

  public static boolean isPowertype(IClass _class) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue isPowertype =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_IS_POWERTYPE)
            : null;
    final String isPowertypeValue = isPowertype != null ? isPowertype.getValueAsString() : "";

    return Boolean.parseBoolean(isPowertypeValue);
  }

  public static void setIsPowertype(IClass _class, boolean isPowertypeValue) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue isPowertype =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_IS_POWERTYPE)
            : null;

    if (isPowertype != null) {
      isPowertype.setValue(isPowertypeValue);
    }
  }

  public static boolean hasIsPowertype(IClass _class) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue isPowertype =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_IS_POWERTYPE)
            : null;

    return isPowertype != null;
  }

  public static boolean isExtensional(IClass _class) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue isExtensional =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_IS_EXTENSIONAL)
            : null;
    final String isExtensionalValue = isExtensional != null ? isExtensional.getValueAsString() : "";

    return Boolean.parseBoolean(isExtensionalValue);
  }

  public static void isExtensional(IClass _class, boolean isExtensionalValue) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue isExtensional =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_IS_EXTENSIONAL)
            : null;

    if (isExtensional != null) {
      isExtensional.setValue(isExtensionalValue);
    }
  }

  public static boolean hasIsExtensional(IClass _class) {
    final ITaggedValueContainer container = _class.getTaggedValues();
    final ITaggedValue isExtensional =
        container != null
            ? container.getTaggedValueByName(StereotypesManager.PROPERTY_IS_EXTENSIONAL)
            : null;

    return isExtensional != null;
  }

  public static boolean isOntoumlClass(IClass _class) {
    if (_class == null) return false;

    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return Stereotype.getOntoumlClassStereotypeNames().contains(stereotype);
  }

  public static boolean isRestrictedToEditable(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return RestrictedTo.isRestrictedToEditable(stereotype);
  }

  public static boolean isRestrictedTo(IClass _class, Set<String> restrictions) {
    Set<String> classRestrictions = new HashSet<>(getRestrictedToList(_class));
    return restrictions.equals(classRestrictions);
  }

  public static boolean isAbstractEditable(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return stereotype == null || !Stereotype.isNonSortal(stereotype);
  }

  public static boolean isCollective(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return Stereotype.COLLECTIVE.equals(stereotype);
  }

  public static boolean isType(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return Stereotype.TYPE.equals(stereotype);
  }

  public static boolean hasCollectiveNature(IClass _class) {
    return RestrictedTo.COLLECTIVE.equals(getRestrictedTo(_class));
  }

  public static boolean isBaseSortal(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return Stereotype.isBaseSortal(stereotype);
  }

  public static boolean isUltimateSortal(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return Stereotype.isUltimateSortal(stereotype);
  }

  public static boolean isSortal(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return Stereotype.isSortal(stereotype);
  }

  public static void propagateRestrictionsToChildren(IClass _class) {
    Class.applyOnChildren(
        _class,
        descendent -> {
          if (!doesItInheritItsRestrictions(descendent)) {
            return;
          }

          final Set<IClass> parents =
              Class.getParents(descendent).stream()
                  .filter(Class::isSortal)
                  .collect(Collectors.toSet());
          final String parentsRestrictions = combineClassesRestrictions(parents);
          final String descendentRestrictions = Class.getRestrictedTo(descendent);

          if (!parentsRestrictions.equals(descendentRestrictions)) {
            Class.setRestrictedTo(descendent, parentsRestrictions);
          }
        });
  }

  public static String combineClassesRestrictions(Set<IClass> classes) {
    return classes.stream()
        .flatMap(_class -> new HashSet<>(Class.getRestrictedToList(_class)).stream())
        .sorted()
        .collect(Collectors.joining(" "));
  }

  public static boolean canItHaveMultipleRestrictions(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return STEREOTYPES_WITH_MULTIPLE_RESTRICTED_TO_VALUES.contains(
        stereotype != null ? stereotype : "");
  }

  public static boolean doesItInheritItsRestrictions(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return STEREOTYPES_WITH_INHERITED_RESTRICTED_TO_VALUE.contains(
        stereotype != null ? stereotype : "");
  }

  public static boolean doesItHaveFixedRestrictions(IClass _class) {
    final String stereotype = ModelElement.getUniqueStereotypeName(_class);
    return STEREOTYPES_WITH_FIXED_RESTRICTED_TO_VALUE.contains(
        stereotype != null ? stereotype : "");
  }
}
