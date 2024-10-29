package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Property;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import java.util.List;

public class IAssociationTransformer {
  public static ModelElement transform(IModelElement sourceElement) {
    if (!(sourceElement instanceof IAssociation)) return null;

    IAssociation source = (IAssociation) sourceElement;
    Relation target = new Relation();

    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);
    IStereotypeTransformer.transform(source, target);

    boolean isDerived = isDerived(source);
    target.setDerived(isDerived);

    boolean isAbstract = source.isAbstract();
    target.setAbstract(isAbstract);

    Property sourceEnd = IPropertyTransformer.transform(Association.getSourceEnd(source));
    Property targetEnd = IPropertyTransformer.transform(Association.getTargetEnd(source));
    target.setProperties(List.of(sourceEnd, targetEnd));

    return target;
  }

  private static boolean isDerived(IAssociation association) {
    return association.isDerived()
        || Association.getSourceEnd(association).isDerived()
        || Association.getTargetEnd(association).isDerived();
  }
}
