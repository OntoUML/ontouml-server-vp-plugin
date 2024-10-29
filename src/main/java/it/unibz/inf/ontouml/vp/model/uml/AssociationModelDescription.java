package it.unibz.inf.ontouml.vp.model.uml;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AssociationModelDescription {

  private IAssociationUIModel associationUIModel;
  public final IAssociation association;
  public final IDiagramUIModel diagram;
  public final IDiagramElement sourceDiagramElement;
  public final IDiagramElement targetDiagramElement;
  public final Point[] points;
  public final boolean isMasterView;
  public final boolean isShowDirection;
  public final boolean isShowSourceRoleName;
  public final boolean isShowSourceRoleVisibility;
  public final boolean isShowTargetRoleName;
  public final boolean isShowTargetRoleVisibility;
  public final boolean isShowAssociationEndPropertyStrings;

  public AssociationModelDescription(IAssociationUIModel associationModel) {
    associationUIModel = associationModel;

    association = (IAssociation) associationModel.getModelElement();
    diagram = associationModel.getDiagramUIModel();
    sourceDiagramElement = associationModel.getFromShape();
    targetDiagramElement = associationModel.getToShape();
    points = associationModel.getPoints();

    isMasterView = associationModel.isMasterView();
    isShowDirection = associationModel.isShowDirection();
    isShowAssociationEndPropertyStrings = associationModel.isShowAssociationEndPropertyStrings();
    isShowSourceRoleName = associationModel.isShowFromRoleName();
    isShowSourceRoleVisibility = associationModel.isShowFromRoleVisibility();
    isShowTargetRoleName = associationModel.isShowToRoleName();
    isShowTargetRoleVisibility = associationModel.isShowToRoleVisibility();
  }

  private Point[] getInvertedPoints() {
    Point[] invertedPoints = points;

    if (invertedPoints != null) {
      final List<Point> pointsListToReverse = Arrays.asList(invertedPoints);
      Collections.reverse(pointsListToReverse);
      invertedPoints = pointsListToReverse.toArray(invertedPoints);
    }

    return invertedPoints;
  }

  public void deleteAssociationModel() {
    if (associationUIModel != null) {
      associationUIModel.setShowAssociationEndPropertyStrings(false);
      associationUIModel.setShowFromRoleName(false);
      associationUIModel.setShowFromRoleVisibility(false);
      associationUIModel.setShowToRoleName(false);
      associationUIModel.setShowToRoleVisibility(false);
      associationUIModel.deleteViewOnly();
      associationUIModel = null;
    }
  }

  public IAssociationUIModel recreateInvertedAssociationModel() {
    final IModelElement currentSource = Association.getFrom(association);
    final IModelElement currentTarget = Association.getTo(association);
    final IModelElement originalSource = sourceDiagramElement.getModelElement();
    final IModelElement originalTarget = targetDiagramElement.getModelElement();

    if (currentSource.equals(originalSource) || currentTarget.equals(originalTarget)) {
      throw new IllegalStateException(
          "Inverted association models can only be created after the original association"
              + " inverted.");
    }

    final DiagramManager dm = ApplicationManager.instance().getDiagramManager();
    final IAssociationUIModel invertedAssociationModel =
        (IAssociationUIModel)
            dm.createConnector(
                diagram,
                association,
                targetDiagramElement,
                sourceDiagramElement,
                getInvertedPoints());

    if (isMasterView) {
      invertedAssociationModel.toBeMasterView();
    }

    invertedAssociationModel.setShowDirection(isShowDirection);
    invertedAssociationModel.setShowFromRoleName(isShowSourceRoleName);
    invertedAssociationModel.setShowFromRoleVisibility(isShowSourceRoleVisibility);
    invertedAssociationModel.setShowToRoleName(isShowTargetRoleName);
    invertedAssociationModel.setShowToRoleVisibility(isShowTargetRoleVisibility);
    invertedAssociationModel.setShowAssociationEndPropertyStrings(
        isShowAssociationEndPropertyStrings);

    invertedAssociationModel.resetCaption();

    return invertedAssociationModel;
  }
}
