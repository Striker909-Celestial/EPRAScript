package com.epra.eprascript.basic_parsers;

import com.epra.eprascript.parser_modifiers.Combinator;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/// A [Function] that parses a [String] input into an [Token] of type [T].
/// @author Striker-909
/// @since v0.0.0
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
    /// Replaces the first success of the parser when parsing the input with the value of the parsed [Token].
    /// @param input The string to be parsed
    /// @return The string with the first parser success replaced with the value of the parsed token
    public String replace(String input) {
        return replace(input, Object::toString);
    }

    /// Replaces the first success of the parser when parsing the input with the replacement.
    /// @param input The string to be parsed
    /// @param replacement The string to replace the section that triggered the parser success with
    /// @return The string with the first parser success replaced with the replacement
    public String replace(String input, String replacement) {
        Token<T> t = parse(input);
        if (!t.success()) return input;
        return t.head() + replacement + t.follow();
    }
    /// Replaces the first success of the parser when parsing the input with the replacement
    /// found by applying the function to the parsed value.
    /// @param input The string to be parsed
    /// @param replacement A function that returns a replacement string based on the parsed value
    /// @return The string with the first parser success replaced with the replacement
    public String replace(String input, Function<T, String> replacement) {
        Token<T> t = parse(input);
        if (!t.success()) return input;
        return t.head() + replacement.apply(t.value()) + t.follow();
    }
    /// Replaces all successes of the parser when parsing the input with the value of the parsed [Token].
    ///
    /// @param input The string to be parsed
    /// @return The string with the all parser successes replaced with the value of the parsed token
    public String replaceAll(String input) {
        return replaceAll(input, Object::toString);
    }
    /// Replaces all successes of the parser when parsing the input with the replacement.
    ///
    /// @param input The string to be parsed
    /// @param replacement The string to replace the section that triggered the parser success with
    /// @return The string with the all parser successes replaced with the replacement
    public String replaceAll(String input, String replacement) {
        Token<T> t = parse(input);
        if (!t.success()) return input;
        return t.head() + replacement + replaceAll(t.follow(), replacement);
    }
    /// Replaces all successes of the parser when parsing the input with the replacement
    /// found by applying the function to the parsed value.
    ///
    /// @param input The string to be parsed
    /// @param replacement A function that returns a replacement string based on the parsed value
    /// @return The string with the all parser successes replaced with the replacement
    public String replaceAll(String input, Function<T, String> replacement) {
        Token<T> t = parse(input);
        if (!t.success()) return input;
        return t.head() + replacement.apply(t.value()) + replaceAll(t.follow(), replacement);
    }
    /// Replaces all successes of the parser when parsing the input with the value of the parsed [Token].
    ///
    /// **Warning: The replacement value should not trigger parser success to avoid the risk of infinite recursion.**
    /// @param input The string to be parsed
    /// @return The string with the all parser successes replaced with the value of the parsed token
    public String recursiveReplaceAll(String input) {
        return recursiveReplaceAll(input, Object::toString);
    }
    /// Replaces all successes of the parser when parsing the input with the replacement.
    ///
    /// **Warning: The replacement value should not trigger parser success to avoid the risk of infinite recursion.**
    /// @param input The string to be parsed
    /// @param replacement The string to replace the section that triggered the parser success with
    /// @return The string with the all parser successes replaced with the replacement
    public String recursiveReplaceAll(String input, String replacement) {
        Token<T> t = parse(input);
        if (!t.success()) return input;
        return recursiveReplaceAll(replace(input, replacement), replacement);
    }

    /// Replaces all successes of the parser when parsing the input with the replacement
    /// found by applying the function to the parsed value.
    ///
    /// **Warning: The replacement value should not trigger parser success to avoid the risk of infinite recursion.**
    /// @param input The string to be parsed
    /// @param replacement A function that returns a replacement string based on the parsed value
    /// @return The string with the all parser successes replaced with the replacement
    public String recursiveReplaceAll(String input, Function<T, String> replacement) {
        Token<T> t = parse(input);
        if (!t.success()) return input;
        return recursiveReplaceAll(replace(input, replacement), replacement);
    }
}

