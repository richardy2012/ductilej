//
// $Id$

package org.typelessj.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.samskivert.util.LogBuilder;
import com.sun.tools.javac.code.Symbol;

/**
 * Provides dynamic method dispatch, operator evaluation and other bits.
 */
public class RT
{
    /**
     * Emits a debug message.
     *
     * @param args key/value pairs, (e.g. "age", someAge, "size", someSize) which will be appended
     * to the log message as [age=someAge, size=someSize].
     */
    public static void debug (String message, Object... args)
    {
        System.out.println(new LogBuilder(message, args));
    }

    /**
     * Invokes the specified method via reflection, performing runtime type resolution and handling
     * the necessary name mangling to cope with de-typed overloads.
     */
    public static Object invoke (String mname, Object receiver, Object... args)
    {
        if (receiver == null) {
            throw new NullPointerException();
        }

        Method method = findMethod(mname, receiver.getClass(), args);
        if (method == null) {
            throw new NoSuchMethodError(); // TODO
        }

        try {
            return method.invoke(receiver, args);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite);
        }
    }

    /**
     * Executes the specified comparison operation on the supplied left- and right-hand-sides.
     *
     * @param opcode the string value of com.sun.source.tree.Tree.Kind for the comparison operator
     * in question.
     */
    public static boolean compare (String opcode, Object lhs, Object rhs)
    {
// TODO: we need to detect these in the AST and cast back to a boolean expression
//         LESS_THAN(BinaryTree.class),
//         GREATER_THAN(BinaryTree.class),
//         LESS_THAN_EQUAL(BinaryTree.class),
//         GREATER_THAN_EQUAL(BinaryTree.class),
//         EQUAL_TO(BinaryTree.class),
//         NOT_EQUAL_TO(BinaryTree.class),
//         CONDITIONAL_AND(BinaryTree.class),
//         CONDITIONAL_OR(BinaryTree.class),

        return false;
    }

    /**
     * Executes the specified operation on the supplied left- and right-hand-sides.
     *
     * @param opcode the string value of com.sun.source.tree.Tree.Kind for the operator in
     * question.
     */
    public static Object op (String opcode, Object lhs, Object rhs)
    {
// TODO: implement
//         POSTFIX_INCREMENT(UnaryTree.class),
//         POSTFIX_DECREMENT(UnaryTree.class),
//         PREFIX_INCREMENT(UnaryTree.class),
//         PREFIX_DECREMENT(UnaryTree.class),
//         UNARY_PLUS(UnaryTree.class),
//         UNARY_MINUS(UnaryTree.class),
//         BITWISE_COMPLEMENT(UnaryTree.class),
//         LOGICAL_COMPLEMENT(UnaryTree.class),
//         MULTIPLY(BinaryTree.class),
//         DIVIDE(BinaryTree.class),
//         REMAINDER(BinaryTree.class),
//         PLUS(BinaryTree.class),
//         MINUS(BinaryTree.class),
//         LEFT_SHIFT(BinaryTree.class),
//         RIGHT_SHIFT(BinaryTree.class),
//         UNSIGNED_RIGHT_SHIFT(BinaryTree.class),
//         AND(BinaryTree.class),
//         XOR(BinaryTree.class),
//         OR(BinaryTree.class),
//         MULTIPLY_ASSIGNMENT(CompoundAssignmentTree.class),
//         DIVIDE_ASSIGNMENT(CompoundAssignmentTree.class),
//         REMAINDER_ASSIGNMENT(CompoundAssignmentTree.class),
//         PLUS_ASSIGNMENT(CompoundAssignmentTree.class),
//         MINUS_ASSIGNMENT(CompoundAssignmentTree.class),
//         LEFT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),
//         RIGHT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),
//         UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),
//         AND_ASSIGNMENT(CompoundAssignmentTree.class),
//         XOR_ASSIGNMENT(CompoundAssignmentTree.class),
//         OR_ASSIGNMENT(CompoundAssignmentTree.class),

        return null;
    }

    /**
     * A helper for {@link #invoke}.
     */
    protected static Method findMethod (String mname, Class clazz, Object... args)
    {
        // TODO: this needs to be much smarter :)
      METHODS:
        for (Method method : clazz.getDeclaredMethods()) {
            Class<?>[] ptypes = method.getParameterTypes();
            if (!method.getName().equals(mname) || ptypes.length != args.length) {
                continue METHODS;
            }
            // debug("Checking " + method.getName() + " for match", "ptypes", ptypes, "args", args);
            for (int ii = 0; ii < args.length; ii++) {
                Class<?> ptype = ptypes[ii].isPrimitive() ? WRAPPERS.get(ptypes[ii]) : ptypes[ii];
                if (args[ii] != null && !ptype.isAssignableFrom(args[ii].getClass())) {
                    continue METHODS;
                }
            }
            return method;
        }
        Class parent = clazz.getSuperclass();
        return (parent == null) ? null : findMethod(mname, parent, args);
    }

    protected static final Map<Class<?>, Class<?>> WRAPPERS =
        ImmutableMap.<Class<?>, Class<?>>builder().
        put(Boolean.TYPE, Boolean.class).
        put(Byte.TYPE, Byte.class).
        put(Character.TYPE, Character.class).
        put(Short.TYPE, Short.class).
        put(Integer.TYPE, Integer.class).
        put(Long.TYPE, Long.class).
        put(Float.TYPE, Float.class).
        put(Double.TYPE, Double.class).
        build();
}
