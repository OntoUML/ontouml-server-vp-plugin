package it.unibz.inf.ontouml.vp.model.ontouml.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlUtils;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.ClassDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.ClassSerializer;
import it.unibz.inf.ontouml.vp.utils.ApplicationManagerUtils;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonSerialize(using = ClassSerializer.class)
@JsonDeserialize(using = ClassDeserializer.class)
public final class Class extends Classifier<Class, ClassStereotype> {

  public static Integer ORDERLESS = Integer.MAX_VALUE;
  public static String ORDERLESS_STRING = "*";

  protected Boolean isExtensional;
  protected Boolean isPowertype;
  protected Integer order;
  protected Set<Nature> restrictedTo = new TreeSet<>();
  protected List<Literal> literals = new ArrayList<>();

  public Class(String id, MultilingualText name, ClassStereotype ontoumlStereotype) {
    super(id, name, ontoumlStereotype);
  }

  public Class(String id, MultilingualText name, String stereotypeName) {
    this(id, name, (ClassStereotype) null);
    setStereotype(stereotypeName);
  }

  public Class(String id, String name, ClassStereotype stereotype) {
    this(id, new MultilingualText(name), stereotype);
  }

  public Class(String id, String name, String stereotype) {
    this(id, new MultilingualText(name), stereotype);
  }

  public Class(String name, ClassStereotype stereotype) {
    this(null, name, stereotype);
  }

  public Class(String name, String stereotype) {
    this(null, name, stereotype);
  }

  public Class(ClassStereotype stereotype) {
    this(null, (MultilingualText) null, stereotype);
  }

  public Class(String name) {
    this(null, name, (ClassStereotype) null);
  }

  public Class() {
    this(null, (MultilingualText) null, (ClassStereotype) null);
  }

  @Override
  public String getType() {
    return "Class";
  }

  public Optional<Boolean> isExtensional() {
    return Optional.ofNullable(isExtensional);
  }

  public void setExtensional(Boolean value) {
    isExtensional = value;
  }

  public Optional<Boolean> isPowertype() {
    return Optional.ofNullable(isPowertype);
  }

  public void setPowertype(Boolean value) {
    isPowertype = value;
  }

  public Optional<Integer> getOrder() {
    return Optional.ofNullable(order);
  }

  public void setOrder(Integer value) {
    order = value;
  }

  public Optional<String> getOrderAsString() {
    if (order == null) return Optional.empty();
    else
      return ORDERLESS.equals(order)
          ? Optional.of(ORDERLESS_STRING)
          : Optional.of(order.toString());
  }

  public Set<Nature> getRestrictedTo() {
    return restrictedTo;
  }

  public void setRestrictedTo(Collection<Nature> restrictedTo) {
    this.restrictedTo.clear();
    this.restrictedTo.addAll(restrictedTo);
  }

  public void setRestrictedTo(String[] array) {
    List<Nature> natures =
        Stream.of(array)
            .map(Nature::findByName)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    this.restrictedTo.clear();
    this.restrictedTo.addAll(natures);
  }

  public void setRestrictedTo(Nature... restrictedTo) {
    this.restrictedTo.clear();
    this.restrictedTo.addAll(Arrays.asList(restrictedTo));
  }

  public List<Literal> getLiterals() {
    return literals;
  }

  public boolean hasLiterals() {
    return literals != null && literals.size() > 0;
  }

  public List<Literal> createLiterals(String[] names) {
    return Stream.of(names).map(name -> createLiteral(name)).collect(Collectors.toList());
  }

  public Literal createLiteral(String name) {
    if (literals == null) literals = new ArrayList<>();

    Literal literal = new Literal(name);
    addLiteral(literal);
    return literal;
  }

  public void addLiteral(Literal literal) {
    if (literals == null) literals = new ArrayList<>();

    literal.setContainer(this);
    literals.add(literal);
  }

  public void setLiterals(Collection<Literal> literals) {
    literals.forEach(l -> l.setContainer(this));
    this.literals.clear();
    this.literals.addAll(literals);
  }

  public boolean hasAttributes() {
    return hasProperties();
  }

  public List<Property> getAttributes() {
    return getProperties();
  }

  public void addAttribute(Property attribute) {
    if (attribute == null) return;

    attribute.setContainer(this);
    properties.add(attribute);
  }

  public void setAttributes(Collection<Property> attributes) {
    if (properties == null) properties = new ArrayList<>();
    else properties.clear();

    if (attributes == null) return;

    attributes.forEach(a -> addAttribute(a));
  }

  public Property createAttribute(String name, Classifier<?, ?> type) {
    return createAttribute(null, name, type);
  }

  public Property createAttribute(String id, String name, Classifier<?, ?> type) {
    Property attribute = new Property(id, name, type);
    attribute.setContainer(this);
    properties.add(attribute);

    return attribute;
  }

  @Override
  public void setStereotype(String stereotypeName) {
    Optional<ClassStereotype> stereotype = ClassStereotype.findByName(stereotypeName);

    stereotype.ifPresentOrElse(
        s -> setOntoumlStereotype(stereotype.get()), () -> setCustomStereotype(stereotypeName));
  }

  @Override
  public List<OntoumlElement> getContents() {
    List<OntoumlElement> contents = new ArrayList<>();

    if (hasAttributes()) OntoumlUtils.addIfNotNull(contents, getAttributes());

    if (hasLiterals()) OntoumlUtils.addIfNotNull(contents, getLiterals());

    return contents;
  }

  public boolean restrictedToOverlaps(List<Nature> natures) {
    if (natures == null) return false;

    TreeSet<Nature> natureSet = new TreeSet<>(natures);
    natureSet.retainAll(restrictedTo);
    return natureSet.size() > 0;
  }

  public boolean restrictedToContainedIn(List<Nature> natures) {
    if (natures == null) return false;

    return natures.containsAll(restrictedTo);
  }

  public boolean restrictedToContains(Nature nature) {
    return restrictedToContains(Collections.singletonList(nature));
  }

  public boolean restrictedToContains(List<Nature> natures) {
    if (natures == null) return false;

    return restrictedTo.containsAll(natures);
  }

  public boolean restrictedToEquals(Nature nature) {
    return restrictedToEquals(Collections.singletonList(nature));
  }

  public boolean restrictedToEquals(List<Nature> natures) {
    if (natures == null) return false;

    if (restrictedTo.size() != natures.size()) return false;

    TreeSet<Nature> naturesSet = new TreeSet<>(natures);
    return restrictedTo.equals(naturesSet);
  }

  public boolean isRestrictedToMoments() {
    return restrictedTo.stream().allMatch(Nature::isMoment);
  }

  public boolean isRestrictedToSubstantials() {
    return restrictedTo.stream().allMatch(Nature::isSubstantial);
  }

  public boolean isEnumeration() {
    return hasStereotype(ClassStereotype.ENUMERATION);
  }

  public boolean isRestrictedToEndurants() {
    return restrictedTo.stream().allMatch(Nature::isEndurant);
  }

  public boolean isRestrictedToFunctionalComplexes() {
    return restrictedToEquals(Nature.FUNCTIONAL_COMPLEX);
  }

  public boolean isRestrictedToCollectives() {
    return restrictedToEquals(Nature.COLLECTIVE);
  }

  public boolean isRestrictedToQuantity() {
    return restrictedToEquals(Nature.QUANTITY);
  }

  public boolean isRestrictedToIntrinsicMoments() {
    return restrictedTo.stream().allMatch(Nature::isIntrinsicMoment);
  }

  public boolean isRestrictedToExtrinsicMoments() {
    return restrictedTo.stream().allMatch(Nature::isExtrinsicMoment);
  }

  public boolean isRestrictedToRelators() {
    return restrictedToEquals(Nature.RELATOR);
  }

  public boolean isRestrictedToIntrinsicModes() {
    return restrictedToEquals(Nature.INTRINSIC_MODE);
  }

  public boolean isRestrictedToExtrinsicModes() {
    return restrictedToEquals(Nature.EXTRINSIC_MODE);
  }

  public boolean isRestrictedToQualities() {
    return restrictedToEquals(Nature.QUALITY);
  }

  public boolean isRestrictedToEvents() {
    return restrictedToEquals(Nature.EVENT);
  }

  public boolean isRestrictedToSituations() {
    return restrictedToEquals(Nature.SITUATION);
  }

  public boolean isRestrictedToTypes() {
    return restrictedToEquals(Nature.TYPE);
  }

  public boolean isRestrictedToAbstract() {
    return restrictedToEquals(Nature.ABSTRACT);
  }

  public boolean isDatatype() {
    return hasStereotype(ClassStereotype.DATATYPE);
  }

  private boolean isProjectInstalledDatatype() {
    IProject p = ApplicationManagerUtils.getCurrentProject();
    String type = IModelElementFactory.MODEL_TYPE_DATA_TYPE;

    // In VP, data types only exist in the project's root
    return Optional.ofNullable(p.toModelElementArray(type)).stream()
        .flatMap(Arrays::stream)
        .anyMatch(installedDatatype -> installedDatatype.getId().equals(getId()));
  }

  public boolean isInstalledDatatype() {
    return isDatatype() && isProjectInstalledDatatype();
  }

  public static Class createKind(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.KIND);
    clazz.setRestrictedTo(Nature.FUNCTIONAL_COMPLEX);
    return clazz;
  }

  public static Class createKind(String name) {
    return createKind(null, name);
  }

  public static Class createCollective(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.COLLECTIVE);
    clazz.setRestrictedTo(Nature.COLLECTIVE);
    return clazz;
  }

  public static Class createQuantity(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.QUANTITY);
    clazz.setRestrictedTo(Nature.QUANTITY);
    return clazz;
  }

  public static Class createRelator(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.RELATOR);
    clazz.setRestrictedTo(Nature.RELATOR);
    return clazz;
  }

  public static Class createMode(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.MODE);
    clazz.setRestrictedTo(Nature.INTRINSIC_MODE, Nature.EXTRINSIC_MODE);
    return clazz;
  }

  public static Class createMode(String name) {
    return createMode(null, name);
  }

  public static Class createQuality(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.QUALITY);
    clazz.setRestrictedTo(Nature.QUALITY);
    return clazz;
  }

  public static Class createSubkind(String id, String name) {
    return new Class(id, name, ClassStereotype.SUBKIND);
  }

  public static Class createRole(String id, String name) {
    return new Class(id, name, ClassStereotype.ROLE);
  }

  public static Class createPhase(String id, String name) {
    return new Class(id, name, ClassStereotype.PHASE);
  }

  public static Class createHistoricalRole(String id, String name) {
    return new Class(id, name, ClassStereotype.HISTORICAL_ROLE);
  }

  public static Class createMixin(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.MIXIN);
    clazz.setAbstract(true);
    clazz.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clazz;
  }

  public static Class createCategory(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.CATEGORY);
    clazz.setAbstract(true);
    clazz.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clazz;
  }

  public static Class createRoleMixin(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.ROLE_MIXIN);
    clazz.setAbstract(true);
    clazz.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clazz;
  }

  public static Class createPhaseMixin(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.PHASE_MIXIN);
    clazz.setAbstract(true);
    clazz.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clazz;
  }

  public static Class createHistoricalRoleMixin(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.HISTORICAL_ROLE_MIXIN);
    clazz.setAbstract(true);
    clazz.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clazz;
  }

  public static Class createEvent(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.EVENT);
    clazz.setRestrictedTo(Nature.EVENT);
    return clazz;
  }

  public static Class createSituation(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.SITUATION);
    clazz.setRestrictedTo(Nature.TYPE);
    return clazz;
  }

  public static Class createType(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.TYPE);
    clazz.setRestrictedTo(Nature.FUNCTIONAL_COMPLEX);
    return clazz;
  }

  public static Class createAbstract(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.ABSTRACT);
    clazz.setRestrictedTo(Nature.ABSTRACT);
    return clazz;
  }

  public static Class createDatatype(String id, String name) {
    Class clazz = new Class(id, name, ClassStereotype.DATATYPE);
    clazz.setRestrictedTo(Nature.ABSTRACT);
    return clazz;
  }

  public static Class createEnumeration(String id, String name, String... literals) {
    Class enumeration = new Class(id, name, ClassStereotype.ENUMERATION);
    enumeration.setRestrictedTo(Nature.ABSTRACT);
    enumeration.createLiterals(literals);
    return enumeration;
  }
}
