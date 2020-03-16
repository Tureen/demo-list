package club.tulane.design.factory.factorymethod;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.PropertiesRuleConfigParser;

public class PropertiesRuleConfigParseFactory implements IRuleConfigParserFactory{
    @Override
    public IRuleConfigParser createParser() {
        return new PropertiesRuleConfigParser();
    }
}
