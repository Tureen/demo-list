package club.tulane.design.factory.abstractfactory;

import club.tulane.design.factory.base.IRuleConfigParser;

/**
 * 抽象工厂, 包容多种类型的 工厂方法 接口
 */
public interface IConfigParserFactory {

    IRuleConfigParser createRuleParser();
    ISystemConfigParser createSystemParser();
}
