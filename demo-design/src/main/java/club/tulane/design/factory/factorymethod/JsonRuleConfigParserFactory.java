package club.tulane.design.factory.factorymethod;

import club.tulane.design.factory.base.IRuleConfigParser;
import club.tulane.design.factory.base.JsonRuleConfigParser;

public class JsonRuleConfigParserFactory implements IRuleConfigParserFactory {
    @Override
    public IRuleConfigParser createParser() {
        return new JsonRuleConfigParser();
    }
}
