package org.apache.storm.utils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(value = Parameterized.class)
public class ObjectReaderTest {


    private final Object stringTest;
    private final Object intTest;
    private final Object longTest;
    private final int type;



    public ObjectReaderTest(int type, Object stringTest, Object intTest, Object longTest){
        this.type = type;
        this.stringTest = stringTest;
        this.intTest = intTest;
        this.longTest = longTest;

    }

    @Parameterized.Parameters
    public static Collection returnParams() {

        return Arrays.asList(new Object[][] {
                {1, "string", 33, 11},
                {2, null, null, null},
                {3, 33, "33", "22"},

                //Aggiuntivo
                {4, 33, (long) 33, "22"}

        });
    }

    @Test
    public void checkTests(){
        if(this.type == 1){

            String s = "string";
            assertEquals(s, ObjectReader.getString(this.stringTest, null));


            int num2 = 33;
            assertEquals(num2, ObjectReader.getInt(this.intTest,0));

            long numL = 11;

            assertEquals(numL, ObjectReader.getLong(this.longTest,null));


        }else if(this.type == 2){

            assertEquals("default", ObjectReader.getString(this.stringTest, "default"));

            assertEquals(null,ObjectReader.getLong(this.longTest,null));


            assertEquals(null,ObjectReader.getInt(this.intTest,null));


        } else if (this.type == 3) {

            assertThrows(IllegalArgumentException.class, () -> {
                ObjectReader.getString(this.stringTest,null);
            });


            assertEquals(33,ObjectReader.getInt(this.intTest,null));


            assertEquals(22,ObjectReader.getLong(this.longTest,null));


        } else if (this.type == 4) {

            assertThrows(IllegalArgumentException.class, () -> {
                ObjectReader.getInt((long)Integer.MAX_VALUE+1,null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                ObjectReader.getInt((long)Integer.MIN_VALUE-1,null);
            });

            //converte valore che è un long in int
            long value = 33;
            assertEquals(33,ObjectReader.getInt(value,null));

            //Exception con valore che getInt non sa gestire
            assertThrows(IllegalArgumentException.class, () -> {
                ObjectReader.getInt((double) 22,null);
            });

            //Exception con valore che getLong non sa gestire
            assertThrows(IllegalArgumentException.class, () -> {
                ObjectReader.getLong(true,null);
            });
        }
    }


}