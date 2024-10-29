package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ConnectorView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ElementView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IConnectorTransformer {

  public static void transform(IConnectorUIModel source, ConnectorView<?> target) {
    setPath(source, target);
    setSourceShape(source, target);
    setTargetShape(source, target);
  }

  public static void transform(IAssociationUIModel source, ConnectorView<?> target) {
    setPath(source, target);
    setSourceShape(source, target);
    setTargetShape(source, target);
  }

  public static void transform(IAssociationClassUIModel source, ConnectorView<?> target) {
    setDerivationPath(source, target);
    setConnectorSource(source, target);
    setClassShapeTarget(source, target);
  }

  private static void setPath(IConnectorUIModel source, ConnectorView<?> target) {
    Path path = new Path();
    path.setId(source.getId() + "_path");

    List<Point> points = Arrays.asList(source.getPoints());
    points.forEach(p -> path.moveTo((int) p.getX(), (int) p.getY()));

    target.setPath(path);
  }

  private static void setPath(IAssociationUIModel source, ConnectorView<?> target) {
    IAssociation association = (IAssociation) source.getModelElement();
    List<Point> points = Arrays.asList(source.getPoints());

    if (!Association.doesFromAndSourceMatch(association)) Collections.reverse(points);

    Path path = new Path();
    path.setId(source.getId() + "_path");
    points.forEach(p -> path.moveTo((int) p.getX(), (int) p.getY()));

    target.setPath(path);
  }

  private static void setDerivationPath(IAssociationClassUIModel source, ConnectorView<?> target) {
    List<Point> points = Arrays.asList(source.getPoints());

    if (isDerivationInverted(source)) Collections.reverse(points);

    Path path = new Path();
    path.setId(source.getId() + "_path");
    points.forEach(p -> path.moveTo((int) p.getX(), (int) p.getY()));

    target.setPath(path);
  }

  private static boolean isDerivationInverted(IAssociationClassUIModel connector) {
    IAssociationClass derivation = (IAssociationClass) connector.getModelElement();
    IModelElement from = derivation.getFrom();
    IModelElement to = derivation.getTo();

    return from instanceof IClass && to instanceof IAssociation;
  }

  private static void setSourceShape(IConnectorUIModel source, ConnectorView<?> target) {
    ElementView<?, ?> connectorSourceStub = null;
    if (hasFromShape(source))
      connectorSourceStub = ReferenceTransformer.transformStub(getFromShape(source));
    else if (hasFromConnector(source))
      connectorSourceStub = ReferenceTransformer.transformStub(getFromConnector(source));

    target.setSource(connectorSourceStub);
  }

  private static void setTargetShape(IConnectorUIModel source, ConnectorView<?> target) {
    ElementView<?, ?> connectorTargetStub = null;
    if (hasToShape(source))
      connectorTargetStub = ReferenceTransformer.transformStub(getToShape(source));
    else if (hasToConnector(source))
      connectorTargetStub = ReferenceTransformer.transformStub(getToConnector(source));

    target.setTarget(connectorTargetStub);
  }

  private static void setSourceShape(IAssociationUIModel source, ConnectorView<?> target) {
    IAssociation association = (IAssociation) source.getModelElement();
    IDiagramElement shapeOrConnector = null;

    if (Association.doesFromAndSourceMatch(association)) {
      shapeOrConnector = hasFromShape(source) ? getFromShape(source) : getFromConnector(source);
    } else {
      shapeOrConnector = hasToShape(source) ? getToShape(source) : getToConnector(source);
    }

    ElementView<?, ?> connectorSourceStub =
        shapeOrConnector != null ? ReferenceTransformer.transformStub(shapeOrConnector) : null;

    target.setSource(connectorSourceStub);
  }

  private static void setTargetShape(IAssociationUIModel source, ConnectorView<?> target) {
    IAssociation association = (IAssociation) source.getModelElement();
    IDiagramElement shapeOrConnector = null;

    if (Association.doesFromAndSourceMatch(association)) {
      shapeOrConnector = hasToShape(source) ? getToShape(source) : getToConnector(source);
    } else {
      shapeOrConnector = hasFromShape(source) ? getFromShape(source) : getFromConnector(source);
    }

    ElementView<?, ?> connectorSourceStub =
        shapeOrConnector != null ? ReferenceTransformer.transformStub(shapeOrConnector) : null;

    target.setTarget(connectorSourceStub);
  }

  private static void setConnectorSource(IAssociationClassUIModel source, ConnectorView<?> target) {
    IDiagramElement relationConnector = getRelationConnector(source);
    ElementView<?, ?> connectorStub = ReferenceTransformer.transformStub(relationConnector);
    target.setSource(connectorStub);
  }

  private static void setClassShapeTarget(
      IAssociationClassUIModel source, ConnectorView<?> target) {
    IDiagramElement classShape = getClassShape(source);
    ElementView<?, ?> shapeStub = ReferenceTransformer.transformStub(classShape);
    target.setTarget(shapeStub);
  }

  private static IDiagramElement getClassShape(IAssociationClassUIModel derivation) {
    if (hasToShape(derivation) && !hasFromShape(derivation)) return getToShape(derivation);
    if (hasFromShape(derivation) && !hasToShape(derivation)) return getFromShape(derivation);
    return null;
  }

  private static IDiagramElement getRelationConnector(IAssociationClassUIModel derivation) {
    if (hasFromConnector(derivation) && !hasToConnector(derivation))
      return getFromConnector(derivation);
    if (hasToConnector(derivation) && !hasFromConnector(derivation))
      return getToConnector(derivation);
    return null;
  }

  private static boolean hasFromShape(IConnectorUIModel connector) {
    return connector.getFromShape() != null;
  }

  private static boolean hasFromConnector(IConnectorUIModel connector) {
    return connector.getFromConnector() != null;
  }

  private static boolean hasToShape(IConnectorUIModel connector) {
    return connector.getToShape() != null;
  }

  private static boolean hasToConnector(IConnectorUIModel connector) {
    return connector.getToConnector() != null;
  }

  private static IDiagramElement getFromShape(IConnectorUIModel connector) {
    return connector.getFromShape();
  }

  private static IDiagramElement getFromConnector(IConnectorUIModel connector) {
    return connector.getFromConnector();
  }

  private static IDiagramElement getToShape(IConnectorUIModel connector) {
    return connector.getToShape();
  }

  private static IDiagramElement getToConnector(IConnectorUIModel connector) {
    return connector.getToConnector();
  }
}
