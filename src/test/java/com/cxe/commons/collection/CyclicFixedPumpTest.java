/*
 * Copyright 2015 sathyasy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cxe.commons.collection;

import org.junit.*;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * @author sathyasy
 */
public class CyclicFixedPumpTest {

    public CyclicFixedPumpTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of Add and Remove method
     */
    @Test
    public void testAddRemove() {
        System.out.println("#### CyclicFixedPump Test_Add");
        CyclicFixedPump fastFixedPump = new CyclicFixedPump(1, 1);
        String value = "V";
        fastFixedPump.add(value);
        assertTrue(fastFixedPump.size() == 1);
        assertTrue(fastFixedPump.remove(value));
        assertTrue(fastFixedPump.size() == 0);
    }

    /**
     * Test of Add and Remove method
     */
    @Test
    public void testCyclicAdd() {
        System.out.println("#### CyclicFixedPump Test_Cyclic_Add");
        int count = 2;
        CyclicFixedPump fastFixedPump = new CyclicFixedPump(count, 1);
        fastFixedPump.add("V");
        fastFixedPump.add("S");
        //Now theretically Evicts V and has X instead
        fastFixedPump.add("X");
        assertTrue(fastFixedPump.size() == count); // Regardless of the fact that we added 3 items;
        Object[] pumped1 = new Object[1];
        fastFixedPump.pump(pumped1);
        assertTrue(pumped1[0] == "X");//Top was X
        Object[] pumped2 = new Object[1];
        fastFixedPump.pump(pumped2);
        assertTrue(pumped2[0] == "S");

        System.out.println("LastDrop: " + Arrays.toString(fastFixedPump.lastDrop()));
        System.out.println("To Array: " + Arrays.toString(fastFixedPump.toArray()));
        System.out.println("Pump Again: " + Arrays.toString(fastFixedPump.pump()));
        System.out.println("LastDrop Again: " + Arrays.toString(fastFixedPump.lastDrop()));
        System.out.println("To Array Again: " + Arrays.toString(fastFixedPump.toArray()));
    }


    @Test
    public void testAddALot() {
        System.out.println("#### CyclicFixedPump AddALot");
        long begin = System.nanoTime();
        int count = 100000;
        CyclicFixedPump fastFixedPump = new CyclicFixedPump(count, 4);
        for (int i = 0; i < count; i++) {
            fastFixedPump.add(i);
        }
        long end = System.nanoTime();
        assertTrue(fastFixedPump.size() == count);
        long timeTaken = end - begin;
        System.out.println("Time Taken:" + timeTaken);
    }

    @Test
    public void test_Pumping() {
        int count = 4;
        int pumpingVolume = 4;
        System.out.println("###### CyclicFixedPump Pumping Count:" + count + ", PumpingVolume:" + pumpingVolume);
        CyclicFixedPump<Integer> fastFixedPump = new CyclicFixedPump<Integer>(count, pumpingVolume);
        for (int i = 0; i < count; i++) {
            fastFixedPump.add(i);
        }
        assertTrue(fastFixedPump.size() == count);
        //First Pump Cannot be null
        Object[] pumped1 = fastFixedPump.pump();
        assertTrue(pumped1 != null);
        assertTrue(pumped1.length == pumpingVolume);
        System.out.println("Pumped 1: " + pumped1.length + ":" + Arrays.toString(pumped1));
        Object[] pumped2 = fastFixedPump.pump();
        assertTrue(pumped2 != null);
        assertTrue(pumped2.length == pumpingVolume);
        System.out.println("Pumped 2 : " + pumped2.length + ":" + Arrays.toString(pumped2));
        Object[] pumped3 = fastFixedPump.pump();
        assertTrue(pumped3 != null);
        System.out.println("Pumped 3: " + pumped3.length + ":" + Arrays.toString(pumped3));
    }

    @Test
    public void test_Pumping_with_Container() {
        int count = 6;
        int pumpingVolume = 2;
        System.out.println("###### CyclicFixedPump Count:" + count + ", PumpingVolume:" + pumpingVolume);
        CyclicFixedPump<Integer> fastFixedPump = new CyclicFixedPump<Integer>(count, pumpingVolume);
        for (int i = 0; i < count; i++) {
            fastFixedPump.add(i);
        }
        assertTrue(fastFixedPump.size() == count);
        //First Pump Cannot be null
        Object[] pumped1 = fastFixedPump.pump();
        System.out.println("Pumped 1: " + Arrays.toString(pumped1) + " Hash" + pumped1.hashCode());
        assertTrue(pumped1 != null);
        assertTrue(pumped1.length == pumpingVolume);
        Object[] pumped2 = fastFixedPump.pump();
        assertTrue(pumped2 != null);
        assertTrue(pumped2.length == pumpingVolume);
        System.out.println("Pumped 2: " + Arrays.toString(pumped2) + " Hash" + pumped2.hashCode());
        System.out.println("Check Pumped 1: " + Arrays.toString(pumped1) + " --- It Changed and shows same values as Pump2!!");
        assertTrue(Arrays.deepEquals(pumped1, pumped2));
        System.out.println("Hence, Pumped 1 Collection Hash === Pumped 2 Collection Hash");
        assertTrue(pumped1.hashCode() == pumped2.hashCode());
        Object[] pumped3Container = new Object[pumpingVolume];
        pumped3Container = fastFixedPump.pump(pumped3Container);
        assertTrue(Arrays.deepEquals(pumped2, pumped3Container));
        System.out.println("Pumped 3: " + Arrays.toString(pumped3Container) + " Hash" + pumped3Container.hashCode());
        System.out.println("Even though we Pumped 3 into a Container, the internal valve is going to be having the same state as a regular pumping");
        System.out.println("Check Pumped 2: " + Arrays.toString(pumped2) + " --- It Changed and shows same values as Pump3!!");
        System.out.println("BUT - Pumped 2 Collection Hash:" + pumped2.hashCode() + " !== Pumped 3 Collection Hash:" + pumped3Container.hashCode());
        assertTrue(pumped3Container != null);
    }

    @Test
    public void test_Pumping_with_small_Container() {
        int count = 6;
        int pumpingVolume = 4;
        System.out.println("###### CyclicFixedPump Count:" + count + ", PumpingVolume:" + pumpingVolume);
        CyclicFixedPump<Integer> fastFixedPump = new CyclicFixedPump<Integer>(count, pumpingVolume);
        for (int i = 0; i < count; i++) {
            fastFixedPump.add(i);
        }
        assertTrue(fastFixedPump.size() == count);
        //First Pump Cannot be null
        Object[] pumped1 = new Object[2];
        Object[] resultantContainer = fastFixedPump.pump(pumped1);
        assertTrue(pumped1 != null);
        //Make sure the Pump didnt change the container due to smaller size and overflow prevention logic
        assertTrue(pumped1.hashCode() == resultantContainer.hashCode());
    }

    @Test
    public void test_Pumping_with_bigger_Container() {
        int count = 6;
        int pumpingVolume = 2;
        System.out.println("###### CyclicFixedPump Test_Pumping_with_Bigger_Container Count:" + count + ", PumpingVolume:" + pumpingVolume);
        CyclicFixedPump<Integer> fastFixedPump = new CyclicFixedPump<Integer>(count, pumpingVolume);
        for (int i = 0; i < count; i++) {
            fastFixedPump.add(i);
        }
        assertTrue(fastFixedPump.size() == count);
        //First Pump Cannot be null
        Object[] pumped1 = new Object[12];
        Object[] resultantContainer = fastFixedPump.pump(pumped1);
        assertTrue(pumped1 != null);
        //Make sure the Pump didnt change the container due to smaller size and overflow prevention logic
        assertTrue(pumped1.hashCode() == resultantContainer.hashCode());
    }


    @Test
    public void test_Pumping_lastDrop() {
        int count = 4;
        int pumpingVolume = 2;
        System.out.println("###### CyclicFixedPump Test_LastDrop Count:" + count + ", PumpingVolume:" + pumpingVolume);
        CyclicFixedPump<Integer> fastFixedPump = new CyclicFixedPump<Integer>(count, pumpingVolume);
        for (int i = 0; i < count; i++) {
            fastFixedPump.add(i);
        }
        assertTrue(fastFixedPump.size() == count);
        //First Pump Cannot be null
        Object[] pumped1 = fastFixedPump.pump();
        assertTrue(pumped1 != null);
        assertTrue(pumped1.length == pumpingVolume);
        System.out.println("Pumped: " + pumped1.length + ":" + Arrays.toString(pumped1));
        Object[] pumped2 = fastFixedPump.pump();
        assertTrue(pumped2 != null);
        assertTrue(pumped2.length == pumpingVolume);
        System.out.println("Pumped: " + pumped2.length + ":" + Arrays.toString(pumped2));
        Object[] lastDrop = fastFixedPump.lastDrop();
        System.out.println("Last Drop: " + Arrays.toString(lastDrop));
        assertTrue(lastDrop.length == pumpingVolume);
    }
}
