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

    private final Object stringColl;
    private final Object becomeString;
    private final Object becomeInt;
    private final Object becomeDouble;
    private final Object becomeLong;
    private final Object checkBool;
    private final int type;
    private final Object stayString;


    public ObjectReaderTest(int type, Object stringColl, Object becomeString, Object becomeInt, Object becomeDouble, Object becomeLong, Object checkBool, String stayStrig){
        this.type = type;
        this.stringColl = stringColl;
        this.becomeString = becomeString;
        this.becomeInt = becomeInt;
        this.becomeDouble = becomeDouble;
        this.becomeLong = becomeLong;
        this.checkBool = checkBool;
        this.stayString = stayStrig;
    }

    @Parameterized.Parameters
    public static Collection returnParams() {
        Collection<String> stringCollection = new ArrayList<>();
        stringCollection.add("Hello");
        stringCollection.add("World");
        return Arrays.asList(new Object[][] {
                {1,stringCollection, 44,"33",22,"11",11>12,"string"},
                {2,33,null,null,null,null,null,""},
                {3,null,22,(long) 33,"",3,null,""}

        });
    }

    @Test
    public void checkTests(){
        if(this.type == 1){

            //Right String with default
            String s = "string";
            assertEquals(s, ObjectReader.getString(this.stayString, null));

            //Right Int con default
            int num2 = 33;
            assertEquals(num2, ObjectReader.getInt(this.becomeInt,0));

            long numL = 11;
            //Right Long con default
            assertEquals(numL, ObjectReader.getLong(this.becomeLong,(long) 0));

        }else if(this.type == 2){
            //Valori diversi
            //String che ritorna il defaultValue se null
            assertEquals("default", ObjectReader.getString(this.becomeString, "default"));

            long valLong = 0;
            assertEquals(valLong,ObjectReader.getLong(this.becomeLong,(long)0));

            //Exception con String e default
            assertThrows(Exception.class, () -> {
                ObjectReader.getString(this.stringColl,null);
            });

            assertEquals(0,ObjectReader.getInt(this.becomeInt,0));


        }
    }


}