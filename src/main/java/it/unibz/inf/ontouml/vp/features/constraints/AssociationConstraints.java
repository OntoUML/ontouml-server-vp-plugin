package it.unibz.inf.ontouml.vp.features.constraints;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class AssociationConstraints {
	
	final private static Type mapOfListsType = new TypeToken<Map<String, List<String>>>() {
	}.getType();
	private static String filename = "association_constraints.json";
	private static String filepath;

	private static Map<String,List<String>> constraints;
	
	private static void loadConstraints() {
		try {
			if(filepath == null) {
				final ApplicationManager app = ApplicationManager.instance();
				final VPPluginInfo[] plugins = app.getPluginInfos();
				
				for (VPPluginInfo plugin : plugins) {
					if(plugin.getPluginId().equals(OntoUMLPlugin.PLUGIN_ID)) {
						File pluginDir = plugin.getPluginDir();
						filepath = pluginDir.getAbsolutePath() + File.separator + filename;
					}
				}
			}
			
			final JsonParser parser = new JsonParser();
			final JsonElement element = parser.parse(new FileReader(filepath));
			final Gson gson = new Gson();
			constraints = gson.fromJson(element, mapOfListsType);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getAllowedActionIDs(String sourceStereotype, String targetStereotype) {
		if(constraints == null) {
			loadConstraints();
		}
		
		List<String> allowedStereotypes = constraints.get(sourceStereotype + ":" + targetStereotype);
		
		if(allowedStereotypes == null) {
			allowedStereotypes = new ArrayList<String>();
		}
		
		return allowedStereotypes
				.stream()
				.map(allowed -> ActionIds.associationStereotypeToActionID(allowed))
				.collect(Collectors.toList());
	}
	
	public static List<String> getAllowedAssociations(String sourceStereotype, String targetStereotype) {
		if(constraints == null) {
			loadConstraints();
		}
		
		List<String> allowedStereotypes = constraints.get(sourceStereotype + ":" + targetStereotype);
		
		if(allowedStereotypes == null) {
			allowedStereotypes = new ArrayList<String>();
		}
		
		return allowedStereotypes;
	}

}
