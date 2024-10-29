package it.unibz.inf.ontouml.vp.model.uml;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of ModelElement to handle IAssociation objects to be serialized as
 * ontouml-schema/Association
 *
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 */
public class Association implements ModelElement {

  private final IAssociation sourceModelElement;

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
  private List<Property> properties;

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

  public Association(IAssociation source) {
    this.sourceModelElement = source;

    this.type = ModelElement.TYPE_RELATION;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    addProperty(new Property((IAssociationEnd) source.getFromEnd()));
    addProperty(new Property((IAssociationEnd) source.getToEnd()));

    String[] stereotypes = source.toStereotypeArray();
    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      addStereotype(stereotypes[i]);
    }

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
    setAbstract(source.isAbstract());
    setDerived(source.isDerived());
  }

  public Association(IAssociation source, HashSet<String> modelElements) {
    this.sourceModelElement = source;

    this.type = ModelElement.TYPE_RELATION;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    if (modelElements.contains(source.getFromEnd().getId())) {
      addProperty(new Property((IAssociationEnd) source.getFromEnd()));
    }

    if (modelElements.contains(source.getToEnd().getId())) {
      addProperty(new Property((IAssociationEnd) source.getToEnd()));
    }

    String[] stereotypes = source.toStereotypeArray();
    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      addStereotype(stereotypes[i]);
    }

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
    setAbstract(source.isAbstract());
    setDerived(source.isDerived());
  }

  public static boolean holdsBetweenClasses(IAssociation association) {
    return association.getTo() instanceof IClass && association.getFrom() instanceof IClass;
  }

  @Override
  public String getId() {
    return getSourceModelElement().getId();
  }

  @Override
  public IAssociation getSourceModelElement() {
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
    if (this.stereotypes == null) {
      this.stereotypes = new ArrayList<String>();
    }

    this.stereotypes.add(name);
  }

  public void removeStereotype(String name) {

    if (this.stereotypes.contains(name)) {
      this.stereotypes.remove(name);
    }
  }

  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  public Property getProperty(int position) {
    return this.properties.get(position);
  }

  public void addProperty(Property property) {
    if (this.properties == null) {
      this.properties = new ArrayList<Property>();
    }

    this.properties.add(property);
  }

  public void removeProperty(Property property) {

    if (this.properties.contains(property)) {
      this.properties.remove(property);
    }
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

  public static IClass getFrom(IAssociation association) {
    return (IClass) association.getFrom();
  }

  public static IClass getTo(IAssociation association) {
    return (IClass) association.getTo();
  }

  public static void setFrom(IAssociation association, IClass iClass) {
    association.setFrom(iClass);
  }

  public static void setTo(IAssociation association, IClass iClass) {
    association.setTo(iClass);
  }

  public static IAssociationEnd getFromEnd(IAssociation association) {
    return (IAssociationEnd) association.getFromEnd();
  }

  public static IAssociationEnd getToEnd(IAssociation association) {
    return (IAssociationEnd) association.getToEnd();
  }

  public static List<String> getFromRestrictions(IAssociation association) {
    return Class.getRestrictedToList(getFrom(association));
  }

  public static List<String> getToRestrictions(IAssociation association) {
    return Class.getRestrictedToList(getTo(association));
  }

  public static void invertAssociation(IAssociation association) {
    IAssociationEnd originalSourceEnd = getSourceEnd(association);
    IAssociationEnd originalTargetEnd = getTargetEnd(association);

    String originalSourceAgg = originalSourceEnd.getAggregationKind();
    String originalSourceMult = originalSourceEnd.getMultiplicity();
    int originalSourceNav = originalSourceEnd.getNavigable();

    String originalTargetAgg = originalTargetEnd.getAggregationKind();
    String originalTargetMult = originalTargetEnd.getMultiplicity();
    int originalTargetNav = originalTargetEnd.getNavigable();

    originalSourceEnd.setAggregationKind(originalTargetAgg);
    originalSourceEnd.setMultiplicity(originalTargetMult);
    originalSourceEnd.setNavigable(originalTargetNav);

    originalTargetEnd.setAggregationKind(originalSourceAgg);
    originalTargetEnd.setMultiplicity(originalSourceMult);
    originalTargetEnd.setNavigable(originalSourceNav);
  }

  private static List<AssociationModelDescription> retrieveAndDeleteAssociationModels(
      IAssociation association) {
    final IDiagramElement[] associationModels = association.getDiagramElements();
    final List<AssociationModelDescription> associationModelsDecriptions = new ArrayList<>();

    for (int i = 0; associationModels != null && i < associationModels.length; i++) {
      final IAssociationUIModel associationModel = (IAssociationUIModel) associationModels[i];
      final AssociationModelDescription associationModelDescription =
          new AssociationModelDescription(associationModel);

      associationModelsDecriptions.add(associationModelDescription);
      associationModelDescription.deleteAssociationModel();
    }

    return associationModelsDecriptions;
  }

  public static void setNavigability(IAssociation association) {
    if (!Association.isOntoumlAssociation(association)) {
      return;
    }

    final IAssociationEnd sourceEnd = getFromEnd(association);
    final IAssociationEnd targetEnd = getToEnd(association);
    final String targetAgg = targetEnd.getAggregationKind().toLowerCase();

    sourceEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);

    if (IAssociationEnd.AGGREGATION_KIND_none.equals(targetAgg)) {
      targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
    } else {
      targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
    }
  }

  public static void setDefaultAggregationKind(IAssociation association, boolean forceOverride) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final IAssociationEnd sourceEnd = getFromEnd(association);
    final IAssociationEnd targetEnd = getToEnd(association);
    String aggregationKind = IAssociationEnd.AGGREGATION_KIND_none;

    switch (stereotype) {
      case Stereotype.MEMBER_OF:
        aggregationKind = IAssociationEnd.AGGREGATION_KIND_shared;
        break;
      case Stereotype.COMPONENT_OF:
      case Stereotype.SUB_COLLECTION_OF:
      case Stereotype.SUB_QUANTITY_OF:
      case Stereotype.PARTICIPATIONAL:
        aggregationKind = IAssociationEnd.AGGREGATION_KIND_composite;
        break;
      default:
        if (hasOntoumlStereotype(association)) {
          // When there is a non-parthood stereotype we must remove any aggregation kind
          sourceEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
          targetEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
        }
        // We don't interfere where there is not stereotype
        return;
    }

    if (forceOverride) {
      sourceEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
      targetEnd.setAggregationKind(aggregationKind);
    } else {
      // By not forcing override we keep user-defined aggregation (e.g., "shared")
      // The 16.3 release changes the aggregation kind's string and we must keep backwards
      // compatibility
      final String targetCurrentAggregation =
          targetEnd.getAggregationKind() != null
              ? targetEnd.getAggregationKind().toLowerCase()
              : "none";

      if (IAssociationEnd.AGGREGATION_KIND_none.equals(targetCurrentAggregation)) {
        targetEnd.setAggregationKind(aggregationKind);
      }
    }
  }

  public static void setDefaultMultiplicity(IAssociation association, boolean forceOverride) {
    final IClass source = getFrom(association);
    final IClass target = getTo(association);
    final IAssociationEnd sourceEnd = getFromEnd(association);
    final IAssociationEnd targetEnd = getToEnd(association);
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final String sourceStereotype = ModelElement.getUniqueStereotypeName(source);
    final String targetStereotype = ModelElement.getUniqueStereotypeName(target);
    String sourceMultiplicity = IAssociationEnd.MULTIPLICITY_UNSPECIFIED;
    String targetMultiplicity = IAssociationEnd.MULTIPLICITY_UNSPECIFIED;
    boolean sourceMustBeReadOnly = false;
    boolean targetMustBeReadOnly = false;

    switch (stereotype) {
      case Stereotype.CHARACTERIZATION:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Source: Characterized end
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Target: Mode/Quality end
        targetMustBeReadOnly = true;
        break;

      case Stereotype.COMPARATIVE:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        break;

      case Stereotype.COMPONENT_OF:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        break;

      case Stereotype.MATERIAL:
        if (Stereotype.ROLE.equals(targetStereotype)
            || Stereotype.ROLE_MIXIN.equals(targetStereotype)) {
          sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        } else {
          sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        }
        if (Stereotype.ROLE.equals(sourceStereotype)
            || Stereotype.ROLE_MIXIN.equals(sourceStereotype)) {
          targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        } else {
          targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        }
        break;

      case Stereotype.EXTERNAL_DEPENDENCE:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY; // Source: Mode/Quality end
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY; // Target: Dependee end
        targetMustBeReadOnly = true;
        break;

      case Stereotype.MEDIATION:
        if (Stereotype.ROLE.equals(targetStereotype)
            || Stereotype.ROLE_MIXIN.equals(targetStereotype)) {
          sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        } else {
          sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        }
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        targetMustBeReadOnly = true;
        break;

      case Stereotype.MEMBER_OF:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        break;

      case Stereotype.SUB_COLLECTION_OF:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        break;

      case Stereotype.SUB_QUANTITY_OF:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        sourceMustBeReadOnly = true;
        break;

      case Stereotype.CREATION:
      case Stereotype.TERMINATION:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Source: Endurant end
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Target: Event end
        sourceMustBeReadOnly = true;
        targetMustBeReadOnly = true;
        break;

      case Stereotype.HISTORICAL_DEPENDENCE:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY; // Source: Depender end
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Target: Dependee end
        targetMustBeReadOnly = true;
        break;

      case Stereotype.MANIFESTATION:
        // Source: Mode/Quality/Relator end
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY; // Target: Event end
        sourceMustBeReadOnly = true;
        break;

      case Stereotype.PARTICIPATION:
        // Source: Endurant end (participant)
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        // Target: Event end
        if (Stereotype.HISTORICAL_ROLE.equals(sourceStereotype)
            || Stereotype.HISTORICAL_ROLE_MIXIN.equals(sourceStereotype)) {
          targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        } else {
          targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        }
        sourceMustBeReadOnly = true;
        break;

      case Stereotype.PARTICIPATIONAL:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        sourceMustBeReadOnly = true;
        targetMustBeReadOnly = true;
        break;

      case Stereotype.INSTANTIATION:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY; // Source: lower order type
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY; // Target: higher order type
        break;

      case Stereotype.BRINGS_ABOUT:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Source: event
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Target: situation
        sourceMustBeReadOnly = true;
        targetMustBeReadOnly = true;
        break;

      case Stereotype.TRIGGERS:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Source: situation
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE; // Target: event
        sourceMustBeReadOnly = true;
        break;

      default:
        return;
    }

    if (forceOverride) {
      sourceEnd.setMultiplicity(sourceMultiplicity);
      targetEnd.setMultiplicity(targetMultiplicity);
    } else {
      if (IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(sourceEnd.getMultiplicity())) {
        sourceEnd.setMultiplicity(sourceMultiplicity);
      }
      if (IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(targetEnd.getMultiplicity())) {
        targetEnd.setMultiplicity(targetMultiplicity);
      }
    }

    if (sourceMustBeReadOnly) {
      sourceEnd.setReadOnly(true);
    }

    if (targetMustBeReadOnly) {
      targetEnd.setReadOnly(true);
    }
  }

  public static String getDefaultSourceMultiplicity(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final String targetStereotype =
        ModelElement.getUniqueStereotypeName(Association.getTo(association));

    switch (stereotype != null ? stereotype : "") {
      case Stereotype.CHARACTERIZATION:
      case Stereotype.SUB_COLLECTION_OF:
      case Stereotype.SUB_QUANTITY_OF:
      case Stereotype.CREATION:
      case Stereotype.TERMINATION:
      case Stereotype.BRINGS_ABOUT:
      case Stereotype.TRIGGERS:
        return IAssociationEnd.MULTIPLICITY_ONE;
      case Stereotype.COMPONENT_OF:
      case Stereotype.MEMBER_OF:
      case Stereotype.MANIFESTATION:
      case Stereotype.PARTICIPATION:
      case Stereotype.PARTICIPATIONAL:
        return IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
      case Stereotype.COMPARATIVE:
      case Stereotype.EXTERNAL_DEPENDENCE:
      case Stereotype.HISTORICAL_DEPENDENCE:
      case Stereotype.INSTANTIATION:
        return IAssociationEnd.MULTIPLICITY_MANY;
      case Stereotype.MATERIAL:
      case Stereotype.MEDIATION:
        return Stereotype.ROLE.equals(targetStereotype)
                || Stereotype.ROLE_MIXIN.equals(targetStereotype)
            ? IAssociationEnd.MULTIPLICITY_ONE_TO_MANY
            : IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
    }

    return IAssociationEnd.MULTIPLICITY_MANY;
  }

  public static String getDefaultTargetMultiplicity(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final String sourceStereotype =
        ModelElement.getUniqueStereotypeName(Association.getFrom(association));

    switch (stereotype != null ? stereotype : "") {
      case Stereotype.TRIGGERS:
        return IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE;
      case Stereotype.CHARACTERIZATION:
      case Stereotype.COMPONENT_OF:
      case Stereotype.MEDIATION:
      case Stereotype.SUB_COLLECTION_OF:
      case Stereotype.SUB_QUANTITY_OF:
      case Stereotype.CREATION:
      case Stereotype.TERMINATION:
      case Stereotype.HISTORICAL_DEPENDENCE:
      case Stereotype.PARTICIPATIONAL:
      case Stereotype.BRINGS_ABOUT:
        return IAssociationEnd.MULTIPLICITY_ONE;
      case Stereotype.EXTERNAL_DEPENDENCE:
      case Stereotype.MEMBER_OF:
      case Stereotype.INSTANTIATION:
        return IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
      case Stereotype.COMPARATIVE:
      case Stereotype.MANIFESTATION:
        return IAssociationEnd.MULTIPLICITY_MANY;
      case Stereotype.MATERIAL:
        return Stereotype.ROLE.equals(sourceStereotype)
                || Stereotype.ROLE_MIXIN.equals(sourceStereotype)
            ? IAssociationEnd.MULTIPLICITY_ONE_TO_MANY
            : IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
      case Stereotype.PARTICIPATION:
        return Stereotype.HISTORICAL_ROLE.equals(sourceStereotype)
                || Stereotype.HISTORICAL_ROLE_MIXIN.equals(sourceStereotype)
            ? IAssociationEnd.MULTIPLICITY_ONE_TO_MANY
            : IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
    }

    return IAssociationEnd.MULTIPLICITY_MANY;
  }

  public static boolean isOntoumlAssociation(IAssociation association) {
    boolean hasSource = getFrom(association) != null;
    boolean hasTarget = getTo(association) != null;
    boolean hasOntoumlStereotype = hasOntoumlStereotype(association);
    boolean hasOntoumlSource = hasSource && Class.isOntoumlClass(getFrom(association));
    boolean hasOntoumlTarget = hasTarget && Class.isOntoumlClass(getTo(association));

    return hasSource && hasTarget && (hasOntoumlStereotype || hasOntoumlSource || hasOntoumlTarget);
  }

  public static boolean hasOntoumlStereotype(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    return stereotype != null
        && Stereotype.getOntoumlAssociationStereotypeNames().contains(stereotype);
  }

  public static boolean hasMereologyStereotype(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    return stereotype != null
        && Stereotype.getOntoUMLMereologyStereotypeNames().contains(stereotype);
  }

  public static boolean hasAggregationOnFromEnd(IAssociation association) {
    return Property.isWholeEnd(getFromEnd(association));
  }

  public static boolean hasAggregationOnToEnd(IAssociation association) {
    return Property.isWholeEnd(getToEnd(association));
  }

  public static boolean isSourceAlwaysReadOnly(IAssociation association) {
    final Set<String> necessarySourceStereotypes =
        Set.of(
            Stereotype.SUB_QUANTITY_OF,
            Stereotype.CREATION,
            Stereotype.TERMINATION,
            Stereotype.MANIFESTATION,
            Stereotype.PARTICIPATION,
            Stereotype.PARTICIPATIONAL,
            Stereotype.BRINGS_ABOUT,
            Stereotype.TRIGGERS);
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    return stereotype != null && necessarySourceStereotypes.contains(stereotype);
  }

  public static boolean isTargetAlwaysReadOnly(IAssociation association) {
    final Set<String> necessaryTargetStereotypes =
        Set.of(
            Stereotype.CHARACTERIZATION,
            Stereotype.EXTERNAL_DEPENDENCE,
            Stereotype.MEDIATION,
            Stereotype.CREATION,
            Stereotype.TERMINATION,
            Stereotype.HISTORICAL_DEPENDENCE,
            Stereotype.PARTICIPATIONAL,
            Stereotype.BRINGS_ABOUT);
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    return stereotype != null && necessaryTargetStereotypes.contains(stereotype);
  }

  public static String getDefaultAggregationKind(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final Set<String> sharedDefault = Set.of(Stereotype.MEMBER_OF);
    final Set<String> compositeDefault =
        Set.of(
            Stereotype.COMPONENT_OF,
            Stereotype.SUB_COLLECTION_OF,
            Stereotype.SUB_QUANTITY_OF,
            Stereotype.PARTICIPATIONAL);

    if (sharedDefault.contains(stereotype)) return IAssociationEnd.AGGREGATION_KIND_shared;
    if (compositeDefault.contains(stereotype)) return IAssociationEnd.AGGREGATION_KIND_composite;
    return IAssociationEnd.AGGREGATION_KIND_none;
  }

  public static boolean hasNavigableFromEnd(IAssociation association) {
    return Property.isNavigableEnd(getFromEnd(association));
  }

  public static boolean hasNavigableToEnd(IAssociation association) {
    return Property.isNavigableEnd(getToEnd(association));
  }

  public static IAssociationEnd getTargetEnd(IAssociation association) {
    IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
    IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

    if (hasAggregationOnToEnd(association)) {
      return toEnd;
    } else if (hasAggregationOnFromEnd(association)) {
      return fromEnd;
    } else if (hasNavigableToEnd(association)) {
      return toEnd;
    } else if (hasNavigableFromEnd(association)) {
      return fromEnd;
    } else {
      return toEnd;
    }
  }

  public static IAssociationEnd getSourceEnd(IAssociation association) {
    IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
    IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

    return !fromEnd.equals(getTargetEnd(association)) ? fromEnd : toEnd;
  }

  public static IClass getSource(IAssociation association) {
    return (IClass) getSourceEnd(association).getTypeAsElement();
  }

  public static IClass getTarget(IAssociation association) {
    return (IClass) getTargetEnd(association).getTypeAsElement();
  }

  public static boolean doesFromAndSourceMatch(IAssociation association) {
    return association.getFromEnd() != null
        && association.getFromEnd().equals(getSourceEnd(association));
  }

  public static void setSourceEndProperties(IAssociation association, IAssociationEnd sourceEnd) {
    String sourceMultiplicity = sourceEnd.getMultiplicity();

    sourceEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
    sourceEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);

    if (sourceMultiplicity == null
        || IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(sourceMultiplicity)) {
      String defaultSourceMultiplicity = Association.getDefaultSourceMultiplicity(association);
      sourceEnd.setMultiplicity(defaultSourceMultiplicity);
    }

    if (Association.isSourceAlwaysReadOnly(association)) {
      sourceEnd.setReadOnly(true);
    }
  }

  public static void setTargetEndProperties(IAssociation association, IAssociationEnd targetEnd) {
    if (Association.hasMereologyStereotype(association)) {
      if (!Property.isWholeEnd(targetEnd)) {
        String defaultAggKind = Association.getDefaultAggregationKind(association);
        targetEnd.setAggregationKind(defaultAggKind);
      }
      targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
    } else {
      targetEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
      targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAVIGABLE);
    }

    String targetMultiplicity = targetEnd.getMultiplicity();

    if (targetMultiplicity == null
        || IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(targetMultiplicity)) {
      String defaultTargetMultiplicity = Association.getDefaultTargetMultiplicity(association);
      targetEnd.setMultiplicity(defaultTargetMultiplicity);
    }

    if (Association.isTargetAlwaysReadOnly(association)) {
      targetEnd.setReadOnly(true);
    }
  }
}
