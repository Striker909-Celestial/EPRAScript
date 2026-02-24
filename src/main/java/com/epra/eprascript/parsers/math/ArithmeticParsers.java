package com.epra.eprascript.parsers.math;

import com.epra.eprascript.parsers.function.FunctionParser;
import com.epra.eprascript.parsers.modifiers.Combinator;
import com.epra.eprascript.parsers.Token;
import com.epra.eprascript.parsers.Parser;

/// A group of [Parsers](Parser) for parsing basic arithmetic functions.
/// @author Striker-909
/// @since v0.2.0
public class ArithmeticParsers {

    /// A [`FunctionParser`](Parser#FunctionParser) for addition.
    ///
    /// Signature: `x+y`
    ///
    /// Result: The sum of `x` and `y`
    public static final FunctionParser<Double> ADDITION = new FunctionParser<>(
            "$val1$+$val2$",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val1 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val1")).value();
                double val2 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val2")).value();
                //System.out.println(val1 + " + " + val2 + " = " + (val1 + val2));
                return () -> val1 + val2;
            }
    );
    /// A [`FunctionParser`](Parser#FunctionParser) for subtraction.
    ///
    /// Signature: `x-y`
    ///
    /// Result: The difference of `x` and `y`
    public static final FunctionParser<Double> SUBTRACTION = new FunctionParser<>(
            "$val1$-$val2$",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val1 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val1")).value();
                double val2 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val2")).value();
                //System.out.println(val1 + " - " + val2 + " = " + (val1 - val2));
                return () -> val1 - val2;
            }
    );
    /// A [`FunctionParser`](Parser#FunctionParser) for multiplication.
    ///
    /// Signature: `x*y`
    ///
    /// Result: The product of `x` and `y`
    public static final FunctionParser<Double> MULTIPLICATION = new FunctionParser<>(
            "$val1$*$val2$",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val1 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val1")).value();
                double val2 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val2")).value();
                //System.out.println(val1 + " * " + val2 + " = " + (val1 * val2));
                return () -> val1 * val2;
            }
    );

    /// A [`FunctionParser`](Parser#FunctionParser) for division.
    ///
    /// Signature: `x/y`
    ///
    /// Result: The quotient of `x` and `y`
    public static final FunctionParser<Double> DIVISION = new FunctionParser<>(
            "$val1$/$val2$",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val1 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val1")).value();
                double val2 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val2")).value();
                //System.out.println(val1 + " / " + val2 + " = " + (val1 / val2));
                return () -> val1 / val2;
            }
    );

    /// A [`FunctionParser`](Parser#FunctionParser) for integer division.
    ///
    /// Signature: `x//y`
    ///
    /// Result: The quotient of `x` and `y` with the decimal component removed
    public static final FunctionParser<Double> INTEGER_DIVISION = new FunctionParser<>(
            "$val1$//$val2$",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val1 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val1")).value();
                double val2 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val2")).value();
                //System.out.println(val1 + " // " + val2 + " = " + (double)(int)(val1 / val2));
                return () -> (double)(int)(val1 / val2);
            }
    );

    /// A [`FunctionParser`](Parser#FunctionParser) for modular division.
    ///
    /// Signature: `x%y`
    ///
    /// Result: The result of `x` mod `y`
    public static final FunctionParser<Double> MODULUS = new FunctionParser<>(
            "$val1$%$val2$",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val1 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val1")).value();
                double val2 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val2")).value();
                //System.out.println(val1 + " % " + val2 + " = " + (val1 % val2));
                return () -> val1 % val2;
            }
    );

    /// A [`FunctionParser`](Parser#FunctionParser) for exponentiation.
    ///
    /// Signature: `x^y`
    ///
    /// Result: `x` raised to the `y`
    public static final FunctionParser<Double> EXPONENT = new FunctionParser<>(
            "$val1$^$val2$",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val1 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val1")).value();
                double val2 = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val2")).value();
                //System.out.println(val1 + " ^ " + val2 + " = " + Math.pow(val1, val2));
                return () -> Math.pow(val1, val2);
            }
    );

    /// A [`FunctionParser`](Parser#FunctionParser) for absolute value.
    ///
    /// Signature: `|x|`
    ///
    /// Result: The absolute value of `x`
    public static final FunctionParser<Double> ABSOLUTE = new FunctionParser<>(
            "|$val$|",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val")).value();
                //System.out.println("|" + val + "| = " + Math.abs(val));
                return () -> Math.abs(val);
            }
    );

    /// A [`FunctionParser`](Parser#FunctionParser) for parentheses.
    ///
    /// Signature: `(x)`
    ///
    /// Result: The value of `x`
    public static final FunctionParser<Double> PARENTHESES = new FunctionParser<>(
            "($val$)",
            "-?\\d+(\\.\\d+)?",
            new Parser<>(s -> {
                Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                if (!token.success()) { return token; }
                return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
            }),
            inputs -> {
                double val = NumberParsers.DOUBLE_PARSER.parse(inputs.get("val")).value();
                //System.out.println("(" + val + ") = " + val);
                return () -> val;
            }
    );

    /// A [Parser] for addition and subtraction.
    public final static Parser<?> ADDITION_SUBTRACTION = Combinator.OR.combine(
            ADDITION,
            SUBTRACTION
    );

    /// A [Parser] for multiplication, division, integer division, and modular division.
    public final static Parser<?> MULTIPLICATION_DIVISION = Combinator.OR.combine(
            MULTIPLICATION,
            DIVISION,
            INTEGER_DIVISION,
            MODULUS
    );

    /// A [Parser] for all arithmetic expressions. (Uses PEMDAS)
    public final static Parser<?> ARITHMETIC = Combinator.SEQUENCE.combine(
            PARENTHESES,
            ABSOLUTE,
            EXPONENT,
            MULTIPLICATION_DIVISION,
            ADDITION_SUBTRACTION
    );
}
