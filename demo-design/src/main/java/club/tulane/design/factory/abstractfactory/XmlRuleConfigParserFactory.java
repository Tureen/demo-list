package club.tulane.design.factory.abstractfactory;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.XmlRuleConfigParser;

public class XmlRuleConfigParserFactory implements IConfigParserFactory{

    @Override
    public IRuleConfigParser createRuleParser() {
        return new XmlRuleConfigParser();
    }

    @Override
    public ISystemConfigParser createSystemParser() {
        return new XmlSystemConfigParser();
    }
}
