package club.tulane.design.factory.factorymethod.v2;

import club.tulane.design.factory.factorymethod.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 在工厂方法模式下, 再包装一层简单工厂模式
 */
public class RuleConfigParserFactoryMap {

    private static final Map<String, IRuleConfigParserFactory> cachedFactories = new HashMap<>();

    static {
        cachedFactories.put("json", new JsonRuleConfigParserFactory());
        cachedFactories.put("xml", new XmlRuleConfigParserFactory());
        cachedFactories.put("yaml", new YamlRuleConfigParserFactory());
        cachedFactories.put("properties", new PropertiesRuleConfigParseFactory());
    }

    public static IRuleConfigParserFactory getParserFactory(String configFormat){
        if(configFormat == null || configFormat.isEmpty()){
            return null;
        }
        IRuleConfigParserFactory parserFactory = cachedFactories.get(configFormat.toLowerCase());
        return parserFactory;
    }
}
