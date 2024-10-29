package it.unibz.inf.ontouml.vp.model.uml;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IMultiplicity;
import com.vp.plugin.model.factory.IModelElementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementation of ModelElement to handle IAtrribute and IAssociationEnd objects to be serialized
 * as ontouml-schema/Property
 *
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 */
public class Property implements ModelElement {

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

  @SerializedName("propertyType")
  @Expose
  private Reference propertyType;

  @SerializedName("cardinality")
  @Expose
  private String cardinality;

  @SerializedName("isDerived")
  @Expose
  private boolean isDerived;

  @SerializedName("isOrdered")
  @Expose
  private boolean isOrdered;

  @SerializedName("isReadOnly")
  @Expose
  private boolean isReadOnly;

  @SerializedName("stereotypes")
  @Expose
  private List<String> stereotypes;

  @SerializedName("propertyAssignments")
  @Expose
  private JsonObject propertyAssignments;

  @SerializedName("subsettedProperties")
  @Expose
  private List<Reference> subsettedProperties;

  @SerializedName("redefinedProperties")
  @Expose
  private List<Reference> redefinedProperties;

  @SerializedName("aggregationKind")
  @Expose
  private String aggregationKind;

  private Property(IModelElement source) {
    this.sourceModelElement = source;

    this.type = ModelElement.TYPE_PROPERTY;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
  }

  public Property(IAttribute source) {
    this((IModelElement) source);

    IModelElement reference = source.getTypeAsElement();
    if (reference != null) {
      setPropertyType(new Reference(reference));
    } else {
      System.out.println(
          "Attribute "
              + source.getParent().getName()
              + "::"
              + source.getName()
              + " type is a non-standard string: "
              + source.getTypeAsString());
    }

    if (!((source.getMultiplicity()).equals(IAttribute.MULTIPLICITY_UNSPECIFIED))) {
      setCardinality(source.getMultiplicity());
    }

    setOrdered(ModelElement.isOrdered(source));
    setDerived(source.isDerived());
    setReadOnly(source.isReadOnly());

    final String[] stereotypes = source.toStereotypeArray();
    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      addStereotype(stereotypes[i]);
    }

    Iterator<?> subsettedIterator = source.subsettedPropertyIterator();
    while (subsettedIterator.hasNext()) {
      IAttribute atr = (IAttribute) subsettedIterator.next();
      addSubsettedProperty(new Reference(atr));
    }

    Iterator<?> redefinedProperties = source.redefinedPropertyIterator();
    while (redefinedProperties.hasNext()) {
      IAttribute rdp = (IAttribute) redefinedProperties.next();
      addRedefinedProperty(new Reference(rdp));
    }

    setAggregationKind(source.getAggregation());
    setDerived(source.isDerived());
  }

  public Property(IAssociationEnd source) {
    this((IModelElement) source);

    IModelElement reference = source.getTypeAsElement();
    if (reference != null) {
      setPropertyType(new Reference(reference));
    } else {
      if (source != null && source.getParent() != null) {
        System.out.println(
            "Association End "
                + source.getParent().getName()
                + "::"
                + source.getName()
                + " type is a non-standard string: "
                + source.getTypeAsString());
      } else {
        System.out.println("Strange...");
      }
    }

    if (!((source.getMultiplicity()).equals(IAttribute.MULTIPLICITY_UNSPECIFIED))) {
      setCardinality(source.getMultiplicity());
    }

    setOrdered(ModelElement.isOrdered(source));
    setDerived(source.isDerived());
    setReadOnly(source.isReadOnly());

    final String[] stereotypes = source.toStereotypeArray();
    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      addStereotype(stereotypes[i]);
    }

    Iterator<?> subsettedIterator = source.subsettedPropertyIterator();

    while (subsettedIterator.hasNext()) {
      IAssociationEnd sub = (IAssociationEnd) subsettedIterator.next();
      addSubsettedProperty(new Reference(sub));
    }

    Iterator<?> redefinedProperties = source.redefinedPropertyIterator();

    while (redefinedProperties.hasNext()) {
      IAssociationEnd rdp = (IAssociationEnd) redefinedProperties.next();
      addRedefinedProperty(new Reference(rdp));
    }

    setAggregationKind(source.getAggregationKind());
  }

  public Property(IAssociationClass associationClass, IModelElement type) {
    this.sourceModelElement = null;

    this.type = ModelElement.TYPE_PROPERTY;
    this.id = associationClass.getId() + type.getId();
    setName(null);
    setDescription(null);

    if (type != null) {
      setPropertyType(new Reference(type));
    }

    setCardinality("0..*");
  }

  public static String getTypeStereotype(IAssociationEnd associationEnd) {
    String noStereotype = "";

    try {
      final IModelElement type = associationEnd.getTypeAsElement();

      if (!type.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
        return noStereotype;
      }

      final String[] stereotypes = ((IClass) type).toStereotypeArray();

      if (stereotypes != null && stereotypes.length == 1) {
        return stereotypes[0];
      }

      return noStereotype;
    } catch (Exception e) {
      return noStereotype;
    }
  }

  public static boolean isOntoumlProperty(IModelElement element) {
    final IModelElement parent = element != null ? element.getParent() : null;
    return ModelElement.isOntoumlElement(parent);
  }

  @Override
  public String getId() {
    return getSourceModelElement().getId();
  }

  @Override
  public IModelElement getSourceModelElement() {
    return this.sourceModelElement;
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

  public Reference getPropertyType() {
    return propertyType;
  }

  public void setPropertyType(Reference propertyType) {
    this.propertyType = propertyType;
  }

  public String getCardinality() {
    return cardinality;
  }

  public void setCardinality(String cardinality) {
    this.cardinality = cardinality;
  }

  public boolean isDerived() {
    return isDerived;
  }

  public void setDerived(boolean isDerived) {
    this.isDerived = isDerived;
  }

  public boolean isOrdered() {
    return isOrdered;
  }

  public void setOrdered(boolean isOrdered) {
    this.isOrdered = isOrdered;
  }

  public void setOrdered(IMultiplicity multiplicity) {
    this.isOrdered = multiplicity != null && multiplicity.isOrdered();
  }

  public boolean isReadOnly() {
    return isReadOnly;
  }

  public void setReadOnly(boolean isReadOnly) {
    this.isReadOnly = isReadOnly;
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
    if (this.stereotypes == null) {
      this.stereotypes = new ArrayList<String>();
    }

    this.stereotypes.add(name);
  }

  public void removeStereotype(String name) {
    if (this.stereotypes != null) {
      this.stereotypes.remove(name);
    }
  }

  public JsonObject getPropertyAssignments() {
    return propertyAssignments;
  }

  public void setPropertyAssignments(JsonObject propertyAssignments) {
    this.propertyAssignments = propertyAssignments;
  }

  public List<Reference> getSubsettedProperties() {
    return subsettedProperties;
  }

  public void setSubsettedProperties(List<Reference> subsettedProperties) {
    this.subsettedProperties = subsettedProperties;
  }

  public void addSubsettedProperty(Reference ref) {
    if (this.subsettedProperties == null) {
      this.subsettedProperties = new ArrayList<Reference>();
    }

    this.subsettedProperties.add(ref);
  }

  public void removeSubsettedProperty(Reference ref) {
    if (this.subsettedProperties != null) {
      this.subsettedProperties.remove(ref);
    }
  }

  public List<Reference> getRedefinedProperties() {
    return redefinedProperties;
  }

  public void setRedefinedProperties(List<Reference> redefinedPropeties) {
    this.redefinedProperties = redefinedPropeties;
  }

  public void addRedefinedProperty(Reference ref) {
    if (this.redefinedProperties == null) {
      this.redefinedProperties = new ArrayList<Reference>();
    }

    this.redefinedProperties.add(ref);
  }

  public void removeRedefinedProperty(Reference ref) {
    if (this.redefinedProperties != null) {
      this.redefinedProperties.remove(ref);
    }
  }

  public String getAggregationKind() {
    return aggregationKind;
  }

  public void setAggregationKind(int aggregation) {
    switch (aggregation) {
      case 0:
        this.aggregationKind = IAssociationEnd.AGGREGATION_KIND_none;
        break;
      case 1:
        this.aggregationKind = IAssociationEnd.AGGREGATION_KIND_shared;
        break;
      case 2:
        this.aggregationKind = IAssociationEnd.AGGREGATION_KIND_composite;
        break;
      default:
    }
  }

  public void setAggregationKind(String aggregationKind) {
    if (aggregationKind == null) {
      this.aggregationKind = null;
      return;
    }

    switch (aggregationKind.toLowerCase()) {
      case IAssociationEnd.AGGREGATION_KIND_none:
        this.aggregationKind = IAssociationEnd.AGGREGATION_KIND_none;
        return;
      case IAssociationEnd.AGGREGATION_KIND_shared:
        this.aggregationKind = IAssociationEnd.AGGREGATION_KIND_shared;
        return;
      case IAssociationEnd.AGGREGATION_KIND_composite:
        this.aggregationKind = IAssociationEnd.AGGREGATION_KIND_composite;
        return;
      default:
        this.aggregationKind = null;
    }
  }

  public String getType() {
    return type;
  }

  public static void removeRedefinedProperties(IAssociationEnd associationEnd) {
    var array = associationEnd.toRedefinedPropertyArray();

    if (array == null) {
      return;
    }

    Stream.of(array).forEach(redefined -> associationEnd.removeRedefinedProperty(redefined));
  }

  public static void removeSubsettedProperties(IAssociationEnd associationEnd) {
    var array = associationEnd.toSubsettedPropertyArray();

    if (array == null) {
      return;
    }

    Stream.of(array).forEach(subsetted -> associationEnd.removeSubsettedProperty(subsetted));
  }

  public static void addRedefinedProperties(
      IAssociationEnd associationEnd, IModelElement[] redefinedProperties) {

    if (redefinedProperties == null || associationEnd == null) return;

    Stream.of(redefinedProperties)
        .filter(prop -> prop instanceof IAssociationEnd || prop instanceof IAttribute)
        .forEach(prop -> associationEnd.addRedefinedProperty((IAssociationEnd) prop));
  }

  public static void addSubsettedProperties(
      IAssociationEnd associationEnd, IModelElement[] subsettedProperties) {

    if (subsettedProperties == null || associationEnd == null) return;

    Stream.of(subsettedProperties)
        .filter(prop -> prop instanceof IAssociationEnd || prop instanceof IAttribute)
        .forEach(prop -> associationEnd.addSubsettedProperty((IAssociationEnd) prop));
  }

  public static boolean isWholeEnd(IAssociationEnd associationEnd) {
    String aggregationKind =
        Optional.ofNullable(associationEnd.getAggregationKind())
            .map(String::toLowerCase)
            .orElse("");

    return IAssociationEnd.AGGREGATION_KIND_shared.equals(aggregationKind)
        || IAssociationEnd.AGGREGATION_KIND_composite.equals(aggregationKind);
  }

  public static boolean isNavigableEnd(IAssociationEnd associationEnd) {
    return IAssociationEnd.NAVIGABLE_NAVIGABLE == associationEnd.getNavigable();
  }
}
