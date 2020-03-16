package club.tulane.design.factory.simplefactory.v1;

import club.tulane.design.factory.base.*;

/**
 * 1. 根据配置文件的后缀（json、xml、yaml、properties），选择不同的解析器（JsonRuleConfigParser、XmlRuleConfigParser……)
 * 2. 将存储在文件中的配置解析成内存对象 RuleConfig
 */
public class RuleConfigSource {

    public RuleConfig load(String ruleConfigFilePath){
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        IRuleConfigParser parser = createParser(ruleConfigFileExtension);
        if(parser == null){
            throw new InvalidRuleConfigException("Rule config file format is not supported: " + ruleConfigFilePath);
        }

        String configText = "";
        // 从 ruleConfigFilePath 文件中读取配置文本到 configText 中
        RuleConfig ruleConfig = parser.parse(configText);
        return ruleConfig;
    }

    private String getFileExtension(String filePath) {
        // 根据文件名, 解析后缀
        return "json";
    }

    public IRuleConfigParser createParser(String ruleConfigFileExtension) {
        IRuleConfigParser parser = null;
        if("json".equalsIgnoreCase(ruleConfigFileExtension)){
            parser = new JsonRuleConfigParser();
        }else if("xml".equalsIgnoreCase(ruleConfigFileExtension)){
            parser = new XmlRuleConfigParser();
        }else if("yaml".equalsIgnoreCase(ruleConfigFileExtension)){
            parser = new PropertiesRuleConfigParser();
        }
        return parser;
    }
}
