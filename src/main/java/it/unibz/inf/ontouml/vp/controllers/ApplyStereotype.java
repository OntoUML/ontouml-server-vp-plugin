package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.SmartModelling;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

/**
 * 
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 * 
 * @author Claudenir Fonseca
 *
 */
public class ApplyStereotype implements VPContextActionController {

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
		final IModelElement element = context.getModelElement();
		final IStereotype[] stereotypes = element.toStereotypeModelArray();

		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			element.removeStereotype(stereotypes[i]);
		}

		switch (action.getActionId()) {
		case ActionIds.TYPE:
			element.addStereotype(StereotypeUtils.STR_TYPE);
			break;
		case ActionIds.HISTORICAL_ROLE:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_ROLE);
			break;
		case ActionIds.EVENT:
			element.addStereotype(StereotypeUtils.STR_EVENT);
			break;
		case ActionIds.ENUMERATION:
			element.addStereotype(StereotypeUtils.STR_ENUMERATION);
			break;
		case ActionIds.DATATYPE:
			element.addStereotype(StereotypeUtils.STR_DATATYPE);
			break;
		case ActionIds.SUBKIND:
			element.addStereotype(StereotypeUtils.STR_SUBKIND);
			break;
		case ActionIds.ROLE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_ROLE_MIXIN);
			break;
		case ActionIds.ROLE:
			element.addStereotype(StereotypeUtils.STR_ROLE);
			break;
		case ActionIds.RELATOR:
			element.addStereotype(StereotypeUtils.STR_RELATOR);
			break;
		case ActionIds.QUANTITY:
			element.addStereotype(StereotypeUtils.STR_QUANTITY);
			break;
		case ActionIds.QUALITY:
			element.addStereotype(StereotypeUtils.STR_QUALITY);
			break;
		case ActionIds.PHASE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_PHASE_MIXIN);
			break;
		case ActionIds.PHASE:
			element.addStereotype(StereotypeUtils.STR_PHASE);
			break;
		case ActionIds.MODE:
			element.addStereotype(StereotypeUtils.STR_MODE);
			break;
		case ActionIds.MIXIN:
			element.addStereotype(StereotypeUtils.STR_MIXIN);
			break;
		case ActionIds.KIND:
			element.addStereotype(StereotypeUtils.STR_KIND);
			break;
		case ActionIds.COLLECTIVE:
			element.addStereotype(StereotypeUtils.STR_COLLECTIVE);
			break;
		case ActionIds.CATEGORY:
			element.addStereotype(StereotypeUtils.STR_CATEGORY);
			break;
		case ActionIds.INSTANTIATION:
			element.addStereotype(StereotypeUtils.STR_INSTANTIATION);
			break;
		case ActionIds.TERMINATION:
			element.addStereotype(StereotypeUtils.STR_TERMINATION);
			break;
		case ActionIds.PARTICIPATIONAL:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATIONAL);
			break;
		case ActionIds.PARTICIPATION:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATION);
			break;
		case ActionIds.HISTORICAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
			break;
		case ActionIds.CREATION:
			element.addStereotype(StereotypeUtils.STR_CREATION);
			break;
		case ActionIds.MANIFESTATION:
			element.addStereotype(StereotypeUtils.STR_MANIFESTATION);
			break;
		case ActionIds.MATERIAL:
			element.addStereotype(StereotypeUtils.STR_MATERIAL);
			break;
		case ActionIds.COMPARATIVE:
			element.addStereotype(StereotypeUtils.STR_COMPARATIVE);
			break;
		case ActionIds.MEDIATION:
			element.addStereotype(StereotypeUtils.STR_MEDIATION);
			break;
		case ActionIds.CHARACTERIZATION:
			element.addStereotype(StereotypeUtils.STR_CHARACTERIZATION);
			break;
		case ActionIds.EXTERNAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
			break;
		case ActionIds.COMPONENT_OF:
			element.addStereotype(StereotypeUtils.STR_COMPONENT_OF);
			break;
		case ActionIds.MEMBER_OF:
			element.addStereotype(StereotypeUtils.STR_MEMBER_OF);
			break;
		case ActionIds.SUB_COLLECTION_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_COLLECTION_OF);
			break;
		case ActionIds.SUB_QUANTITY_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_QUANTITY_OF);
			break;
		case ActionIds.BEGIN:
			element.addStereotype(StereotypeUtils.STR_BEGIN);
			break;
		case ActionIds.END:
			element.addStereotype(StereotypeUtils.STR_END);
			break;
		}

		if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
			SmartModelling.setClassMetaProperties((IClass) element);
			SmartColoring.paint((IClass) element);
		}

		if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION))
			SmartModelling.setAssociationMetaProperties((IAssociation) element);

		SmartColoring.smartPaint();
	}

	@Override
	public void update(VPAction action, VPContext context) {
		final IModelElement element = context.getModelElement();

		if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
			final IAssociation association = (IAssociation) element;
			SmartModelling.manageAssociationStereotypes(association, action);
			return;
		}

		if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
			final IClass _class = (IClass) element;
			SmartModelling.manageClassStereotypes(_class, action);
			return;
		}
	}

}
