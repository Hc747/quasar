/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.paralleluniverse.fibers.instrument.live.fibers.instrument;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Stack;
import co.paralleluniverse.fibers.TestsHelper;
import co.paralleluniverse.strands.SuspendableRunnable;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * @author Matthias Mann
 */
public class InitialSizeTest implements SuspendableRunnable {
    
    @Test
    public void test1() {
        testWithSize(1);
    }
    
    @Test
    public void test2() {
        testWithSize(2);
    }
    
    @Test
    public void test3() {
        testWithSize(3);
    }
    
    private void testWithSize(int stackSize) {
        Fiber c = new Fiber(null, null, stackSize, this);
        //assertEquals(getStackSize(c), stackSize);
        boolean res = TestsHelper.exec(c);
        assertEquals(false, res);
        res = TestsHelper.exec(c);
        assertEquals(true, res);
        assertTrue(getStackSize(c) > 10);
    }

    @Override
    public void run() {
        int fac10 = factorial(10);
        assertEquals(3628800, fac10);
    }
    
    public int factorial(Integer a) {
        if(a == 0) {
            Fiber.park();
            return 1;
        }
        return a * factorial(a - 1);
    }
    
    private int getStackSize(Fiber c) {
        try {
            final Field stackField = Fiber.class.getDeclaredField("stack");
            stackField.setAccessible(true);
            final Object stack = stackField.get(c);
            final Field dataObjectField = Stack.class.getDeclaredField("dataObject");
            dataObjectField.setAccessible(true);
            final Object[] dataObject = (Object[])dataObjectField.get(stack);
            return dataObject.length;
        } catch (final Throwable ex) {
            throw new AssertionError(ex);
        }
    }
}