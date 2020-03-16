package club.tulane.design.factory.abstractfactory;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.JsonRuleConfigParser;

public class JsonRuleConfigParserFactory implements IConfigParserFactory {


    @Override
    public IRuleConfigParser createRuleParser() {
        return new JsonRuleConfigParser();
    }

    @Override
    public ISystemConfigParser createSystemParser() {
        return new JsonSystemConfigParser();
    }
}
