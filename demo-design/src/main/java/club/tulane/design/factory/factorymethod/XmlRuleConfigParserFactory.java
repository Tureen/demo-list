package club.tulane.design.factory.factorymethod;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.XmlRuleConfigParser;

public class XmlRuleConfigParserFactory implements IRuleConfigParserFactory {
    @Override
    public IRuleConfigParser createParser() {
        return new XmlRuleConfigParser();
    }
}
