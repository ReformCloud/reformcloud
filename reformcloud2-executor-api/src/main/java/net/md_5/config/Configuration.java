package net.md_5.config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Configuration {

  private static final char SEPARATOR = '.';

  final Map<String, Object> self;

  private final Configuration defaults;

  public Configuration() { this(null); }

  public Configuration(Configuration defaults) {
    this(new LinkedHashMap<String, Object>(), defaults);
  }

  Configuration(Map<?, ?> map, Configuration defaults) {
    this.self = new LinkedHashMap<>();
    this.defaults = defaults;

    for (Map.Entry<?, ?> entry : map.entrySet()) {
      String key =
          (entry.getKey() == null) ? "null" : entry.getKey().toString();

      if (entry.getValue() instanceof Map) {
        this.self.put(key, new Configuration((Map<?, ?>)entry.getValue(),
                                             (defaults == null)
                                                 ? null
                                                 : defaults.getSection(key)));
      } else {
        this.self.put(key, entry.getValue());
      }
    }
  }

  private Configuration getSectionFor(String path) {
    int index = path.indexOf(SEPARATOR);
    if (index == -1) {
      return this;
    }

    String root = path.substring(0, index);
    Object section = self.get(root);
    if (section == null) {
      section = new Configuration(
          (defaults == null) ? null : defaults.getSection(path));
      self.put(root, section);
    }

    return (Configuration)section;
  }

  private String getChild(String path) {
    int index = path.indexOf(SEPARATOR);
    return (index == -1) ? path : path.substring(index + 1);
  }

  /*------------------------------------------------------------------------*/

  @SuppressWarnings("unchecked")
  public <T> T get(String path, T def) {
    Configuration section = getSectionFor(path);
    Object val;
    if (section == this) {
      val = self.get(path);
    } else {
      val = section.get(getChild(path), def);
    }

    if (val == null && def instanceof Configuration) {
      self.put(path, def);
    }

    return val != null ? (T)val : def;
  }

  public boolean contains(String path) { return get(path, null) != null; }

  public Object get(String path) { return get(path, getDefault(path)); }

  private Object getDefault(String path) {
    return (defaults == null) ? null : defaults.get(path);
  }

  public void set(String path, Object value) {
    if (value instanceof Map) {
      value = new Configuration((Map<?, ?>)value,
                                (defaults == null) ? null
                                                   : defaults.getSection(path));
    }

    Configuration section = getSectionFor(path);
    if (section == this) {
      if (value == null) {
        self.remove(path);
      } else {
        self.put(path, value);
      }
    } else {
      section.set(getChild(path), value);
    }
  }

  /*------------------------------------------------------------------------*/
  private Configuration getSection(String path) {
    Object def = getDefault(path);
    return (Configuration)get(
        path, (def instanceof Configuration)
                  ? def
                  : new Configuration(
                        (defaults == null) ? null : defaults.getSection(path)));
  }
}
