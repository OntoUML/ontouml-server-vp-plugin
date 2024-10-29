package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.utils.SimpleServiceWorker;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.util.List;

public class ModelSanitizeController implements VPActionController {

  private static final String MESSAGE_MODEL_SANITIZE_SUCCESS =
      "Model sanitize executed successfully.";
  private static final String MESSAGE_MODEL_IMPORT_UNEXPECTED_ERROR =
      "Unexpected error performing model sanitize.";

  private boolean shouldProceed = false;

  @Override
  public void update(VPAction vpAction) {}

  @Override
  public void performAction(VPAction vpAction) {
    showFixStereotypesWarning();

    if (!shouldProceed) return;

    new SimpleServiceWorker(this::sanitizeModelTask).execute();
  }

  private void showFixStereotypesWarning() {
    shouldProceed = ViewManagerUtils.showFixStereotypesWarningDialog();
  }

  private List<String> sanitizeModelTask(SimpleServiceWorker context) {
    try {
      if (!context.isCancelled()) {
        ModelSanitizeManager.run();
        ViewManagerUtils.log(MESSAGE_MODEL_SANITIZE_SUCCESS);
        return List.of(MESSAGE_MODEL_SANITIZE_SUCCESS);
      }

      return List.of();
    } catch (Exception e) {
      e.printStackTrace();
      ViewManagerUtils.log(MESSAGE_MODEL_IMPORT_UNEXPECTED_ERROR);
      return List.of(MESSAGE_MODEL_IMPORT_UNEXPECTED_ERROR);
    }
  }
}
