package zutil.ui.conf;

import java.util.ArrayList;
import java.util.List;

/**
 * A value provider that will give all Enum values
 */
public class ConfigEnumValueProvider implements Configurator.ConfigValueProvider<Enum> {
    private Class<Enum> enumCLass;


    public ConfigEnumValueProvider(Class<Enum> enumCLass) {
        this.enumCLass = enumCLass;
    }


    public String getValue(Enum obj) {
        return (obj == null ? "null" : obj.name());
    }

    @Override
    public List<String> getPossibleValues() {
        Object[] constants = enumCLass.getEnumConstants();
        List<String> values = new ArrayList<>(constants.length);

        for (int i = 0; i < constants.length; ++i)
            values.add(((Enum<?>) constants[i]).name());

        return values;
    }

    @Override
    public Enum getObject(String value) {
        return Enum.valueOf(enumCLass, value);
    }
}