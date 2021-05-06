package zutil.ui;

/**
 * A value provider that will give all Enum values
 */
public class ConfigEnumValueProvider implements Configurator.ConfigValueProvider<Enum> {
    private Class<Enum> enumCLass;


    public ConfigEnumValueProvider(Class<Enum> enumCLass) {
        this.enumCLass = enumCLass;
    }


    @Override
    public String[] getPossibleValues() {
        Object[] constants = enumCLass.getEnumConstants();
        String[] values = new String[constants.length];

        for (int i = 0; i < constants.length; ++i)
            values[i] = ((Enum<?>) constants[i]).name();

        return values;
    }

    @Override
    public Enum getValueObject(String value) {
        return Enum.valueOf(enumCLass, value);
    }
}