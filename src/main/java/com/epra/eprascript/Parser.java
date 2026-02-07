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
}

