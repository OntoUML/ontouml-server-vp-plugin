package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;

public class IAssociationClassTransformer {
  public static ModelElement transform(IModelElement sourceElement) {
    if (!(sourceElement instanceof IAssociationClass)) return null;

    IAssociationClass source = (IAssociationClass) sourceElement;

    IModelElement from = source.getFrom();
    IModelElement to = source.getTo();
    IModelElement derivingAssociation = from instanceof IAssociation ? from : to;
    IModelElement derivedClass = to instanceof IClass ? to : from;
    Classifier<?, ?> derivingAssociationStub = createClassifierStub(derivingAssociation);
    Classifier<?, ?> derivedClassStub = createClassifierStub(derivedClass);

    Relation target =
        Relation.createDerivation(null, null, derivingAssociationStub, derivedClassStub);

    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);
    IStereotypeTransformer.transform(source, target);

    target.setDerived(true);
    target.setAbstract(false);

    return target;
  }

  private static Classifier<?, ?> createClassifierStub(IModelElement classifier) {
    OntoumlElement targetType = ReferenceTransformer.transformStub(classifier);
    return targetType instanceof Classifier<?, ?> ? (Classifier<?, ?>) targetType : null;
  }
}
