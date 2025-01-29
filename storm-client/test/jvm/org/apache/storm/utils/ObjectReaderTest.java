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
                {4, 33, (long) 33, "22"}

        });
    }

    @Test
    public void checkTests(){
        if(this.type == 1){

            //Right String with default
            String s = "string";
            assertEquals(s, ObjectReader.getString(this.stringTest, null));

            //Right Int con default
            int num2 = 33;
            assertEquals(num2, ObjectReader.getInt(this.intTest,null));

            long numL = 11;
            //Right Long con default
            assertEquals(numL, ObjectReader.getLong(this.longTest,null));


        }else if(this.type == 2){
            //Valori diversi
            //String che ritorna il defaultValue se null
            assertEquals("default", ObjectReader.getString(this.stringTest, "default"));

           // long valLong = 0;
            assertEquals(null,ObjectReader.getLong(this.longTest,null));


            assertEquals(null,ObjectReader.getInt(this.intTest,null));


        } else if (this.type == 3) {
            //Exception con valore diverso da string
            assertThrows(IllegalArgumentException.class, () -> {
                ObjectReader.getString(this.stringTest,null);
            });

            //converte valore che è una stringa
            assertEquals(33,ObjectReader.getInt(this.intTest,null));

            //converte valore che è una stringa
            assertEquals(22,ObjectReader.getLong(this.longTest,null));


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

        } else if (this.type == 4) {
            //MAX E MINN
            //Exception con valore che getLong non sa gestire
            assertThrows(IllegalArgumentException.class, () -> {
                ObjectReader.getInt((long)Integer.MAX_VALUE+1,null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                ObjectReader.getInt((long)Integer.MIN_VALUE-1,null);
            });
        }
    }


}