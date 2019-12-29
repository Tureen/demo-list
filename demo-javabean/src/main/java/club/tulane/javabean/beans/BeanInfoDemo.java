package club.tulane.javabean.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyEditorSupport;
import java.util.stream.Stream;

/**
 * {@link java.beans.BeanInfo} 示例
 */
public class BeanInfoDemo {

    public static void main(String[] args) throws IntrospectionException {

        // 第二个参数, 排除其父类Object, 因为查找时继承自父类的getClass()方法会混淆至打印结果中
        // 如果仅查当前类, 而不搜索父类的, 可以使用这种办法
        BeanInfo beanInfo = Introspector.getBeanInfo(Person.class, Object.class);

        Stream.of(beanInfo.getPropertyDescriptors())
                .forEach(propertyDescriptor -> {


                    // PropertyDescriptor 允许添加属性编辑器 - PropertyEditor
                    // GUI -> text(String) -> PropertyType
                    // name -> String
                    // age -> Integer
                    Class<?> propertyType = propertyDescriptor.getPropertyType();
                    String propertyName = propertyDescriptor.getName();
                    if("age".equals(propertyDescriptor)){ // 为 "age" 字段/属性增加 PropertyEdit
                        // String -> Integer
                        // Integer.valueOf("")
                        propertyDescriptor.setPropertyEditorClass(StringToIntegerPropertyEdit.class);
//                        propertyDescriptor.createPropertyEditor();
                    }

                    System.out.println(propertyDescriptor.toString());
                });
    }

    static class StringToIntegerPropertyEdit extends PropertyEditorSupport {

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            Integer value = Integer.valueOf(text);
            setValue(value);
        }
    }
}
