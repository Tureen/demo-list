package club.tulane.design.factory.factorymethod;

import club.tulane.design.factory.base.IRuleConfigParser;

public interface IRuleConfigParserFactory {

    IRuleConfigParser createParser();
}
