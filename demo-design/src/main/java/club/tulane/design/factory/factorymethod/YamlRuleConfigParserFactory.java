package club.tulane.design.factory.factorymethod;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.YamlRuleConfigParser;

public class YamlRuleConfigParserFactory implements IRuleConfigParserFactory {
    @Override
    public IRuleConfigParser createParser() {
        return new YamlRuleConfigParser();
    }
}
