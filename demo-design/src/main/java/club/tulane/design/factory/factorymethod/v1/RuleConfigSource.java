package club.tulane.design.factory.factorymethod.v1;

import club.tulane.design.factory.base.*;
import club.tulane.design.factory.factorymethod.IRuleConfigParserFactory;
import club.tulane.design.factory.factorymethod.JsonRuleConfigParserFactory;
import club.tulane.design.factory.factorymethod.XmlRuleConfigParserFactory;
import club.tulane.design.factory.factorymethod.YamlRuleConfigParserFactory;

/**
 * 工厂方法模式
 *
 */
public class RuleConfigSource {

    public RuleConfig load(String ruleConfigFilePath){
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);

        IRuleConfigParserFactory parserFactory = null;
        if("json".equalsIgnoreCase(ruleConfigFileExtension)){
            parserFactory = new JsonRuleConfigParserFactory();
        }else if("xml".equalsIgnoreCase(ruleConfigFileExtension)){
            parserFactory = new XmlRuleConfigParserFactory();
        }else if("yaml".equalsIgnoreCase(ruleConfigFileExtension)){
            parserFactory = new YamlRuleConfigParserFactory();
        }else if("properties".equalsIgnoreCase(ruleConfigFileExtension)){
            parserFactory = new YamlRuleConfigParserFactory();
        }else{
            throw new InvalidRuleConfigException("Rule config file format is not supported: " + ruleConfigFilePath);
        }

        String configText = "";
        // 从 ruleConfigFilePath 文件中读取配置文本到 configText 中
        IRuleConfigParser parser = parserFactory.createParser();
        RuleConfig ruleConfig = parser.parse(configText);
        return ruleConfig;
    }

    private String getFileExtension(String filePath) {
        // 根据文件名, 解析后缀
        return "json";
    }
}
