package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import com.vp.plugin.diagram.shape.IModelUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Diagram;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ElementView;
import java.util.Arrays;

public class IClassDiagramTransformer {

  public static Diagram transform(IDiagramUIModel sourceElement, Package root) {
    if (!(sourceElement instanceof IClassDiagramUIModel)) return null;

    IClassDiagramUIModel source = (IClassDiagramUIModel) sourceElement;

    Diagram target = new Diagram();

    String id = source.getId();
    target.setId(id);

    String name = source.getName();
    target.addName(name);

    String description = source.getDocumentation();
    target.addDescription(description);

    ModelElement owner = getOwner(source, root);
    target.setOwner(owner);

    Arrays.stream(source.toDiagramElementArray())
        .filter(e -> e.getModelElement() != null)
        .map(e -> transfromIDiagramElement(e))
        .forEach(e -> target.addElement(e));

    return target;
  }

  private static ModelElement getOwner(IClassDiagramUIModel source, Package root) {
    IModelElement owner = source.getParentModel();

    if (owner == null) return root;

    return ReferenceTransformer.transformStub(owner);
  }

  public static ElementView<?, ?> transfromIDiagramElement(IDiagramElement source) {
    ElementView<?, ?> target = null;

    if (source instanceof IClassUIModel) {
      target = IClassUIModelTransformer.transform(source);
    } else if (source instanceof IAssociationUIModel) {
      target = IAssociationUIModelTransformer.transform(source);
    } else if (source instanceof IAssociationClassUIModel) {
      target = IAssociationClassUIModelTransformer.transform(source);
    } else if (source instanceof IGeneralizationUIModel) {
      target = IGeneralizationUIModelTransformer.transform(source);
    } else if (source instanceof IGeneralizationSetUIModel) {
      target = IGeneralizationSetUIModelTransformer.transform(source);
    } else if (source instanceof IPackageUIModel || source instanceof IModelUIModel) {
      target = IPackageUIModelTransformer.transform(source);
    }

    Trace.getInstance().put(source.getId(), source, target);

    return target;
  }
}
