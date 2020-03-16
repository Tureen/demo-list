package club.tulane.design.factory.factorymethod.v2;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.InvalidRuleConfigException;
import club.tulane.design.factory.base.RuleConfig;
import club.tulane.design.factory.factorymethod.IRuleConfigParserFactory;

/**
 * 工厂方法模式 + 简单工程模式
 */
public class RuleConfigSource {

    public RuleConfig load(String ruleConfigFilePath) {
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);

        IRuleConfigParserFactory parserFactory = RuleConfigParserFactoryMap.getParserFactory(ruleConfigFileExtension);
        if (parserFactory == null) {
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
