package com.github.gg_a;

import com.github.gg_a.text.StringInterpolator;
import com.github.gg_a.tuple.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.gg_a.MyAliases.*;
import static org.junit.jupiter.api.Assertions.*;


enum MyAliases implements TupleAlias {
    NAME, AGE, NICKNAME, ID, EMAILS, HEIGHT, WEIGHT, AMOUNT
}
/**
 * @author GG
 * @version 1.0
 */
public class InterpolatorTest {

    @Test
    public void testInterpolator() {
        Tuple t1 = Tuple.of("zs", 123456).alias(NAME, ID);
        Tuple t2 = Tuple.of(20, "tom", 190.5).alias("age", "nickName", "height");

        StringInterpolator si = StringInterpolator.of(t1, t2);
        String parse = si.parse("${NAME}==${age}==${nickName}==${ID}==${height}");

        assertEquals("zs==20==tom==123456==190.5", parse);

        Map<String, Object> valueMap = si.getValueMap();
        assertEquals("{ID=123456, nickName=tom, age=20, NAME=zs, height=190.5}", valueMap.toString());
        assertThrows(UnsupportedOperationException.class, () -> valueMap.put("new_value", "1"));

    }

    @Test
    public void testDefaultValueAndEscape() {
        String source = "${NAME}==${age:20}==${ID}==${ID }==$${ID}==${id}==${age::20}" +
                "==$$${ID}==$$$${ID}==$${${ID}";

        Tuple t1 = Tuple.of("zs", 123456, 20.0).alias(NAME, ID, AMOUNT);
        StringInterpolator si = StringInterpolator.of(t1, null, Tuple.empty());
        String parse = si.parse(source);

        assertEquals("zs==20==123456==${ID }==${ID}==${id}==:20==$${ID}==$$${ID}==${123456", parse);

        StringInterpolator si1 = new StringInterpolator().setEscapeChar('\\').add(t1);
        String s1 = "${ID}==\\${ID}==${NAME}==$${AMOUNT}";
        String parse1 = si1.parse(s1);
        System.out.println(parse1);
        assertEquals("123456==${ID}==zs==$20.0", parse1);

    }

    @Test
    public void testAddSetDel() {
        String source = "${NAME}==${age}==${nickName}==${ID}==${height}";

        Tuple t1 = Tuple.of("zs", 123456).alias(NAME, ID);
        StringInterpolator si = StringInterpolator.of(t1);
        String parse = si.parse(source);
        System.out.println(parse);
        assertEquals("zs==${age}==${nickName}==123456==${height}", parse);

        Tuple t2 = Tuple.of(20, "tom").alias("age", "nickName");
        si.add(t2);         // add
        parse = si.parse(source);
        System.out.println(parse);
        assertEquals("zs==20==tom==123456==${height}", parse);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("height", 175);
        si.add(hashMap);    // add
        parse = si.parse(source);
        System.out.println(parse);
        assertEquals("zs==20==tom==123456==175", parse);

        si.del("age", "nickName");  // delete
        parse = si.parse(source);
        System.out.println(parse);
        assertEquals("zs==${age}==${nickName}==123456==175", parse);

        Tuple t3 = Tuple.of(20, "tom").alias("age", "nickName");
        si.set(t3);             // set
        parse = si.parse(source);
        System.out.println(parse);
        assertEquals("${NAME}==20==tom==${ID}==${height}", parse);

    }

    @Test
    public void testNull() {
        StringInterpolator si = new StringInterpolator((Tuple) null, Tuple.empty());
        StringInterpolator si1 = new StringInterpolator((Tuple) null);
    }
}
