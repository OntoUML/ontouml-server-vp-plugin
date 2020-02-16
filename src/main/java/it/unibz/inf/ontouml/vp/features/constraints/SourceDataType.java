package it.unibz.inf.ontouml.vp.features.constraints;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

import java.util.ArrayList;

import com.vp.plugin.action.VPAction;

public class SourceDataType {

	// each ArrayList is a set of permitted associations when the source is DataType
	public static final ArrayList<String> targetKind = new ArrayList<>();
	public static final ArrayList<String> targetQuantity = new ArrayList<>();
	public static final ArrayList<String> targetCollective = new ArrayList<>();
	public static final ArrayList<String> targetRelator = new ArrayList<>();
	public static final ArrayList<String> targetMode = new ArrayList<>();
	public static final ArrayList<String> targetQuality = new ArrayList<>();
	public static final ArrayList<String> targetSubKind = new ArrayList<>();
	public static final ArrayList<String> targetRole = new ArrayList<>();
	public static final ArrayList<String> targetPhase = new ArrayList<>();
	public static final ArrayList<String> targetCategory = new ArrayList<>();
	public static final ArrayList<String> targetMixin = new ArrayList<>();
	public static final ArrayList<String> targetRoleMixin = new ArrayList<>();
	public static final ArrayList<String> targetPhaseMixin = new ArrayList<>();
	public static final ArrayList<String> targetHistoricalRole = new ArrayList<>();
	public static final ArrayList<String> targetEvent = new ArrayList<>();
	public static final ArrayList<String> targetType = new ArrayList<>();
	public static final ArrayList<String> targetDataType = new ArrayList<>();
	public static final ArrayList<String> targetEnumeration = new ArrayList<>();

	public static void setAction(VPAction action, String targetStereotype) {

		switch (targetStereotype) {
		case StereotypeUtils.STR_KIND:
			setActionForTarget(action, targetKind);
			break;
		case StereotypeUtils.STR_QUANTITY:
			setActionForTarget(action, targetQuantity);
			break;
		case StereotypeUtils.STR_COLLECTIVE:
			setActionForTarget(action, targetCollective);
			break;
		case StereotypeUtils.STR_RELATOR:
			setActionForTarget(action, targetRelator);
			break;
		case StereotypeUtils.STR_MODE:
			setActionForTarget(action, targetMode);
			break;
		case StereotypeUtils.STR_QUALITY:
			setActionForTarget(action, targetQuality);
			break;
		case StereotypeUtils.STR_SUBKIND:
			setActionForTarget(action, targetSubKind);
			break;
		case StereotypeUtils.STR_ROLE:
			setActionForTarget(action, targetRole);
			break;
		case StereotypeUtils.STR_PHASE:
			setActionForTarget(action, targetPhase);
			break;
		case StereotypeUtils.STR_CATEGORY:
			setActionForTarget(action, targetRelator);
			break;
		case StereotypeUtils.STR_MIXIN:
			setActionForTarget(action, targetMixin);
			break;
		case StereotypeUtils.STR_ROLE_MIXIN:
			setActionForTarget(action, targetRoleMixin);
			break;
		case StereotypeUtils.STR_PHASE_MIXIN:
			setActionForTarget(action, targetPhaseMixin);
			break;
		case StereotypeUtils.STR_HISTORICAL_ROLE:
			setActionForTarget(action, targetHistoricalRole);
			break;
		case StereotypeUtils.STR_EVENT:
			setActionForTarget(action, targetEvent);
			break;
		case StereotypeUtils.STR_TYPE:
			setActionForTarget(action, targetType);
			break;
		case StereotypeUtils.STR_DATATYPE:
			setActionForTarget(action, targetDataType);
			break;
		case StereotypeUtils.STR_ENUMERATION:
			setActionForTarget(action, targetEnumeration);
			break;
		default:
			action.setEnabled(false);
		}
	}

	public static void setActionForTarget(VPAction action, ArrayList<String> target) {

		if (target.contains(action.getActionId())) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}
}
