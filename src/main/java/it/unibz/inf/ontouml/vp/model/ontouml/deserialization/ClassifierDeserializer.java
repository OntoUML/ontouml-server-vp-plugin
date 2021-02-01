package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeBooleanField;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.Property;
import java.io.IOException;
import java.util.List;

public class ClassifierDeserializer {

  public static void deserialize(Classifier<?, ?> classifier, JsonNode root, ObjectCodec codec)
      throws IOException {

    boolean isAbstract = deserializeBooleanField(root, "isAbstract");
    classifier.setAbstract(isAbstract);

    boolean isDerived = deserializeBooleanField(root, "isDerived");
    classifier.setDerived(isDerived);

    JsonNode propetiesNode = root.get("properties");
    if (propetiesNode != null && propetiesNode.isArray()) {
      List<Property> properties =
          propetiesNode.traverse(codec).readValueAs(new TypeReference<List<Property>>() {});
      classifier.setProperties(properties);
    }
  }
}
