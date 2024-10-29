package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ExportDiagramAsImageOption;
import com.vp.plugin.ModelConvertionManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import it.unibz.inf.ontouml.vp.utils.SimpleServiceWorker;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JFileChooser;

public class ReloadClassesController implements VPActionController {

  private static final String RUN_ON_REPO_CONFIRMATION = "Do wish you to run sanitize on folder?";
  private static final String PROCEDURE_CANCELED = "Procedure cancelled.";
  private static final String UNEXPECTED_ERROR = "Unexpected error occurred.";
  private static final String RELOAD_PLUGIN_CONFIRMATION = "Do wish you to reload the plugin?";

  private static Set<String> blacklistSet =
      Stream.of(
              ".git", ".vscode"
              //          , "abel2015petroleum-system"
              //          , "aguiar2018rdbs-o"
              //          , "ahmad2018aviation"
              //          , "aires2022valuenetworks-geo"
              //          , "albuquerque2011ontobio"
              //          , "alpinebits2022"
              //          , "amaral2019rot"
              //          , "amaral2020rome"
              //          , "ambient-assisted-living2021"
              //          , "andersson2018value-ascription"
              //          , "aristotle-ontology2019"
              //          , "bank-model"
              //          , "barcelos2013normative-acts"
              //          , "barcelos2015transport-networks"
              //          , "barros2020programming"
              //          , "bernasconi2021ontovcm"
              //          , "brazilian-governmental-organizational-structures"
              //          , "buchtela2020connection"
              //          , "buridan-ontology2021"
              //          , "carolla2014campus-management"
              //          , "castro2012cloudvulnerability"
              //          , "cgts2021sebim"
              //          , "chartered-service"
              //          , "clergy-ontology"
              //          , "cmpo2017"
              //          , "dpo2017"
              //          , "duarte2018osdef"
              //          , "duarte2018reqon"
              //          , "duarte2021ross"
              //          , "eu-rent-refactored2022"
              //          , "experiment2013"
              //          , "fernandez-cejas2022curie-o"
              //          , "ferreira2015ontoemergeplan"
              //          , "franco2018rpg"
              //          , "g809-2015"
              //          , "gailly2016value"
              //          , "gi2mo"
              //          , "grueau2013towards"
              //          , "guarino2016value"
              //          , "guarino2018rea"
              //          , "guizzardi2014nfr"
              //          , "guizzardi2020decision-making"
              //          , "hazard-ontology-robotic-strolling2017"
              //          , "hazard-ontology-train-control2017"
              //          , "health-organizations"
              //          , "internal-affairs2013"
              //          , "internship"
              //          , "khantong2020ontology"
              //          , "kostov2017towards"
              //          , "kritz2020ontobg"
              //          , "laurier2018rea"
              //          , "library"
              //          , "martinez2013human-genome"
              //          , "medical-appointment2020"
              //          , "mgic-antt2011"
              //          , "moreira2018saref4health"
              //          , "music-ontology"
              //          , "neves2021grain-production"
              //          , "niederkofler2019dssapple"
              //          , "oliveira2007collaboration"
              //          , "online-mentoring"
              //          , "pereira2015doacao-orgaos"
              //          , "pereira2020ontotrans"
              //          , "photography"
              //          , "plato-ontology2019"
              //          , "porello2020coex"
              //          , "ppo-o2021"
              //          , "public-tender"
              //          , "ramirez2015userfeedback"
              //          , "ramos2021bias"
              //          , "recommendation-ontology"
              //          , "repa2021public-administration"
              //          , "rodrigues2019ontocrime"
              //          , "rodrigues2019turbidite"
              //          , "sales2018competition"
              //          , "sales2018cover"
              //          , "santos2020valuenetworks"
              //          , "sikora2021online-education"
              //          , "silva2012itarchitecture"
              //          , "silveira2021oap"
              //          , "social-contract"
              //          , "spo2017"
              //          , "sportbooking2021"
              //          , "srro-ontology"
              //          , "stock-broker2021"
              //          , "tourbo2021"
              //          , "van-wingerde2020smart-contracts"
              //          , "vieira2020weathering"
              //          , "weigand2021artifact"
              //          , "zanetti2019orm-o"
              //          , "zhou2017hazard"
              )
          .collect(Collectors.toSet());
  private static Set<String> targetSet = Set.of("");

  private File repositoryFile;
  private ProjectManager projectManager;
  private IProject project;
  private File ontologyDir;
  private File diagramsDir;
  private File ontologyVppFile;
  private File ontologyJsonFile;
  private ModelConvertionManager modelConvertionManager;
  private ExportDiagramAsImageOption diagramTypeOption;

  @Override
  public void performAction(VPAction action) {
    projectManager = ApplicationManager.instance().getProjectManager();
    project = projectManager.getProject();
    modelConvertionManager = ApplicationManager.instance().getModelConvertionManager();
    diagramTypeOption = new ExportDiagramAsImageOption(ExportDiagramAsImageOption.IMAGE_TYPE_PNG);

    diagramTypeOption.setTextAntiAliasing(true);
    diagramTypeOption.setScale(3);

    boolean shouldRunOnRepo = ViewManagerUtils.warningDialog(RUN_ON_REPO_CONFIRMATION);

    if (shouldRunOnRepo) {
      repositoryFile = getDirectory();

      new SimpleServiceWorker(this::runOnRepo).execute();
      return;
    }

    boolean shouldReload = ViewManagerUtils.warningDialog(RELOAD_PLUGIN_CONFIRMATION);

    if (shouldReload) {
      reloadPlugin();
    }
  }

  @Override
  public void update(VPAction action) {}

  private void reloadPlugin() {
    System.out.println("----------------------------------------");
    System.out.println("Reloading OntoUML Plugin...");
    ApplicationManager app = ApplicationManager.instance();
    app.reloadPluginClasses(OntoUMLPlugin.PLUGIN_ID);
    System.out.println("Plugin reloaded!");
    ViewManagerUtils.simpleDialog("Plugin reloaded!");
  }

  private List<String> runOnRepo(SimpleServiceWorker context) {
    try {
      if (!context.isCancelled() && repositoryFile != null) {
        ViewManagerUtils.clean();
        processRepository();

        return List.of("Directory '" + repositoryFile + "' has been selected");
      }

      return List.of(PROCEDURE_CANCELED);
    } catch (Exception e) {
      e.printStackTrace();
      ViewManagerUtils.log(UNEXPECTED_ERROR);
      return List.of(UNEXPECTED_ERROR);
    }
  }

  private void processRepository() throws IOException {
    if (!repositoryFile.isDirectory()) {
      throw new RuntimeException(
          "Invalid repository directory: '" + repositoryFile.getAbsolutePath() + "'");
    }

    Files.newDirectoryStream(repositoryFile.toPath())
        .forEach(
            ontologyPath -> {
              ontologyDir = ontologyPath.toFile();

              if (!ontologyDir.isDirectory() || blacklistSet.contains(ontologyDir.getName())) {
                System.out.println("Ignoring file '" + ontologyDir.getName() + "'");
                return;
              }

              //          if(!targetSet.contains(ontologyPath.getFileName().toString())) {
              //            System.out.println("Skipping directory '" + ontologyDir.getName() +
              // "'");
              //            return ;
              //          }

              System.out.println("Checking '" + ontologyDir.getName() + "'");
              try {
                checkOntologyDir(ontologyDir);
                System.out.println("Checked '" + ontologyDir.getName() + "': OK");
              } catch (Exception e) {
                System.out.println("Checked '" + ontologyDir.getName() + "': FAIL!");
                return;
              }
              System.out.println("Opening '" + ontologyDir.getName() + "'");
              try {
                openProject(ontologyDir);
              } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(
                    "Unable to open ontology '" + ontologyDir.getName() + "'");
              }
              System.out.println("Opened '" + ontologyDir.getName() + "': OK");
              System.out.println("Sanitizing '" + ontologyDir.getName() + "'");
              ModelSanitizeManager.run();
              System.out.println("Sanitized '" + ontologyDir.getName() + "': OK");
              //              System.out.println("Exporting diagrams '" + ontologyDir.getName() +
              // "'");
              //              try {
              //                exportDiagrams();
              //              } catch (IOException e) {
              //                e.printStackTrace();
              //                throw new RuntimeException(
              //                    "Unable to export diagrams of '" + ontologyDir.getName() + "'");
              //              }
              //              System.out.println("Exported diagrams '" + ontologyDir.getName() + "':
              // OK");
              System.out.println("Exporting JSON '" + ontologyDir.getName() + "'");
              try {
                exportJson();
              } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(
                    "Unable to export JSON of '" + ontologyDir.getName() + "'");
              }
              System.out.println("Exported JSON '" + ontologyDir.getName() + "': OK");
              System.out.println("Saving VPP file '" + ontologyDir.getName() + "'");
              projectManager.saveProject();
              System.out.println("Saved VPP file '" + ontologyDir.getName() + "': OK");
            });
  }

  private void exportJson() throws IOException {
    final String exportFileContents = Uml2OntoumlTransformer.transformAndSerialize();
    Files.write(ontologyJsonFile.toPath(), exportFileContents.getBytes());
  }

  private void exportDiagrams() throws IOException {
    emptyFolder(diagramsDir);

    Iterator<?> iter = project.diagramIterator();

    while (iter != null && iter.hasNext()) {
      IDiagramUIModel diagram = (IDiagramUIModel) iter.next();
      String name = diagram.getName();
      File diagramFile = Paths.get(diagramsDir.getAbsolutePath(), name + ".png").toFile();

      ViewManagerUtils.log("Exporting diagram '" + diagramFile + "'");
      modelConvertionManager.exportDiagramAsImage(diagram, diagramFile, diagramTypeOption);
    }
  }

  private void emptyFolder(File diagramsDir) throws IOException {
    Files.newDirectoryStream(diagramsDir.toPath()).forEach(path -> path.toFile().delete());
  }

  private void checkOntologyDir(File ontologyDir) {
    ontologyVppFile = Paths.get(ontologyDir.getAbsolutePath(), "ontology.vpp").toFile();
    ontologyJsonFile = Paths.get(ontologyDir.getAbsolutePath(), "ontology.json").toFile();

    File originalDiagramsDir =
        Paths.get(ontologyDir.getAbsolutePath(), "original diagrams").toFile();
    File newDiagramsDir = Paths.get(ontologyDir.getAbsolutePath(), "new diagrams").toFile();

    if (!ontologyDir.exists()) {
      throw new RuntimeException("Ontology directory not found! (" + ontologyDir.getPath() + ")");
    }

    if (!ontologyVppFile.exists()) {
      throw new RuntimeException(
          "Visual Paradigm file not found! (" + ontologyVppFile.getPath() + ")");
    }

    if (!ontologyJsonFile.exists()) {
      throw new RuntimeException("JSON file not found! (" + ontologyJsonFile.getPath() + ")");
    }

    if (!originalDiagramsDir.isDirectory() && !newDiagramsDir.isDirectory()) {
      throw new RuntimeException("No folder found at '" + ontologyJsonFile.getPath() + "'!");
    }

    diagramsDir =
        shouldExportToOriginalDiagramsDir(originalDiagramsDir, newDiagramsDir)
            ? originalDiagramsDir
            : newDiagramsDir;
  }

  private boolean shouldExportToOriginalDiagramsDir(File originalDiagramsDir, File newDiagramsDir) {
    return !newDiagramsDir.isDirectory() || newDiagramsDir.list().length == 0;
  }

  private void openProject(File ontologyDir) throws IOException {
    File tempFile = File.createTempFile("discard-", ".vpp");
    File ontologyFile = Paths.get(ontologyDir.getAbsolutePath(), "ontology.vpp").toFile();

    if (!ontologyFile.exists() || !ontologyFile.isFile()) {
      throw new RuntimeException("Ontology file not found");
    }

    projectManager.saveProjectAs(tempFile);
    projectManager.openProject(ontologyFile);
    project = projectManager.getProject();
  }

  private File getDirectory() {
    File ontoumlModelsDir = Paths.get("/Users/claudenir/git/github/unibz-core").toFile();
    Frame rootFrame = (Frame) ApplicationManager.instance().getViewManager().getRootFrame();
    JFileChooser jc =
        ontoumlModelsDir.isDirectory() ? new JFileChooser(ontoumlModelsDir) : new JFileChooser();

    jc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int result = jc.showOpenDialog(rootFrame);

    return result == JFileChooser.APPROVE_OPTION ? jc.getSelectedFile() : null;
  }
}
