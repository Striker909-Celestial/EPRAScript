package com.epra.eprascript.math_parsers;

import com.epra.eprascript.basic_parsers.CharParser;
import com.epra.eprascript.parser_modifiers.Combinator;
import com.epra.eprascript.parser_modifiers.Map;
import com.epra.eprascript.basic_parsers.Token;
import com.epra.eprascript.basic_parsers.Parser;

import java.util.ArrayList;

/// A group of [Parsers](Parser) that convert text into numerical values.
/// @author Striker-909
/// @since v0.0.0
public class NumberParsers {

    /// A [Map] from a [Character] [Parser] to an [Integer] [Parser].
    public static final Map<Character, Integer> DIGIT_MAP = new Map<>(
            (charParser) -> new Parser<>( s ->
            {
                Token<Character> token = charParser.parse(s);
                if (!token.success()) { return new Token<>(-1, token.head(), token.follow(), false); }
                return switch (token.value()) {
                    case '0' -> new Token<>(0, token.head(), token.follow(), true);
                    case '1' -> new Token<>(1, token.head(), token.follow(), true);
                    case '2' -> new Token<>(2, token.head(), token.follow(), true);
                    case '3' -> new Token<>(3, token.head(), token.follow(), true);
                    case '4' -> new Token<>(4, token.head(), token.follow(), true);
                    case '5' -> new Token<>(5, token.head(), token.follow(), true);
                    case '6' -> new Token<>(6, token.head(), token.follow(), true);
                    case '7' -> new Token<>(7, token.head(), token.follow(), true);
                    case '8' -> new Token<>(8, token.head(), token.follow(), true);
                    case '9' -> new Token<>(9, token.head(), token.follow(), true);
                    default -> new Token<>(-1, token.head(), token.follow(), false);
                };
            }
            )
    );

    /// A [Parser] that succeeds at the first digit [Character] within the intput [String].
    @SuppressWarnings("unchecked")
    public static final Parser<Character> RAW_DIGIT_PARSER = (Parser<Character>) Combinator.OR.combine(
            new CharParser('0'),
            new CharParser('1'),
            new CharParser('2'),
            new CharParser('3'),
            new CharParser('4'),
            new CharParser('5'),
            new CharParser('6'),
            new CharParser('7'),
            new CharParser('8'),
            new CharParser('9')
    );
    /// A [Parser] that succeeds at the first digit [Character] within the intput [String],
    /// and converts that [Character] into an [Integer].
    public static final Parser<Integer> DIGIT_PARSER = DIGIT_MAP.map(RAW_DIGIT_PARSER);

    /// A [Parser] that parses for continuous digit [`Characters`](Character) and converts them into
    /// an [ArrayList] of [`Integers`](Integer).
    public static final Parser<ArrayList<Integer>> DIGIT_LIST_PARSER =
            new Map.Many1<Integer>().map(DIGIT_PARSER);

    /// A [Map] that maps an [ArrayList] of [`Integers`](Integer) to a natural [Number].
    public static final Map<ArrayList<Integer>, Number> NATURAL_MAP = new Map<>(
            (parser) -> new Parser<>(s -> {
                Token<ArrayList<Integer>> token = parser.parse(s);
                if (!token.success()) { return new Token<>(-1, token.head(), token.follow(), false); }
                double total = 0.0;
                for (int i : token.value()) {
                    total *= 10.0;
                    total += i;
                }
                return new Token<>(total, token.head(), token.follow(), true);

            })
    );
    /// A [Map] that maps an [ArrayList] of [`Integers`](Integer) to a [Double] by taking each digit
    /// as the nth digit after the decimal point.
    ///
    /// Ex. `[1, 2, 5, 3, 7]` -> `0.12537`
    public static final Map<ArrayList<Integer>, Double> DECIMAL_MAP = new Map<>(
            parser -> new Parser<Double>( s -> {
                Token<ArrayList<Integer>> token = parser.parse(s);
                if (!token.success()) { return new Token<>(-1.0, token.head(), token.follow(), false); }
                double total = 0.0;
                for (int i : token.value()) {
                    total += i;
                    total /= 10.0;
                }
                return new Token<>(total, token.head(), token.follow(), true);
            })
    );

    /// A [Map] that maps an unsigned [Number] to a signed [Number].
    public static final Map<Number, Number> SIGN_MAP = new Map<>(
            (parser) -> new Parser<>(
                    s -> {
                        Token<Number> token = parser.parse(s);
                        int l = token.head().length();
                        if (!token.success() || l == 0) { return token; }
                        CharParser minus = new CharParser('-');
                        if (minus.parse(token.head().substring(l - 1, l)).success()) {
                            return new Token<>((-1) * (Double) token.value(), token.head().substring(0, l - 1), token.follow(), true);
                        }
                        return token;
                    }
            )
    );
    /// A [Map] that maps a natural [Number] to a real [Number].
    public static final Map<Number, Number> REAL_MAP = new Map<>(
            parser -> new Parser<>(s -> {
                Token<Number> token = parser.parse(s);
                int l = token.follow().length();
                if (!token.success() || l == 0) { return token; }
                CharParser dot = new CharParser('.');
                if (!dot.parse(token.follow().substring(0, 1)).success()) { return token; }
                Token<Double> decimal = DECIMAL_MAP.map(DIGIT_LIST_PARSER).parse(token.follow().substring(1));
                if (!decimal.success()) { return token; }
                return new Token<>((double) token.value() + decimal.value(), token.head(), decimal.follow(), true);
            })
    );

    /// A [Map] that maps a [Number] to an [Integer].
    private static final Map<Number, Integer> NUMBER_TO_INTEGER = new Map<>(parser -> new Parser<>(
    s -> {
        Token<Number> token = parser.parse(s);
        return new Token<>((int) token.value(), token.head(), token.follow(), token.success());
    }));
    /// A [Map] that maps a [Number] to an [Double].
    private static final Map<Number, Double> NUMBER_TO_DOUBLE = new Map<>(parser -> new Parser<>(
            s -> {
                Token<Number> token = parser.parse(s);
                return new Token<>((double) token.value(), token.head(), token.follow(), token.success());
            }));
    /// A [Parser] that parses a [String] for the first [Integer] in that [String].
    public static final Parser<Integer> INTEGER_PARSER = NUMBER_TO_INTEGER.map(SIGN_MAP.map(NATURAL_MAP.map(DIGIT_LIST_PARSER)));

    /// A [Parser] that parses a [String] for the first [Double] in that [String].
    public static final Parser<Double> DOUBLE_PARSER = NUMBER_TO_DOUBLE.map(SIGN_MAP.map(REAL_MAP.map(NATURAL_MAP.map(DIGIT_LIST_PARSER))));
}
