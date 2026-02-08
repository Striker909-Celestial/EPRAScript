package com.epra.eprascript;

import java.util.ArrayList;
import java.util.function.Function;

/// A [Function] that maps a [Parser] of type [I] onto a [Parser] of type [O].
///
/// Queer Coded by Striker-909.
/// If you use this class or a method from this class in its entirety, please make sure to give credit.
public class Map<I, O> {

    private final Function<Parser<I>, Parser<O>> map;

    /// A [Function] that maps a [Parser] of type [I] onto a [Parser] of type [O].
    /// @param map The function to map parsers
    public Map(Function<Parser<I>, Parser<O>> map) {
        this.map = map;
    }

    /// Applies the map [Function] to a [Parser].
    /// @param input A parser to be mapped
    /// @return The output of mapping the parser
    public Parser<O> map(Parser<I> input) {
        return  map.apply(input);
    }

    // Subclasses

    /// A recursive parsing function used in the definition if [Many0] and [Many1].
    /// @param parser The parser to be used
    /// @param s The string to be parsed
    /// @return A token with an array list of all continuous values of successful parses,
    ///  starting at the beginning of the string
    private static <T> Token<ArrayList<T>> many(Parser<T> parser, String s) {
        Token<T> t = parser.parse(s);
        // Fail or discontinuity -> empty array list
        if (!t.success() || !t.head().isEmpty()) { return new Token<>(new ArrayList<>(), "", s, true); }
        // Parse follow, add the value from the last parse to the front
        Token<ArrayList<T>> out = many(parser, t.follow());
        out.value().addFirst(t.value());
        return out;
    }

    /// A [Map] that maps a [Parser] of type [T] onto a [Parser] of type [`ArrayList<T>`](ArrayList).
    ///
    /// The parser will be applied to a string repeatedly until either the parser fails
    /// or the parsed sections are non-continuous. The output [Token]'s `value` will be a list of
    /// all the parsed `values`.
    ///
    /// The returned parser will not fail, even if the input parser fails on the first run.
    ///
    /// Queer Coded by Striker-909.
    /// If you use this class or a method from this class in its entirety, please make sure to give credit.
    public static class Many0<T> extends Map<T, ArrayList<T>> {
        /// A [Map] that maps a [Parser] of type [T] onto a [Parser] of type [`ArrayList<T>`](ArrayList).
        ///
        /// The parser will be applied to a string repeatedly until either the parser fails
        /// or the parsed sections are non-continuous. The output [Token]'s `value` will be a list of
        /// all the parsed `values`.
        ///
        /// The returned parser will not fail, even if the input parser fails on the first run.
        public Many0() {
            super(parser -> new Parser<>(s -> {
                Token<T> t = parser.parse(s);
                Token<ArrayList<T>> out = many(parser, t.follow());
                out.value().addFirst(t.value());
                return new Token<>(out.value(), t.head(), out.follow(), true);
            }));
        }
    }
    /// A [Map] that maps a [Parser] of type [T] onto a [Parser] of type [`ArrayList<T>`](ArrayList).
    ///
    /// The parser will be applied to a string repeatedly until either the parser fails
    /// or the parsed sections are non-continuous. The output [Token]'s `value` will be a list of
    /// all the parsed `values`.
    ///
    /// The returned parser will fail if the input parser fails on the first run.
    ///
    /// Queer Coded by Striker-909.
    /// If you use this class or a method from this class in its entirety, please make sure to give credit.
    public static class Many1<T> extends Map<T, ArrayList<T>> {
        /// A [Map] that maps a [Parser] of type [T] onto a [Parser] of type [`ArrayList<T>`](ArrayList).
        ///
        /// The parser will be applied to a string repeatedly until either the parser fails
        /// or the parsed sections are non-continuous. The output [Token]'s `value` will be a list of
        /// all the parsed `values`.
        ///
        /// The returned parser will fail if the input parser fails on the first run.
        public Many1() {
            super(parser -> new Parser<>(s -> {
                Token<T> t = parser.parse(s);
                if (!t.success()) { return new Token<>(new ArrayList<>(), "", s, false); }
                Token<ArrayList<T>> out = many(parser, t.follow());
                out.value().addFirst(t.value());
                return new Token<>(out.value(), t.head(), out.follow(), true);
            }));
        }
    }
}
