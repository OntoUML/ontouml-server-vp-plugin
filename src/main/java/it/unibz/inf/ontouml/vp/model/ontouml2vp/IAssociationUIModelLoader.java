package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.getIDiagramElement;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.getIModelElement;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RelationView;
import java.awt.*;

public class IAssociationUIModelLoader {

  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(IClassDiagramUIModel toDiagram, RelationView fromView) {
    IModelElement toModelElement = getIModelElement(fromView);

    if (!(toModelElement instanceof IAssociation)) {
      System.out.println(
          LoaderUtils.getIncompatibleMessage(fromView, toModelElement, IAssociation.class));
      return;
    }

    IDiagramElement toSource = getIDiagramElement(toDiagram, fromView.getSource());
    IDiagramElement toTarget = getIDiagramElement(toDiagram, fromView.getTarget());

    Point[] toPoints = IConnectorUIModelLoader.loadPoints(fromView);

    IDiagramElement toView =
        diagramManager.createConnector(toDiagram, toModelElement, toSource, toTarget, toPoints);

    fromView.setId(toView.getId());
    toView.resetCaption();
  }

  public boolean isDirectionInverted(
      IClass source, IClass target, IDiagramElement sourceShape, IDiagramElement targetShape) {
    return !source.getId().equals(target.getId())
        && source.getId().equals(targetShape.getModelElement().getId())
        && target.getId().equals(sourceShape.getModelElement().getId());
  }
}
