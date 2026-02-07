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
            super(parser -> new Parser<ArrayList<T>>(s -> {
                ArrayList<T> out = new ArrayList<>();
                Token<T> t = parser.parse(s);
                String head = t.head();
                out.add(t.value());
                String f = t.follow();
                while (t.success() && !f.isEmpty()) {
                    t = parser.parse(s);
                    if (f.length() - 1 != t.follow().length()) {
                        break;
                    }
                    out.add(t.value());
                    f = t.follow();
                }
                return new Token<>(out, head, f, true);
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
            super(parser -> new Parser<ArrayList<T>>(s -> {
                ArrayList<T> out = new ArrayList<>();
                Token<T> t = parser.parse(s);
                String head = t.head();
                if (!t.success()) { return new Token<>(new ArrayList<>(), "", s, false); }
                out.add(t.value());
                String f = t.follow();
                while (t.success() && !f.isEmpty()) {
                    t = parser.parse(f);
                    if (f.length() - 1 != t.follow().length()) {
                        break;
                    }
                    out.add(t.value());
                    f = t.follow();
                }
                return new Token<>(out, head, f, true);
            }));
        }
    }
}
