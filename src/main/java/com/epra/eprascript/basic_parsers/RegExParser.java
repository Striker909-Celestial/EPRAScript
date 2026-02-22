package com.epra.eprascript.basic_parsers;

import com.epra.eprascript.parser_modifiers.Combinator;

import java.util.HashMap;

/// A [Parser] subclass that parses for a match to a `RegEx` pattern.
///
/// Mostly for convenience. Should be avoided where other parser-based solutions are available.
///
/// @author Striker-909
/// @since v0.1.0
public class RegExParser extends Parser<String>{

    @SuppressWarnings("unchecked")
    public static final Parser<Character> REGEX_META_CHARACTERS = (Parser<Character>) Combinator.OR.combine(
            new CharParser('\\'),
            new CharParser('.'),
            new CharParser('^'),
            new CharParser('$'),
            new CharParser('|'),
            new CharParser('?'),
            new CharParser('*'),
            new CharParser('+'),
            new CharParser('('),
            new CharParser(')'),
            new CharParser('['),
            new CharParser(']'),
            new CharParser('{'),
            new CharParser('}')
    );

    private final java.util.regex.Pattern pattern;
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
        pattern = java.util.regex.Pattern.compile(regex);
    }

    /// Returns a [HashMap] [Token] of all named groups according to the RegEx pattern.
    ///
    /// The token will be successful as long as the parse is successful, and at least one named
    /// group is present.
    /// @param input The string to be parsed
    /// @return A token of a hash map from the names of named groups to the parsed strings of those groups
    public Token<HashMap<String, String>> getNamedGroups(String input) {
        java.util.regex.Matcher m = pattern.matcher(input);
        if (!m.find()) return new Token<>(null, "", input, false);
        HashMap<String, Integer> groupIndices = new HashMap<>(m.namedGroups());
        HashMap<String, String> groups = new HashMap<>();
        groupIndices.forEach((k, v) -> groups.put(k, m.group(v)));
        return new Token<>(groups, "", input, !groups.isEmpty());
    }
}
