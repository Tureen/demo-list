package club.tulane.design.factory.simplefactory.v3;

import club.tulane.design.factory.base.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单工厂 第二种实现: 复用缓存
 */
public class RuleConfigParserFactory {

    private static final Map<String, IRuleConfigParser> cacheParsers = new HashMap<>();

    static {
        cacheParsers.put("json", new JsonRuleConfigParser());
        cacheParsers.put("xml", new XmlRuleConfigParser());
        cacheParsers.put("yaml", new YamlRuleConfigParser());
        cacheParsers.put("properties", new PropertiesRuleConfigParser());
    }

    public static IRuleConfigParser createParser(String configFormat) {
        if(configFormat == null || configFormat.isEmpty()){
            return null;
        }
        IRuleConfigParser parser = cacheParsers.get(configFormat.toLowerCase());
        return parser;
    }
}
