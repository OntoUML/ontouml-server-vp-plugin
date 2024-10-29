package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.getIDiagramElement;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.getIModelElement;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RelationView;
import java.awt.Point;

public class IAssociationClassUIModelLoader {

  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(IClassDiagramUIModel toDiagram, RelationView fromView) {
    IModelElement toModelElement = getIModelElement(fromView);

    // TODO: review this bit: how does it find the derivation if it was created anew
    if (!(toModelElement instanceof IAssociationClass)) {
      System.out.println(
          LoaderUtils.getIncompatibleMessage(fromView, toModelElement, IAssociationClass.class));
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
}
