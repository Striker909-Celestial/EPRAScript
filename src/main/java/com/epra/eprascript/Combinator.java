package com.epra.eprascript;

import java.util.function.BiFunction;

/// A [BiFunction] that combines two [`Parsers`](Parser) to form a new [Parser].
///
/// Queer Coded by Striker-909.
/// If you use this class or a method from this class in its entirety, please make sure to give credit.
public class Combinator {

    private final BiFunction<Parser<?>, Parser<?>, Parser<?>> combine;

    /// A [BiFunction] that combines two [`Parsers`](Parser) to form a new [Parser].
    /// @param combine The function to combine two parsers
    public Combinator(BiFunction<Parser<?>, Parser<?>, Parser<?>> combine) {
        this.combine = combine;
    }

    /// Combines two [`Parsers`](Parser) using the combine [BiFunction].
    /// @param parser1 First parser
    /// @param parser2 Second parser
    /// @return The two parsers, combined
    public Parser<?> combine(Parser<?> parser1, Parser<?> parser2) {
        return combine.apply(parser1, parser2);
    }

    /// Combines two or more [`Parsers`](Parser) using the combine [BiFunction] in series.
    ///
    /// `combine(a, b, c, d, e)` is equivalent to `combine(combine(combine(combine(a, b), c), d), e)`
    /// @param parser A parser
    /// @param parsers Any number of additional parsers
    /// @return The combination of all the parsers
    public Parser<?> combine(Parser<?> parser, Parser<?>... parsers) {
        Parser<?> out = combine(parser, parsers[0]);
        for (int i = 1; i < parsers.length; i++) {
            out = combine.apply(out, parsers[i]);
        }
        return out;
    }

    /// A [Combinator] that applies an `or` [BiFunction] to the success of two [`Parsers`](Parser).
    ///
    /// - If neither [Parser] succeeds, returns a [Token] with `null` value and success as `false`.
    /// - If only one [Parser] succeeds, returns the output of that [Parser].
    /// - If both [`Parsers`](Parser) succeed, returns the output with the longest `follow`,
    /// or the output of the first parser if the two `follows` have the same length.
    public static final Combinator OR = new Combinator(
            (p1, p2) -> new Parser(
                    s -> {
                        Token<?> t = p1.parse(s.toString());
                        Token<?> u = p2.parse(s.toString());
                        if (t.success() && !u.success()) { return t; }
                        if (u.success() && !t.success()) { return u; }
                        if (!t.success()) { return new Token<>(null, "", s.toString(), false); }
                        if (t.follow().length() >= u.follow().length()) { return t; }
                        return u;
                    }
            )
    );
    /// A [Combinator] that applies an `and` [BiFunction] to the success of two [`Parsers`](Parser).
    ///
    /// - If both [`Parsers`](Parser) succeed, returns the output with the longest `follow`,
    /// or the output of the first parser if the two `follows` have the same length.
    /// - Otherwise, returns a [Token] with `null` value and success as `false`.
    public static final Combinator AND = new Combinator(
            (p1, p2) -> new Parser(
                    s -> {
                        Token<?> t = p1.parse(s.toString());
                        Token<?> u = p2.parse(s.toString());
                        if (!t.success() || !u.success()) { return new Token<>(null, "", s.toString(), false); }
                        if (t.follow().length() >= u.follow().length()) { return t; }
                        return u;
                    }
            )
    );
    /// A [Combinator] that applies an `and` [BiFunction] to the success of two [`Parsers`](Parser).
    ///
    /// - If both [`Parsers`](Parser) succeed, returns the output of the first [Parser],
    /// or the output of the first parser if the two `follows` have the same length.
    /// - Otherwise, returns a [Token] with `null` value and success as `false`.
    public static final Combinator AND_1 = new Combinator(
            (p1, p2) -> new Parser(
                    s -> {
                        Token<?> t = p1.parse(s.toString());
                        Token<?> u = p2.parse(s.toString());
                        if (!t.success() || !u.success()) { return new Token<>(null, "", s.toString(), false); }
                        return t;
                    }
            )
    );
    /// A [Combinator] that applies an `and` [BiFunction] to the success of two [`Parsers`](Parser).
    ///
    /// - If both [`Parsers`](Parser) succeed, returns the output of the second [Parser],
    /// or the output of the first parser if the two `follows` have the same length.
    /// - Otherwise, returns a [Token] with `null` value and success as `false`.
    public static final Combinator AND_2 = new Combinator(
            (p1, p2) -> new Parser(
                    s -> {
                        Token<?> t = p1.parse(s.toString());
                        Token<?> u = p2.parse(s.toString());
                        if (!t.success() || !u.success()) { return new Token<Object>(null, "", s.toString(), false); }
                        return u;
                    }
            )
    );
}
