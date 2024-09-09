package org.apache.storm.utils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
            //TEST CHE VANNO A BUON FINE

            //Right List String
            List<String> lis = new ArrayList<>();
            lis.add("Hello");
            lis.add("World");
            assertEquals(lis, ObjectReader.getStrings(this.stringColl));

            //Right String
            List<String> lis2 = new ArrayList<>();
            lis2.add("string");
            assertEquals(lis2, ObjectReader.getStrings(this.stayString)); //SE PASSI UNA STRINGA A getStrings ti torna lista con la stringa

            //Right String
            String str = "44";
            assertEquals(str, ObjectReader.getString(this.becomeString));

            //Right String with default
            String s = "string";
            assertEquals(s, ObjectReader.getString(this.stayString, null));

            //Right Int no default
            int num2 = 33;
            assertEquals(num2, ObjectReader.getInt(this.becomeInt));

            //Right Int con default
            assertEquals(num2, ObjectReader.getInt(this.becomeInt,0));

            //Right Double no deafult
            long numD = 22;
            assertEquals(numD, ObjectReader.getDouble(this.becomeDouble));

            //Right Double con deafult
            assertEquals(numD, ObjectReader.getDouble(this.becomeDouble,0.0));

            //Right Long no default
            long numL = 11;
            assertEquals(numL, ObjectReader.getLong(this.becomeLong));

            //Right Long con default
            assertEquals(numL, ObjectReader.getLong(this.becomeLong,(long) 0));

            //Right Bool
            assertFalse(ObjectReader.getBoolean(this.checkBool, true));

        }else if(this.type == 2){
            //TEST CHE VANNO MALE

            //Valori diversi
            //String che ritorna il defaultValue se null
            assertEquals("default", ObjectReader.getString(this.becomeString, "default"));

            //Null con Long
            assertNull(ObjectReader.getLong(this.becomeLong));

            long valLong = 0;
            assertEquals(valLong,ObjectReader.getLong(this.becomeLong,(long)0));

            //Null con bool
            assertFalse(ObjectReader.getBoolean(this.checkBool, false));

            //Eccezioni
            //Exception con List String
            assertThrows(Exception.class, () -> {
                ObjectReader.getStrings(this.stringColl);
            });

            //Exception con String
            assertThrows(Exception.class, () -> {
                ObjectReader.getString(this.becomeString);
            });

            //Exception con String e default
            assertThrows(Exception.class, () -> {
                ObjectReader.getString(this.stringColl,null);
            });

            //Exception con Int
            assertThrows(Exception.class, () -> {
                ObjectReader.getInt(this.becomeInt);
            });

            assertEquals(0,ObjectReader.getInt(this.becomeInt,0));

            //Exception con Double
            assertThrows(Exception.class, () -> {
                ObjectReader.getDouble(this.becomeDouble);
            });

            assertEquals(0.0,ObjectReader.getDouble(this.becomeDouble,0.0));

            //Exception con Bool
            assertThrows(Exception.class, () -> {
                ObjectReader.getBoolean(this.stringColl,false);
            });


        }else{

            assertFalse(ObjectReader.getBoolean(this.checkBool,false));

            assertEquals(33,ObjectReader.getInt(this.becomeInt)); //passi long a get int

            assertEquals(22,ObjectReader.getInt(22));  //passi int a get int

            assertEquals(new ArrayList<>(),ObjectReader.getStrings(this.stringColl));   //con null a getStrings torna lista vuota


            //Exception con valore double
            double dov = 33.0;
            assertThrows(Exception.class, () -> {
                ObjectReader.getInt(dov,0);
            });

            //Exception con Double
            assertThrows(Exception.class, () -> {
                ObjectReader.getDouble(this.becomeDouble,0.0);
            });


            long val = 3;
            assertEquals(val, ObjectReader.getLong(this.becomeLong));

        }
    }
}