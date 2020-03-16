package club.tulane.design.factory.simplefactory.v2;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.JsonRuleConfigParser;
import club.tulane.design.factory.base.PropertiesRuleConfigParser;
import club.tulane.design.factory.base.XmlRuleConfigParser;

/**
 * 简单工厂 第一种实现: 每次 new 对象
 */
public class RuleConfigParserFactory {

    public static IRuleConfigParser createParser(String configFormat) {
        IRuleConfigParser parser = null;
        if("json".equalsIgnoreCase(configFormat)){
            parser = new JsonRuleConfigParser();
        }else if("xml".equalsIgnoreCase(configFormat)){
            parser = new XmlRuleConfigParser();
        }else if("yaml".equalsIgnoreCase(configFormat)){
            parser = new PropertiesRuleConfigParser();
        }
        return parser;
    }
}
