package nl.rivium.breakdown.core;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The PropertyAdapter solely exists to marshal and unmarshal a map type using attributes, instead of two child elements.
 * Looks cleaner that way, even though we are not supposed to edit the XML by hand.
 */
public class PropertyAdapter extends XmlAdapter<PropertyMap, Map<String, String>> {
    /**
     * Unmarshals the PropertyMap to a HashMap.
     *
     * @param v The 'map'.
     * @return A hashmap with all unmarshalled properties.
     * @throws Exception Whenever something goes wrong.
     */
    @Override
    public Map<String, String> unmarshal(PropertyMap v) throws Exception {
        Map<String, String> map = new HashMap<>();
        List<PropertyEntry> entries = v.getProperties();
        for (PropertyEntry entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * Marshals the given map to a PropertyMap, so JAXB outputs nice and tidy key/value pairs with attributes.
     *
     * @param v The map to marshal.
     * @return The PropertyMap (backed by a list).
     * @throws Exception Whenveer something goes wrong.
     */
    @Override
    public PropertyMap marshal(Map<String, String> v) throws Exception {
        PropertyMap map = new PropertyMap();
        map.setProperties(new ArrayList<PropertyEntry>());
        for (String key : v.keySet()) {
            PropertyEntry entry = new PropertyEntry();
            entry.setKey(key);
            entry.setValue(v.get(key));
            map.getProperties().add(entry);
        }

        return map;
    }
}
