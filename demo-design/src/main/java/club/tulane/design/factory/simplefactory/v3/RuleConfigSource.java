package club.tulane.design.factory.simplefactory.v3;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.InvalidRuleConfigException;
import club.tulane.design.factory.base.RuleConfig;

/**
 * 将 createParser 剥离到工厂类, 使 RuleConfigSource 功能更单一
 *
 */
public class RuleConfigSource {

    public RuleConfig load(String ruleConfigFilePath){
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        IRuleConfigParser parser = RuleConfigParserFactory.createParser(ruleConfigFileExtension);
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
}
