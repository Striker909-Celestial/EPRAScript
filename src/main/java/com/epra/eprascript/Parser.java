package com.epra.eprascript;

import java.util.function.Function;

/// A [Function] that parses a [String] input into an [Token] of type [T].
///
/// Queer Coded by Striker-909.
/// If you use this class or a method from this class in its entirety, please make sure to give credit.
public class Parser<T>{
    private final Function<String, Token<T>> parse;
    /// A [Function] that parses a [String] input into an output of type [T].
    /// @param parse The parser function
    public Parser(Function<String, Token<T>> parse) {
        this.parse = parse;
    }

    /// Returns the output [Token] of the parser [Function] applied to the [String] input
    /// @param input The string input for the parser
    /// @return The output token for the given input
    public Token<T> parse(String input) {
        return parse.apply(input);
    }
    /// Returns the number of times the parser is successful when parsing the [String].
    /// @return The number or parser successes in the string
    public int count(String input) {
        Token<T> t = parse(input);
        if (!t.success()) return 0;
        return 1 + count(t.follow());
    }

    // Subclasses

    /// A [Parser] subclass that parses for a specific [Character].
    ///
    /// The parser function will find the first instance of the character
    /// or fail if that character is not found.
    public static class CharParser extends Parser<Character>{

        /// A [Parser] subclass that parses for a specific [Character].
        ///
        /// The parser function will find the first instance of the character
        /// or fail if that character is not found.
        /// @param c The character to parse for
        public CharParser(char c) {
            super(s -> {
                int i = s.indexOf(c);
                return new Token<>(c, (i >= 0) ? s.substring(0, i) : "", (i < s.length() - 1) ? s.substring(i + 1) : "", i != -1);
            });
        }
    }

    /// A [Parser] subclass that parses for a match to a `RegEx` pattern.
    ///
    /// Mostly for convenience. Should be avoided where other parser-based solutions are available.
    public static class RegExParser extends Parser<String>{
        /// A [Parser] subclass that parses for a match to a `RegEx` pattern.
        ///
        /// Mostly for convenience. Should be avoided where other parser-based solutions are available.
        /// @param regex The RegEx pattern to parse for
        public RegExParser(String regex) {
            super(s -> {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile(regex).matcher(s);
                String match = m.find() ? m.group() : "";
                if (match.isEmpty()) return new Token<>(null, "", s, false);
                return new Token<>(match, s.substring(0, m.start()), s.substring(m.end()), true);
            });
        }
    }

    /// A [Parser] subclass that deconstructs statements into groups that can be parsed individually,
    /// given parsers to find the opening and closing of groups.
    ///
    /// This parser is intended to handle grouping such as parentheticals.
    ///
    /// Ex. `"1 + (2 * 3) - (4 / (1 + 1))"` -> `inner.parse("1 + inner.parse("2 * 6") - inner.parse("4 / inner.parse("1 + 1")"))`.
    ///
    /// The parse will fail if there are not an equal number of successes on the opening and closing parser.
    ///
    /// Queer Coded by Striker-909.
    /// If you use this class or a method from this class in its entirety, please make sure to give credit.
    public static class GroupParser<T> extends Parser<T> {
        /// A [Parser] subclass that deconstructs statements into groups that can be parsed individually,
        /// given parsers to find the opening and closing of groups.
        ///
        /// This parser is intended to handle grouping such as parentheticals.
        ///
        /// Ex. `"1 + (2 * 3) - (4 / (1 + 1))"` -> `inner.parse("1 + inner.parse("2 * 6") - inner.parse("4 / inner.parse("1 + 1")"))`.
        ///
        /// The parse will fail if there are not an equal number of successes on the opening and closing parser.
        /// @param inner The parser that will be used to parse the statements within the groups
        /// @param open A parser for the opening of a group
        /// @param close The parser for the closing of a group
        public GroupParser(Parser<T> inner, Parser<?> open, Parser<?> close) {
            super(s -> {
                if (open.count(s) != close.count(s)) { return new Token<>(null, "", s, false); }
                return parseGroup(inner, open, close, s);
            } );
        }

        /// The recursive function that enables the group parser.
        /// @param inner The parser that will be used to parse the statements within the groups
        /// @param open A parser for the opening of a group
        /// @param close The parser for the closing of a group
        private static <U> Token<U> parseGroup(Parser<U> inner, Parser<?> open, Parser<?> close, String s) {
            Token<?> openToken = open.parse(s);
            Token<?> closeToken = close.parse(s);
            // No opening or closing -> parse statement and return
            if (!openToken.success() && !closeToken.success()) { return inner.parse(s); }
            // No opening or closing first -> innermost layer, parse closing head and follow, then combine the two
            if (!openToken.success() || closeToken.follow().length() >= openToken.follow().length()) {
                return inner.parse(inner.parse(closeToken.head()).value().toString() +
                                parseGroup(inner, open, close, closeToken.follow()).toString());
            }
            // No closing -> fail
            if (!closeToken.success())
                return new Token<>(null, "", s, false);
            // Opening and closing -> parse opening head and follow, then combine the two
            return inner.parse(parseGroup(inner, open, close, openToken.head()).toString() +
                    parseGroup(inner, open, close, openToken.follow()).toString());
        }
    }
}

