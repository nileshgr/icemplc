package icemplc.lib;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/*
 * Common helper functions
 */

public class Common {
    public static String convertValidityMapToJson(
	    LinkedHashMap<String, LinkedHashSet<String>> validity) {
	JsonObjectBuilder builder = Json.createObjectBuilder();
	JsonArrayBuilder valid = Json.createArrayBuilder();
	JsonArrayBuilder invalid = Json.createArrayBuilder();

	for (String field : validity.get("valid"))
	    valid.add(field);

	for (String field : validity.get("invalid"))
	    invalid.add(field);

	builder.add("valid", valid);
	builder.add("invalid", invalid);

	return builder.build().toString();
    }

    public static int[] generateBatches() {
	int year = Calendar.getInstance().get(Calendar.YEAR);
	int batches[] = new int[2];
	batches[0] = year - 3;
	batches[1] = year - 4;
	return batches;
    }
}